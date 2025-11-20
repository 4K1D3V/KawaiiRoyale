package dev.oumaimaa.plugin.constant;

/**
 * Current state of a game
 */
public enum GameState {
    WAITING("Waiting for players"),
    STARTING("Starting"),
    ACTIVE("In progress"),
    ENDING("Ending"),
    ENDED("Ended");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}