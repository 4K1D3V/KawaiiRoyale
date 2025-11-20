package dev.oumaimaa.plugin.config.playerdata;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.database.DatabaseHandler;
import dev.oumaimaa.plugin.config.playerdata.database.MySQLHandler;
import dev.oumaimaa.plugin.config.playerdata.database.SQLiteHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlayerDataManager {

    private final Main plugin;
    private final Cache<UUID, PlayerData> cache;
    private DatabaseHandler database;

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();

        initializeDatabase();
    }

    private void initializeDatabase() {
        String dbType = plugin.getConfigManager().getDatabaseType();

        plugin.logInfo("Initializing " + dbType + " database...");

        if (dbType.equalsIgnoreCase("mysql")) {
            database = new MySQLHandler(plugin);
        } else {
            database = new SQLiteHandler(plugin);
        }

        database.initialize().thenAccept(success -> {
            if (success) {
                plugin.logInfo("Database initialized successfully!");
            } else {
                plugin.logSevere("Failed to initialize database!");
            }
        });
    }

    public PlayerData getPlayerData(@NotNull Player player) {
        PlayerData data = cache.getIfPresent(player.getUniqueId());
        if (data != null) {
            return data;
        }

        data = new PlayerData(player.getUniqueId());
        data.setName(player.getName());
        cache.put(player.getUniqueId(), data);

        database.loadPlayerData(player.getUniqueId()).thenAccept(loadedData -> {
            loadedData.setName(player.getName());
            cache.put(player.getUniqueId(), loadedData);
        });

        return data;
    }

    public CompletableFuture<PlayerData> getPlayerDataAsync(UUID uuid) {
        PlayerData cached = cache.getIfPresent(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return database.loadPlayerData(uuid).thenApply(data -> {
            cache.put(uuid, data);
            return data;
        });
    }

    public void savePlayerData(UUID uuid) {
        PlayerData data = cache.getIfPresent(uuid);
        if (data != null) {
            database.savePlayerData(data);
        }
    }

    public void savePlayerData(@NotNull Player player) {
        savePlayerData(player.getUniqueId());
    }

    public void saveAll() {
        plugin.logInfo("Saving all player data...");
        cache.asMap().values().forEach(database::savePlayerData);
    }

    public CompletableFuture<List<DatabaseHandler.LeaderboardEntry>> getTopPlayers(String stat, int limit) {
        return database.getTopPlayers(stat, limit);
    }

    public void shutdown() {
        saveAll();
        if (database != null) {
            database.close();
        }
    }
}
