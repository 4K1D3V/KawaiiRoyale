package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;

/**
 * Manages all display elements (scoreboard, boss bar, etc.)
 */
public class DisplayManager {

    private final Main plugin;
    private final ScoreboardManager scoreboardManager;
    private final BossBarManager bossBarManager;

    public DisplayManager(Main plugin) {
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager(plugin);
        this.bossBarManager = new BossBarManager(plugin);
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public void shutdown() {
        scoreboardManager.shutdown();
        bossBarManager.shutdown();
    }
}