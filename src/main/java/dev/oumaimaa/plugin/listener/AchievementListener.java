package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.AchievementType;
import dev.oumaimaa.plugin.skeleton.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for achievement tracking
 */
public class AchievementListener implements Listener {

    private final Main plugin;

    public AchievementListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        plugin.getAchievementManager().loadPlayerAchievements(event.getPlayer());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        plugin.getAchievementManager().unloadPlayerAchievements(event.getPlayer().getUniqueId());
    }

    /**
     * Called when player gets a kill
     */
    public void onKill(Player player, int totalKills, int gameKills) {
        plugin.getAchievementManager().checkAchievement(player, AchievementType.KILLS, totalKills);
        plugin.getAchievementManager().checkAchievement(player, AchievementType.KILLS_IN_GAME, gameKills);
    }

    /**
     * Called when player wins
     */
    public void onWin(Player player, @NotNull Game game) {
        int totalWins = plugin.getPlayerDataManager().getPlayerData(player).getStatistics().getWins();
        plugin.getAchievementManager().checkAchievement(player, AchievementType.WINS, totalWins);

        int kills = game.getKills(player.getUniqueId());
        if (kills == 0) {
            plugin.getAchievementManager().checkAchievement(player, AchievementType.WIN_NO_KILLS, 1);
        }

        long duration = (System.currentTimeMillis() - game.getStartTime()) / 1000;
        if (duration < 300) {
            plugin.getAchievementManager().checkAchievement(player, AchievementType.FAST_WIN, (int) duration);
        }
    }

    /**
     * Called when player finishes a game
     */
    public void onGameEnd(Player player) {
        int totalGames = plugin.getPlayerDataManager().getPlayerData(player).getStatistics().getGamesPlayed();
        plugin.getAchievementManager().checkAchievement(player, AchievementType.GAMES_PLAYED, totalGames);
    }

    /**
     * Called when player deals damage
     */
    public void onDamageDealt(Player player, double damage) {
        int totalDamage = (int) plugin.getPlayerDataManager().getPlayerData(player).getStatistics().getDamageDealt();
        plugin.getAchievementManager().checkAchievement(player, AchievementType.DAMAGE_DEALT, totalDamage);
    }

    /**
     * Called when player takes damage
     */
    public void onDamageTaken(Player player, double damage) {
        int totalDamage = (int) plugin.getPlayerDataManager().getPlayerData(player).getStatistics().getDamageTaken();
        plugin.getAchievementManager().checkAchievement(player, AchievementType.DAMAGE_TAKEN, totalDamage);
    }
}