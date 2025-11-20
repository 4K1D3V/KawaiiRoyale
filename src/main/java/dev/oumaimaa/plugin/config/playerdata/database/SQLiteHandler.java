package dev.oumaimaa.plugin.config.playerdata.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.config.playerdata.PlayerStatistics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * SQLite database handler
 */
public class SQLiteHandler implements DatabaseHandler {

    private final Main plugin;
    private HikariDataSource dataSource;

    public SQLiteHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Boolean> initialize() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/data.db");
                config.setMaximumPoolSize(10);
                config.setConnectionTimeout(30000);

                dataSource = new HikariDataSource(config);

                createTables();

                plugin.logInfo("SQLite database initialized successfully!");
                return true;
            } catch (Exception e) {
                plugin.logSevere("Failed to initialize SQLite database: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }

    private void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS player_stats (
                            uuid TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            kills INTEGER DEFAULT 0,
                            deaths INTEGER DEFAULT 0,
                            wins INTEGER DEFAULT 0,
                            games_played INTEGER DEFAULT 0,
                            damage_dealt REAL DEFAULT 0,
                            damage_taken REAL DEFAULT 0,
                            longest_kill_streak INTEGER DEFAULT 0,
                            total_playtime INTEGER DEFAULT 0,
                            last_seen INTEGER DEFAULT 0
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS mode_stats (
                            uuid TEXT NOT NULL,
                            mode TEXT NOT NULL,
                            wins INTEGER DEFAULT 0,
                            games_played INTEGER DEFAULT 0,
                            PRIMARY KEY (uuid, mode),
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid)
                        )
                    """);

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_kills ON player_stats(kills DESC)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_wins ON player_stats(wins DESC)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_kd ON player_stats((kills * 1.0 / NULLIF(deaths, 0)) DESC)");
        }
    }

    @Override
    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT * FROM player_stats WHERE uuid = ?")) {

                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();

                PlayerData data = new PlayerData(uuid);

                if (rs.next()) {
                    PlayerStatistics stats = data.getStatistics();
                    stats.addKill(rs.getInt("kills"));
                    stats.addDeath(rs.getInt("deaths"));
                    stats.addWin(rs.getInt("wins"));
                    stats.addGame(rs.getInt("games_played"));
                    stats.setDamageDealt(rs.getDouble("damage_dealt"));
                    stats.setDamageTaken(rs.getDouble("damage_taken"));
                    stats.setLongestKillStreak(rs.getInt("longest_kill_streak"));
                    stats.setTotalPlaytime(rs.getLong("total_playtime"));
                }

                return data;
            } catch (SQLException e) {
                plugin.logSevere("Failed to load player data: " + e.getMessage());
                return new PlayerData(uuid);
            }
        });
    }

    @Override
    public void savePlayerData(PlayerData data) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("""
                             INSERT INTO player_stats (uuid, name, kills, deaths, wins, games_played,
                                 damage_dealt, damage_taken, longest_kill_streak, total_playtime, last_seen)
                             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                             ON CONFLICT(uuid) DO UPDATE SET
                                 name = excluded.name,
                                 kills = excluded.kills,
                                 deaths = excluded.deaths,
                                 wins = excluded.wins,
                                 games_played = excluded.games_played,
                                 damage_dealt = excluded.damage_dealt,
                                 damage_taken = excluded.damage_taken,
                                 longest_kill_streak = excluded.longest_kill_streak,
                                 total_playtime = excluded.total_playtime,
                                 last_seen = excluded.last_seen
                         """)) {

                PlayerStatistics stats = data.getStatistics();

                stmt.setString(1, data.getUuid().toString());
                stmt.setString(2, data.getName());
                stmt.setInt(3, stats.getKills());
                stmt.setInt(4, stats.getDeaths());
                stmt.setInt(5, stats.getWins());
                stmt.setInt(6, stats.getGamesPlayed());
                stmt.setDouble(7, stats.getDamageDealt());
                stmt.setDouble(8, stats.getDamageTaken());
                stmt.setInt(9, stats.getLongestKillStreak());
                stmt.setLong(10, stats.getTotalPlaytime());
                stmt.setLong(11, System.currentTimeMillis());

                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                plugin.logSevere("Failed to save player data: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<List<LeaderboardEntry>> getTopPlayers(String stat, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<LeaderboardEntry> entries = new ArrayList<>();

            String column = switch (stat.toLowerCase()) {
                case "wins" -> "wins";
                case "kd" -> "(kills * 1.0 / NULLIF(deaths, 0))";
                case "games" -> "games_played";
                default -> "kills";
            };

            String query = "SELECT uuid, name, " + column + " as value " +
                    "FROM player_stats ORDER BY value DESC LIMIT ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    entries.add(new LeaderboardEntry(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getString("name"),
                            rs.getInt("value")
                    ));
                }
            } catch (SQLException e) {
                plugin.logSevere("Failed to get top players: " + e.getMessage());
            }

            return entries;
        });
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.logInfo("SQLite database connection closed.");
        }
    }
}
