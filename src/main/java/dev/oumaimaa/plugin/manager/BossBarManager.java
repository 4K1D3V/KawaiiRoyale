package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.Game;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages boss bars for players
 */
public class BossBarManager {

    private final Main plugin;
    private final Map<UUID, Map<String, BossBar>> playerBossBars;

    public BossBarManager(Main plugin) {
        this.plugin = plugin;
        this.playerBossBars = new HashMap<>();

        startUpdateTask();
    }

    /**
     * Show zone countdown boss bar
     */
    public void showZoneCountdown(@NotNull Player player, int seconds) {
        BossBar bossBar = BossBar.bossBar(
                Component.text("Zone shrinking in " + seconds + " seconds")
                        .color(NamedTextColor.RED),
                1.0f,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(bossBar);

        playerBossBars.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put("zone", bossBar);
    }

    /**
     * Show game progress boss bar
     */
    public void showGameProgress(Player player, @NotNull Game game) {
        String text = switch (game.getPhase()) {
            case GRACE_PERIOD -> "Grace Period - PvP Disabled";
            case ACTIVE_COMBAT -> game.getAlivePlayers().size() + " players remaining";
            case FINAL_ZONE -> "FINAL ZONE!";
            default -> "Game in progress";
        };

        BossBar.Color color = switch (game.getPhase()) {
            case GRACE_PERIOD -> BossBar.Color.GREEN;
            case FINAL_ZONE -> BossBar.Color.RED;
            default -> BossBar.Color.YELLOW;
        };

        float progress = (float) game.getAlivePlayers().size() /
                plugin.getConfigManager().getBattleRoyaleMaxPlayers();

        BossBar bossBar = BossBar.bossBar(
                Component.text(text).color(NamedTextColor.YELLOW),
                Math.max(progress, 0.01f),
                color,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(bossBar);

        playerBossBars.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put("game", bossBar);
    }

    /**
     * Show zone damage boss bar
     */
    public void showZoneDamage(@NotNull Player player) {
        BossBar bossBar = BossBar.bossBar(
                Component.text("⚠ YOU ARE OUTSIDE THE SAFE ZONE! ⚠")
                        .color(NamedTextColor.RED),
                1.0f,
                BossBar.Color.RED,
                BossBar.Overlay.NOTCHED_10
        );

        player.showBossBar(bossBar);

        playerBossBars.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put("damage", bossBar);
    }

    /**
     * Update all boss bars for player
     */
    public void updateBossBars(Player player) {
        Game game = plugin.getGameManager().getPlayerGame(player);

        if (game != null) {
            // Update game progress bar
            Map<String, BossBar> bars = playerBossBars.get(player.getUniqueId());
            if (bars != null && bars.containsKey("game")) {
                BossBar bar = bars.get("game");

                String text = switch (game.getPhase()) {
                    case GRACE_PERIOD -> "Grace Period - PvP Disabled";
                    case ACTIVE_COMBAT -> game.getAlivePlayers().size() + " players remaining";
                    case FINAL_ZONE -> "FINAL ZONE!";
                    default -> "Game in progress";
                };

                bar.name(Component.text(text).color(NamedTextColor.YELLOW));

                float progress = (float) game.getAlivePlayers().size() /
                        plugin.getConfigManager().getBattleRoyaleMaxPlayers();
                bar.progress(Math.max(progress, 0.01f));
            } else {
                showGameProgress(player, game);
            }

            // Check zone damage
            if (game.getZone() != null && game.getZone().isOutsideZone(player.getLocation())) {
                Map<String, BossBar> bars2 = playerBossBars.get(player.getUniqueId());
                if (bars2 == null || !bars2.containsKey("damage")) {
                    showZoneDamage(player);
                }
            } else {
                removeBossBar(player, "damage");
            }
        } else {
            // Remove all bars if not in game
            removeAllBossBars(player);
        }
    }

    /**
     * Remove specific boss bar
     */
    public void removeBossBar(@NotNull Player player, String id) {
        Map<String, BossBar> bars = playerBossBars.get(player.getUniqueId());
        if (bars != null) {
            BossBar bar = bars.remove(id);
            if (bar != null) {
                player.hideBossBar(bar);
            }
        }
    }

    /**
     * Remove all boss bars from player
     */
    public void removeAllBossBars(@NotNull Player player) {
        Map<String, BossBar> bars = playerBossBars.remove(player.getUniqueId());
        if (bars != null) {
            bars.values().forEach(player::hideBossBar);
        }
    }

    /**
     * Start update task
     */
    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateBossBars(player);
            }
        }, 0L, 20L); // Update every second
    }

    /**
     * Shutdown
     */
    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeAllBossBars(player);
        }
        playerBossBars.clear();
    }
}