package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.GameMode;
import dev.oumaimaa.plugin.skeleton.Arena;
import dev.oumaimaa.plugin.skeleton.Game;
import dev.oumaimaa.plugin.skeleton.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

    private final Main plugin;
    private final Map<GameMode, Queue<GamePlayer>> queues;

    public QueueManager(Main plugin) {
        this.plugin = plugin;
        this.queues = new ConcurrentHashMap<>();

        // Initialize queues for each game mode
        for (GameMode mode : GameMode.values()) {
            queues.put(mode, new LinkedList<>());
        }
    }

    public void joinQueue(Player player, GameMode mode) {
        if (plugin.getGameManager().isInGame(player)) {
            player.sendMessage(Component.text("You are already in a game!")
                    .color(NamedTextColor.RED));
            return;
        }

        GamePlayer gamePlayer = plugin.getGameManager().getGamePlayer(player);
        Queue<GamePlayer> queue = queues.get(mode);

        if (queue.contains(gamePlayer)) {
            player.sendMessage(Component.text("You are already in the queue!")
                    .color(NamedTextColor.RED));
            return;
        }

        queue.add(gamePlayer);
        player.sendMessage(Component.text("You joined the " + mode.getDisplayName() + " queue!")
                .color(NamedTextColor.GREEN)
                .append(Component.text(" Position: " + queue.size())
                        .color(NamedTextColor.GRAY)));

        tryStartGame(mode);
    }

    public void leaveQueue(Player player, GameMode mode) {
        GamePlayer gamePlayer = plugin.getGameManager().getGamePlayer(player);
        Queue<GamePlayer> queue = queues.get(mode);

        if (queue.remove(gamePlayer)) {
            player.sendMessage(Component.text("You left the queue.")
                    .color(NamedTextColor.YELLOW));
        }
    }

    private void tryStartGame(GameMode mode) {
        Queue<GamePlayer> queue = queues.get(mode);

        int minPlayers = mode == GameMode.BATTLE_ROYALE
                ? plugin.getConfigManager().getBattleRoyaleMinPlayers()
                : plugin.getConfigManager().getResurgenceMinPlayers();

        if (queue.size() >= minPlayers) {
            Arena arena = plugin.getArenaManager().getRandomArena();
            if (arena == null || !arena.isValid()) {
                plugin.logWarning("No valid arenas available for " + mode.name());
                return;
            }

            Game game = plugin.getGameManager().createGame(arena, mode);

            int maxPlayers = mode == GameMode.BATTLE_ROYALE
                    ? plugin.getConfigManager().getBattleRoyaleMaxPlayers()
                    : plugin.getConfigManager().getResurgenceMaxPlayers();

            List<GamePlayer> players = new ArrayList<>();
            for (int i = 0; i < maxPlayers && !queue.isEmpty(); i++) {
                players.add(queue.poll());
            }

            for (GamePlayer gp : players) {
                game.addPlayer(gp);
            }

            plugin.getGameManager().startGame(game);
        }
    }

    public int getQueueSize(GameMode mode) {
        return queues.get(mode).size();
    }

    public void shutdown() {
        queues.values().forEach(Queue::clear);
    }
}