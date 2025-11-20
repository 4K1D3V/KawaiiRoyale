package dev.oumaimaa.plugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Base GUI interface
 */
public interface GUI {
    void open(Player player);

    void handleClick(int slot, Player player);

    Inventory getInventory();
}