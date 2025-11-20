package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.Challenge;
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
 * Daily Challenges GUI
 */
public class ChallengesGUI {

    private final Main plugin;
    private final Player player;
    private final Inventory inventory;

    public ChallengesGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 27,
                Component.text("Daily Challenges").color(NamedTextColor.GOLD));

        setupItems();
    }

    private void setupItems() {
        List<Challenge> challenges = plugin.getChallengeManager().getPlayerChallenges(player);

        int slot = 10;
        for (Challenge challenge : challenges) {
            if (slot > 16) break;

            inventory.setItem(slot, createChallengeItem(challenge));
            slot++;
        }

        inventory.setItem(22, createInfoItem());
    }

    private @NotNull ItemStack createChallengeItem(@NotNull Challenge challenge) {
        Material material = switch (challenge.getType()) {
            case KILLS -> Material.IRON_SWORD;
            case WINS -> Material.GOLDEN_APPLE;
            case GAMES_PLAYED -> Material.COMPASS;
            case TOP_3 -> Material.EMERALD;
            case DAMAGE_DEALT -> Material.BOW;
            case DAMAGE_TAKEN -> Material.SHIELD;
            default -> Material.PAPER;
        };

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = challenge.getFrequency().getColor() + challenge.getName();
        if (challenge.isCompleted()) {
            name = "§a§l✓ " + name;
        }

        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7" + challenge.getDescription()).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        // Progress bar
        int progress = challenge.getCurrentProgress();
        int required = challenge.getRequiredProgress();
        double percentage = challenge.getProgressPercentage();

        String progressBar = createProgressBar(percentage);
        lore.add(Component.text("§7Progress: " + progressBar).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7" + progress + " / " + required).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        // Rewards
        lore.add(Component.text("§6Rewards:").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• " + challenge.getCoinReward() + " coins").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7• " + challenge.getXpReward() + " XP").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));

        // Status
        if (challenge.isCompleted()) {
            if (challenge.isRewardClaimed()) {
                lore.add(Component.text("§a✓ COMPLETED & CLAIMED").decoration(TextDecoration.ITALIC, false));
            } else {
                lore.add(Component.text("§e✓ COMPLETED!").decoration(TextDecoration.ITALIC, false));
            }
        } else {
            lore.add(Component.text(challenge.getDifficulty().getColor() +
                            challenge.getDifficulty().getDisplayName() + " Challenge")
                    .decoration(TextDecoration.ITALIC, false));
        }

        // Frequency badge
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(challenge.getFrequency().getColor() + "◆ " +
                challenge.getFrequency().getDisplayName()).decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private @NotNull String createProgressBar(double percentage) {
        int bars = 10;
        int filled = (int) ((percentage / 100.0) * bars);

        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < bars; i++) {
            if (i == filled) {
                bar.append("§7");
            }
            bar.append("▉");
        }
        bar.append(" §e").append(String.format("%.0f", percentage)).append("%");

        return bar.toString();
    }

    private @NotNull ItemStack createInfoItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("§e§lChallenge Info").decoration(TextDecoration.ITALIC, false));

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Complete challenges to earn").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7coins and battle pass XP!").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Today's completed: §e" + data.getDailyChallengesCompleted())
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("").decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§eChallenges reset daily at midnight!")
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}