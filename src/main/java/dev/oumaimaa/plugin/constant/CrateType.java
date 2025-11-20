package dev.oumaimaa.plugin.constant;

/**
 * Crate types
 */
public enum CrateType {
    COMMON("Common", "§7"),
    RARE("Rare", "§9"),
    EPIC("Epic", "§5"),
    LEGENDARY("Legendary", "§6");

    private final String displayName;
    private final String color;

    CrateType(String displayName, String color) {
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