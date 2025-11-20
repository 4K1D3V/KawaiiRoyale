package dev.oumaimaa.plugin.constant;

/**
 * Achievement difficulty levels
 */
public enum AchievementDifficulty {
    EASY("Easy", "§a"),
    MEDIUM("Medium", "§e"),
    HARD("Hard", "§c");

    private final String displayName;
    private final String color;

    AchievementDifficulty(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}