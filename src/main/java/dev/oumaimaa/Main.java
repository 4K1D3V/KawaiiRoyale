package dev.oumaimaa;

import dev.oumaimaa.plugin.listener.AchievementListener;
import dev.oumaimaa.plugin.manager.AchievementManager;
import dev.oumaimaa.plugin.lib.KawaiiRoyalePlaceholder;
import dev.oumaimaa.plugin.manager.ArenaManager;
import dev.oumaimaa.plugin.manager.BattlePassManager;
import dev.oumaimaa.plugin.manager.ChallengeManager;
import dev.oumaimaa.plugin.command.CommandManager;
import dev.oumaimaa.plugin.config.ConfigManager;
import dev.oumaimaa.plugin.manager.CosmeticManager;
import dev.oumaimaa.plugin.manager.CrateManager;
import dev.oumaimaa.plugin.manager.DisplayManager;
import dev.oumaimaa.plugin.manager.GameManager;
import dev.oumaimaa.plugin.gui.GUIManager;
import dev.oumaimaa.plugin.listener.ListenerManager;
import dev.oumaimaa.plugin.manager.LootManager;
import dev.oumaimaa.plugin.config.playerdata.PlayerDataManager;
import dev.oumaimaa.plugin.manager.QueueManager;
import dev.oumaimaa.plugin.manager.ZoneManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Level;

/**
 * KawaiiRoyale - Advanced Battle Royale Plugin
 *
 * @author oumaimaa
 * @version 1.0.1.1
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
    private GUIManager guiManager;
    private DisplayManager displayManager;
    private AchievementManager achievementManager;
    private CosmeticManager cosmeticManager;
    private BattlePassManager battlePassManager;
    private ChallengeManager challengeManager;
    private CrateManager crateManager;
    private MiniMessage miniMessage;
    private KawaiiRoyalePlaceholder placeholderExpansion;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

        logInfo("╔══════════════════════════════════════╗");
        logInfo("║      KawaiiRoyale - Starting...      ║");
        logInfo("╚══════════════════════════════════════╝");

        this.miniMessage = MiniMessage.miniMessage();

        if (!initializeManagers()) {
            getLogger().severe("Failed to initialize managers! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerPlaceholders();

        long loadTime = System.currentTimeMillis() - startTime;
        logInfo("╔══════════════════════════════════════╗");
        logInfo("║   KawaiiRoyale Enabled! (" + loadTime + "ms)    ║");
        logInfo("║      Ready for epic battles!         ║");
        logInfo("╚══════════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        logInfo("Shutting down KawaiiRoyale...");

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

        if (listenerManager != null) {
            listenerManager.shutdown();
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

        logInfo("KawaiiRoyale has been disabled. Thanks for playing!");
    }

    /**
     * Initialize all managers
     *
     * @return true if successful, false otherwise
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
     * Register PlaceholderAPI expansion if available
     */
    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logInfo("Hooking into PlaceholderAPI...");
            this.placeholderExpansion = new KawaiiRoyalePlaceholder(this);
            placeholderExpansion.register();
            logInfo("PlaceholderAPI integration enabled!");
        }
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

    public GUIManager getGuiManager() {
        return guiManager;
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

}