package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.skeleton.BattlePassReward;
import dev.oumaimaa.plugin.skeleton.BattlePassTier;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.RewardType;
import dev.oumaimaa.plugin.skeleton.BattlePass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the seasonal battle pass
 */
public class BattlePassManager {

    private final Main plugin;
    private final Map<UUID, Integer> playerXP;
    private BattlePass currentPass;

    public BattlePassManager(Main plugin) {
        this.plugin = plugin;
        this.playerXP = new HashMap<>();

        createCurrentSeasonPass();
    }

    /**
     * Create battle pass for current season
     */
    private void createCurrentSeasonPass() {
        int season = getCurrentSeason();
        long seasonEnd = getSeasonEndDate();

        this.currentPass = new BattlePass(season, seasonEnd);

        registerTiers();
    }

    /**
     * Register all battle pass tiers
     */
    private void registerTiers() {
        // Tier 1-10 (Every tier requires 1000 XP)
        for (int i = 1; i <= 10; i++) {
            BattlePassTier tier = new BattlePassTier(i, i * 1000);

            tier.addFreeReward(new BattlePassReward(
                    RewardType.COINS,
                    100 * i,
                    "§e" + (100 * i) + " Coins"
            ));

            tier.addPremiumReward(new BattlePassReward(
                    RewardType.COINS,
                    200 * i,
                    "§6" + (200 * i) + " Coins"
            ));

            // Add cosmetic every 5 tiers
            if (i % 5 == 0) {
                tier.addPremiumReward(new BattlePassReward(
                        RewardType.COSMETIC,
                        "kill_msg_epic_" + i,
                        "§5Epic Kill Message"
                ));
            }

            currentPass.addTier(tier);
        }

        // Tier 11-20
        for (int i = 11; i <= 20; i++) {
            BattlePassTier tier = new BattlePassTier(i, i * 1000);

            tier.addFreeReward(new BattlePassReward(
                    RewardType.COINS,
                    150 * i,
                    "§e" + (150 * i) + " Coins"
            ));

            tier.addPremiumReward(new BattlePassReward(
                    RewardType.COINS,
                    300 * i,
                    "§6" + (300 * i) + " Coins"
            ));

            if (i % 5 == 0) {
                tier.addPremiumReward(new BattlePassReward(
                        RewardType.COSMETIC,
                        "victory_epic_" + i,
                        "§5Epic Victory Dance"
                ));
            }

            currentPass.addTier(tier);
        }

        // Tier 21-30
        for (int i = 21; i <= 30; i++) {
            BattlePassTier tier = new BattlePassTier(i, i * 1000);

            tier.addFreeReward(new BattlePassReward(
                    RewardType.COINS,
                    200 * i,
                    "§e" + (200 * i) + " Coins"
            ));

            tier.addPremiumReward(new BattlePassReward(
                    RewardType.COINS,
                    400 * i,
                    "§6" + (400 * i) + " Coins"
            ));

            if (i == 30) {
                // Ultimate reward at tier 30
                tier.addPremiumReward(new BattlePassReward(
                        RewardType.COSMETIC,
                        "ultimate_season_" + currentPass.getSeason(),
                        "§6§lUltimate Season Cosmetic"
                ));
            }

            currentPass.addTier(tier);
        }
    }

    /**
     * Add XP to player
     */
    public void addXP(Player player, int amount, String reason) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int currentXP = data.getBattlePassXP();
        int currentTier = data.getBattlePassTier();

        currentXP += amount;
        data.setBattlePassXP(currentXP);

        // Check for tier up
        BattlePassTier nextTier = currentPass.getTier(currentTier + 1);
        if (nextTier != null && currentXP >= nextTier.getRequiredXP()) {
            tierUp(player, currentTier + 1);
        }

        // Notify player
        player.sendMessage(
                Component.text("+ " + amount + " XP").color(NamedTextColor.GREEN)
                        .append(Component.text(" (" + reason + ")").color(NamedTextColor.GRAY))
        );
    }

    /**
     * Tier up player
     */
    private void tierUp(Player player, int newTier) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.setBattlePassTier(newTier);

        BattlePassTier tier = currentPass.getTier(newTier);
        if (tier == null) return;

        for (BattlePassReward reward : tier.getFreeRewards()) {
            giveReward(player, reward);
        }

        if (data.hasPremiumBattlePass()) {
            for (BattlePassReward reward : tier.getPremiumRewards()) {
                giveReward(player, reward);
            }
        }

        // Announce tier up
        player.sendMessage(Component.text("")
                .append(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("  TIER UP! ").color(NamedTextColor.YELLOW))
                .append(Component.text("You reached Tier " + newTier).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD))
        );

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
    }

    /**
     * Give reward to player
     */
    private void giveReward(Player player, @NotNull BattlePassReward reward) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        switch (reward.getType()) {
            case COINS -> {
                data.addCoins(reward.getAmountOrId());
                player.sendMessage(Component.text("  + ").color(NamedTextColor.GREEN)
                        .append(Component.text(reward.getDisplayName())));
            }
            case COSMETIC -> {
                data.addCosmetic(reward.getStringValue());
                player.sendMessage(Component.text("  + ").color(NamedTextColor.GREEN)
                        .append(Component.text(reward.getDisplayName())));
            }
        }
    }

    /**
     * Purchase premium battle pass
     */
    public boolean purchasePremium(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.hasPremiumBattlePass()) {
            player.sendMessage(Component.text("You already have the premium battle pass!")
                    .color(NamedTextColor.RED));
            return false;
        }

        int price = 5000; // 5000 coins

        if (data.getCoins() < price) {
            player.sendMessage(Component.text("Not enough coins! Need " + price + " coins.")
                    .color(NamedTextColor.RED));
            return false;
        }

        data.removeCoins(price);
        data.setPremiumBattlePass(true);

        // Give all past premium rewards
        int currentTier = data.getBattlePassTier();
        for (int i = 1; i <= currentTier; i++) {
            BattlePassTier tier = currentPass.getTier(i);
            if (tier != null) {
                for (BattlePassReward reward : tier.getPremiumRewards()) {
                    giveReward(player, reward);
                }
            }
        }

        player.sendMessage(Component.text("✓ Premium Battle Pass Activated!")
                .color(NamedTextColor.GREEN));

        return true;
    }

    /**
     * Get current season number
     */
    private int getCurrentSeason() {
        // Season changes every 3 months
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        return (year - 2025) * 4 + ((month - 1) / 3) + 1;
    }

    /**
     * Get season end date (epoch milliseconds)
     */
    private long getSeasonEndDate() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int seasonEndMonth = ((currentMonth - 1) / 3 + 1) * 3 + 1;

        LocalDate endDate;
        if (seasonEndMonth > 12) {
            endDate = LocalDate.of(now.getYear() + 1, 1, 1);
        } else {
            endDate = LocalDate.of(now.getYear(), seasonEndMonth, 1);
        }

        return endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Get days remaining in season
     */
    public int getDaysRemaining() {
        long now = System.currentTimeMillis();
        long diff = currentPass.getEndDate() - now;
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public BattlePass getCurrentPass() {
        return currentPass;
    }
}