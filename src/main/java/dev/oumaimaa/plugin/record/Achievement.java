package dev.oumaimaa.plugin.record;

import dev.oumaimaa.plugin.constant.AchievementCategory;
import dev.oumaimaa.plugin.constant.AchievementDifficulty;
import dev.oumaimaa.plugin.constant.AchievementType;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.config.playerdata.PlayerStatistics;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an achievement
 */
public record Achievement(String id, String name, String description, AchievementCategory category,
                          AchievementDifficulty difficulty, int reward, AchievementType type, int requiredValue) {

    /**
     * Check if player has completed this achievement
     */
    public boolean checkProgress(int currentValue) {
        return currentValue >= requiredValue;
    }

    /**
     * Get current progress for player
     */
    public int getCurrentProgress(@NotNull PlayerData data) {
        PlayerStatistics stats = data.getStatistics();

        return switch (type) {
            case KILLS -> stats.getKills();
            case WINS -> stats.getWins();
            case GAMES_PLAYED -> stats.getGamesPlayed();
            case DAMAGE_DEALT -> (int) stats.getDamageDealt();
            case DAMAGE_TAKEN -> (int) stats.getDamageTaken();
            default -> 0;
        };
    }

    /**
     * Get progress percentage
     */
    public double getProgressPercentage(PlayerData data) {
        int current = getCurrentProgress(data);
        return Math.min(100.0, (double) current / requiredValue * 100);
    }
}