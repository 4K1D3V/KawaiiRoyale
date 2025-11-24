package dev.oumaimaa.plugin.config.playerdata;

/**
 * Comprehensive player statistics tracking
 */
public class PlayerStatistics {

    private int kills;
    private int deaths;
    private int wins;
    private int gamesPlayed;
    private double damageDealt;
    private double damageTaken;
    private int longestKillStreak;
    private int currentKillStreak;
    private long totalPlaytime;
    private int headshots;
    private int assists;
    private int top3Finishes;
    private int top10Finishes;
    private double distanceTraveled;
    private int itemsLooted;
    private int chestsOpened;
    private long fastestWin;
    private int mostKillsInGame;

    public PlayerStatistics() {
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.gamesPlayed = 0;
        this.damageDealt = 0;
        this.damageTaken = 0;
        this.longestKillStreak = 0;
        this.currentKillStreak = 0;
        this.totalPlaytime = 0;
        this.headshots = 0;
        this.assists = 0;
        this.top3Finishes = 0;
        this.top10Finishes = 0;
        this.distanceTraveled = 0;
        this.itemsLooted = 0;
        this.chestsOpened = 0;
        this.fastestWin = 0;
        this.mostKillsInGame = 0;
    }

    public void addKill(int kills) {
        this.kills++;
        this.currentKillStreak++;
        if (this.currentKillStreak > this.longestKillStreak) {
            this.longestKillStreak = this.currentKillStreak;
        }
    }

    public void addDeath(int deaths) {
        this.deaths++;
        this.currentKillStreak = 0;
    }

    public void addWin(int wins) {
        this.wins++;
    }

    public void addGame(int gamesPlayed) {
        this.gamesPlayed++;
    }

    public void addTop3Finish() {
        this.top3Finishes++;
    }

    public void addTop10Finish() {
        this.top10Finishes++;
    }

    public void updateFastestWin(long duration) {
        if (this.fastestWin == 0 || duration < this.fastestWin) {
            this.fastestWin = duration;
        }
    }

    public void updateMostKills(int kills) {
        if (kills > this.mostKillsInGame) {
            this.mostKillsInGame = kills;
        }
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

    public void addDamageDealt(double damage) {
        this.damageDealt += damage;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(double damageTaken) {
        this.damageTaken = damageTaken;
    }

    public void addDamageTaken(double damage) {
        this.damageTaken += damage;
    }

    public int getLongestKillStreak() {
        return longestKillStreak;
    }

    public void setLongestKillStreak(int longestKillStreak) {
        this.longestKillStreak = longestKillStreak;
    }

    public int getCurrentKillStreak() {
        return currentKillStreak;
    }

    public void setCurrentKillStreak(int currentKillStreak) {
        this.currentKillStreak = currentKillStreak;
    }

    public void resetKillStreak() {
        this.currentKillStreak = 0;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }

    public void addPlaytime(long duration) {
        this.totalPlaytime += duration;
    }

    public int getHeadshots() {
        return headshots;
    }

    public void setHeadshots(int headshots) {
        this.headshots = headshots;
    }

    public void addHeadshot() {
        this.headshots++;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void addAssist() {
        this.assists++;
    }

    public int getTop3Finishes() {
        return top3Finishes;
    }

    public void setTop3Finishes(int top3Finishes) {
        this.top3Finishes = top3Finishes;
    }

    public int getTop10Finishes() {
        return top10Finishes;
    }

    public void setTop10Finishes(int top10Finishes) {
        this.top10Finishes = top10Finishes;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public void addDistanceTraveled(double distance) {
        this.distanceTraveled += distance;
    }

    public int getItemsLooted() {
        return itemsLooted;
    }

    public void setItemsLooted(int itemsLooted) {
        this.itemsLooted = itemsLooted;
    }

    public void addItemLooted() {
        this.itemsLooted++;
    }

    public int getChestsOpened() {
        return chestsOpened;
    }

    public void setChestsOpened(int chestsOpened) {
        this.chestsOpened = chestsOpened;
    }

    public void addChestOpened() {
        this.chestsOpened++;
    }

    public long getFastestWin() {
        return fastestWin;
    }

    public void setFastestWin(long fastestWin) {
        this.fastestWin = fastestWin;
    }

    public int getMostKillsInGame() {
        return mostKillsInGame;
    }

    public void setMostKillsInGame(int mostKillsInGame) {
        this.mostKillsInGame = mostKillsInGame;
    }

    public double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public double getWinRate() {
        return gamesPlayed == 0 ? 0 : (double) wins / gamesPlayed * 100;
    }

    public double getAverageDamagePerGame() {
        return gamesPlayed == 0 ? 0 : damageDealt / gamesPlayed;
    }

    public double getHeadshotPercentage() {
        return kills == 0 ? 0 : (double) headshots / kills * 100;
    }
}