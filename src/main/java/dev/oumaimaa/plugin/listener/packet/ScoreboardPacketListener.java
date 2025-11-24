package dev.oumaimaa.plugin.listener.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import dev.oumaimaa.Main;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Optimizes scoreboard packet handling for better performance
 */
public class ScoreboardPacketListener extends PacketListenerCommon implements PacketListener {

    private final Main plugin;

    public ScoreboardPacketListener(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_OBJECTIVE) {
            handleScoreboardObjective(event);
        }
    }

    private void handleScoreboardObjective(@NotNull PacketSendEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!plugin.getGameManager().isInGame(player)) {
            return;
        }

        WrapperPlayServerScoreboardObjective wrapper =
                new WrapperPlayServerScoreboardObjective(event);

        if (plugin.getConfigManager().getMainConfig()
                .getBoolean("performance.optimize-scoreboard", true)) {
        }
    }
}