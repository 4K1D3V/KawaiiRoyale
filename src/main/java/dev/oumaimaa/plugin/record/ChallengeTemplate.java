package dev.oumaimaa.plugin.record;

import dev.oumaimaa.plugin.constant.ChallengeDifficulty;
import dev.oumaimaa.plugin.constant.ChallengeType;

/**
 * Challenge template for generation
 */
public record ChallengeTemplate(String id, String name, String description, ChallengeType type, int requiredProgress,
                                int coinReward, int xpReward, ChallengeDifficulty difficulty) {

}