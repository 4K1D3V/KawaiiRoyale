package dev.oumaimaa.plugin.config;

import dev.oumaimaa.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Advanced configuration manager with hot-reload support
 */
public class ConfigManager {

    private static final String CONFIG_YML = "config.yml";
    private static final String MESSAGES_YML = "messages.yml";
    private static final String ARENAS_YML = "arenas.yml";
    private static final String LOOT_YML = "loot.yml";
    private static final String ZONES_YML = "zones.yml";

    private final Main plugin;
    private final Map<String, FileConfiguration> configs;
    private final Map<String, File> configFiles;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.configs = new HashMap<>();
        this.configFiles = new HashMap<>();
    }

    /**
     * Load all configuration files
     */
    public boolean load() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            loadConfig(CONFIG_YML);
            loadConfig(MESSAGES_YML);
            loadConfig(ARENAS_YML);
            loadConfig(LOOT_YML);
            loadConfig(ZONES_YML);

            plugin.logInfo("All configuration files loaded successfully!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load specific configuration file
     */
    private void loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(fileName, config);
        configFiles.put(fileName, file);
    }

    /**
     * Reload all configurations
     */
    public void reload() {
        configs.clear();
        load();
        plugin.logInfo("Configuration reloaded successfully!");
    }

    /**
     * Save specific configuration
     */
    public void save(String fileName) {
        try {
            FileConfiguration config = configs.get(fileName);
            File file = configFiles.get(fileName);
            if (config != null && file != null) {
                config.save(file);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + fileName + ": " + e.getMessage());
        }
    }

    public FileConfiguration getConfig(String fileName) {
        return configs.getOrDefault(fileName, null);
    }

    public FileConfiguration getMainConfig() {
        return getConfig(CONFIG_YML);
    }

    public FileConfiguration getMessagesConfig() {
        return getConfig(MESSAGES_YML);
    }

    public FileConfiguration getArenasConfig() {
        return getConfig(ARENAS_YML);
    }

    public FileConfiguration getLootConfig() {
        return getConfig(LOOT_YML);
    }

    public FileConfiguration getZonesConfig() {
        return getConfig(ZONES_YML);
    }

    public int getBattleRoyaleMinPlayers() {
        return getMainConfig().getInt("game-modes.battle-royale.min-players", 10);
    }

    public int getBattleRoyaleMaxPlayers() {
        return getMainConfig().getInt("game-modes.battle-royale.max-players", 100);
    }

    public int getResurgenceMinPlayers() {
        return getMainConfig().getInt("game-modes.resurgence.min-players", 8);
    }

    public int getResurgenceMaxPlayers() {
        return getMainConfig().getInt("game-modes.resurgence.max-players", 50);
    }

    public int getCountdownTime() {
        return getMainConfig().getInt("timers.countdown", 15);
    }

    public int getGracePeriod() {
        return getMainConfig().getInt("timers.grace-period", 60);
    }

    public int getInitialZoneSize() {
        return getMainConfig().getInt("zone.initial-size", 1000);
    }

    public int getFinalZoneSize() {
        return getMainConfig().getInt("zone.final-size", 50);
    }

    public int getZoneShrinkInterval() {
        return getMainConfig().getInt("zone.shrink-interval", 120);
    }

    public int getZoneDamage() {
        return getMainConfig().getInt("zone.damage-per-tick", 2);
    }

    public boolean isLootDropsEnabled() {
        return getMainConfig().getBoolean("loot.drops.enabled", true);
    }

    public int getLootDropInterval() {
        return getMainConfig().getInt("loot.drops.interval", 180);
    }

    public boolean isParachuteEnabled() {
        return getMainConfig().getBoolean("features.parachute.enabled", true);
    }

    public boolean isSpectatingEnabled() {
        return getMainConfig().getBoolean("features.spectating.enabled", true);
    }

    public boolean isRewardsEnabled() {
        return getMainConfig().getBoolean("features.rewards.enabled", true);
    }

    public boolean useEconomy() {
        return getMainConfig().getBoolean("features.rewards.use-economy", true);
    }

    public boolean useInternalCoins() {
        return getMainConfig().getBoolean("features.rewards.use-internal-coins", true);
    }

    public int getWinReward(String mode) {
        return getMainConfig().getInt("features.rewards." + mode + ".win-reward", 100);
    }

    public int getKillReward(String mode) {
        return getMainConfig().getInt("features.rewards." + mode + ".kill-reward", 10);
    }

    public String getDatabaseType() {
        return getMainConfig().getString("database.type", "sqlite");
    }

    public String getDatabaseHost() {
        return getMainConfig().getString("database.mysql.host", "localhost");
    }

    public int getDatabasePort() {
        return getMainConfig().getInt("database.mysql.port", 3306);
    }

    public String getDatabaseName() {
        return getMainConfig().getString("database.mysql.database", "kawaiiroyale");
    }

    public String getDatabaseUser() {
        return getMainConfig().getString("database.mysql.username", "root");
    }

    public String getDatabasePassword() {
        return getMainConfig().getString("database.mysql.password", "");
    }
}