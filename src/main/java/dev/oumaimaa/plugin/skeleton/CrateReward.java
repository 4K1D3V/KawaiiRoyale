package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.plugin.constant.RewardItemType;

import java.util.Random;

/**
 * Reward from a crate
 */
public class CrateReward {

    private final RewardItemType type;
    private final int minAmount;
    private final int maxAmount;
    private final String cosmeticId;
    private final double weight;
    private int actualAmount;

    public CrateReward(RewardItemType type, int minAmount, int maxAmount, double weight) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.cosmeticId = null;
        this.weight = weight;
    }

    public CrateReward(RewardItemType type, String cosmeticId, double weight) {
        this.type = type;
        this.minAmount = 1;
        this.maxAmount = 1;
        this.cosmeticId = cosmeticId;
        this.weight = weight;
    }

    /**
     * Generate instance with random amount
     */
    public CrateReward generateInstance() {
        Random random = new Random();
        this.actualAmount = minAmount + random.nextInt(maxAmount - minAmount + 1);
        return this;
    }

    public RewardItemType getType() {
        return type;
    }

    public int getAmount() {
        return actualAmount;
    }

    public String getCosmeticId() {
        return cosmeticId;
    }

    public double getWeight() {
        return weight;
    }
}
