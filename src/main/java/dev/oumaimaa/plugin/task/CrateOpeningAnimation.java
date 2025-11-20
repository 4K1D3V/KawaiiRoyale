package dev.oumaimaa.plugin.task;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.CrateReward;
import dev.oumaimaa.plugin.skeleton.CrateDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

/**
 * Animated crate opening
 */
public class CrateOpeningAnimation {

    private final Main plugin;
    private final Player player;
    private final CrateDefinition crate;
    private final CrateReward reward;
    private BukkitTask task;
    private int ticks;

    public CrateOpeningAnimation(Main plugin, Player player,
                                 CrateDefinition crate, CrateReward reward) {
        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
        this.reward = reward;
        this.ticks = 0;
    }

    /**
     * Start the animation
     */
    public void start() {
        player.showTitle(Title.title(
                Component.text("Opening...").color(NamedTextColor.YELLOW),
                Component.text(crate.getName()).color(NamedTextColor.GOLD),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(500)
                )
        ));

        // Start animation task
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            ticks++;

            // Particle effects
            if (ticks % 5 == 0) {
                spawnParticles();
            }

            // Sounds
            if (ticks % 10 == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,
                        1.0f, 1.0f + (ticks * 0.05f));
            }

            // End after 3 seconds (60 ticks)
            if (ticks >= 60) {
                finish();
            }
        }, 0L, 1L);
    }

    /**
     * Spawn particles around player
     */
    private void spawnParticles() {
        Location loc = player.getLocation().add(0, 1.5, 0);

        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * 2 * Math.PI;
            double x = Math.cos(angle) * 1.5;
            double z = Math.sin(angle) * 1.5;

            Location particleLoc = loc.clone().add(x, 0, z);

            player.getWorld().spawnParticle(
                    Particle.END_ROD,
                    particleLoc,
                    1,
                    0, 0, 0,
                    0.02
            );
        }
    }

    /**
     * Finish animation and give reward
     */
    private void finish() {
        task.cancel();

        // Show reward
        String rewardText = switch (reward.getType()) {
            case COINS -> "§6" + reward.getAmount() + " Coins";
            case BP_XP -> "§b" + reward.getAmount() + " XP";
            case COSMETIC -> {
                assert plugin.getCosmeticManager() != null;
                var cosmetic = plugin.getCosmeticManager().getCosmetic(reward.getCosmeticId());
                yield "Cosmetic";
            }
        };

        player.showTitle(Title.title(
                plugin.getMiniMessage().deserialize("<gradient:#ffd700:#ffed4e>★ REWARD ★</gradient>"),
                Component.text(rewardText),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(500)
                )
        ));

        // Big explosion of particles
        Location loc = player.getLocation().add(0, 2, 0);
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 50, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 30, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.2f);

        plugin.getCrateManager().giveReward(player, reward);
    }
}