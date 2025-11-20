package dev.oumaimaa.plugin.constant;

/**
 * Cosmetic rarity levels
 */
public enum CosmeticRarity {
    COMMON("Common", "§7", 1.0),
    RARE("Rare", "§9", 1.5),
    EPIC("Epic", "§5", 2.0),
    LEGENDARY("Legendary", "§6", 3.0);

    private final String displayName;
    private final String color;
    private final double valueMultiplier;

    CosmeticRarity(String displayName, String color, double valueMultiplier) {
        this.displayName = displayName;
        this.color = color;
        this.valueMultiplier = valueMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public double getValueMultiplier() {
        return valueMultiplier;
    }
}