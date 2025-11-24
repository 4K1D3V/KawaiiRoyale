package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.config.playerdata.PlayerStatistics;
import dev.oumaimaa.plugin.skeleton.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player scoreboards
 */
public class ScoreboardManager {

    private final Main plugin;
    private final Map<UUID, Scoreboard> scoreboards;

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
        this.scoreboards = new HashMap<>();

        startUpdateTask();
    }

    /**
     * Create scoreboard for player
     */
    public void createScoreboard(@NotNull Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("kawaii", Criteria.DUMMY,
                plugin.getMiniMessage().deserialize("<gradient:#ff69b4:#ff1493>KawaiiRoyale</gradient>"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);
        scoreboards.put(player.getUniqueId(), scoreboard);

        updateScoreboard(player);
    }

    /**
     * Update scoreboard for player
     */
    public void updateScoreboard(@NotNull Player player) {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective("kawaii");
        if (objective == null) return;

        // Clear existing scores
        scoreboard.getEntries().forEach(scoreboard::resetScores);

        Game game = plugin.getGameManager().getPlayerGame(player);

        if (game != null) {
            updateInGameScoreboard(player, game, objective);
        } else {
            updateLobbyScoreboard(player, objective);
        }
    }

    /**
     * Update in-game scoreboard
     */
    private void updateInGameScoreboard(@NotNull Player player, @NotNull Game game, Objective objective) {
        int line = 15;

        setScore(objective, " ", line--);
        setScore(objective, "§7Arena: §f" + game.getArena().getDisplayName(), line--);
        setScore(objective, "§7Mode: §f" + game.getMode().getDisplayName(), line--);
        setScore(objective, "  ", line--);

        // Game stats
        setScore(objective, "§7Players: §f" + game.getAlivePlayers().size(), line--);
        setScore(objective, "§7Phase: §f" + game.getPhase().getDisplayName(), line--);
        setScore(objective, "   ", line--);

        // Player stats
        int kills = game.getKills(player.getUniqueId());
        setScore(objective, "§7Your Kills: §f" + kills, line--);

        if (game.getZone() != null) {
            setScore(objective, "    ", line--);
            setScore(objective, "§7Zone Radius: §f" + (int) game.getZone().getCurrentRadius(), line--);

            if (game.getZone().isShrinking()) {
                setScore(objective, "§c⚠ Zone Shrinking!", line--);
            }
        }

        setScore(objective, "     ", line--);
        setScore(objective, "§ewww.yourserver.com", line--);
    }

    /**
     * Update lobby scoreboard
     */
    private void updateLobbyScoreboard(Player player, Objective objective) {
        int line = 15;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        PlayerStatistics stats = data.getStatistics();

        setScore(objective, " ", line--);
        setScore(objective, "§7Your Statistics:", line--);
        setScore(objective, "  ", line--);
        setScore(objective, "§7Kills: §f" + stats.getKills(), line--);
        setScore(objective, "§7Deaths: §f" + stats.getDeaths(), line--);
        setScore(objective, "§7Wins: §f" + stats.getWins(), line--);
        setScore(objective, "§7K/D: §f" + String.format("%.2f", stats.getKDRatio()), line--);
        setScore(objective, "   ", line--);
        setScore(objective, "§7Active Games: §f" + plugin.getGameManager().getActiveGameCount(), line--);
        setScore(objective, "    ", line--);
        setScore(objective, "§e/kawaii to play!", line--);
    }

    /**
     * Set score on objective
     */
    private void setScore(@NotNull Objective objective, String text, int score) {
        Score s = objective.getScore(text);
        s.setScore(score);
    }

    /**
     * Remove scoreboard from player
     */
    public void removeScoreboard(@NotNull Player player) {
        scoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * Start update task
     */
    private void startUpdateTask() {
        int interval = plugin.getConfigManager().getMainConfig()
                .getInt("customization.scoreboard.update-interval", 10);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID uuid : scoreboards.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    updateScoreboard(player);
                }
            }
        }, 0L, interval);
    }

    /**
     * Shutdown
     */
    public void shutdown() {
        scoreboards.clear();
    }
}