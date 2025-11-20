package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.LootTier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LootManager {

    private final Main plugin;
    private final Map<LootTier, List<LootItem>> lootTables;
    private final Random random;

    public LootManager(Main plugin) {
        this.plugin = plugin;
        this.lootTables = new EnumMap<>(LootTier.class);
        this.random = new Random();

        initializeLootTables();
    }

    private void initializeLootTables() {
        // COMMON tier
        List<LootItem> common = new ArrayList<>();
        common.add(new LootItem(Material.WOODEN_SWORD, 1, 30));
        common.add(new LootItem(Material.WOODEN_AXE, 1, 25));
        common.add(new LootItem(Material.BOW, 1, 20));
        common.add(new LootItem(Material.ARROW, 16, 40));
        common.add(new LootItem(Material.BREAD, 5, 35));
        common.add(new LootItem(Material.LEATHER_HELMET, 1, 15));
        common.add(new LootItem(Material.LEATHER_CHESTPLATE, 1, 15));
        lootTables.put(LootTier.COMMON, common);

        // RARE tier
        List<LootItem> rare = new ArrayList<>();
        rare.add(new LootItem(Material.IRON_SWORD, 1, 30));
        rare.add(new LootItem(Material.IRON_AXE, 1, 25));
        rare.add(new LootItem(Material.CROSSBOW, 1, 20));
        rare.add(new LootItem(Material.GOLDEN_APPLE, 2, 15));
        rare.add(new LootItem(Material.IRON_HELMET, 1, 20));
        rare.add(new LootItem(Material.IRON_CHESTPLATE, 1, 20));
        lootTables.put(LootTier.RARE, rare);

        // EPIC tier
        List<LootItem> epic = new ArrayList<>();
        epic.add(new LootItem(Material.DIAMOND_SWORD, 1, 25));
        epic.add(new LootItem(Material.DIAMOND_AXE, 1, 20));
        epic.add(new LootItem(Material.TRIDENT, 1, 10));
        epic.add(new LootItem(Material.ENCHANTED_GOLDEN_APPLE, 1, 5));
        epic.add(new LootItem(Material.DIAMOND_HELMET, 1, 15));
        epic.add(new LootItem(Material.DIAMOND_CHESTPLATE, 1, 15));
        lootTables.put(LootTier.EPIC, epic);

        // LEGENDARY tier
        List<LootItem> legendary = new ArrayList<>();
        legendary.add(new LootItem(Material.NETHERITE_SWORD, 1, 20));
        legendary.add(new LootItem(Material.NETHERITE_AXE, 1, 15));
        legendary.add(new LootItem(Material.TOTEM_OF_UNDYING, 1, 5));
        legendary.add(new LootItem(Material.NETHERITE_HELMET, 1, 10));
        legendary.add(new LootItem(Material.NETHERITE_CHESTPLATE, 1, 10));
        lootTables.put(LootTier.LEGENDARY, legendary);
    }

    public ItemStack generateLoot(LootTier tier) {
        List<LootItem> items = lootTables.get(tier);
        if (items == null || items.isEmpty()) {
            return new ItemStack(Material.STICK);
        }

        double totalWeight = items.stream().mapToDouble(LootItem::weight).sum();
        double randomValue = random.nextDouble() * totalWeight;

        double currentWeight = 0;
        for (LootItem item : items) {
            currentWeight += item.weight();
            if (randomValue <= currentWeight) {
                return new ItemStack(item.material(), item.amount());
            }
        }

        return items.getFirst().toItemStack();
    }

    public LootTier getRandomTier() {
        double roll = random.nextDouble() * 100;

        if (roll < 50) return LootTier.COMMON;
        if (roll < 80) return LootTier.RARE;
        if (roll < 95) return LootTier.EPIC;
        return LootTier.LEGENDARY;
    }

    private record LootItem(Material material, int amount, double weight) {
        @Contract(" -> new")
        @NotNull ItemStack toItemStack() {
            return new ItemStack(material, amount);
        }
    }
}