package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.record.ChallengeTemplate;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.ChallengeDifficulty;
import dev.oumaimaa.plugin.constant.ChallengeFrequency;
import dev.oumaimaa.plugin.constant.ChallengeType;
import dev.oumaimaa.plugin.skeleton.Challenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages daily and weekly challenges
 */
public class ChallengeManager {

    private final Main plugin;
    private final Map<UUID, List<Challenge>> playerChallenges;
    private final List<ChallengeTemplate> dailyTemplates;
    private final List<ChallengeTemplate> weeklyTemplates;

    public ChallengeManager(Main plugin) {
        this.plugin = plugin;
        this.playerChallenges = new ConcurrentHashMap<>();
        this.dailyTemplates = new ArrayList<>();
        this.weeklyTemplates = new ArrayList<>();

        registerChallenges();
        startResetTask();
    }

    /**
     * Register all challenge templates
     */
    private void registerChallenges() {
        // Daily challenges
        dailyTemplates.add(new ChallengeTemplate(
                "daily_kills_5",
                "Get 5 Kills",
                "Eliminate 5 players",
                ChallengeType.KILLS,
                5,
                100,
                50,
                ChallengeDifficulty.EASY
        ));

        dailyTemplates.add(new ChallengeTemplate(
                "daily_win_1",
                "Win a Game",
                "Win any game mode",
                ChallengeType.WINS,
                1,
                200,
                100,
                ChallengeDifficulty.MEDIUM
        ));

        dailyTemplates.add(new ChallengeTemplate(
                "daily_games_3",
                "Play 3 Games",
                "Complete 3 matches",
                ChallengeType.GAMES_PLAYED,
                3,
                75,
                30,
                ChallengeDifficulty.EASY
        ));

        dailyTemplates.add(new ChallengeTemplate(
                "daily_damage_1000",
                "Deal 1000 Damage",
                "Deal 1000 total damage",
                ChallengeType.DAMAGE_DEALT,
                1000,
                150,
                75,
                ChallengeDifficulty.MEDIUM
        ));

        dailyTemplates.add(new ChallengeTemplate(
                "daily_top3",
                "Top 3 Finish",
                "Finish in the top 3",
                ChallengeType.TOP_3,
                1,
                125,
                60,
                ChallengeDifficulty.MEDIUM
        ));

        // Weekly challenges
        weeklyTemplates.add(new ChallengeTemplate(
                "weekly_kills_50",
                "50 Eliminations",
                "Get 50 kills this week",
                ChallengeType.KILLS,
                50,
                500,
                250,
                ChallengeDifficulty.HARD
        ));

        weeklyTemplates.add(new ChallengeTemplate(
                "weekly_wins_5",
                "5 Victories",
                "Win 5 games this week",
                ChallengeType.WINS,
                5,
                750,
                400,
                ChallengeDifficulty.HARD
        ));

        weeklyTemplates.add(new ChallengeTemplate(
                "weekly_games_20",
                "Play 20 Games",
                "Complete 20 matches",
                ChallengeType.GAMES_PLAYED,
                20,
                400,
                200,
                ChallengeDifficulty.MEDIUM
        ));
    }

    /**
     * Generate daily challenges for player
     */
    public List<Challenge> generateDailyChallenges(Player player) {
        List<Challenge> challenges = new ArrayList<>();

        // Select 3 random daily challenges
        Collections.shuffle(dailyTemplates);
        for (int i = 0; i < Math.min(3, dailyTemplates.size()); i++) {
            challenges.add(new Challenge(dailyTemplates.get(i), ChallengeFrequency.DAILY));
        }

        // Select 1 weekly challenge
        if (!weeklyTemplates.isEmpty()) {
            ChallengeTemplate weekly = weeklyTemplates.get(new Random().nextInt(weeklyTemplates.size()));
            challenges.add(new Challenge(weekly, ChallengeFrequency.WEEKLY));
        }

        playerChallenges.put(player.getUniqueId(), challenges);
        return challenges;
    }

    /**
     * Get player's active challenges
     */
    public List<Challenge> getPlayerChallenges(@NotNull Player player) {
        List<Challenge> challenges = playerChallenges.get(player.getUniqueId());

        if (challenges == null || shouldResetDaily(player)) {
            challenges = generateDailyChallenges(player);
        }

        return challenges;
    }

    /**
     * Update challenge progress
     */
    public void updateProgress(Player player, ChallengeType type, int amount) {
        List<Challenge> challenges = getPlayerChallenges(player);

        for (Challenge challenge : challenges) {
            if (challenge.getType() == type && !challenge.isCompleted()) {
                challenge.addProgress(amount);

                if (challenge.isCompleted() && !challenge.isRewardClaimed()) {
                    completeChallenge(player, challenge);
                }
            }
        }
    }

    /**
     * Complete challenge and give rewards
     */
    private void completeChallenge(Player player, @NotNull Challenge challenge) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        data.addCoins(challenge.getCoinReward());

        plugin.getBattlePassManager().addXP(player, challenge.getXpReward(), "Challenge Completed");

        challenge.setRewardClaimed(true);

        // Notify player
        player.sendMessage(Component.text("")
                .append(Component.text("━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("  Challenge Completed!").color(NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  " + challenge.getName()).color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("  + " + challenge.getCoinReward() + " coins").color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("  + " + challenge.getXpReward() + " XP").color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD))
        );

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    /**
     * Check if daily challenges should reset
     */
    private boolean shouldResetDaily(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        long lastReset = data.getLastDailyChallengeReset();

        LocalDate lastResetDate = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(lastReset),
                ZoneId.systemDefault()
        ).toLocalDate();

        LocalDate today = LocalDate.now();

        return !lastResetDate.equals(today);
    }

    /**
     * Start automatic reset task
     */
    private void startResetTask() {
        // Check every hour for resets
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            LocalDate today = LocalDate.now();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (shouldResetDaily(player)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
                        data.resetDailyChallenges();
                        playerChallenges.remove(player.getUniqueId());

                        player.sendMessage(Component.text("✓ Daily challenges have been reset!")
                                .color(NamedTextColor.GREEN));
                    });
                }
            }
        }, 0L, 72000L); // Every hour
    }

    /**
     * Load player challenges
     */
    public void loadPlayerChallenges(Player player) {
        getPlayerChallenges(player);
    }

    /**
     * Unload player challenges
     */
    public void unloadPlayerChallenges(UUID uuid) {
        playerChallenges.remove(uuid);
    }
}