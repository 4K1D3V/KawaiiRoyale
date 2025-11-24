package dev.oumaimaa.plugin.listener.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import dev.oumaimaa.Main;
import org.jetbrains.annotations.NotNull;

/**
 * Optimizes boss bar packet handling
 */
public class BossBarPacketListener extends PacketListenerCommon implements PacketListener {

    private final Main plugin;

    public BossBarPacketListener(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.BOSS_BAR) {
            if (plugin.getConfigManager().getMainConfig()
                    .getBoolean("performance.optimize-bossbar", true)) {
            }
        }
    }
}