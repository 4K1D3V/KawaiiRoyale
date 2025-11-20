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
 * MySQL database handler
 */
public class MySQLHandler implements DatabaseHandler {

    private final Main plugin;
    private HikariDataSource dataSource;

    public MySQLHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Boolean> initialize() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:mysql://" +
                        plugin.getConfigManager().getDatabaseHost() + ":" +
                        plugin.getConfigManager().getDatabasePort() + "/" +
                        plugin.getConfigManager().getDatabaseName() +
                        "?useSSL=false&autoReconnect=true");
                config.setUsername(plugin.getConfigManager().getDatabaseUser());
                config.setPassword(plugin.getConfigManager().getDatabasePassword());
                config.setMaximumPoolSize(plugin.getConfigManager().getMainConfig().getInt("database.pool-size", 10));
                config.setConnectionTimeout(30000);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                dataSource = new HikariDataSource(config);

                createTables();

                plugin.logInfo("MySQL database initialized successfully!");
                return true;
            } catch (Exception e) {
                plugin.logSevere("Failed to initialize MySQL database: " + e.getMessage());
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
                            uuid VARCHAR(36) PRIMARY KEY,
                            name VARCHAR(16) NOT NULL,
                            kills INT DEFAULT 0,
                            deaths INT DEFAULT 0,
                            wins INT DEFAULT 0,
                            games_played INT DEFAULT 0,
                            damage_dealt DOUBLE DEFAULT 0,
                            damage_taken DOUBLE DEFAULT 0,
                            longest_kill_streak INT DEFAULT 0,
                            total_playtime BIGINT DEFAULT 0,
                            last_seen BIGINT DEFAULT 0,
                            INDEX idx_kills (kills DESC),
                            INDEX idx_wins (wins DESC)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS mode_stats (
                            uuid VARCHAR(36) NOT NULL,
                            mode VARCHAR(20) NOT NULL,
                            wins INT DEFAULT 0,
                            games_played INT DEFAULT 0,
                            PRIMARY KEY (uuid, mode),
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);
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
                             ON DUPLICATE KEY UPDATE
                                 name = VALUES(name),
                                 kills = VALUES(kills),
                                 deaths = VALUES(deaths),
                                 wins = VALUES(wins),
                                 games_played = VALUES(games_played),
                                 damage_dealt = VALUES(damage_dealt),
                                 damage_taken = VALUES(damage_taken),
                                 longest_kill_streak = VALUES(longest_kill_streak),
                                 total_playtime = VALUES(total_playtime),
                                 last_seen = VALUES(last_seen)
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
                case "kd" -> "(kills / NULLIF(deaths, 0))";
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
            plugin.logInfo("MySQL database connection closed.");
        }
    }
}