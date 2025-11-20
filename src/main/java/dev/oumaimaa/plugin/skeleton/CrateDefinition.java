package dev.oumaimaa.plugin.skeleton;

import dev.oumaimaa.plugin.constant.CrateType;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Definition of a crate type
 */
public class CrateDefinition {

    private final CrateType type;
    private final String name;
    private final String description;
    private final Material displayMaterial;
    private final int price;
    private final List<CrateReward> possibleRewards;
    private final Random random;

    public CrateDefinition(CrateType type, String name, String description,
                           Material displayMaterial, int price) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.displayMaterial = displayMaterial;
        this.price = price;
        this.possibleRewards = new ArrayList<>();
        this.random = new Random();
    }

    public void addReward(CrateReward reward) {
        possibleRewards.add(reward);
    }

    /**
     * Generate a random reward from this crate
     */
    public CrateReward generateReward() {
        double totalWeight = possibleRewards.stream()
                .mapToDouble(CrateReward::getWeight)
                .sum();

        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (CrateReward reward : possibleRewards) {
            currentWeight += reward.getWeight();
            if (randomValue <= currentWeight) {
                return reward.generateInstance();
            }
        }

        return possibleRewards.getFirst().generateInstance();
    }

    public CrateType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public int getPrice() {
        return price;
    }

    public List<CrateReward> getPossibleRewards() {
        return possibleRewards;
    }
}