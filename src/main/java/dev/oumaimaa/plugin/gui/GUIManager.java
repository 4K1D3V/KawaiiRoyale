package dev.oumaimaa.plugin.gui;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all GUI menus including Phase 3
 */
public class GUIManager implements Listener {

    private final Main plugin;
    private final Map<UUID, Object> openGUIs;

    public GUIManager(@NotNull Main plugin) {
        this.plugin = plugin;
        this.openGUIs = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openMainMenu(Player player) {
        MainMenuGUI gui = new MainMenuGUI(plugin);
        gui.open(player);
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openStatsGUI(Player player, Player target) {
        StatsGUI gui = new StatsGUI(plugin, target);
        gui.open(player);
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openLeaderboardGUI(Player player, String type) {
        LeaderboardGUI gui = new LeaderboardGUI(plugin, type);
        gui.open(player);
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openCosmeticsShop(Player player, CosmeticType type) {
        CosmeticsShopGUI gui = new CosmeticsShopGUI(plugin, player, type);
        gui.open();
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openBattlePass(Player player, int startTier) {
        BattlePassGUI gui = new BattlePassGUI(plugin, player, startTier);
        gui.open();
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openChallenges(Player player) {
        ChallengesGUI gui = new ChallengesGUI(plugin, player);
        gui.open();
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openCrates(Player player) {
        CratesGUI gui = new CratesGUI(plugin, player);
        gui.open();
        openGUIs.put(player.getUniqueId(), gui);
    }

    public void openCrateShop(Player player) {
        CrateShopGUI gui = new CrateShopGUI(plugin, player);
        gui.open();
        openGUIs.put(player.getUniqueId(), gui);
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Object gui = openGUIs.get(player.getUniqueId());
        if (gui == null) return;

        switch (gui) {
            case MainMenuGUI mainMenuGUI when event.getInventory().equals(mainMenuGUI.getInventory()) -> {
                event.setCancelled(true);
                mainMenuGUI.handleClick(event.getSlot(), player);
            }
            case StatsGUI statsGUI when event.getInventory().equals(statsGUI.getInventory()) -> {
                event.setCancelled(true);
                statsGUI.handleClick(event.getSlot(), player);
            }
            case LeaderboardGUI lbGUI when event.getInventory().equals(lbGUI.getInventory()) -> {
                event.setCancelled(true);
                lbGUI.handleClick(event.getSlot(), player);
            }
            case CosmeticsShopGUI cosmeticsGUI when event.getInventory().equals(cosmeticsGUI.getInventory()) -> {
                event.setCancelled(true);
                cosmeticsGUI.handleClick(event.getSlot());
            }
            case BattlePassGUI bpGUI when event.getInventory().equals(bpGUI.getInventory()) -> {
                event.setCancelled(true);
                bpGUI.handleClick(event.getSlot());
            }
            case ChallengesGUI challengesGUI when event.getInventory().equals(challengesGUI.getInventory()) ->
                    event.setCancelled(true);

            // Challenges GUI is view-only, no click handling needed
            case CratesGUI cratesGUI when event.getInventory().equals(cratesGUI.getInventory()) -> {
                event.setCancelled(true);
                cratesGUI.handleClick(event.getSlot());
            }
            case CrateShopGUI shopGUI when event.getInventory().equals(shopGUI.getInventory()) -> {
                event.setCancelled(true);
                shopGUI.handleClick(event.getSlot());
            }
            default -> {
            }
        }
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            // Keep GUI reference for 1 tick to allow for GUI switching
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> closeGUI(player), 1L);
        }
    }

    public void closeGUI(@NotNull Player player) {
        openGUIs.remove(player.getUniqueId());
    }
}