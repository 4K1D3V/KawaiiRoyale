package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ZoneListener implements Listener {
    private final Main plugin;

    public ZoneListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getPlayerGame(player);

        // Zone damage is handled by Zone class's damage task
    }
}