package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.Arena;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Manages all game arenas
 */
public class ArenaManager {

    private final Main plugin;
    private final Map<String, Arena> arenas;

    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        loadArenas();
    }

    /**
     * Load all arenas from config
     */
    public void loadArenas() {
        FileConfiguration config = plugin.getConfigManager().getArenasConfig();
        if (config == null) {
            plugin.logWarning("Arenas config not found!");
            return;
        }

        ConfigurationSection arenasSection = config.getConfigurationSection("arenas");
        if (arenasSection == null) {
            plugin.logInfo("No arenas configured yet.");
            return;
        }

        for (String arenaName : arenasSection.getKeys(false)) {
            try {
                Arena arena = Arena.load(plugin, arenaName, Objects.requireNonNull(arenasSection.getConfigurationSection(arenaName)));
                arenas.put(arenaName.toLowerCase(), arena);
                plugin.logInfo("Loaded arena: " + arenaName);
            } catch (Exception e) {
                plugin.logWarning("Failed to load arena: " + arenaName);
                e.printStackTrace();
            }
        }

        plugin.logInfo("Loaded " + arenas.size() + " arena(s)");
    }

    /**
     * Get an arena by name
     */
    public Arena getArena(@NotNull String name) {
        return arenas.get(name.toLowerCase());
    }

    /**
     * Get all arenas
     */
    public Collection<Arena> getAllArenas() {
        return Collections.unmodifiableCollection(arenas.values());
    }

    /**
     * Add a new arena
     */
    public void addArena(Arena arena) {
        arenas.put(arena.getName().toLowerCase(), arena);
        saveArena(arena);
    }

    /**
     * Remove an arena
     */
    public void removeArena(@NotNull String name) {
        arenas.remove(name.toLowerCase());

        FileConfiguration config = plugin.getConfigManager().getArenasConfig();
        config.set("arenas." + name, null);
        plugin.getConfigManager().save("arenas.yml");
    }

    /**
     * Save arena to config
     */
    public void saveArena(@NotNull Arena arena) {
        FileConfiguration config = plugin.getConfigManager().getArenasConfig();
        arena.save(config, "arenas." + arena.getName());
        plugin.getConfigManager().save("arenas.yml");
    }

    /**
     * Get a random arena
     */
    public Arena getRandomArena() {
        if (arenas.isEmpty()) {
            return null;
        }
        List<Arena> arenaList = new ArrayList<>(arenas.values());
        return arenaList.get(new Random().nextInt(arenaList.size()));
    }

    /**
     * Check if arena exists
     */
    public boolean exists(@NotNull String name) {
        return arenas.containsKey(name.toLowerCase());
    }

    /**
     * Reload all arenas
     */
    public void reload() {
        arenas.clear();
        loadArenas();
    }

    /**
     * Shutdown
     */
    public void shutdown() {
        arenas.clear();
    }
}
