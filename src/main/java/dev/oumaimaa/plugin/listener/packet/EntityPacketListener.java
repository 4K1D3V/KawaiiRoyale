package dev.oumaimaa.plugin.listener.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import dev.oumaimaa.Main;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Optimizes entity packet handling for spectators and players
 */
public class EntityPacketListener extends PacketListenerCommon implements PacketListener {

    private final Main plugin;

    public EntityPacketListener(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            handleEntitySpawn(event, player);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            handleEntityMetadata(event, player);
        }
    }

    private void handleEntitySpawn(PacketSendEvent event, Player player) {
        if (!plugin.getGameManager().isInGame(player)) {
            return;
        }

        if (plugin.getConfigManager().getMainConfig()
                .getBoolean("performance.optimize-entities", true)) {
        }
    }

    private void handleEntityMetadata(PacketSendEvent event, Player player) {
        if (!plugin.getGameManager().isInGame(player)) {
        }
    }
}