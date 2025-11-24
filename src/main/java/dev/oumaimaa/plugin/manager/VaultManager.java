package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Manages Vault economy integration
 */
public class VaultManager {

    private final Main plugin;
    private Economy economy;
    private final boolean enabled;

    public VaultManager(Main plugin) {
        this.plugin = plugin;
        this.enabled = setupEconomy();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.logWarning("Vault not found! Economy features disabled.");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null) {
            plugin.logWarning("No economy provider found! Economy features disabled.");
            return false;
        }

        economy = rsp.getProvider();
        plugin.logInfo("Vault economy integration enabled!");
        return true;
    }

    public boolean isEnabled() {
        return enabled && economy != null;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return enabled && economy.has(player, amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (!enabled) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        if (!enabled) return false;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(OfflinePlayer player) {
        return enabled ? economy.getBalance(player) : 0.0;
    }

    public String format(double amount) {
        return enabled ? economy.format(amount) : String.valueOf(amount);
    }
}