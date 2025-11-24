package dev.oumaimaa.plugin.listener.packet;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import dev.oumaimaa.Main;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Optimizes inventory packet handling for GUIs
 */
public class InventoryPacketListener extends PacketListenerCommon implements PacketListener {

    private final Main plugin;

    public InventoryPacketListener(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            handleInventoryClick(event, player);
        }
    }

    private void handleInventoryClick(PacketReceiveEvent event, Player player) {
        if (plugin.getConfigManager().getMainConfig()
                .getBoolean("performance.optimize-gui", true)) {
        }
    }
}