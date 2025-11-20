package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.database.DatabaseHandler;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Leaderboard GUI
 */
public class LeaderboardGUI implements GUI {

    private final Main plugin;
    private final String type;
    private final Inventory inventory;

    public LeaderboardGUI(Main plugin, @NotNull String type) {
        this.plugin = plugin;
        this.type = type;
        this.inventory = Bukkit.createInventory(null, 54,
                Component.text("Leaderboard - " + type.toUpperCase()).color(NamedTextColor.GOLD));

        setupItems();
    }

    private void setupItems() {
        inventory.setItem(0, createItem(Material.IRON_SWORD, "§cKills Leaderboard"));
        inventory.setItem(1, createItem(Material.GOLDEN_APPLE, "§6Wins Leaderboard"));
        inventory.setItem(2, createItem(Material.DIAMOND, "§bK/D Leaderboard"));

        plugin.getPlayerDataManager().getTopPlayers(type, 45).thenAccept(entries -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < Math.min(entries.size(), 45); i++) {
                DatabaseHandler.LeaderboardEntry entry = entries.get(i);
                int slot = 9 + i;

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();

                String rank = "§7#" + (i + 1);
                if (i == 0) rank = "§6§l#1 ★";
                else if (i == 1) rank = "§7§l#2 ★";
                else if (i == 2) rank = "§c§l#3 ★";

                meta.displayName(Component.text(rank + " §f" + entry.name())
                        .decoration(TextDecoration.ITALIC, false));

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("§e" + getStatName() + ": §f" + entry.value())
                        .decoration(TextDecoration.ITALIC, false));
                meta.lore(lore);

                head.setItemMeta(meta);
                inventory.setItem(slot, head);
            }
        }));
    }

    @Contract(pure = true)
    private @NotNull String getStatName() {
        return switch (type.toLowerCase()) {
            case "kills" -> "Kills";
            case "wins" -> "Wins";
            case "kd" -> "K/D Ratio";
            default -> "Value";
        };
    }

    private @NotNull ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void handleClick(int slot, Player player) {
        if (slot == 0) {
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new LeaderboardGUI(plugin, "kills").open(player));
        } else if (slot == 1) {
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new LeaderboardGUI(plugin, "wins").open(player));
        } else if (slot == 2) {
            player.closeInventory();
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new LeaderboardGUI(plugin, "kd").open(player));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}