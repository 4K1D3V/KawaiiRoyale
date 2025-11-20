package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game arena
 */
public class Arena {

    private final String name;
    private final List<Location> spawnLocations;
    private String displayName;
    private Location center;
    private Location lobbyLocation;
    private int maxPlayers;
    private boolean enabled;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.spawnLocations = new ArrayList<>();
        this.maxPlayers = 100;
        this.enabled = true;
    }

    /**
     * Load arena from configuration
     */
    public static @NotNull Arena load(Main plugin, String name, @NotNull ConfigurationSection section) {
        Arena arena = new Arena(name);

        arena.displayName = section.getString("display-name", name);
        arena.enabled = section.getBoolean("enabled", true);
        arena.maxPlayers = section.getInt("max-players", 100);

        // Load center
        if (section.contains("center")) {
            arena.center = deserializeLocation(section.getConfigurationSection("center"));
        }

        // Load lobby
        if (section.contains("lobby")) {
            arena.lobbyLocation = deserializeLocation(section.getConfigurationSection("lobby"));
        }

        // Load spawns
        ConfigurationSection spawnsSection = section.getConfigurationSection("spawns");
        if (spawnsSection != null) {
            for (String key : spawnsSection.getKeys(false)) {
                Location spawn = deserializeLocation(spawnsSection.getConfigurationSection(key));
                if (spawn != null) {
                    arena.spawnLocations.add(spawn);
                }
            }
        }

        return arena;
    }

    /**
     * Serialize location to config
     */
    private static void serializeLocation(@NotNull FileConfiguration config, String path, @NotNull Location loc) {
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }

    /**
     * Deserialize location from config
     */
    private static Location deserializeLocation(ConfigurationSection section) {
        if (section == null) return null;

        String worldName = section.getString("world");
        assert worldName != null;
        World world = Main.getInstance().getServer().getWorld(worldName);
        if (world == null) return null;

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Save arena to configuration
     */
    public void save(@NotNull FileConfiguration config, String path) {
        config.set(path + ".display-name", displayName);
        config.set(path + ".enabled", enabled);
        config.set(path + ".max-players", maxPlayers);

        if (center != null) {
            serializeLocation(config, path + ".center", center);
        }

        if (lobbyLocation != null) {
            serializeLocation(config, path + ".lobby", lobbyLocation);
        }

        for (int i = 0; i < spawnLocations.size(); i++) {
            serializeLocation(config, path + ".spawns." + i, spawnLocations.get(i));
        }
    }

    /**
     * Add a spawn location
     */
    public void addSpawn(Location location) {
        spawnLocations.add(location);
    }

    /**
     * Remove a spawn location
     */
    public void removeSpawn(int index) {
        if (index >= 0 && index < spawnLocations.size()) {
            spawnLocations.remove(index);
        }
    }

    /**
     * Clear all spawns
     */
    public void clearSpawns() {
        spawnLocations.clear();
    }

    /**
     * Check if arena is properly configured
     */
    public boolean isValid() {
        return center != null &&
                lobbyLocation != null &&
                !spawnLocations.isEmpty() &&
                enabled;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public List<Location> getSpawnLocations() {
        return new ArrayList<>(spawnLocations);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}