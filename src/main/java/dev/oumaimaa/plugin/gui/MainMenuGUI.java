package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.GameMode;
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
 * Main menu GUI
 */
public class MainMenuGUI implements GUI {

    private final Main plugin;
    private final Inventory inventory;

    public MainMenuGUI(Main plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 27,
                Component.text("KawaiiRoyale Menu").color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));

        setupItems();
    }

    private void setupItems() {
        // Battle Royale (Slot 11)
        inventory.setItem(11, createItem(Material.DIAMOND_SWORD,
                "§6§lBattle Royale",
                "§7Last player standing wins!",
                "",
                "§eMode: §fClassic BR",
                "§ePlayers: §f" + plugin.getQueueManager().getQueueSize(GameMode.BATTLE_ROYALE) + " in queue",
                "",
                "§aClick to join!"));

        // Resurgence (Slot 13)
        inventory.setItem(13, createItem(Material.GOLDEN_SWORD,
                "§6§lResurgence",
                "§7Top players by kills win!",
                "",
                "§eMode: §fRespawn BR",
                "§ePlayers: §f" + plugin.getQueueManager().getQueueSize(GameMode.RESURGENCE) + " in queue",
                "",
                "§aClick to join!"));

        // Stats (Slot 15)
        inventory.setItem(15, createItem(Material.BOOK,
                "§6§lYour Statistics",
                "§7View your stats and progress",
                "",
                "§aClick to view!"));

        // Leaderboard (Slot 22)
        inventory.setItem(22, createItem(Material.EMERALD,
                "§6§lLeaderboards",
                "§7Top players worldwide",
                "",
                "§aClick to view!"));
    }

    private @NotNull ItemStack createItem(Material material, String name, String @NotNull ... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> loreList = new ArrayList<>();
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
        switch (slot) {
            case 11 -> {
                player.closeInventory();
                plugin.getQueueManager().joinQueue(player, GameMode.BATTLE_ROYALE);
            }
            case 13 -> {
                player.closeInventory();
                plugin.getQueueManager().joinQueue(player, GameMode.RESURGENCE);
            }
            case 15 -> {
                player.closeInventory();
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        new StatsGUI(plugin, player).open(player));
            }
            case 22 -> {
                player.closeInventory();
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        new LeaderboardGUI(plugin, "kills").open(player));
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}