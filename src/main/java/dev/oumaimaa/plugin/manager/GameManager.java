package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.GameMode;
import dev.oumaimaa.plugin.constant.GameState;
import dev.oumaimaa.plugin.skeleton.Arena;
import dev.oumaimaa.plugin.skeleton.GamePlayer;
import dev.oumaimaa.plugin.skeleton.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all active games and player game instances
 */
public class GameManager {

    private final Main plugin;
    private final Map<UUID, Game> activeGames;
    private final Map<UUID, GamePlayer> gamePlayers;
    private final Map<UUID, Game> playerGameMap;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.activeGames = new ConcurrentHashMap<>();
        this.gamePlayers = new ConcurrentHashMap<>();
        this.playerGameMap = new ConcurrentHashMap<>();
    }

    /**
     * Create a new game instance
     */
    public Game createGame(Arena arena, GameMode mode) {
        Game game = new Game(plugin, arena, mode);
        activeGames.put(game.getId(), game);
        plugin.logInfo("Created new " + mode.name() + " game in arena: " + arena.getName());
        return game;
    }

    /**
     * Start a game
     */
    public void startGame(@NotNull Game game) {
        if (game.getState() != GameState.WAITING) {
            return;
        }
        game.start();
    }

    /**
     * End a game and cleanup
     */
    public void endGame(@NotNull Game game) {
        game.end();

        // Remove player mappings
        for (GamePlayer gp : game.getAllPlayers()) {
            playerGameMap.remove(gp.getUuid());
        }

        // Remove from active games after a delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            activeGames.remove(game.getId());
        }, 200L); // 10 seconds delay
    }

    /**
     * Get or create a GamePlayer instance
     */
    public GamePlayer getGamePlayer(@NotNull Player player) {
        return gamePlayers.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new GamePlayer(player)
        );
    }

    /**
     * Get the game a player is currently in
     */
    public Game getPlayerGame(@NotNull Player player) {
        return playerGameMap.get(player.getUniqueId());
    }

    /**
     * Check if player is in a game
     */
    public boolean isInGame(@NotNull Player player) {
        return playerGameMap.containsKey(player.getUniqueId());
    }

    /**
     * Add player to game mapping
     */
    public void addPlayerToGame(@NotNull Player player, Game game) {
        playerGameMap.put(player.getUniqueId(), game);
    }

    /**
     * Remove player from game mapping
     */
    public void removePlayerFromGame(@NotNull Player player) {
        playerGameMap.remove(player.getUniqueId());
    }

    /**
     * Get all active games
     */
    public Collection<Game> getActiveGames() {
        return Collections.unmodifiableCollection(activeGames.values());
    }

    /**
     * Get game by ID
     */
    public Game getGame(UUID gameId) {
        return activeGames.get(gameId);
    }

    /**
     * Get total active games count
     */
    public int getActiveGameCount() {
        return activeGames.size();
    }

    /**
     * Get total players in games
     */
    public int getTotalPlayersInGames() {
        return activeGames.values().stream()
                .mapToInt(game -> game.getAllPlayers().size())
                .sum();
    }

    /**
     * Shutdown all games
     */
    public void shutdown() {
        plugin.logInfo("Ending all active games...");

        new ArrayList<>(activeGames.values()).forEach(Game::forceEnd);

        // Clear all mappings
        activeGames.clear();
        gamePlayers.clear();
        playerGameMap.clear();

        plugin.logInfo("All games have been ended.");
    }

    /**
     * Remove a GamePlayer instance (when player quits server)
     */
    public void removeGamePlayer(UUID uuid) {
        gamePlayers.remove(uuid);
        playerGameMap.remove(uuid);
    }
}