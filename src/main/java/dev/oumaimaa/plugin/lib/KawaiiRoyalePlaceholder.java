package dev.oumaimaa.plugin.lib;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.config.playerdata.PlayerStatistics;
import dev.oumaimaa.plugin.config.playerdata.database.DatabaseHandler;
import dev.oumaimaa.plugin.skeleton.Game;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * PlaceholderAPI expansion for KawaiiRoyale
 */
public class KawaiiRoyalePlaceholder extends PlaceholderExpansion {

    private final Main plugin;

    public KawaiiRoyalePlaceholder(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kawaiiroyale";
    }

    @Override
    public @NotNull String getAuthor() {
        return "oumaimaa";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.1.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        PlayerStatistics stats = data.getStatistics();

        return switch (params.toLowerCase()) {
            case "kills" -> String.valueOf(stats.getKills());
            case "deaths" -> String.valueOf(stats.getDeaths());
            case "wins" -> String.valueOf(stats.getWins());
            case "games", "games_played" -> String.valueOf(stats.getGamesPlayed());
            case "kd", "kd_ratio" -> String.format("%.2f", stats.getKDRatio());
            case "winrate", "win_rate" -> String.format("%.1f", stats.getWinRate());
            case "damage_dealt" -> String.format("%.0f", stats.getDamageDealt());
            case "damage_taken" -> String.format("%.0f", stats.getDamageTaken());
            case "killstreak", "longest_killstreak" -> String.valueOf(stats.getLongestKillStreak());
            case "playtime" -> formatPlaytime(stats.getTotalPlaytime());
            case "ingame" -> plugin.getGameManager().isInGame(player) ? "Yes" : "No";
            case "current_game" -> {
                Game game = plugin.getGameManager().getPlayerGame(player);
                yield game != null ? game.getId().toString().substring(0, 8) : "None";
            }
            case "current_players" -> {
                Game game = plugin.getGameManager().getPlayerGame(player);
                yield game != null ? String.valueOf(game.getAllPlayers().size()) : "0";
            }
            case "current_alive" -> {
                Game game = plugin.getGameManager().getPlayerGame(player);
                yield game != null ? String.valueOf(game.getAlivePlayers().size()) : "0";
            }
            case "current_kills" -> {
                Game game = plugin.getGameManager().getPlayerGame(player);
                if (game != null) {
                    yield String.valueOf(game.getKills(player.getUniqueId()));
                }
                yield "0";
            }
            case "rank_kills" -> getPlayerRank(player, "kills");
            case "rank_wins" -> getPlayerRank(player, "wins");
            case "rank_kd" -> getPlayerRank(player, "kd");
            default -> {
                if (params.startsWith("top_kills_")) {
                    int position = getPosition(params, "top_kills_");
                    yield getTopPlayer("kills", position);
                } else if (params.startsWith("top_wins_")) {
                    int position = getPosition(params, "top_wins_");
                    yield getTopPlayer("wins", position);
                } else if (params.startsWith("top_kd_")) {
                    int position = getPosition(params, "top_kd_");
                    yield getTopPlayer("kd", position);
                } else if (params.startsWith("top_kills_value_")) {
                    int position = getPosition(params, "top_kills_value_");
                    yield getTopPlayerValue("kills", position);
                } else if (params.startsWith("top_wins_value_")) {
                    int position = getPosition(params, "top_wins_value_");
                    yield getTopPlayerValue("wins", position);
                } else if (params.startsWith("top_kd_value_")) {
                    int position = getPosition(params, "top_kd_value_");
                    yield getTopPlayerValue("kd", position);
                }
                yield null;
            }
        };
    }

    @Contract(pure = true)
    private @NotNull String formatPlaytime(long milliseconds) {
        long hours = milliseconds / 3600000;
        long minutes = (milliseconds % 3600000) / 60000;
        return hours + "h " + minutes + "m";
    }

    private int getPosition(@NotNull String params, @NotNull String prefix) {
        try {
            return Integer.parseInt(params.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String getTopPlayer(String stat, int position) {
        CompletableFuture<List<DatabaseHandler.LeaderboardEntry>> future =
                plugin.getPlayerDataManager().getTopPlayers(stat, position);

        try {
            List<DatabaseHandler.LeaderboardEntry> entries = future.get();
            if (position > 0 && position <= entries.size()) {
                return entries.get(position - 1).name();
            }
        } catch (Exception e) {
            plugin.logWarning("Failed to get top player: " + e.getMessage());
        }

        return "N/A";
    }

    private @NotNull String getTopPlayerValue(String stat, int position) {
        CompletableFuture<List<DatabaseHandler.LeaderboardEntry>> future =
                plugin.getPlayerDataManager().getTopPlayers(stat, position);

        try {
            List<DatabaseHandler.LeaderboardEntry> entries = future.get();
            if (position > 0 && position <= entries.size()) {
                return String.valueOf(entries.get(position - 1).value());
            }
        } catch (Exception e) {
            plugin.logWarning("Failed to get top player value: " + e.getMessage());
        }

        return "0";
    }

    private @NotNull String getPlayerRank(Player player, String stat) {
        CompletableFuture<List<DatabaseHandler.LeaderboardEntry>> future =
                plugin.getPlayerDataManager().getTopPlayers(stat, 1000);

        try {
            List<DatabaseHandler.LeaderboardEntry> entries = future.get();
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).uuid().equals(player.getUniqueId())) {
                    return String.valueOf(i + 1);
                }
            }
        } catch (Exception e) {
            plugin.logWarning("Failed to get player rank: " + e.getMessage());
        }

        return "Unranked";
    }

    /**
     * Register this expansion
     *
     * @return false
     */
    public boolean register() {
        if (!super.register()) {
            plugin.logWarning("Failed to register PlaceholderAPI expansion!");
        } else {
            plugin.logInfo("PlaceholderAPI expansion registered successfully!");
        }
        return false;
    }
}