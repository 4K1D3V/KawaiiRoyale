package dev.oumaimaa.plugin.manager;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.CosmeticRarity;
import dev.oumaimaa.plugin.constant.CosmeticType;
import dev.oumaimaa.plugin.record.Cosmetic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cosmetic items
 */
public class CosmeticManager {

    private final Main plugin;
    private final Map<String, Cosmetic> cosmetics;
    private final Map<UUID, Map<CosmeticType, String>> equippedCosmetics;

    public CosmeticManager(Main plugin) {
        this.plugin = plugin;
        this.cosmetics = new LinkedHashMap<>();
        this.equippedCosmetics = new ConcurrentHashMap<>();

        registerCosmetics();
    }

    /**
     * Register all cosmetics
     */
    private void registerCosmetics() {
        // Kill Messages
        register(new Cosmetic(
                "kill_msg_default",
                "Default Kill Message",
                "Standard elimination message",
                CosmeticType.KILL_MESSAGE,
                CosmeticRarity.COMMON,
                0,
                "<red>{victim} was eliminated by {killer}",
                Material.PAPER
        ));

        register(new Cosmetic(
                "kill_msg_destroyed",
                "Destroyed",
                "{victim} was destroyed by {killer}",
                CosmeticType.KILL_MESSAGE,
                CosmeticRarity.RARE,
                500,
                "<dark_red>{victim} was destroyed by {killer}!",
                Material.PAPER
        ));

        register(new Cosmetic(
                "kill_msg_obliterated",
                "Obliterated",
                "{victim} was obliterated by {killer}",
                CosmeticType.KILL_MESSAGE,
                CosmeticRarity.EPIC,
                1000,
                "<gradient:#ff0000:#ff6666>{victim} was obliterated by {killer}!</gradient>",
                Material.PAPER
        ));

        register(new Cosmetic(
                "kill_msg_legendary",
                "Legendary Elimination",
                "Legendary kill message",
                CosmeticType.KILL_MESSAGE,
                CosmeticRarity.LEGENDARY,
                2500,
                "<gradient:#ffd700:#ffed4e>⚡ {killer} has DESTROYED {victim}! ⚡</gradient>",
                Material.ENCHANTED_BOOK
        ));

        // Victory Dances (Titles)
        register(new Cosmetic(
                "victory_default",
                "Classic Victory",
                "Standard victory display",
                CosmeticType.VICTORY_DANCE,
                CosmeticRarity.COMMON,
                0,
                "<gradient:#ffd700:#ffed4e>VICTORY ROYALE!</gradient>",
                Material.GOLDEN_APPLE
        ));

        register(new Cosmetic(
                "victory_epic",
                "Epic Victory",
                "Epic win celebration",
                CosmeticType.VICTORY_DANCE,
                CosmeticRarity.EPIC,
                1500,
                "<gradient:#ff69b4:#ff1493>✨ EPIC VICTORY! ✨</gradient>",
                Material.ENCHANTED_GOLDEN_APPLE
        ));

        register(new Cosmetic(
                "victory_legendary",
                "Legendary Win",
                "Ultimate victory celebration",
                CosmeticType.VICTORY_DANCE,
                CosmeticRarity.LEGENDARY,
                3000,
                "<gradient:#00ff00:#00ffff>🌟 LEGENDARY CHAMPION! 🌟</gradient>",
                Material.NETHER_STAR
        ));

        // Parachute Colors
        register(new Cosmetic(
                "parachute_white",
                "White Parachute",
                "Classic white elytra",
                CosmeticType.PARACHUTE,
                CosmeticRarity.COMMON,
                0,
                "WHITE",
                Material.ELYTRA
        ));

        register(new Cosmetic(
                "parachute_red",
                "Red Parachute",
                "Fiery red elytra",
                CosmeticType.PARACHUTE,
                CosmeticRarity.RARE,
                750,
                "RED",
                Material.ELYTRA
        ));

        register(new Cosmetic(
                "parachute_rainbow",
                "Rainbow Parachute",
                "Magical rainbow trail",
                CosmeticType.PARACHUTE,
                CosmeticRarity.LEGENDARY,
                5000,
                "RAINBOW",
                Material.ELYTRA
        ));

        // Death Effects
        register(new Cosmetic(
                "death_lightning",
                "Lightning Strike",
                "Lightning on elimination",
                CosmeticType.DEATH_EFFECT,
                CosmeticRarity.EPIC,
                2000,
                "LIGHTNING",
                Material.LIGHTNING_ROD
        ));

        register(new Cosmetic(
                "death_explosion",
                "Explosion",
                "Explosion effect on death",
                CosmeticType.DEATH_EFFECT,
                CosmeticRarity.RARE,
                1000,
                "EXPLOSION",
                Material.TNT
        ));

        register(new Cosmetic(
                "death_firework",
                "Firework Show",
                "Colorful fireworks",
                CosmeticType.DEATH_EFFECT,
                CosmeticRarity.LEGENDARY,
                3500,
                "FIREWORK",
                Material.FIREWORK_ROCKET
        ));

        // Player Titles
        register(new Cosmetic(
                "title_warrior",
                "Warrior",
                "Title: Warrior",
                CosmeticType.TITLE,
                CosmeticRarity.RARE,
                800,
                "§c⚔ Warrior",
                Material.IRON_SWORD
        ));

        register(new Cosmetic(
                "title_legend",
                "Legend",
                "Title: Legend",
                CosmeticType.TITLE,
                CosmeticRarity.EPIC,
                1500,
                "§6★ Legend",
                Material.DIAMOND_SWORD
        ));

        register(new Cosmetic(
                "title_champion",
                "Champion",
                "Title: Champion",
                CosmeticType.TITLE,
                CosmeticRarity.LEGENDARY,
                3000,
                "§e♔ Champion",
                Material.NETHERITE_SWORD
        ));
    }

    /**
     * Register a cosmetic
     */
    public void register(Cosmetic cosmetic) {
        cosmetics.put(cosmetic.id(), cosmetic);
    }

    /**
     * Purchase cosmetic
     */
    public boolean purchaseCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = cosmetics.get(cosmeticId);
        if (cosmetic == null) {
            player.sendMessage(Component.text("Cosmetic not found!").color(NamedTextColor.RED));
            return false;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Check if already owned
        if (data.hasCosmetic(cosmeticId)) {
            player.sendMessage(Component.text("You already own this cosmetic!").color(NamedTextColor.RED));
            return false;
        }

        // Check price
        if (data.getCoins() < cosmetic.price()) {
            player.sendMessage(Component.text("Not enough coins! Need " + cosmetic.price() + " coins.")
                    .color(NamedTextColor.RED));
            return false;
        }

        // Purchase
        data.removeCoins(cosmetic.price());
        data.addCosmetic(cosmeticId);

        player.sendMessage(Component.text("✓ Purchased ").color(NamedTextColor.GREEN)
                .append(Component.text(cosmetic.name()).color(NamedTextColor.YELLOW))
                .append(Component.text(" for " + cosmetic.price() + " coins!").color(NamedTextColor.GREEN)));

        return true;
    }

    /**
     * Equip cosmetic
     */
    public boolean equipCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = cosmetics.get(cosmeticId);
        if (cosmetic == null) return false;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Check ownership
        if (!data.hasCosmetic(cosmeticId) && cosmetic.price() > 0) {
            player.sendMessage(Component.text("You don't own this cosmetic!").color(NamedTextColor.RED));
            return false;
        }

        // Equip
        equippedCosmetics.computeIfAbsent(player.getUniqueId(), k -> new EnumMap<>(CosmeticType.class))
                .put(cosmetic.type(), cosmeticId);
        data.equipCosmetic(cosmetic.type(), cosmeticId);

        player.sendMessage(Component.text("✓ Equipped ").color(NamedTextColor.GREEN)
                .append(Component.text(cosmetic.name()).color(NamedTextColor.YELLOW)));

        return true;
    }

    /**
     * Get equipped cosmetic
     */
    public String getEquippedCosmetic(@NotNull Player player, CosmeticType type) {
        Map<CosmeticType, String> equipped = equippedCosmetics.get(player.getUniqueId());
        if (equipped == null) return null;
        return equipped.get(type);
    }

    /**
     * Get cosmetic by ID
     */
    public Cosmetic getCosmetic(String id) {
        return cosmetics.get(id);
    }

    /**
     * Get all cosmetics
     */
    public Collection<Cosmetic> getAllCosmetics() {
        return Collections.unmodifiableCollection(cosmetics.values());
    }

    /**
     * Get cosmetics by type
     */
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return cosmetics.values().stream()
                .filter(c -> c.type() == type)
                .toList();
    }

    /**
     * Load player cosmetics
     */
    public void loadPlayerCosmetics(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        equippedCosmetics.put(player.getUniqueId(), new EnumMap<>(data.getEquippedCosmetics()));
    }

    /**
     * Unload player cosmetics
     */
    public void unloadPlayerCosmetics(UUID uuid) {
        equippedCosmetics.remove(uuid);
    }
}