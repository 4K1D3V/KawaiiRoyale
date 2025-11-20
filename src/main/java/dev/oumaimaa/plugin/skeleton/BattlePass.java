package dev.oumaimaa.plugin.skeleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a battle pass season
 */
public class BattlePass {

    private final int season;
    private final long endDate;
    private final Map<Integer, BattlePassTier> tiers;

    public BattlePass(int season, long endDate) {
        this.season = season;
        this.endDate = endDate;
        this.tiers = new HashMap<>();
    }

    public void addTier(BattlePassTier tier) {
        tiers.put(tier.getTierNumber(), tier);
    }

    public BattlePassTier getTier(int tierNumber) {
        return tiers.get(tierNumber);
    }

    public int getMaxTier() {
        return tiers.size();
    }

    public int getSeason() {
        return season;
    }

    public long getEndDate() {
        return endDate;
    }
}
