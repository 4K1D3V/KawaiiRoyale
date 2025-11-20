package dev.oumaimaa.plugin.constant;

/**
 * Game mode types
 */
public enum GameMode {
    BATTLE_ROYALE("Battle Royale", "Last player standing wins"),
    RESURGENCE("Resurgence", "Top players by kills win with respawning");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}