package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.config.playerdata.PlayerStatistics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Player statistics GUI
 */
public class StatsGUI implements GUI {

    private final Main plugin;
    private final Player target;
    private final Inventory inventory;

    public StatsGUI(Main plugin, @NotNull Player target) {
        this.plugin = plugin;
        this.target = target;
        this.inventory = Bukkit.createInventory(null, 27,
                Component.text(target.getName() + "'s Statistics").color(NamedTextColor.AQUA));

        setupItems();
    }

    private void setupItems() {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        PlayerStatistics stats = data.getStatistics();

        // Player head (Slot 13)
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(target);
        skullMeta.displayName(Component.text("§6§l" + target.getName())
                .decoration(TextDecoration.ITALIC, false));
        head.setItemMeta(skullMeta);
        inventory.setItem(13, head);

        // Kills (Slot 10)
        inventory.setItem(10, createStatItem(Material.IRON_SWORD,
                "§c§lKills",
                "§f" + stats.getKills(),
                "",
                "§7Total eliminations"));

        // Deaths (Slot 12)
        inventory.setItem(12, createStatItem(Material.SKELETON_SKULL,
                "§4§lDeaths",
                "§f" + stats.getDeaths(),
                "",
                "§7Times eliminated"));

        // Wins (Slot 14)
        inventory.setItem(14, createStatItem(Material.GOLDEN_APPLE,
                "§6§lWins",
                "§f" + stats.getWins(),
                "",
                "§7Victory royales"));

        // Games Played (Slot 16)
        inventory.setItem(16, createStatItem(Material.COMPASS,
                "§b§lGames Played",
                "§f" + stats.getGamesPlayed(),
                "",
                "§7Total matches"));

        // K/D Ratio (Slot 19)
        inventory.setItem(19, createStatItem(Material.DIAMOND,
                "§e§lK/D Ratio",
                "§f" + String.format("%.2f", stats.getKDRatio()),
                "",
                "§7Kills per death"));

        // Win Rate (Slot 21)
        inventory.setItem(21, createStatItem(Material.EMERALD,
                "§a§lWin Rate",
                "§f" + String.format("%.1f%%", stats.getWinRate()),
                "",
                "§7Percentage of wins"));

        // Damage Dealt (Slot 23)
        inventory.setItem(23, createStatItem(Material.BLAZE_ROD,
                "§c§lDamage Dealt",
                "§f" + String.format("%.0f", stats.getDamageDealt()),
                "",
                "§7Total damage output"));

        // Damage Taken (Slot 25)
        inventory.setItem(25, createStatItem(Material.SHIELD,
                "§4§lDamage Taken",
                "§f" + String.format("%.0f", stats.getDamageTaken()),
                "",
                "§7Total damage received"));
    }

    private @NotNull ItemStack createStatItem(Material material, String name, String value, String @NotNull ... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> loreList = new ArrayList<>();
        loreList.add(Component.text(value).decoration(TextDecoration.ITALIC, false));
        for (String line : lore) {
            loreList.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(loreList);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void handleClick(int slot, Player player) {
        // No actions needed for stats GUI
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}