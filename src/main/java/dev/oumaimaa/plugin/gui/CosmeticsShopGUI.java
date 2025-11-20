package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.record.Cosmetic;
import dev.oumaimaa.plugin.constant.CosmeticType;
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
 * Cosmetics shop GUI
 */
public class CosmeticsShopGUI {

    private final Main plugin;
    private final Player player;
    private final CosmeticType currentType;
    private final Inventory inventory;

    public CosmeticsShopGUI(Main plugin, Player player, @NotNull CosmeticType type) {
        this.plugin = plugin;
        this.player = player;
        this.currentType = type;
        this.inventory = Bukkit.createInventory(null, 54,
                Component.text("Cosmetics Shop - " + type.getDisplayName()).color(NamedTextColor.LIGHT_PURPLE));

        setupItems();
    }

    private void setupItems() {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        int slot = 0;
        for (CosmeticType type : CosmeticType.values()) {
            inventory.setItem(slot++, createTypeSelector(type));
        }

        assert plugin.getCosmeticManager() != null;
        List<Cosmetic> cosmetics = plugin.getCosmeticManager().getCosmeticsByType(currentType);
        int cosmeticSlot = 9;

        for (Cosmetic cosmetic : cosmetics) {
            boolean owned = data.hasCosmetic(cosmetic.id()) || cosmetic.price() == 0;
            boolean equipped = plugin.getCosmeticManager()
                    .getEquippedCosmetic(player, currentType) != null &&
                    plugin.getCosmeticManager().getEquippedCosmetic(player, currentType).equals(cosmetic.id());

            inventory.setItem(cosmeticSlot++, createCosmeticItem(cosmetic, owned, equipped, data));
        }

        inventory.setItem(49, createCoinDisplay(data));
    }

    private @NotNull ItemStack createTypeSelector(@NotNull CosmeticType type) {
        ItemStack item = new ItemStack(switch (type) {
            case KILL_MESSAGE -> Material.PAPER;
            case VICTORY_DANCE -> Material.GOLDEN_APPLE;
            case PARACHUTE -> Material.ELYTRA;
            case DEATH_EFFECT -> Material.FIREWORK_ROCKET;
            case TITLE -> Material.NAME_TAG;
        });

        ItemMeta meta = item.getItemMeta();
        String name = type == currentType ? "§a§l" + type.getDisplayName() : "§7" + type.getDisplayName();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(type == currentType ? "§a● Selected" : "§7○ Click to view")
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createCosmeticItem(@NotNull Cosmetic cosmetic, boolean owned, boolean equipped, PlayerData data) {
        ItemStack item = new ItemStack(cosmetic.displayItem());
        ItemMeta meta = item.getItemMeta();

        String name = cosmetic.rarity().getColor() + cosmetic.name();
        if (equipped) name = "§a§l✓ " + name;

        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7" + cosmetic.description()).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(cosmetic.rarity().getColor() + cosmetic.rarity().getDisplayName())
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        if (owned) {
            if (equipped) {
                lore.add(Component.text("§a✓ EQUIPPED").decoration(TextDecoration.ITALIC, false));
            } else {
                lore.add(Component.text("§eClick to equip!").decoration(TextDecoration.ITALIC, false));
            }
        } else {
            lore.add(Component.text("§6Price: " + cosmetic.price() + " coins")
                    .decoration(TextDecoration.ITALIC, false));
            if (data.getCoins() >= cosmetic.price()) {
                lore.add(Component.text("§aClick to purchase!").decoration(TextDecoration.ITALIC, false));
            } else {
                lore.add(Component.text("§cNot enough coins!").decoration(TextDecoration.ITALIC, false));
            }
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull ItemStack createCoinDisplay(@NotNull PlayerData data) {
        ItemStack item = new ItemStack(org.bukkit.Material.SUNFLOWER);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§6§lYour Coins").decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§f" + data.getCoins() + " coins").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Earn coins by:").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Winning games").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Getting kills").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• Completing achievements").decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void handleClick(int slot) {
        // Type selector clicks (slots 0-4)
        if (slot >= 0 && slot < CosmeticType.values().length) {
            CosmeticType type = CosmeticType.values()[slot];
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new CosmeticsShopGUI(plugin, player, type).open());
            return;
        }

        // Cosmetic item clicks
        assert plugin.getCosmeticManager() != null;
        List<Cosmetic> cosmetics = plugin.getCosmeticManager().getCosmeticsByType(currentType);
        int cosmeticIndex = slot - 9;

        if (cosmeticIndex >= 0 && cosmeticIndex < cosmetics.size()) {
            Cosmetic cosmetic = cosmetics.get(cosmeticIndex);
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

            if (data.hasCosmetic(cosmetic.id()) || cosmetic.price() == 0) {
                // Equip
                plugin.getCosmeticManager().equipCosmetic(player, cosmetic.id());
            } else {
                // Purchase
                if (plugin.getCosmeticManager().purchaseCosmetic(player, cosmetic.id())) {
                    plugin.getCosmeticManager().equipCosmetic(player, cosmetic.id());
                }
            }

            // Refresh GUI
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new CosmeticsShopGUI(plugin, player, currentType).open());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}