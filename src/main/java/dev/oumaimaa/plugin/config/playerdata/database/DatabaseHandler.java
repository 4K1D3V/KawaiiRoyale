package dev.oumaimaa.plugin.config.playerdata.database;

import dev.oumaimaa.plugin.config.playerdata.PlayerData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Database handler interface
 */
public interface DatabaseHandler {

    /**
     * Initialize database connection
     */
    CompletableFuture<Boolean> initialize();

    /**
     * Load player data from database
     */
    CompletableFuture<PlayerData> loadPlayerData(UUID uuid);

    /**
     * Save player data to database
     */
    void savePlayerData(PlayerData data);

    /**
     * Get top players by stat
     */
    CompletableFuture<List<LeaderboardEntry>> getTopPlayers(String stat, int limit);

    /**
     * Close database connection
     */
    void close();

    /**
     * Leaderboard entry record
     */
    record LeaderboardEntry(UUID uuid, String name, int value) {
    }
}
