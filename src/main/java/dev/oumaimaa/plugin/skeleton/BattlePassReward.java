package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.plugin.constant.RewardType;

/**
 * Represents a battle pass reward
 */
public class BattlePassReward {

    private final RewardType type;
    private final Object value; // Can be int (coins) or String (cosmetic ID)
    private final String displayName;

    public BattlePassReward(RewardType type, int amount, String displayName) {
        this.type = type;
        this.value = amount;
        this.displayName = displayName;
    }

    public BattlePassReward(RewardType type, String id, String displayName) {
        this.type = type;
        this.value = id;
        this.displayName = displayName;
    }

    public RewardType getType() {
        return type;
    }

    public int getAmountOrId() {
        return value instanceof Integer ? (Integer) value : 0;
    }

    public String getStringValue() {
        return value instanceof String ? (String) value : "";
    }

    public String getDisplayName() {
        return displayName;
    }
}