package dev.oumaimaa.plugin.constant;

/**
 * Game phase within an active game
 */
public enum GamePhase {
    PRE_GAME("Pre-game"),
    GRACE_PERIOD("Grace Period"),
    ACTIVE_COMBAT("Active Combat"),
    FINAL_ZONE("Final Zone"),
    POST_GAME("Post-game");

    private final String displayName;

    GamePhase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}