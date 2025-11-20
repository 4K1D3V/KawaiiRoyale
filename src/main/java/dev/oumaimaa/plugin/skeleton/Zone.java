package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.task.ZoneShrinkTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a shrinking zone for a game
 */
public class Zone {

    private final Main plugin;
    private final Game game;
    private final Location center;
    private double currentRadius;
    private double targetRadius;
    private boolean shrinking;
    private int shrinkStage;
    private BukkitTask shrinkTask;
    private BukkitTask damageTask;
    private BukkitTask particleTask;

    public Zone(@NotNull Main plugin, @NotNull Game game) {
        this.plugin = plugin;
        this.game = game;
        this.shrinking = false;
        this.shrinkStage = 0;
        this.currentRadius = plugin.getConfigManager().getInitialZoneSize();
        this.center = game.getArena().getCenter();
        this.targetRadius = currentRadius;

        startDamageTask();

        if (plugin.getConfigManager().getMainConfig().getBoolean("zone.show-particles", true)) {
            startParticleTask();
        }
    }

    /**
     * Start shrinking the zone
     */
    public void startShrinking() {
        if (shrinking) return;

        shrinking = true;
        shrinkStage++;

        // Calculate new target
        targetRadius = calculateNextRadius();

        // Announce
        game.broadcastMessage(Component.text("⚠ The zone is shrinking!")
                .color(NamedTextColor.YELLOW));

        // Play sound
        for (GamePlayer gp : game.getAllPlayers()) {
            gp.getPlayer().playSound(gp.getPlayer().getLocation(),
                    Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.8f);
        }

        shrinkTask = new ZoneShrinkTask(this).runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * Stop shrinking
     */
    public void stopShrinking() {
        if (shrinkTask != null) {
            shrinkTask.cancel();
            shrinkTask = null;
        }
        if (damageTask != null) {
            damageTask.cancel();
            damageTask = null;
        }
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        shrinking = false;
    }

    /**
     * Update zone radius (called by shrink task)
     */
    public void updateRadius(double newRadius) {
        this.currentRadius = newRadius;

        if (currentRadius <= targetRadius) {
            stopCurrentShrink();
            scheduleNextShrink();
        }
    }

    /**
     * Stop current shrink phase
     */
    private void stopCurrentShrink() {
        if (shrinkTask != null) {
            shrinkTask.cancel();
            shrinkTask = null;
        }
        shrinking = false;

        game.broadcastMessage(Component.text("Zone has stopped shrinking!")
                .color(NamedTextColor.GREEN));
    }

    /**
     * Schedule next shrink phase
     */
    private void scheduleNextShrink() {
        int interval = plugin.getConfigManager().getZoneShrinkInterval();
        double finalSize = plugin.getConfigManager().getFinalZoneSize();

        if (currentRadius > finalSize) {
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    this::startShrinking, interval * 20L);
        }
    }

    /**
     * Calculate next radius
     */
    private double calculateNextRadius() {
        double finalSize = plugin.getConfigManager().getFinalZoneSize();
        double shrinkFactor = 0.6; // 60% of current size each stage

        return Math.max(currentRadius * shrinkFactor, finalSize);
    }

    /**
     * Check if location is outside zone
     */
    public boolean isOutsideZone(Location location) {
        if (center == null || location.getWorld() != center.getWorld()) {
            return false;
        }
        return location.distance(center) > currentRadius;
    }

    /**
     * Start damage task for players outside zone
     */
    private void startDamageTask() {
        int damageInterval = plugin.getConfigManager().getMainConfig()
                .getInt("zone.damage-interval", 20);

        damageTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (GamePlayer gp : game.getAlivePlayers()) {
                Player player = gp.getPlayer();
                if (isOutsideZone(player.getLocation())) {
                    damagePlayer(player);
                }
            }
        }, 0L, damageInterval);
    }

    /**
     * Damage player outside zone
     */
    public void damagePlayer(@NotNull Player player) {
        int damage = plugin.getConfigManager().getZoneDamage();
        player.damage(damage);

        // Send warning
        Component warning = plugin.getMiniMessage().deserialize(
                "<gradient:#ff0000:#ff6666>⚠ You're outside the safe zone! Taking damage!</gradient>"
        );
        player.sendActionBar(warning);

        // Play warning sound
        player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.0f);
    }

    /**
     * Start particle border display
     */
    private void startParticleTask() {
        int density = plugin.getConfigManager().getMainConfig()
                .getInt("zone.particle-density", 5);

        particleTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (center == null || center.getWorld() == null) return;

            World world = center.getWorld();
            double radius = currentRadius;

            for (double angle = 0; angle < 360; angle += density) {
                double radians = Math.toRadians(angle);
                double x = center.getX() + radius * Math.cos(radians);
                double z = center.getZ() + radius * Math.sin(radians);

                Location particleLoc = new Location(world, x, center.getY() + 1, z);

                world.spawnParticle(
                        Particle.DUST,
                        particleLoc,
                        1,
                        new Particle.DustOptions(Color.RED, 1.0f)
                );
            }
        }, 0L, 10L);
    }

    /**
     * Get distance from center
     */
    public double getDistanceFromCenter(Location location) {
        if (center == null) return 0;
        return location.distance(center);
    }

    /**
     * Get distance from zone edge
     */
    public double getDistanceFromEdge(Location location) {
        double distanceFromCenter = getDistanceFromCenter(location);
        return currentRadius - distanceFromCenter;
    }

    public Location getCenter() {
        return center;
    }

    public double getCurrentRadius() {
        return currentRadius;
    }

    public double getTargetRadius() {
        return targetRadius;
    }

    public boolean isShrinking() {
        return shrinking;
    }

    public int getShrinkStage() {
        return shrinkStage;
    }
}
