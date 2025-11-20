package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import org.bukkit.event.Listener;

public class ListenerManager {

    private final Main plugin;

    public ListenerManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {
        registerListener(new PlayerJoinListener(plugin));
        registerListener(new PlayerQuitListener(plugin));
        registerListener(new GameListener(plugin));
        registerListener(new CombatListener(plugin));
        registerListener(new ZoneListener(plugin));
        registerListener(new AchievementListener(plugin));
    }

    private void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void shutdown() {
        // Nothing needed here
    }
}