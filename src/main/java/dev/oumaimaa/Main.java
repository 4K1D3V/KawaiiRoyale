package dev.oumaimaa;

import com.github.retrooper.packetevents.PacketEvents;
import dev.oumaimaa.api.API;
import dev.oumaimaa.plugin.command.CommandManager;
import dev.oumaimaa.plugin.config.ConfigManager;
import dev.oumaimaa.plugin.config.playerdata.PlayerDataManager;
import dev.oumaimaa.plugin.gui.GUIManager;
import dev.oumaimaa.plugin.lib.KawaiiRoyalePlaceholder;
import dev.oumaimaa.plugin.listener.ListenerManager;
import dev.oumaimaa.plugin.listener.packet.PacketListenerManager;
import dev.oumaimaa.plugin.manager.*;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * KawaiiRoyale Main Plugin Class
 * Advanced Battle Royale with PacketEvents optimization
 *
 * @author oumaimaa
 * @version 2.0.0
 */
public final class Main extends JavaPlugin {

    private static Main instance;

    private ConfigManager configManager;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private QueueManager queueManager;
    private PlayerDataManager playerDataManager;
    private ZoneManager zoneManager;
    private LootManager lootManager;
    private CommandManager commandManager;
    private ListenerManager listenerManager;
    private PacketListenerManager packetListenerManager;
    private DisplayManager displayManager;
    private AchievementManager achievementManager;
    private CosmeticManager cosmeticManager;
    private BattlePassManager battlePassManager;
    private ChallengeManager challengeManager;
    private CrateManager crateManager;
    private VaultManager vaultManager;
    private MiniMessage miniMessage;
    private KawaiiRoyalePlaceholder placeholderExpansion;
    private GUIManager guiManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

        logBanner();

        PacketEvents.getAPI().init();

        this.miniMessage = MiniMessage.miniMessage();

        if (!initializeManagers()) {
            getLogger().severe("Critical failure during initialization! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        API.initialize(this);
        registerPlaceholders();

        long loadTime = System.currentTimeMillis() - startTime;
        logSuccess(loadTime);
    }

    @Override
    public void onDisable() {
        logInfo("Shutting down KawaiiRoyale...");

        PacketEvents.getAPI().terminate();

        shutdownManagers();

        logInfo("KawaiiRoyale disabled successfully. Goodbye!");
    }

    /**
     * Initialize all managers in correct order
     */
    private boolean initializeManagers() {
        try {

            logInfo("Loading configuration...");

            this.configManager = new ConfigManager(this);
            if (!configManager.load()) {
                logSevere("Failed to load configuration!");
                return false;
            }

            logInfo("Initializing player data system...");
            this.playerDataManager = new PlayerDataManager(this);

            logInfo("Initializing arena system...");
            this.arenaManager = new ArenaManager(this);

            logInfo("Initializing zone management...");
            this.zoneManager = new ZoneManager(this);

            logInfo("Initializing loot system...");
            this.lootManager = new LootManager(this);

            logInfo("Initializing queue system...");
            this.queueManager = new QueueManager(this);

            logInfo("Initializing game management...");
            this.gameManager = new GameManager(this);

            logInfo("Initializing GUI system...");
            this.guiManager = new GUIManager(this);

            logInfo("Initializing display system...");
            this.displayManager = new DisplayManager(this);

            logInfo("Initializing achievement system...");
            this.achievementManager = new AchievementManager(this);

            logInfo("Initializing cosmetic system...");
            this.cosmeticManager = new CosmeticManager(this);

            logInfo("Initializing battle pass...");
            this.battlePassManager = new BattlePassManager(this);

            logInfo("Initializing daily challenges...");
            this.challengeManager = new ChallengeManager(this);

            logInfo("Initializing reward crates...");
            this.crateManager = new CrateManager(this);

            logInfo("Registering commands...");
            this.commandManager = new CommandManager(this);
            commandManager.registerCommands();

            logInfo("Registering event listeners...");
            this.listenerManager = new ListenerManager(this);
            listenerManager.registerListeners();

            return true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during manager initialization", e);
            return false;
        }
    }

    /**
     * Shutdown all managers in reverse order
     */
    private void shutdownManagers() {
        if (packetListenerManager != null) {
            logInfo("Unregistering packet listeners...");
            packetListenerManager.shutdown();
        }

        if (listenerManager != null) {
            logInfo("Unregistering event listeners...");
            listenerManager.shutdown();
        }

        if (crateManager != null) {
            logInfo("Shutting down crate manager...");
        }

        if (challengeManager != null) {
            logInfo("Shutting down challenge manager...");
        }

        if (battlePassManager != null) {
            logInfo("Shutting down battle pass...");
        }

        if (cosmeticManager != null) {
            logInfo("Shutting down cosmetic manager...");
        }

        if (achievementManager != null) {
            logInfo("Shutting down achievement manager...");
        }

        if (displayManager != null) {
            displayManager.shutdown();
        }

        if (gameManager != null) {
            gameManager.shutdown();
        }

        if (queueManager != null) {
            queueManager.shutdown();
        }

        if (playerDataManager != null) {
            playerDataManager.shutdown();
        }

        if (arenaManager != null) {
            arenaManager.shutdown();
        }
    }

    /**
     * Register PlaceholderAPI if available
     */
    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logInfo("Hooking into PlaceholderAPI...");
            this.placeholderExpansion = new KawaiiRoyalePlaceholder(this);
            if (placeholderExpansion.register()) {
                logInfo("PlaceholderAPI integration enabled!");
            }
        }
    }

    private void logBanner() {
        logInfo("╔══════════════════════════════════════╗");
        logInfo("║    KawaiiRoyale v2.0 - Starting...   ║");
        logInfo("║   PacketEvents • Vault • Advanced    ║");
        logInfo("╚══════════════════════════════════════╝");
    }

    private void logSuccess(long loadTime) {
        logInfo("╔══════════════════════════════════════╗");
        logInfo("║   KawaiiRoyale Enabled! (" + loadTime + "ms)    ║");
        logInfo("║     Ready for epic battles! 🎮       ║");
        logInfo("╚══════════════════════════════════════╝");
    }

    public void logInfo(String message) {
        getLogger().info(message);
    }

    public void logWarning(String message) {
        getLogger().warning(message);
    }

    public void logSevere(String message) {
        getLogger().severe(message);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public CosmeticManager getCosmeticManager() {
        return cosmeticManager;
    }

    public BattlePassManager getBattlePassManager() {
        return battlePassManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public PacketListenerManager getPacketListenerManager() {
        return packetListenerManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}