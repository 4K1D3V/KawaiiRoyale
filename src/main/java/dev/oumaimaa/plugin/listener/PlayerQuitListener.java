package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {
    private final Main plugin;

    public PlayerQuitListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGameManager().isInGame(player)) {
            var game = plugin.getGameManager().getPlayerGame(player);
            if (game != null) {
                var gamePlayer = plugin.getGameManager().getGamePlayer(player);
                game.removePlayer(gamePlayer);
            }
        }

        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
        plugin.getGameManager().removeGamePlayer(player.getUniqueId());

        plugin.getDisplayManager().getScoreboardManager().removeScoreboard(player);
        plugin.getDisplayManager().getBossBarManager().removeAllBossBars(player);

        plugin.getAchievementManager().unloadPlayerAchievements(player.getUniqueId());
        plugin.getCosmeticManager().unloadPlayerCosmetics(player.getUniqueId());
        plugin.getChallengeManager().unloadPlayerChallenges(player.getUniqueId());
    }
}