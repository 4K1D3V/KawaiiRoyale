package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {
    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerDataManager().getPlayerData(player);
        plugin.getDisplayManager().getScoreboardManager().createScoreboard(player);
        plugin.getAchievementManager().loadPlayerAchievements(player);
        plugin.getCosmeticManager().loadPlayerCosmetics(player);
        plugin.getChallengeManager().loadPlayerChallenges(player);
    }
}