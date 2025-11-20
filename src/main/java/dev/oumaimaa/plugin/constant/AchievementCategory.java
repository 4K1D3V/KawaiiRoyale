package dev.oumaimaa.plugin.constant;

/**
 * Achievement categories
 */
public enum AchievementCategory {
    COMBAT("Combat", "⚔"),
    VICTORIES("Victories", "🏆"),
    PROGRESSION("Progression", "📈"),
    SPECIAL("Special", "⭐");

    private final String displayName;
    private final String icon;

    AchievementCategory(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }
}
