package dev.oumaimaa.plugin.config.playerdata.database;

import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Database handler interface for abstraction
 */
public interface DatabaseHandler {

    /**
     * Initialize database connection and create tables
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
     * Get top players by statistic
     */
    CompletableFuture<List<LeaderboardEntry>> getTopPlayers(String stat, int limit);

    /**
     * Check if player exists in database
     */
    CompletableFuture<Boolean> playerExists(UUID uuid);

    /**
     * Delete player data
     */
    CompletableFuture<Boolean> deletePlayerData(UUID uuid);

    /**
     * Get total player count
     */
    CompletableFuture<Integer> getTotalPlayers();

    /**
     * Close database connection
     */
    void close();

    /**
     * Leaderboard entry record
     */
    record LeaderboardEntry(UUID uuid, String name, int value) {

        @Contract(pure = true)
        public @NotNull String getFormattedValue() {
            return String.format("%,d", value);
        }
    }
}