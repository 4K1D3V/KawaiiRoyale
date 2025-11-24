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

/**
 * Manages player data with Caffeine caching for optimal performance
 */
public class PlayerDataManager {

    private final Main plugin;
    private final Cache<UUID, PlayerData> cache;
    private DatabaseHandler database;

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;

        int cacheSize = plugin.getConfigManager().getMainConfig()
                .getInt("performance.cache.cache-size", 1000);
        int cacheExpiry = plugin.getConfigManager().getMainConfig()
                .getInt("performance.cache.cache-expiry", 300);

        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(cacheExpiry, TimeUnit.SECONDS)
                .maximumSize(cacheSize)
                .recordStats()
                .build();

        initializeDatabase();
    }

    /**
     * Initialize database connection based on configuration
     */
    private void initializeDatabase() {
        String dbType = plugin.getConfigManager().getDatabaseType();

        plugin.logInfo("Initializing " + dbType.toUpperCase() + " database...");

        if (dbType.equalsIgnoreCase("mysql")) {
            database = new MySQLHandler(plugin);
        } else {
            database = new SQLiteHandler(plugin);
        }

        database.initialize().thenAccept(success -> {
            if (success) {
                plugin.logInfo("Database initialized successfully!");
                plugin.logInfo("Cache stats: " + cache.stats());
            } else {
                plugin.logSevere("Failed to initialize database!");
            }
        });
    }

    /**
     * Get player data with caching
     */
    @NotNull
    public PlayerData getPlayerData(@NotNull Player player) {
        return getPlayerData(player.getUniqueId(), player.getName());
    }

    /**
     * Get player data by UUID with caching
     */
    @NotNull
    public PlayerData getPlayerData(@NotNull UUID uuid) {
        return getPlayerData(uuid, null);
    }

    /**
     * Internal method to get player data
     */
    @NotNull
    private PlayerData getPlayerData(@NotNull UUID uuid, String name) {
        PlayerData data = cache.getIfPresent(uuid);

        if (data != null) {
            if (name != null && !name.equals(data.getName())) {
                data.setName(name);
            }
            return data;
        }

        data = new PlayerData(uuid);
        if (name != null) {
            data.setName(name);
        }

        cache.put(uuid, data);

        if (plugin.getConfigManager().getMainConfig()
                .getBoolean("performance.async-loads", true)) {
            database.loadPlayerData(uuid).thenAccept(loadedData -> {
                if (name != null) {
                    loadedData.setName(name);
                }
                loadedData.updateLastSeen();
                cache.put(uuid, loadedData);
            });
        }

        return data;
    }

    /**
     * Get player data asynchronously
     */
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

    /**
     * Save player data
     */
    public void savePlayerData(@NotNull UUID uuid) {
        PlayerData data = cache.getIfPresent(uuid);
        if (data != null) {
            data.updateLastSeen();

            if (plugin.getConfigManager().getMainConfig()
                    .getBoolean("performance.async-saves", true)) {
                CompletableFuture.runAsync(() -> database.savePlayerData(data));
            } else {
                database.savePlayerData(data);
            }
        }
    }

    /**
     * Save player data by player
     */
    public void savePlayerData(@NotNull Player player) {
        savePlayerData(player.getUniqueId());
    }

    /**
     * Save all cached player data
     */
    public void saveAll() {
        plugin.logInfo("Saving all player data... (" + cache.estimatedSize() + " players)");

        cache.asMap().values().parallelStream().forEach(data -> {
            try {
                database.savePlayerData(data);
            } catch (Exception e) {
                plugin.logSevere("Failed to save data for " + data.getName() + ": " + e.getMessage());
            }
        });

        plugin.logInfo("All player data saved!");
    }

    /**
     * Get top players by statistic
     */
    public CompletableFuture<List<DatabaseHandler.LeaderboardEntry>> getTopPlayers(String stat, int limit) {
        return database.getTopPlayers(stat, limit);
    }

    /**
     * Invalidate cache entry
     */
    public void invalidateCache(UUID uuid) {
        cache.invalidate(uuid);
    }

    /**
     * Clear entire cache
     */
    public void clearCache() {
        cache.invalidateAll();
        plugin.logInfo("Player data cache cleared!");
    }

    /**
     * Get cache statistics
     */
    public String getCacheStats() {
        return String.format(
                "Cache: %d entries, Hit Rate: %.2f%%, Evictions: %d",
                cache.estimatedSize(),
                cache.stats().hitRate() * 100,
                cache.stats().evictionCount()
        );
    }

    /**
     * Shutdown manager
     */
    public void shutdown() {
        plugin.logInfo("Shutting down player data manager...");
        saveAll();

        if (database != null) {
            database.close();
        }

        cache.invalidateAll();
        plugin.logInfo("Player data manager shutdown complete!");
    }
}