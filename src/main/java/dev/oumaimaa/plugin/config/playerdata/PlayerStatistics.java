package dev.oumaimaa.plugin.config.playerdata;

public class PlayerStatistics {
    private int kills;
    private int deaths;
    private int wins;
    private int gamesPlayed;
    private double damageDealt;
    private double damageTaken;
    private int longestKillStreak;
    private long totalPlaytime;

    public PlayerStatistics() {
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.gamesPlayed = 0;
        this.damageDealt = 0;
        this.damageTaken = 0;
        this.longestKillStreak = 0;
        this.totalPlaytime = 0;
    }

    public void addKill(int kills) {
        this.kills++;
    }

    public void addDeath(int deaths) {
        this.deaths++;
    }

    public void addWin(int wins) {
        this.wins++;
    }

    public void addGame(int gamesPlayed) {
        this.gamesPlayed++;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(double damageDealt) {
        this.damageDealt = damageDealt;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(double damageTaken) {
        this.damageTaken = damageTaken;
    }

    public int getLongestKillStreak() {
        return longestKillStreak;
    }

    public void setLongestKillStreak(int longestKillStreak) {
        this.longestKillStreak = longestKillStreak;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }

    public double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public double getWinRate() {
        return gamesPlayed == 0 ? 0 : (double) wins / gamesPlayed * 100;
    }
}