package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.BattlePassReward;
import dev.oumaimaa.plugin.skeleton.BattlePassTier;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Battle Pass GUI
 */
public class BattlePassGUI {

    private final Main plugin;
    private final Player player;
    private final Inventory inventory;
    private final int startTier;

    public BattlePassGUI(@NotNull Main plugin, Player player, int startTier) {
        this.plugin = plugin;
        this.player = player;
        this.startTier = startTier;
        this.inventory = Bukkit.createInventory(null, 54,
                Component.text("Battle Pass - Season " + plugin.getBattlePassManager().getCurrentPass().getSeason())
                        .color(NamedTextColor.GOLD));

        setupItems();
    }

    private void setupItems() {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int currentTier = data.getBattlePassTier();
        int currentXP = data.getBattlePassXP();
        boolean hasPremium = data.hasPremiumBattlePass();

        for (int i = 0; i < 7; i++) {
            int tierNum = startTier + i;
            BattlePassTier tier = plugin.getBattlePassManager().getCurrentPass().getTier(tierNum);

            if (tier != null) {
                inventory.setItem(i, createTierItem(tier, false, currentTier, currentXP, hasPremium));
                inventory.setItem(i + 36, createTierItem(tier, true, currentTier, currentXP, hasPremium));
            }
        }

        // Navigation
        if (startTier > 1) {
            inventory.setItem(45, createNavigationItem("§aPrevious", -7));
        }
        if (startTier + 7 <= 30) {
            inventory.setItem(53, createNavigationItem("§aNext", 7));
        }

        // Premium purchase button
        if (!hasPremium) {
            inventory.setItem(49, createPremiumPurchaseItem(data));
        } else {
            inventory.setItem(49, createPremiumActiveItem());
        }

        // Info display
        inventory.setItem(48, createInfoItem(data, currentTier, currentXP));
    }

    private @NotNull ItemStack createTierItem(@NotNull BattlePassTier tier, boolean premium,
                                              int currentTier, int currentXP, boolean hasPremium) {
        boolean unlocked = currentTier >= tier.getTierNumber();
        boolean locked = !premium || !hasPremium;

        Material material;
        if (unlocked) {
            material = premium ? Material.DIAMOND : Material.EMERALD;
        } else {
            material = Material.GRAY_DYE;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String tierName = "§6Tier " + tier.getTierNumber();
        if (unlocked) tierName = "§a✓ " + tierName;

        meta.displayName(Component.text(tierName).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Required: " + tier.getRequiredXP() + " XP")
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        List<BattlePassReward> rewards = premium ? tier.getPremiumRewards() : tier.getFreeRewards();

        if (premium) {
            lore.add(Component.text("§6★ Premium Rewards:").decoration(TextDecoration.ITALIC, false));
            if (locked) {
                lore.add(Component.text("§c✘ Premium Required").decoration(TextDecoration.ITALIC, false));
            }
        } else {
            lore.add(Component.text("§aFree Rewards:").decoration(TextDecoration.ITALIC, false));
        }

        for (BattlePassReward reward : rewards) {
            String status = unlocked ? "§a✓ " : "§7";
            lore.add(Component.text(status + reward.getDisplayName()).decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createNavigationItem(String name, int offset) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createPremiumPurchaseItem(@NotNull PlayerData data) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§6§lPurchase Premium Pass").decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Unlock premium rewards").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7for all tiers!").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§6Price: 5000 coins").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Your coins: " + data.getCoins()).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        if (data.getCoins() >= 5000) {
            lore.add(Component.text("§aClick to purchase!").decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("§cNot enough coins!").decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createPremiumActiveItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§6§l★ Premium Active ★").decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§aYou have premium!").decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createInfoItem(PlayerData data, int currentTier, int currentXP) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§e§lYour Progress").decoration(TextDecoration.ITALIC, false));

        BattlePassTier nextTier = plugin.getBattlePassManager().getCurrentPass().getTier(currentTier + 1);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Current Tier: §6" + currentTier).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Current XP: §e" + currentXP).decoration(TextDecoration.ITALIC, false));

        if (nextTier != null) {
            int needed = nextTier.getRequiredXP() - currentXP;
            lore.add(Component.text("§7Next Tier: §e" + needed + " XP").decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("§a§lMAX TIER!").decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Season ends in:").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§e" + plugin.getBattlePassManager().getDaysRemaining() + " days")
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void handleClick(int slot) {
        // Previous button
        if (slot == 45 && startTier > 1) {
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new BattlePassGUI(plugin, player, Math.max(1, startTier - 7)).open());
        }

        // Next button
        if (slot == 53 && startTier + 7 <= 30) {
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new BattlePassGUI(plugin, player, startTier + 7).open());
        }

        // Premium purchase
        if (slot == 49) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            if (!data.hasPremiumBattlePass()) {
                if (plugin.getBattlePassManager().purchasePremium(player)) {
                    player.closeInventory();
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            new BattlePassGUI(plugin, player, startTier).open());
                }
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}