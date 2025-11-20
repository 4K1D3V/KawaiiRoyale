package dev.oumaimaa.plugin.constant;

/**
 * Challenge frequency
 */
public enum ChallengeFrequency {
    DAILY("Daily", "§e"),
    WEEKLY("Weekly", "§6");

    private final String displayName;
    private final String color;

    ChallengeFrequency(String displayName, String color) {
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
