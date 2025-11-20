package dev.oumaimaa.plugin.constant;

/**
 * Challenge difficulty
 */
public enum ChallengeDifficulty {
    EASY("Easy", "§a"),
    MEDIUM("Medium", "§e"),
    HARD("Hard", "§c");

    private final String displayName;
    private final String color;

    ChallengeDifficulty(String displayName, String color) {
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