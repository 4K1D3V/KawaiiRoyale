package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.CrateDefinition;
import dev.oumaimaa.plugin.skeleton.CrateReward;
import dev.oumaimaa.plugin.constant.CrateType;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Crates inventory GUI
 */
public class CratesGUI {

    private final Main plugin;
    private final Player player;
    private final Inventory inventory;

    public CratesGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54,
                Component.text("Reward Crates").color(NamedTextColor.LIGHT_PURPLE));

        setupItems();
    }

    private void setupItems() {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Common Crate (Slot 11)
        CrateDefinition commonCrate = plugin.getCrateManager().getCrate(CrateType.COMMON);
        inventory.setItem(11, createCrateItem(commonCrate, data.getCrateCount(CrateType.COMMON)));

        // Rare Crate (Slot 13)
        CrateDefinition rareCrate = plugin.getCrateManager().getCrate(CrateType.RARE);
        inventory.setItem(13, createCrateItem(rareCrate, data.getCrateCount(CrateType.RARE)));

        // Epic Crate (Slot 15)
        CrateDefinition epicCrate = plugin.getCrateManager().getCrate(CrateType.EPIC);
        inventory.setItem(15, createCrateItem(epicCrate, data.getCrateCount(CrateType.EPIC)));

        // Legendary Crate (Slot 29)
        CrateDefinition legendaryCrate = plugin.getCrateManager().getCrate(CrateType.LEGENDARY);
        inventory.setItem(29, createCrateItem(legendaryCrate, data.getCrateCount(CrateType.LEGENDARY)));

        // Shop button (Slot 31)
        inventory.setItem(31, createShopButton());

        // Info (Slot 49)
        inventory.setItem(49, createInfoItem(data));
    }

    private @NotNull ItemStack createCrateItem(@NotNull CrateDefinition crate, int owned) {
        ItemStack item = new ItemStack(crate.getDisplayMaterial());
        ItemMeta meta = item.getItemMeta();

        String name = crate.getType().getColor() + "§l" + crate.getName();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7" + crate.getDescription()).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        lore.add(Component.text("§7Owned: §f" + owned).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        lore.add(Component.text("§6Possible Rewards:").decoration(TextDecoration.ITALIC, false));
        for (CrateReward reward : crate.getPossibleRewards()) {
            String rewardLine = "§7• ";
            switch (reward.getType()) {
                case COINS -> rewardLine += "Coins";
                case BP_XP -> rewardLine += "Battle Pass XP";
                case COSMETIC -> rewardLine += "Cosmetic Item";
            }
            lore.add(Component.text(rewardLine).decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        if (owned > 0) {
            lore.add(Component.text("§a§lClick to open!").decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("§cYou don't own any of these!").decoration(TextDecoration.ITALIC, false));
            if (crate.getPrice() > 0) {
                lore.add(Component.text("§7Purchase in shop for " + crate.getPrice() + " coins")
                        .decoration(TextDecoration.ITALIC, false));
            }
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createShopButton() {
        ItemStack item = new ItemStack(org.bukkit.Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§a§lCrate Shop").decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Purchase crates with coins!").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§aClick to open shop!").decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createInfoItem(@NotNull PlayerData data) {
        ItemStack item = new ItemStack(org.bukkit.Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§e§lYour Coins").decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§6" + data.getCoins() + " coins").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Earn coins by:").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Winning games").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Getting kills").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Completing achievements").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Daily challenges").decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void handleClick(int slot) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        switch (slot) {
            case 11 -> {
                // Common crate
                if (data.getCrateCount(CrateType.COMMON) > 0) {
                    player.closeInventory();
                    plugin.getCrateManager().openCrate(player, CrateType.COMMON);
                }
            }
            case 13 -> {
                // Rare crate
                if (data.getCrateCount(CrateType.RARE) > 0) {
                    player.closeInventory();
                    plugin.getCrateManager().openCrate(player, CrateType.RARE);
                }
            }
            case 15 -> {
                // Epic crate
                if (data.getCrateCount(CrateType.EPIC) > 0) {
                    player.closeInventory();
                    plugin.getCrateManager().openCrate(player, CrateType.EPIC);
                }
            }
            case 29 -> {
                // Legendary crate
                if (data.getCrateCount(CrateType.LEGENDARY) > 0) {
                    player.closeInventory();
                    plugin.getCrateManager().openCrate(player, CrateType.LEGENDARY);
                }
            }
            case 31 -> {
                // Shop
                player.closeInventory();
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        new CrateShopGUI(plugin, player).open());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}