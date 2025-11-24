package dev.oumaimaa.api;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.GameMode;
import dev.oumaimaa.plugin.skeleton.Arena;
import dev.oumaimaa.plugin.skeleton.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * KawaiiRoyale Public API for external plugin integration
 *
 * @author oumaimaa
 * @version 2.0.0
 */
public final class API {

    private static Main plugin;

    private API() {
        throw new UnsupportedOperationException("API class cannot be instantiated");
    }

    /**
     * Initialize the API with plugin instance
     *
     * @param pluginInstance Main plugin instance
     */
    public static void initialize(@NotNull Main pluginInstance) {
        if (plugin != null) {
            throw new IllegalStateException("API already initialized");
        }
        plugin = pluginInstance;
    }

    /**
     * Check if player is currently in a game
     *
     * @param player Player to check
     * @return true if player is in game
     */
    public static boolean isInGame(@NotNull Player player) {
        ensureInitialized();
        return plugin.getGameManager().isInGame(player);
    }

    /**
     * Get the game a player is currently in
     *
     * @param player Player to check
     * @return Game instance or null if not in game
     */
    @Nullable
    public static Game getPlayerGame(@NotNull Player player) {
        ensureInitialized();
        return plugin.getGameManager().getPlayerGame(player);
    }

    /**
     * Get player's statistics data
     *
     * @param player Player to get data for
     * @return PlayerData instance
     */
    @NotNull
    public static PlayerData getPlayerData(@NotNull Player player) {
        ensureInitialized();
        return plugin.getPlayerDataManager().getPlayerData(player);
    }

    /**
     * Add player to game queue
     *
     * @param player Player to add
     * @param mode   Game mode to queue for
     */
    public static void joinQueue(@NotNull Player player, @NotNull GameMode mode) {
        ensureInitialized();
        plugin.getQueueManager().joinQueue(player, mode);
    }

    /**
     * Remove player from queue
     *
     * @param player Player to remove
     * @param mode   Game mode queue
     */
    public static void leaveQueue(@NotNull Player player, @NotNull GameMode mode) {
        ensureInitialized();
        plugin.getQueueManager().leaveQueue(player, mode);
    }

    /**
     * Get all active games
     *
     * @return Collection of active games
     */
    @NotNull
    public static Collection<Game> getActiveGames() {
        ensureInitialized();
        return plugin.getGameManager().getActiveGames();
    }

    /**
     * Get all registered arenas
     *
     * @return Collection of arenas
     */
    @NotNull
    public static Collection<Arena> getArenas() {
        ensureInitialized();
        return plugin.getArenaManager().getAllArenas();
    }

    /**
     * Get arena by name
     *
     * @param name Arena name
     * @return Arena instance or null
     */
    @Nullable
    public static Arena getArena(@NotNull String name) {
        ensureInitialized();
        return plugin.getArenaManager().getArena(name);
    }

    /**
     * Give coins to player
     *
     * @param player Player to give coins to
     * @param amount Amount of coins
     */
    public static void giveCoins(@NotNull Player player, int amount) {
        ensureInitialized();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.addCoins(amount);
    }

    /**
     * Check if player has enough coins
     *
     * @param player Player to check
     * @param amount Amount to check
     * @return true if player has enough coins
     */
    public static boolean hasCoins(@NotNull Player player, int amount) {
        ensureInitialized();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        return data.getCoins() >= amount;
    }

    /**
     * Award achievement to player
     *
     * @param player        Player to award
     * @param achievementId Achievement ID
     */
    public static void giveAchievement(@NotNull Player player, @NotNull String achievementId) {
        ensureInitialized();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.addAchievement(achievementId);
    }

    /**
     * Check if economy is enabled
     *
     * @return true if Vault economy is available
     */
    public static boolean isEconomyEnabled() {
        ensureInitialized();
        return plugin.getVaultManager().isEnabled();
    }

    /**
     * Deposit money to player's economy account
     *
     * @param player Player to deposit to
     * @param amount Amount to deposit
     * @return true if successful
     */
    public static boolean depositMoney(@NotNull Player player, double amount) {
        ensureInitialized();
        return plugin.getVaultManager().deposit(player, amount);
    }

    /**
     * Withdraw money from player's economy account
     *
     * @param player Player to withdraw from
     * @param amount Amount to withdraw
     * @return true if successful
     */
    public static boolean withdrawMoney(@NotNull Player player, double amount) {
        ensureInitialized();
        return plugin.getVaultManager().withdraw(player, amount);
    }

    /**
     * Get game by ID
     *
     * @param gameId Game UUID
     * @return Game instance or null
     */
    @Nullable
    public static Game getGame(@NotNull UUID gameId) {
        ensureInitialized();
        return plugin.getGameManager().getGame(gameId);
    }

    /**
     * Get total active games count
     *
     * @return Number of active games
     */
    public static int getActiveGameCount() {
        ensureInitialized();
        return plugin.getGameManager().getActiveGameCount();
    }

    /**
     * Get queue size for game mode
     *
     * @param mode Game mode
     * @return Number of players in queue
     */
    public static int getQueueSize(@NotNull GameMode mode) {
        ensureInitialized();
        return plugin.getQueueManager().getQueueSize(mode);
    }

    private static void ensureInitialized() {
        if (plugin == null) {
            throw new IllegalStateException("API not initialized. Please wait for KawaiiRoyale to enable.");
        }
    }
}