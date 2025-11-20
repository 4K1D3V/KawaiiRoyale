package dev.oumaimaa.plugin.constant;

/**
 * Types of cosmetics
 */
public enum CosmeticType {
    KILL_MESSAGE("Kill Messages"),
    VICTORY_DANCE("Victory Celebrations"),
    PARACHUTE("Parachutes"),
    DEATH_EFFECT("Death Effects"),
    TITLE("Player Titles");

    private final String displayName;

    CosmeticType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}