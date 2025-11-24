package dev.oumaimaa.plugin.listener.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.oumaimaa.Main;

/**
 * Manages PacketEvents listeners for optimized packet handling
 */
public class PacketListenerManager {

    private final Main plugin;
    private final ScoreboardPacketListener scoreboardListener;
    private final BossBarPacketListener bossBarListener;
    private final EntityPacketListener entityListener;
    private final InventoryPacketListener inventoryListener;

    public PacketListenerManager(Main plugin) {
        this.plugin = plugin;
        this.scoreboardListener = new ScoreboardPacketListener(plugin);
        this.bossBarListener = new BossBarPacketListener(plugin);
        this.entityListener = new EntityPacketListener(plugin);
        this.inventoryListener = new InventoryPacketListener(plugin);
    }

    /**
     * Register all packet listeners
     */
    public void registerListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(
                scoreboardListener,
                PacketListenerPriority.NORMAL
        );

        PacketEvents.getAPI().getEventManager().registerListener(
                bossBarListener,
                PacketListenerPriority.NORMAL
        );

        PacketEvents.getAPI().getEventManager().registerListener(
                entityListener,
                PacketListenerPriority.HIGH
        );

        PacketEvents.getAPI().getEventManager().registerListener(
                inventoryListener,
                PacketListenerPriority.NORMAL
        );

        plugin.logInfo("Registered " + 4 + " packet listeners");
    }

    /**
     * Unregister all packet listeners
     */
    public void shutdown() {
        PacketEvents.getAPI().getEventManager().unregisterListener(scoreboardListener);
        PacketEvents.getAPI().getEventManager().unregisterListener(bossBarListener);
        PacketEvents.getAPI().getEventManager().unregisterListener(entityListener);
        PacketEvents.getAPI().getEventManager().unregisterListener(inventoryListener);
    }
}