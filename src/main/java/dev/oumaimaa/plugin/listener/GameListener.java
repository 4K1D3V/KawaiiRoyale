package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.AchievementType;
import dev.oumaimaa.plugin.constant.ChallengeType;
import dev.oumaimaa.plugin.constant.CrateType;
import dev.oumaimaa.plugin.skeleton.Game;
import dev.oumaimaa.plugin.skeleton.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Game event listener with Phase 3 integration
 */
public class GameListener implements Listener {
    private final Main plugin;

    public GameListener(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a game ends - integrate with Phase 3
     */
    public void onGameEnd(@NotNull Game game) {
        var winners = game.getAlivePlayers();

        for (GamePlayer gp : game.getAllPlayers()) {
            Player player = gp.getPlayer();
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

            data.getStatistics().addGame(1);

            plugin.getBattlePassManager().addXP(player, 100, "Game Completed");
            plugin.getChallengeManager().updateProgress(player, ChallengeType.GAMES_PLAYED, 1);
            plugin.getAchievementManager().checkAchievement(
                    player,
                    AchievementType.GAMES_PLAYED,
                    data.getStatistics().getGamesPlayed()
            );

            boolean isWinner = winners.contains(gp);

            if (isWinner) {
                data.getStatistics().addWin(1);

                int winCoins = plugin.getConfigManager().getMainConfig().getInt("rewards.win-coins", 150);
                data.addCoins(winCoins);

                player.sendMessage(Component.text("+ " + winCoins + " coins for winning!")
                        .color(NamedTextColor.GOLD));

                plugin.getBattlePassManager().addXP(player, 500, "Victory");
                plugin.getAchievementManager().checkAchievement(
                        player,
                        AchievementType.WINS,
                        data.getStatistics().getWins()
                );
                plugin.getChallengeManager().updateProgress(player, ChallengeType.WINS, 1);

                // Give random crate
                if (Math.random() < 0.3) { // 30% chance
                    plugin.getCrateManager().giveCrate(player, CrateType.COMMON, 1);
                }
            }
        }
    }
}
