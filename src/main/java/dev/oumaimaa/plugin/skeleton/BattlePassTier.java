package dev.oumaimaa.plugin.skeleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tier in the battle pass
 */
public class BattlePassTier {

    private final int tierNumber;
    private final int requiredXP;
    private final List<BattlePassReward> freeRewards;
    private final List<BattlePassReward> premiumRewards;

    public BattlePassTier(int tierNumber, int requiredXP) {
        this.tierNumber = tierNumber;
        this.requiredXP = requiredXP;
        this.freeRewards = new ArrayList<>();
        this.premiumRewards = new ArrayList<>();
    }

    public void addFreeReward(BattlePassReward reward) {
        freeRewards.add(reward);
    }

    public void addPremiumReward(BattlePassReward reward) {
        premiumRewards.add(reward);
    }

    public int getTierNumber() {
        return tierNumber;
    }

    public int getRequiredXP() {
        return requiredXP;
    }

    public List<BattlePassReward> getFreeRewards() {
        return freeRewards;
    }

    public List<BattlePassReward> getPremiumRewards() {
        return premiumRewards;
    }
}