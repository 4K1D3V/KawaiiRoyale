package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.AchievementCategory;
import dev.oumaimaa.plugin.constant.AchievementDifficulty;
import dev.oumaimaa.plugin.constant.AchievementType;
import dev.oumaimaa.plugin.record.Achievement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player achievements
 */
public class AchievementManager {

    private final Main plugin;
    private final Map<String, Achievement> achievements;
    private final Map<UUID, Set<String>> playerAchievements;

    public AchievementManager(Main plugin) {
        this.plugin = plugin;
        this.achievements = new LinkedHashMap<>();
        this.playerAchievements = new ConcurrentHashMap<>();

        registerAchievements();
    }

    /**
     * Register all achievements
     */
    private void registerAchievements() {

        register(new Achievement(
                "first_blood",
                "First Blood",
                "Get your first kill",
                AchievementCategory.COMBAT,
                AchievementDifficulty.EASY,
                100,
                AchievementType.KILLS,
                1
        ));

        register(new Achievement(
                "killing_spree",
                "Killing Spree",
                "Get 5 kills in one game",
                AchievementCategory.COMBAT,
                AchievementDifficulty.MEDIUM,
                250,
                AchievementType.KILLS_IN_GAME,
                5
        ));

        register(new Achievement(
                "first_victory",
                "First Victory",
                "Win your first game",
                AchievementCategory.VICTORIES,
                AchievementDifficulty.EASY,
                150,
                AchievementType.WINS,
                1
        ));

        register(new Achievement(
                "unstoppable",
                "Unstoppable",
                "Win 3 games in a row",
                AchievementCategory.VICTORIES,
                AchievementDifficulty.HARD,
                500,
                AchievementType.WIN_STREAK,
                3
        ));

        register(new Achievement(
                "veteran",
                "Veteran",
                "Play 100 games",
                AchievementCategory.PROGRESSION,
                AchievementDifficulty.MEDIUM,
                300,
                AchievementType.GAMES_PLAYED,
                100
        ));

        register(new Achievement(
                "sharpshooter",
                "Sharpshooter",
                "Get 100 kills",
                AchievementCategory.COMBAT,
                AchievementDifficulty.MEDIUM,
                400,
                AchievementType.KILLS,
                100
        ));

        register(new Achievement(
                "assassin",
                "Assassin",
                "Get 500 kills",
                AchievementCategory.COMBAT,
                AchievementDifficulty.HARD,
                1000,
                AchievementType.KILLS,
                500
        ));

        register(new Achievement(
                "champion",
                "Champion",
                "Win 10 games",
                AchievementCategory.VICTORIES,
                AchievementDifficulty.MEDIUM,
                600,
                AchievementType.WINS,
                10
        ));

        register(new Achievement(
                "legend",
                "Legend",
                "Win 50 games",
                AchievementCategory.VICTORIES,
                AchievementDifficulty.HARD,
                2000,
                AchievementType.WINS,
                50
        ));

        register(new Achievement(
                "survivor",
                "Survivor",
                "Survive to top 3 ten times",
                AchievementCategory.PROGRESSION,
                AchievementDifficulty.MEDIUM,
                350,
                AchievementType.TOP_3,
                10
        ));

        register(new Achievement(
                "pacifist",
                "Pacifist",
                "Win a game with 0 kills",
                AchievementCategory.SPECIAL,
                AchievementDifficulty.HARD,
                750,
                AchievementType.WIN_NO_KILLS,
                1
        ));

        register(new Achievement(
                "zone_master",
                "Zone Master",
                "Never take zone damage in 10 games",
                AchievementCategory.SPECIAL,
                AchievementDifficulty.MEDIUM,
                400,
                AchievementType.NO_ZONE_DAMAGE,
                10
        ));

        register(new Achievement(
                "damage_dealer",
                "Damage Dealer",
                "Deal 10,000 damage",
                AchievementCategory.COMBAT,
                AchievementDifficulty.MEDIUM,
                450,
                AchievementType.DAMAGE_DEALT,
                10000
        ));

        register(new Achievement(
                "tank",
                "Tank",
                "Take 10,000 damage",
                AchievementCategory.COMBAT,
                AchievementDifficulty.MEDIUM,
                450,
                AchievementType.DAMAGE_TAKEN,
                10000
        ));

        register(new Achievement(
                "quick_draw",
                "Quick Draw",
                "Win a game in under 5 minutes",
                AchievementCategory.SPECIAL,
                AchievementDifficulty.HARD,
                800,
                AchievementType.FAST_WIN,
                300 // 5 minutes in seconds
        ));
    }

    /**
     * Register an achievement
     */
    public void register(Achievement achievement) {
        achievements.put(achievement.id(), achievement);
    }

    /**
     * Check and award achievement to player
     */
    public void checkAchievement(Player player, AchievementType type, int value) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Set<String> unlocked = playerAchievements.computeIfAbsent(
                player.getUniqueId(),
                k -> data.getUnlockedAchievements()
        );

        for (Achievement achievement : achievements.values()) {
            if (achievement.type() == type && !unlocked.contains(achievement.id())) {
                if (achievement.checkProgress(value)) {
                    unlockAchievement(player, achievement);
                }
            }
        }
    }

    /**
     * Unlock achievement for player
     */
    private void unlockAchievement(Player player, @NotNull Achievement achievement) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        Set<String> unlocked = playerAchievements.get(player.getUniqueId());
        unlocked.add(achievement.id());
        data.addAchievement(achievement.id());
        giveRewards(player, achievement);
        announceAchievement(player, achievement);
    }

    /**
     * Give achievement rewards
     */
    private void giveRewards(Player player, @NotNull Achievement achievement) {
        // TODO: Add economy rewards if Vault is present
        // For now, just track the coins internally
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.addCoins(achievement.reward());
    }

    /**
     * Announce achievement unlock
     */
    private void announceAchievement(@NotNull Player player, @NotNull Achievement achievement) {

        Component title = plugin.getMiniMessage().deserialize(
                "<gradient:#ffd700:#ffed4e>Achievement Unlocked!</gradient>"
        );
        Component subtitle = plugin.getMiniMessage().deserialize(
                "<yellow>" + achievement.name()
        );

        player.showTitle(Title.title(
                title,
                subtitle,
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(500)
                )
        ));

        // Chat message
        player.sendMessage(Component.text("")
                .append(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("  Achievement Unlocked!").color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("  ").color(NamedTextColor.GRAY))
                .append(Component.text(achievement.name()).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("  " + achievement.description()).color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text("  +").color(NamedTextColor.GREEN))
                .append(Component.text(achievement.reward() + " coins").color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD))
        );

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        // Broadcast to server
        if (achievement.difficulty() == AchievementDifficulty.HARD) {
            plugin.getServer().broadcast(
                    Component.text(player.getName()).color(NamedTextColor.GOLD)
                            .append(Component.text(" has unlocked ").color(NamedTextColor.GRAY))
                            .append(Component.text(achievement.name()).color(NamedTextColor.YELLOW))
                            .append(Component.text("!").color(NamedTextColor.GRAY))
            );
        }
    }

    /**
     * Get all achievements
     */
    public Collection<Achievement> getAllAchievements() {
        return Collections.unmodifiableCollection(achievements.values());
    }

    /**
     * Get achievement by ID
     */
    public Achievement getAchievement(String id) {
        return achievements.get(id);
    }

    /**
     * Check if player has achievement
     */
    public boolean hasAchievement(@NotNull Player player, String achievementId) {
        Set<String> unlocked = playerAchievements.get(player.getUniqueId());
        return unlocked != null && unlocked.contains(achievementId);
    }

    /**
     * Get player's unlocked achievements
     */
    public Set<String> getUnlockedAchievements(@NotNull Player player) {
        return playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    /**
     * Get achievement progress
     */
    public int getProgress(Player player, String achievementId) {
        Achievement achievement = achievements.get(achievementId);
        if (achievement == null) return 0;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        return achievement.getCurrentProgress(data);
    }

    /**
     * Load player achievements
     */
    public void loadPlayerAchievements(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        playerAchievements.put(player.getUniqueId(), new HashSet<>(data.getUnlockedAchievements()));
    }

    /**
     * Unload player achievements
     */
    public void unloadPlayerAchievements(UUID uuid) {
        playerAchievements.remove(uuid);
    }
}