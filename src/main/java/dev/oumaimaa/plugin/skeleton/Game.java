package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.GameMode;
import dev.oumaimaa.plugin.constant.GamePhase;
import dev.oumaimaa.plugin.constant.GameState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents a single game instance
 */
public class Game {

    private final UUID id;
    private final Main plugin;
    private final Arena arena;
    private final GameMode mode;
    private final Set<GamePlayer> players;
    private final Set<GamePlayer> alivePlayers;
    private final Set<GamePlayer> spectators;
    private final Map<UUID, Integer> killCounts;
    private GameState state;
    private GamePhase phase;
    private Zone zone;
    private int countdown;
    private long startTime;
    private long endTime;

    private BukkitTask countdownTask;
    private BukkitTask gracePeriodTask;

    public Game(Main plugin, Arena arena, GameMode mode) {
        this.id = UUID.randomUUID();
        this.plugin = plugin;
        this.arena = arena;
        this.mode = mode;
        this.state = GameState.WAITING;
        this.phase = GamePhase.PRE_GAME;

        this.players = ConcurrentHashMap.newKeySet();
        this.alivePlayers = ConcurrentHashMap.newKeySet();
        this.spectators = ConcurrentHashMap.newKeySet();
        this.killCounts = new ConcurrentHashMap<>();
    }

    /**
     * Add a player to the game
     */
    public void addPlayer(GamePlayer gamePlayer) {
        if (state != GameState.WAITING) {
            return;
        }

        int maxPlayers = mode == GameMode.BATTLE_ROYALE
                ? plugin.getConfigManager().getBattleRoyaleMaxPlayers()
                : plugin.getConfigManager().getResurgenceMaxPlayers();

        if (players.size() >= maxPlayers) {
            return;
        }

        players.add(gamePlayer);
        killCounts.put(gamePlayer.getUuid(), 0);
        plugin.getGameManager().addPlayerToGame(gamePlayer.getPlayer(), this);

        broadcastMessage(Component.text(gamePlayer.getPlayer().getName() + " joined the game! ")
                .color(NamedTextColor.GREEN)
                .append(Component.text("(" + players.size() + "/" + maxPlayers + ")")
                        .color(NamedTextColor.GRAY)));

        checkStart();

    }

    /**
     * Remove a player from the game
     */
    public void removePlayer(GamePlayer gamePlayer) {
        players.remove(gamePlayer);
        alivePlayers.remove(gamePlayer);
        spectators.remove(gamePlayer);
        plugin.getGameManager().removePlayerFromGame(gamePlayer.getPlayer());

        if (state == GameState.WAITING) {
            broadcastMessage(Component.text(gamePlayer.getPlayer().getName() + " left the game!")
                    .color(NamedTextColor.RED));
        }
    }

    /**
     * Check if game can start
     */
    private void checkStart() {
        int minPlayers = mode == GameMode.BATTLE_ROYALE
                ? plugin.getConfigManager().getBattleRoyaleMinPlayers()
                : plugin.getConfigManager().getResurgenceMinPlayers();

        if (players.size() >= minPlayers && state == GameState.WAITING) {
            startCountdown();
        }
    }

    /**
     * Start countdown
     */
    private void startCountdown() {
        state = GameState.STARTING;
        countdown = plugin.getConfigManager().getCountdownTime();

        countdownTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (countdown <= 0) {
                countdownTask.cancel();
                begin();
                return;
            }

            if (countdown <= 5 || countdown % 10 == 0) {
                broadcastMessage(Component.text("Game starting in " + countdown + " seconds...")
                        .color(NamedTextColor.YELLOW));

                for (GamePlayer gp : players) {
                    gp.getPlayer().playSound(gp.getPlayer().getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }
            }

            countdown--;
        }, 0L, 20L);
    }

    /**
     * Start the game
     */
    public void start() {
        // This method is called externally, but actual start logic is in begin()
        if (state == GameState.WAITING) {
            checkStart();
        }
    }

    /**
     * Begin the actual game
     */
    private void begin() {
        this.state = GameState.ACTIVE;
        this.phase = GamePhase.GRACE_PERIOD;
        this.startTime = System.currentTimeMillis();
        this.zone = new Zone(plugin, this);

        // Teleport players and setup
        teleportPlayersToArena();
        giveStartingItems();

        // Announce start
        for (GamePlayer gp : players) {
            Player p = gp.getPlayer();
            p.showTitle(Title.title(
                    Component.text("GAME START!").color(NamedTextColor.GREEN),
                    Component.text("Good luck!").color(NamedTextColor.GRAY),
                    Title.Times.times(
                            Duration.ofMillis(500),
                            Duration.ofMillis(2000),
                            Duration.ofMillis(500)
                    )
            ));
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        }

        broadcastMessage(Component.text("Grace period active! PvP disabled for " +
                        plugin.getConfigManager().getGracePeriod() + " seconds")
                .color(NamedTextColor.YELLOW));

        startGracePeriod();
    }

    /**
     * Teleport all players to arena spawns
     */
    private void teleportPlayersToArena() {
        List<Location> spawns = arena.getSpawnLocations();
        if (spawns.isEmpty()) {
            plugin.logWarning("No spawn locations for arena: " + arena.getName());
            return;
        }

        List<GamePlayer> playerList = new ArrayList<>(players);
        Collections.shuffle(playerList);

        for (int i = 0; i < playerList.size(); i++) {
            GamePlayer gp = playerList.get(i);
            Location spawn = spawns.get(i % spawns.size());

            Player p = gp.getPlayer();
            p.teleport(spawn);
            p.setGameMode(org.bukkit.GameMode.SURVIVAL);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.getInventory().clear();

            alivePlayers.add(gp);
        }
    }

    /**
     * Give starting items to players
     */
    private void giveStartingItems() {
        // Players start with nothing - they must find loot
        // Optionally give basic items here
    }

    /**
     * Start grace period timer
     */
    private void startGracePeriod() {
        int gracePeriod = plugin.getConfigManager().getGracePeriod();

        gracePeriodTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            phase = GamePhase.ACTIVE_COMBAT;
            broadcastMessage(Component.text("Grace period ended! PvP is now enabled!")
                    .color(NamedTextColor.RED));

            zone.startShrinking();
        }, gracePeriod * 20L);
    }

    /**
     * Handle player elimination
     */
    public void eliminatePlayer(GamePlayer gamePlayer, GamePlayer killer) {
        alivePlayers.remove(gamePlayer);
        spectators.add(gamePlayer);

        Player p = gamePlayer.getPlayer();
        p.setGameMode(org.bukkit.GameMode.SPECTATOR);

        // Update kill count
        if (killer != null) {
            killCounts.merge(killer.getUuid(), 1, Integer::sum);

            broadcastMessage(Component.text(p.getName())
                    .color(NamedTextColor.RED)
                    .append(Component.text(" was eliminated by "))
                    .append(Component.text(killer.getPlayer().getName())
                            .color(NamedTextColor.GOLD))
                    .append(Component.text("! "))
                    .append(Component.text("(" + alivePlayers.size() + " remaining)")
                            .color(NamedTextColor.GRAY)));
        } else {
            broadcastMessage(Component.text(p.getName() + " was eliminated! " +
                            "(" + alivePlayers.size() + " remaining)")
                    .color(NamedTextColor.RED));
        }

        checkWinCondition();
    }

    /**
     * Check if game should end
     */
    private void checkWinCondition() {
        if (mode == dev.oumaimaa.plugin.constant.GameMode.BATTLE_ROYALE) {
            if (alivePlayers.size() <= 1) {
                end();
            }
        } else {
            // RESURGENCE ends based on time or score
            // For now, we'll end when only one player remains
            if (alivePlayers.size() <= 1) {
                end();
            }
        }
    }

    /**
     * End the game
     */
    public void end() {
        if (state == GameState.ENDED || state == GameState.ENDING) {
            return;
        }

        this.state = GameState.ENDING;
        this.endTime = System.currentTimeMillis();

        if (countdownTask != null) countdownTask.cancel();
        if (gracePeriodTask != null) gracePeriodTask.cancel();
        if (zone != null) zone.stopShrinking();

        // Determine winners
        List<GamePlayer> winners = determineWinners();

        // Announce results
        announceWinners(winners);

        // Give rewards
        distributeRewards(winners);

        // Cleanup after delay
        plugin.getServer().getScheduler().runTaskLater(plugin, this::cleanup, 100L);
    }

    /**
     * Force end the game (emergency shutdown)
     */
    public void forceEnd() {
        this.state = GameState.ENDED;

        if (countdownTask != null) countdownTask.cancel();
        if (gracePeriodTask != null) gracePeriodTask.cancel();
        if (zone != null) zone.stopShrinking();

        cleanup();
    }

    /**
     * Determine game winners
     */
    private List<GamePlayer> determineWinners() {
        if (mode == dev.oumaimaa.plugin.constant.GameMode.BATTLE_ROYALE) {
            // Last player alive wins
            return new ArrayList<>(alivePlayers);
        } else {
            // Top 3 by kills
            return killCounts.entrySet().stream()
                    .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                    .limit(3)
                    .map(entry -> players.stream()
                            .filter(gp -> gp.getUuid().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Announce winners to all players
     */
    private void announceWinners(@NotNull List<GamePlayer> winners) {
        if (winners.isEmpty()) {
            broadcastMessage(Component.text("No winners this round!")
                    .color(NamedTextColor.YELLOW));
            return;
        }

        Component winnerMsg = plugin.getMiniMessage().deserialize(
                "<gradient:#ffd700:#ffed4e>🏆 VICTORY ROYALE! 🏆</gradient>"
        );

        for (GamePlayer winner : winners) {
            Player p = winner.getPlayer();
            p.showTitle(Title.title(
                    winnerMsg,
                    Component.text("You won!").color(NamedTextColor.GOLD),
                    Title.Times.times(
                            Duration.ofMillis(500),
                            Duration.ofMillis(3000),
                            Duration.ofMillis(500)
                    )
            ));
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        // Broadcast to all
        if (mode == dev.oumaimaa.plugin.constant.GameMode.BATTLE_ROYALE) {
            GamePlayer winner = winners.getFirst();
            int kills = killCounts.getOrDefault(winner.getUuid(), 0);

            broadcastMessage(Component.text(winner.getPlayer().getName())
                    .color(NamedTextColor.GOLD)
                    .append(Component.text(" won the Battle Royale with " + kills + " kills!"))
                    .color(NamedTextColor.YELLOW));
        } else {
            broadcastMessage(Component.text("Top 3 Players:").color(NamedTextColor.GOLD));
            for (int i = 0; i < winners.size(); i++) {
                GamePlayer gp = winners.get(i);
                int kills = killCounts.getOrDefault(gp.getUuid(), 0);
                broadcastMessage(Component.text((i + 1) + ". " + gp.getPlayer().getName() +
                        " - " + kills + " kills").color(NamedTextColor.YELLOW));
            }
        }
    }

    /**
     * Distribute rewards to winners
     */
    private void distributeRewards(List<GamePlayer> winners) {
        if (!plugin.getConfigManager().isRewardsEnabled()) {
            return;
        }

        // TODO: Implement reward distribution via Vault/Economy
        // For now, just send a message
        for (GamePlayer winner : winners) {
            winner.getPlayer().sendMessage(Component.text("You earned rewards!")
                    .color(NamedTextColor.GREEN));
        }
    }

    /**
     * Cleanup game resources
     */
    private void cleanup() {
        this.state = GameState.ENDED;

        // Teleport players back to lobby
        Location lobby = arena.getLobbyLocation();
        if (lobby != null) {
            for (GamePlayer gp : players) {
                Player p = gp.getPlayer();
                p.teleport(lobby);
                p.setGameMode(org.bukkit.GameMode.ADVENTURE);
                p.getInventory().clear();
                p.setHealth(20.0);
                p.setFoodLevel(20);
            }
        }

        // Clear references
        players.clear();
        alivePlayers.clear();
        spectators.clear();
        killCounts.clear();

        // Notify game manager
        plugin.getGameManager().endGame(this);
    }

    /**
     * Broadcast message to all players in game
     */
    public void broadcastMessage(Component message) {
        for (GamePlayer gp : players) {
            gp.getPlayer().sendMessage(message);
        }
    }

    public UUID getId() {
        return id;
    }

    public Arena getArena() {
        return arena;
    }

    public dev.oumaimaa.plugin.constant.GameMode getMode() {
        return mode;
    }

    public GameState getState() {
        return state;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public Set<GamePlayer> getAllPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public Set<GamePlayer> getAlivePlayers() {
        return Collections.unmodifiableSet(alivePlayers);
    }

    public Set<GamePlayer> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }

    public Zone getZone() {
        return zone;
    }

    public int getKills(UUID playerId) {
        return killCounts.getOrDefault(playerId, 0);
    }

    public long getStartTime() {
        return startTime;
    }
}