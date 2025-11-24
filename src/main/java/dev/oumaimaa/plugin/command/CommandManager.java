package dev.oumaimaa.plugin.command;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.constant.CosmeticType;
import dev.oumaimaa.plugin.constant.CrateType;
import dev.oumaimaa.plugin.constant.GameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Complete command manager with all phases
 */
public class CommandManager {

    private final Main plugin;

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        Objects.requireNonNull(plugin.getCommand("kawaii")).setExecutor(new MainCommand());
        Objects.requireNonNull(plugin.getCommand("battleroyale")).setExecutor(new JoinCommand(GameMode.BATTLE_ROYALE));
        Objects.requireNonNull(plugin.getCommand("resurgence")).setExecutor(new JoinCommand(GameMode.RESURGENCE));
        Objects.requireNonNull(plugin.getCommand("leave")).setExecutor(new LeaveCommand());
        Objects.requireNonNull(plugin.getCommand("stats")).setExecutor(new StatsCommand());
        Objects.requireNonNull(plugin.getCommand("leaderboard")).setExecutor(new LeaderboardCommand());
        Objects.requireNonNull(plugin.getCommand("achievements")).setExecutor(new AchievementsCommand());
        Objects.requireNonNull(plugin.getCommand("cosmetics")).setExecutor(new CosmeticsCommand());
        Objects.requireNonNull(plugin.getCommand("battlepass")).setExecutor(new BattlePassCommand());
        Objects.requireNonNull(plugin.getCommand("challenges")).setExecutor(new ChallengesCommand());
        Objects.requireNonNull(plugin.getCommand("crates")).setExecutor(new CratesCommand());
    }

    private static class AchievementsCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            // TODO: Open achievements GUI
            player.sendMessage(Component.text("Achievements GUI coming soon!")
                    .color(NamedTextColor.YELLOW));

            return true;
        }
    }

    private class MainCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            if (args.length > 0 && player.hasPermission("kawaii.admin")) {
                return handleAdminCommand(player, args);
            }

            plugin.getGuiManager().openMainMenu(player);
            return true;
        }

        private boolean handleAdminCommand(Player player, String @NotNull [] args) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    plugin.getConfigManager().reload();
                    player.sendMessage(Component.text("✓ Configuration reloaded!").color(NamedTextColor.GREEN));
                    return true;
                }
                case "coins" -> {
                    if (args.length < 4) {
                        player.sendMessage(Component.text("Usage: /kawaii coins <add|remove|set> <player> <amount>")
                                .color(NamedTextColor.RED));
                        return true;
                    }
                    return handleCoinsCommand(player, args);
                }
                case "achievement" -> {
                    if (args.length < 3) {
                        player.sendMessage(Component.text("Usage: /kawaii achievement give <player> <id>")
                                .color(NamedTextColor.RED));
                        return true;
                    }
                    return handleAchievementCommand(player, args);
                }
                case "cosmetic" -> {
                    if (args.length < 3) {
                        player.sendMessage(Component.text("Usage: /kawaii cosmetic give <player> <id>")
                                .color(NamedTextColor.RED));
                        return true;
                    }
                    return handleCosmeticCommand(player, args);
                }
                case "crate" -> {
                    if (args.length < 4) {
                        player.sendMessage(Component.text("Usage: /kawaii crate give <player> <type> [amount]")
                                .color(NamedTextColor.RED));
                        return true;
                    }
                    return handleCrateCommand(player, args);
                }
                case "bp", "battlepass" -> {
                    if (args.length < 3) {
                        player.sendMessage(Component.text("Usage: /kawaii bp <setxp|settier|givepremium> <player> [value]")
                                .color(NamedTextColor.RED));
                        return true;
                    }
                    return handleBattlePassCommand(player, args);
                }
                default -> {
                    player.sendMessage(Component.text("Unknown admin command!").color(NamedTextColor.RED));
                    return true;
                }
            }
        }

        private boolean handleCoinsCommand(Player sender, String @NotNull [] args) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid amount!").color(NamedTextColor.RED));
                return true;
            }

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);

            switch (args[1].toLowerCase()) {
                case "add" -> {
                    data.addCoins(amount);
                    sender.sendMessage(Component.text("✓ Added " + amount + " coins to " + target.getName())
                            .color(NamedTextColor.GREEN));
                }
                case "remove" -> {
                    data.removeCoins(amount);
                    sender.sendMessage(Component.text("✓ Removed " + amount + " coins from " + target.getName())
                            .color(NamedTextColor.GREEN));
                }
                case "set" -> {
                    data.setCoins(amount);
                    sender.sendMessage(Component.text("✓ Set " + target.getName() + "'s coins to " + amount)
                            .color(NamedTextColor.GREEN));
                }
            }

            target.sendMessage(Component.text("Your coins have been updated by an admin.")
                    .color(NamedTextColor.YELLOW));
            return true;
        }

        private boolean handleAchievementCommand(Player sender, String @NotNull [] args) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
                return true;
            }

            String achievementId = args.length > 3 ? args[3] : null;
            if (achievementId == null) {
                sender.sendMessage(Component.text("Please specify achievement ID!").color(NamedTextColor.RED));
                return true;
            }

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
            data.addAchievement(achievementId);

            sender.sendMessage(Component.text("✓ Gave achievement to " + target.getName())
                    .color(NamedTextColor.GREEN));
            return true;
        }

        private boolean handleCosmeticCommand(Player sender, String @NotNull [] args) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
                return true;
            }

            String cosmeticId = args.length > 3 ? args[3] : null;
            if (cosmeticId == null) {
                sender.sendMessage(Component.text("Please specify cosmetic ID!").color(NamedTextColor.RED));
                return true;
            }

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
            data.addCosmetic(cosmeticId);

            sender.sendMessage(Component.text("✓ Gave cosmetic to " + target.getName())
                    .color(NamedTextColor.GREEN));
            target.sendMessage(Component.text("You received a cosmetic from an admin!")
                    .color(NamedTextColor.GREEN));
            return true;
        }

        private boolean handleCrateCommand(Player sender, String @NotNull [] args) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
                return true;
            }

            CrateType type;
            try {
                type = CrateType.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text("Invalid crate type! Use: COMMON, RARE, EPIC, LEGENDARY")
                        .color(NamedTextColor.RED));
                return true;
            }

            int amount = args.length > 4 ? Integer.parseInt(args[4]) : 1;

            plugin.getCrateManager().giveCrate(target, type, amount);
            sender.sendMessage(Component.text("✓ Gave " + amount + " " + type.name() + " crate(s) to " + target.getName())
                    .color(NamedTextColor.GREEN));
            return true;
        }

        private boolean handleBattlePassCommand(Player sender, String @NotNull [] args) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
                return true;
            }

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);

            switch (args[1].toLowerCase()) {
                case "setxp" -> {
                    int xp = Integer.parseInt(args[3]);
                    data.setBattlePassXP(xp);
                    sender.sendMessage(Component.text("✓ Set " + target.getName() + "'s BP XP to " + xp)
                            .color(NamedTextColor.GREEN));
                }
                case "settier" -> {
                    int tier = Integer.parseInt(args[3]);
                    data.setBattlePassTier(tier);
                    sender.sendMessage(Component.text("✓ Set " + target.getName() + "'s BP tier to " + tier)
                            .color(NamedTextColor.GREEN));
                }
                case "givepremium" -> {
                    data.setPremiumBattlePass(true);
                    sender.sendMessage(Component.text("✓ Gave premium battle pass to " + target.getName())
                            .color(NamedTextColor.GREEN));
                    target.sendMessage(Component.text("✓ You received premium battle pass from an admin!")
                            .color(NamedTextColor.GREEN));
                }
            }

            return true;
        }
    }

    private class JoinCommand implements CommandExecutor {
        private final GameMode mode;

        JoinCommand(GameMode mode) {
            this.mode = mode;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            plugin.getQueueManager().joinQueue(player, mode);
            return true;
        }
    }

    private class LeaveCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            if (plugin.getGameManager().isInGame(player)) {
                var game = plugin.getGameManager().getPlayerGame(player);
                if (game != null) {
                    var gamePlayer = plugin.getGameManager().getGamePlayer(player);
                    game.removePlayer(gamePlayer);
                    player.sendMessage(Component.text("You left the game!")
                            .color(NamedTextColor.YELLOW));
                }
            } else {
                player.sendMessage(Component.text("You are not in a game!")
                        .color(NamedTextColor.RED));
            }

            return true;
        }
    }

    private class StatsCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            Player target = args.length > 0
                    ? plugin.getServer().getPlayer(args[0])
                    : player;

            if (target == null) {
                player.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
                return true;
            }

            plugin.getGuiManager().openStatsGUI(player, target);

            return true;
        }
    }

    private class LeaderboardCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            String type = args.length > 0 ? args[0] : "kills";
            plugin.getGuiManager().openLeaderboardGUI(player, type);

            return true;
        }
    }

    private class CosmeticsCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            CosmeticType type = CosmeticType.KILL_MESSAGE;
            if (args.length > 0) {
                try {
                    type = CosmeticType.valueOf(args[0].toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            plugin.getGuiManager().openCosmeticsShop(player, type);

            return true;
        }
    }

    private class BattlePassCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            int currentTier = data.getBattlePassTier();
            int startTier = Math.max(1, currentTier - 3);

            plugin.getGuiManager().openBattlePass(player, startTier);

            return true;
        }
    }

    private class ChallengesCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            plugin.getGuiManager().openChallenges(player);

            return true;
        }
    }

    private class CratesCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String @NotNull [] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("open")) {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /crate open <type>")
                            .color(NamedTextColor.RED));
                    return true;
                }

                try {
                    CrateType type = CrateType.valueOf(args[1].toUpperCase());
                    plugin.getCrateManager().openCrate(player, type);
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("Invalid crate type!")
                            .color(NamedTextColor.RED));
                }

                return true;
            }

            plugin.getGuiManager().openCrates(player);

            return true;
        }
    }
}