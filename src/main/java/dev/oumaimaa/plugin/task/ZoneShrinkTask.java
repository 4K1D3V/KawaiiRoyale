package dev.oumaimaa.plugin.task;

import dev.oumaimaa.plugin.skeleton.Zone;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that handles zone shrinking animation
 */
public class ZoneShrinkTask extends BukkitRunnable {

    private static final double SHRINK_RATE = 1.0; // blocks per second
    private final Zone zone;

    public ZoneShrinkTask(Zone zone) {
        this.zone = zone;
    }

    @Override
    public void run() {
        double current = zone.getCurrentRadius();
        double target = zone.getTargetRadius();

        if (current <= target) {
            cancel();
            return;
        }

        double newRadius = Math.max(current - SHRINK_RATE, target);
        zone.updateRadius(newRadius);
    }
}