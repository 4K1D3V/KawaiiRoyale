package dev.oumaimaa.plugin.record;

import dev.oumaimaa.plugin.constant.CosmeticRarity;
import dev.oumaimaa.plugin.constant.CosmeticType;
import org.bukkit.Material;

/**
 * Represents a cosmetic item
 */
public record Cosmetic(String id, String name, String description, CosmeticType type, CosmeticRarity rarity, int price,
                       String data, Material displayItem) {

}