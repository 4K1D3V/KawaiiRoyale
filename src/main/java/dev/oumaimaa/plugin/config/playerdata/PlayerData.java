package dev.oumaimaa.plugin.config.playerdata;

import dev.oumaimaa.plugin.constant.CosmeticType;
import dev.oumaimaa.plugin.constant.CrateType;

import java.util.*;

public class PlayerData {
    private final UUID uuid;
    private String name;
    private PlayerStatistics statistics;
    private int coins;
    private Set<String> unlockedAchievements;
    private Set<String> ownedCosmetics;
    private Map<CosmeticType, String> equippedCosmetics;
    private int battlePassXP;
    private int battlePassTier;
    private boolean premiumBattlePass;
    private int dailyChallengesCompleted;
    private long lastDailyChallengeReset;
    private Map<CrateType, Integer> ownedCrates;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.name = "";
        this.statistics = new PlayerStatistics();
        this.coins = 0;
        this.unlockedAchievements = new HashSet<>();
        this.ownedCosmetics = new HashSet<>();
        this.equippedCosmetics = new EnumMap<>(CosmeticType.class);
        this.battlePassXP = 0;
        this.battlePassTier = 0;
        this.premiumBattlePass = false;
        this.dailyChallengesCompleted = 0;
        this.lastDailyChallengeReset = System.currentTimeMillis();
        this.ownedCrates = new EnumMap<>(CrateType.class);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(PlayerStatistics statistics) {
        this.statistics = statistics;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void removeCoins(int amount) {
        this.coins = Math.max(0, this.coins - amount);
    }

    public Set<String> getUnlockedAchievements() {
        return new HashSet<>(unlockedAchievements);
    }

    public void setUnlockedAchievements(Set<String> achievements) {
        this.unlockedAchievements = new HashSet<>(achievements);
    }

    public void addAchievement(String achievementId) {
        unlockedAchievements.add(achievementId);
    }

    public boolean hasAchievement(String achievementId) {
        return unlockedAchievements.contains(achievementId);
    }

    public Set<String> getOwnedCosmetics() {
        return new HashSet<>(ownedCosmetics);
    }

    public void setOwnedCosmetics(Set<String> cosmetics) {
        this.ownedCosmetics = new HashSet<>(cosmetics);
    }

    public void addCosmetic(String cosmeticId) {
        ownedCosmetics.add(cosmeticId);
    }

    public boolean hasCosmetic(String cosmeticId) {
        return ownedCosmetics.contains(cosmeticId);
    }

    public Map<CosmeticType, String> getEquippedCosmetics() {
        return new EnumMap<>(equippedCosmetics);
    }

    public void setEquippedCosmetics(Map<CosmeticType, String> equipped) {
        this.equippedCosmetics = new EnumMap<>(equipped);
    }

    public void equipCosmetic(CosmeticType type, String cosmeticId) {
        equippedCosmetics.put(type, cosmeticId);
    }

    public String getEquippedCosmetic(CosmeticType type) {
        return equippedCosmetics.get(type);
    }

    public int getBattlePassXP() {
        return battlePassXP;
    }

    public void setBattlePassXP(int xp) {
        this.battlePassXP = xp;
    }

    public void addBattlePassXP(int xp) {
        this.battlePassXP += xp;
    }

    public int getBattlePassTier() {
        return battlePassTier;
    }

    public void setBattlePassTier(int tier) {
        this.battlePassTier = tier;
    }

    public boolean hasPremiumBattlePass() {
        return premiumBattlePass;
    }

    public void setPremiumBattlePass(boolean premium) {
        this.premiumBattlePass = premium;
    }

    public int getDailyChallengesCompleted() {
        return dailyChallengesCompleted;
    }

    public void setDailyChallengesCompleted(int completed) {
        this.dailyChallengesCompleted = completed;
    }

    public void incrementDailyChallengesCompleted() {
        this.dailyChallengesCompleted++;
    }

    public long getLastDailyChallengeReset() {
        return lastDailyChallengeReset;
    }

    public void setLastDailyChallengeReset(long timestamp) {
        this.lastDailyChallengeReset = timestamp;
    }

    public void resetDailyChallenges() {
        this.dailyChallengesCompleted = 0;
        this.lastDailyChallengeReset = System.currentTimeMillis();
    }

    public int getCrateCount(CrateType type) {
        return ownedCrates.getOrDefault(type, 0);
    }

    public void addCrate(CrateType type, int amount) {
        ownedCrates.merge(type, amount, Integer::sum);
    }

    public void removeCrate(CrateType type, int amount) {
        int current = ownedCrates.getOrDefault(type, 0);
        ownedCrates.put(type, Math.max(0, current - amount));
    }

    public Map<CrateType, Integer> getOwnedCrates() {
        return new EnumMap<>(ownedCrates);
    }

    public void setOwnedCrates(Map<CrateType, Integer> crates) {
        this.ownedCrates = new EnumMap<>(crates);
    }
}