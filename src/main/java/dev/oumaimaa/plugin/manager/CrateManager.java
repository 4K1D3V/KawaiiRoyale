package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.CrateType;
import dev.oumaimaa.plugin.constant.RewardItemType;
import dev.oumaimaa.plugin.record.Cosmetic;
import dev.oumaimaa.plugin.skeleton.CrateDefinition;
import dev.oumaimaa.plugin.skeleton.CrateReward;
import dev.oumaimaa.plugin.task.CrateOpeningAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages reward crates
 */
public class CrateManager {

    private final Main plugin;
    private final Map<CrateType, CrateDefinition> crates;

    public CrateManager(Main plugin) {
        this.plugin = plugin;
        this.crates = new EnumMap<>(CrateType.class);

        registerCrates();
    }

    /**
     * Register all crate types
     */
    private void registerCrates() {
        // Common Crate
        CrateDefinition commonCrate = new CrateDefinition(
                CrateType.COMMON,
                "Common Crate",
                "Basic rewards",
                Material.CHEST,
                0
        );
        commonCrate.addReward(new CrateReward(RewardItemType.COINS, 50, 100, 50.0));
        commonCrate.addReward(new CrateReward(RewardItemType.COINS, 100, 200, 30.0));
        commonCrate.addReward(new CrateReward(RewardItemType.BP_XP, 50, 100, 15.0));
        commonCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "kill_msg_default", 5.0));
        crates.put(CrateType.COMMON, commonCrate);

        // Rare Crate
        CrateDefinition rareCrate = new CrateDefinition(
                CrateType.RARE,
                "Rare Crate",
                "Better rewards",
                Material.ENDER_CHEST,
                500
        );
        rareCrate.addReward(new CrateReward(RewardItemType.COINS, 200, 400, 40.0));
        rareCrate.addReward(new CrateReward(RewardItemType.COINS, 400, 600, 25.0));
        rareCrate.addReward(new CrateReward(RewardItemType.BP_XP, 100, 200, 20.0));
        rareCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "kill_msg_destroyed", 10.0));
        rareCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "parachute_red", 5.0));
        crates.put(CrateType.RARE, rareCrate);

        // Epic Crate
        CrateDefinition epicCrate = new CrateDefinition(
                CrateType.EPIC,
                "Epic Crate",
                "Epic rewards",
                Material.SHULKER_BOX,
                1500
        );
        epicCrate.addReward(new CrateReward(RewardItemType.COINS, 500, 800, 35.0));
        epicCrate.addReward(new CrateReward(RewardItemType.COINS, 800, 1200, 25.0));
        epicCrate.addReward(new CrateReward(RewardItemType.BP_XP, 200, 400, 20.0));
        epicCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "kill_msg_obliterated", 10.0));
        epicCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "victory_epic", 7.0));
        epicCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "death_explosion", 3.0));
        crates.put(CrateType.EPIC, epicCrate);

        // Legendary Crate
        CrateDefinition legendaryCrate = new CrateDefinition(
                CrateType.LEGENDARY,
                "Legendary Crate",
                "Legendary rewards!",
                Material.NETHER_STAR,
                5000
        );
        legendaryCrate.addReward(new CrateReward(RewardItemType.COINS, 1000, 2000, 30.0));
        legendaryCrate.addReward(new CrateReward(RewardItemType.COINS, 2000, 3000, 20.0));
        legendaryCrate.addReward(new CrateReward(RewardItemType.BP_XP, 500, 1000, 15.0));
        legendaryCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "kill_msg_legendary", 15.0));
        legendaryCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "victory_legendary", 10.0));
        legendaryCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "parachute_rainbow", 7.0));
        legendaryCrate.addReward(new CrateReward(RewardItemType.COSMETIC, "death_firework", 3.0));
        crates.put(CrateType.LEGENDARY, legendaryCrate);
    }

    /**
     * Give crate to player
     */
    public void giveCrate(Player player, CrateType type, int amount) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.addCrate(type, amount);

        player.sendMessage(Component.text("+ " + amount + " ").color(NamedTextColor.GREEN)
                .append(Component.text(crates.get(type).getName()).color(NamedTextColor.YELLOW))
                .append(Component.text("!").color(NamedTextColor.GREEN)));
    }

    /**
     * Purchase crate with coins
     */
    public boolean purchaseCrate(Player player, CrateType type) {
        CrateDefinition crate = crates.get(type);
        if (crate == null) return false;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.getCoins() < crate.getPrice()) {
            player.sendMessage(Component.text("Not enough coins! Need " + crate.getPrice() + " coins.")
                    .color(NamedTextColor.RED));
            return false;
        }

        data.removeCoins(crate.getPrice());
        data.addCrate(type, 1);

        player.sendMessage(Component.text("✓ Purchased 1 ").color(NamedTextColor.GREEN)
                .append(Component.text(crate.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" for " + crate.getPrice() + " coins!").color(NamedTextColor.GREEN)));

        return true;
    }

    /**
     * Open a crate
     */
    public void openCrate(Player player, CrateType type) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.getCrateCount(type) <= 0) {
            player.sendMessage(Component.text("You don't have any of these crates!")
                    .color(NamedTextColor.RED));
            return;
        }

        // Remove crate
        data.removeCrate(type, 1);

        // Generate reward
        CrateDefinition crate = crates.get(type);
        CrateReward reward = crate.generateReward();

        new CrateOpeningAnimation(plugin, player, crate, reward).start();
    }

    /**
     * Give reward to player
     */
    public void giveReward(Player player, @NotNull CrateReward reward) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        switch (reward.getType()) {
            case COINS -> {
                data.addCoins(reward.getAmount());
                player.sendMessage(Component.text("+ " + reward.getAmount() + " coins!")
                        .color(NamedTextColor.GOLD));
            }
            case BP_XP -> {
                plugin.getBattlePassManager().addXP(player, reward.getAmount(), "Crate Reward");
            }
            case COSMETIC -> {
                String cosmeticId = reward.getCosmeticId();
                if (!data.hasCosmetic(cosmeticId)) {
                    data.addCosmetic(cosmeticId);
                    assert plugin.getCosmeticManager() != null;
                    Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
                    if (cosmetic != null) {
                        player.sendMessage(Component.text("+ New cosmetic: ").color(NamedTextColor.GREEN)
                                .append(Component.text(cosmetic.name()).color(NamedTextColor.YELLOW)));
                    }
                } else {
                    // Duplicate - give coins instead
                    int coinValue = 100;
                    data.addCoins(coinValue);
                    player.sendMessage(Component.text("Duplicate! + " + coinValue + " coins instead")
                            .color(NamedTextColor.GOLD));
                }
            }
        }
    }

    public CrateDefinition getCrate(CrateType type) {
        return crates.get(type);
    }
}