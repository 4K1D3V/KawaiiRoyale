package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.CrateType;
import dev.oumaimaa.plugin.skeleton.CrateDefinition;
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
 * Crate shop GUI
 */
public class CrateShopGUI {

    private final Main plugin;
    private final Player player;
    private final Inventory inventory;

    public CrateShopGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 27,
                Component.text("Crate Shop").color(NamedTextColor.GOLD));

        setupItems();
    }

    private void setupItems() {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Rare Crate (Slot 11)
        CrateDefinition rareCrate = plugin.getCrateManager().getCrate(CrateType.RARE);
        inventory.setItem(11, createShopItem(rareCrate, data));

        // Epic Crate (Slot 13)
        CrateDefinition epicCrate = plugin.getCrateManager().getCrate(CrateType.EPIC);
        inventory.setItem(13, createShopItem(epicCrate, data));

        // Legendary Crate (Slot 15)
        CrateDefinition legendaryCrate = plugin.getCrateManager().getCrate(CrateType.LEGENDARY);
        inventory.setItem(15, createShopItem(legendaryCrate, data));

        // Back button (Slot 22)
        inventory.setItem(22, createBackButton());
    }

    private @NotNull ItemStack createShopItem(@NotNull CrateDefinition crate, @NotNull PlayerData data) {
        ItemStack item = new ItemStack(crate.getDisplayMaterial());
        ItemMeta meta = item.getItemMeta();

        String name = crate.getType().getColor() + "§l" + crate.getName();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7" + crate.getDescription()).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        lore.add(Component.text("§6Price: " + crate.getPrice() + " coins")
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Your coins: " + data.getCoins())
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        if (data.getCoins() >= crate.getPrice()) {
            lore.add(Component.text("§aClick to purchase!").decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("§cNot enough coins!").decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createBackButton() {
        ItemStack item = new ItemStack(org.bukkit.Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§aBack to Crates").decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void handleClick(int slot) {
        switch (slot) {
            case 11 -> {
                // Purchase Rare
                if (plugin.getCrateManager().purchaseCrate(player, CrateType.RARE)) {
                    player.closeInventory();
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            new CrateShopGUI(plugin, player).open());
                }
            }
            case 13 -> {
                // Purchase Epic
                if (plugin.getCrateManager().purchaseCrate(player, CrateType.EPIC)) {
                    player.closeInventory();
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            new CrateShopGUI(plugin, player).open());
                }
            }
            case 15 -> {
                // Purchase Legendary
                if (plugin.getCrateManager().purchaseCrate(player, CrateType.LEGENDARY)) {
                    player.closeInventory();
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            new CrateShopGUI(plugin, player).open());
                }
            }
            case 22 -> {
                // Back to crates
                player.closeInventory();
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        new CratesGUI(plugin, player).open());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}