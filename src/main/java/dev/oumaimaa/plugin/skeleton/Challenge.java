package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.plugin.constant.ChallengeDifficulty;
import dev.oumaimaa.plugin.constant.ChallengeFrequency;
import dev.oumaimaa.plugin.record.ChallengeTemplate;
import dev.oumaimaa.plugin.constant.ChallengeType;

/**
 * Active challenge instance
 */
public class Challenge {

    private final ChallengeTemplate template;
    private final ChallengeFrequency frequency;
    private int currentProgress;
    private boolean rewardClaimed;

    public Challenge(ChallengeTemplate template, ChallengeFrequency frequency) {
        this.template = template;
        this.frequency = frequency;
        this.currentProgress = 0;
        this.rewardClaimed = false;
    }

    public void addProgress(int amount) {
        this.currentProgress += amount;
    }

    public boolean isCompleted() {
        return currentProgress >= template.requiredProgress();
    }

    public double getProgressPercentage() {
        return Math.min(100.0, (double) currentProgress / template.requiredProgress() * 100);
    }

    public String getName() {
        return template.name();
    }

    public String getDescription() {
        return template.description();
    }

    public ChallengeType getType() {
        return template.type();
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getRequiredProgress() {
        return template.requiredProgress();
    }

    public int getCoinReward() {
        return template.coinReward();
    }

    public int getXpReward() {
        return template.xpReward();
    }

    public ChallengeFrequency getFrequency() {
        return frequency;
    }

    public ChallengeDifficulty getDifficulty() {
        return template.difficulty();
    }

    public boolean isRewardClaimed() {
        return rewardClaimed;
    }

    public void setRewardClaimed(boolean claimed) {
        this.rewardClaimed = claimed;
    }
}