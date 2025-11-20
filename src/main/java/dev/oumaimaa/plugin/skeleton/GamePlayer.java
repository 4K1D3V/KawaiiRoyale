package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.plugin.constant.PlayerState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a player in an active game
 */
public class GamePlayer {

    private final UUID uuid;
    private final String name;
    private final Player player;
    private PlayerState state;
    private int kills;
    private int deaths;
    private long survivalTime;
    private double damageDealt;
    private double damageTaken;

    public GamePlayer(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.player = player;
        this.state = PlayerState.ALIVE;
        this.kills = 0;
        this.deaths = 0;
        this.survivalTime = 0;
        this.damageDealt = 0;
        this.damageTaken = 0;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void addDamageDealt(double amount) {
        this.damageDealt += amount;
    }

    public void addDamageTaken(double amount) {
        this.damageTaken += amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public long getSurvivalTime() {
        return survivalTime;
    }

    public void setSurvivalTime(long time) {
        this.survivalTime = time;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GamePlayer other)) return false;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}