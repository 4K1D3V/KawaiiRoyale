package dev.oumaimaa.plugin.config.playerdata.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import dev.oumaimaa.plugin.config.playerdata.PlayerStatistics;
import dev.oumaimaa.plugin.constant.CosmeticType;
import dev.oumaimaa.plugin.constant.CrateType;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * MySQL database handler with HikariCP connection pooling
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
                config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=%b&autoReconnect=true",
                        plugin.getConfigManager().getDatabaseHost(),
                        plugin.getConfigManager().getDatabasePort(),
                        plugin.getConfigManager().getDatabaseName(),
                        plugin.getConfigManager().getMainConfig().getBoolean("database.mysql.use-ssl", false)
                ));
                config.setUsername(plugin.getConfigManager().getDatabaseUser());
                config.setPassword(plugin.getConfigManager().getDatabasePassword());
                config.setMaximumPoolSize(plugin.getConfigManager().getMainConfig()
                        .getInt("performance.database.pool-size", 10));
                config.setConnectionTimeout(plugin.getConfigManager().getMainConfig()
                        .getLong("performance.database.connection-timeout", 30) * 1000);
                config.setValidationTimeout(plugin.getConfigManager().getMainConfig()
                        .getLong("performance.database.validation-timeout", 5) * 1000);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("useServerPrepStmts", "true");

                dataSource = new HikariDataSource(config);
                createTables();

                plugin.logInfo("MySQL database connected with " + config.getMaximumPoolSize() + " connections");
                return true;
            } catch (Exception e) {
                plugin.logSevere("Failed to initialize MySQL: " + e.getMessage());
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
                            headshots INT DEFAULT 0,
                            assists INT DEFAULT 0,
                            top3_finishes INT DEFAULT 0,
                            top10_finishes INT DEFAULT 0,
                            distance_traveled DOUBLE DEFAULT 0,
                            items_looted INT DEFAULT 0,
                            chests_opened INT DEFAULT 0,
                            fastest_win BIGINT DEFAULT 0,
                            most_kills_in_game INT DEFAULT 0,
                            coins INT DEFAULT 0,
                            total_playtime BIGINT DEFAULT 0,
                            last_seen BIGINT DEFAULT 0,
                            INDEX idx_kills (kills DESC),
                            INDEX idx_wins (wins DESC),
                            INDEX idx_kd ((kills / NULLIF(deaths, 1)) DESC)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS player_achievements (
                            uuid VARCHAR(36) NOT NULL,
                            achievement_id VARCHAR(64) NOT NULL,
                            unlocked_at BIGINT DEFAULT 0,
                            PRIMARY KEY (uuid, achievement_id),
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid) ON DELETE CASCADE
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS player_cosmetics (
                            uuid VARCHAR(36) NOT NULL,
                            cosmetic_id VARCHAR(64) NOT NULL,
                            purchased_at BIGINT DEFAULT 0,
                            PRIMARY KEY (uuid, cosmetic_id),
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid) ON DELETE CASCADE
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS player_equipped_cosmetics (
                            uuid VARCHAR(36) NOT NULL,
                            cosmetic_type VARCHAR(32) NOT NULL,
                            cosmetic_id VARCHAR(64) NOT NULL,
                            PRIMARY KEY (uuid, cosmetic_type),
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid) ON DELETE CASCADE
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS player_battle_pass (
                            uuid VARCHAR(36) PRIMARY KEY,
                            xp INT DEFAULT 0,
                            tier INT DEFAULT 0,
                            has_premium BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid) ON DELETE CASCADE
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS player_crates (
                            uuid VARCHAR(36) NOT NULL,
                            crate_type VARCHAR(32) NOT NULL,
                            amount INT DEFAULT 0,
                            PRIMARY KEY (uuid, crate_type),
                            FOREIGN KEY (uuid) REFERENCES player_stats(uuid) ON DELETE CASCADE
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);
        }
    }

    @Override
    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = new PlayerData(uuid);

            try (Connection conn = dataSource.getConnection()) {
                loadStats(conn, data);
                loadAchievements(conn, data);
                loadCosmetics(conn, data);
                loadEquippedCosmetics(conn, data);
                loadBattlePass(conn, data);
                loadCrates(conn, data);
            } catch (SQLException e) {
                plugin.logSevere("Failed to load player data: " + e.getMessage());
            }

            return data;
        });
    }

    private void loadStats(Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = "SELECT * FROM player_stats WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                data.setName(rs.getString("name"));
                PlayerStatistics stats = data.getStatistics();
                stats.setKills(rs.getInt("kills"));
                stats.setDeaths(rs.getInt("deaths"));
                stats.setWins(rs.getInt("wins"));
                stats.setGamesPlayed(rs.getInt("games_played"));
                stats.setDamageDealt(rs.getDouble("damage_dealt"));
                stats.setDamageTaken(rs.getDouble("damage_taken"));
                stats.setLongestKillStreak(rs.getInt("longest_kill_streak"));
                stats.setHeadshots(rs.getInt("headshots"));
                stats.setAssists(rs.getInt("assists"));
                stats.setTop3Finishes(rs.getInt("top3_finishes"));
                stats.setTop10Finishes(rs.getInt("top10_finishes"));
                stats.setDistanceTraveled(rs.getDouble("distance_traveled"));
                stats.setItemsLooted(rs.getInt("items_looted"));
                stats.setChestsOpened(rs.getInt("chests_opened"));
                stats.setFastestWin(rs.getLong("fastest_win"));
                stats.setMostKillsInGame(rs.getInt("most_kills_in_game"));
                data.setCoins(rs.getInt("coins"));
                data.setTotalPlaytime(rs.getLong("total_playtime"));
                data.setLastSeen(rs.getLong("last_seen"));
            }
        }
    }

    private void loadAchievements(Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = "SELECT achievement_id FROM player_achievements WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            ResultSet rs = stmt.executeQuery();

            Set<String> achievements = new HashSet<>();
            while (rs.next()) {
                achievements.add(rs.getString("achievement_id"));
            }
            data.setUnlockedAchievements(achievements);
        }
    }

    private void loadCosmetics(Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = "SELECT cosmetic_id FROM player_cosmetics WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            ResultSet rs = stmt.executeQuery();

            Set<String> cosmetics = new HashSet<>();
            while (rs.next()) {
                cosmetics.add(rs.getString("cosmetic_id"));
            }
            data.setOwnedCosmetics(cosmetics);
        }
    }

    private void loadEquippedCosmetics(Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = "SELECT cosmetic_type, cosmetic_id FROM player_equipped_cosmetics WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            ResultSet rs = stmt.executeQuery();

            Map<CosmeticType, String> equipped = new EnumMap<>(CosmeticType.class);
            while (rs.next()) {
                CosmeticType type = CosmeticType.valueOf(rs.getString("cosmetic_type"));
                equipped.put(type, rs.getString("cosmetic_id"));
            }
            data.setEquippedCosmetics(equipped);
        }
    }

    private void loadBattlePass(Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = "SELECT * FROM player_battle_pass WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                data.setBattlePassXP(rs.getInt("xp"));
                data.setBattlePassTier(rs.getInt("tier"));
                data.setPremiumBattlePass(rs.getBoolean("has_premium"));
            }
        }
    }

    private void loadCrates(Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = "SELECT crate_type, amount FROM player_crates WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            ResultSet rs = stmt.executeQuery();

            Map<CrateType, Integer> crates = new EnumMap<>(CrateType.class);
            while (rs.next()) {
                CrateType type = CrateType.valueOf(rs.getString("crate_type"));
                crates.put(type, rs.getInt("amount"));
            }
            data.setOwnedCrates(crates);
        }
    }

    @Override
    public void savePlayerData(PlayerData data) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    saveStats(conn, data);
                    saveAchievements(conn, data);
                    saveCosmetics(conn, data);
                    saveEquippedCosmetics(conn, data);
                    saveBattlePass(conn, data);
                    saveCrates(conn, data);
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                plugin.logSevere("Failed to save player data: " + e.getMessage());
            }
        });
    }

    private void saveStats(@NotNull Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = """
                    INSERT INTO player_stats VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                    name=VALUES(name), kills=VALUES(kills), deaths=VALUES(deaths), wins=VALUES(wins),
                    games_played=VALUES(games_played), damage_dealt=VALUES(damage_dealt),
                    damage_taken=VALUES(damage_taken), longest_kill_streak=VALUES(longest_kill_streak),
                    headshots=VALUES(headshots), assists=VALUES(assists), top3_finishes=VALUES(top3_finishes),
                    top10_finishes=VALUES(top10_finishes), distance_traveled=VALUES(distance_traveled),
                    items_looted=VALUES(items_looted), chests_opened=VALUES(chests_opened),
                    fastest_win=VALUES(fastest_win), most_kills_in_game=VALUES(most_kills_in_game),
                    coins=VALUES(coins), total_playtime=VALUES(total_playtime), last_seen=VALUES(last_seen)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
            stmt.setInt(10, stats.getHeadshots());
            stmt.setInt(11, stats.getAssists());
            stmt.setInt(12, stats.getTop3Finishes());
            stmt.setInt(13, stats.getTop10Finishes());
            stmt.setDouble(14, stats.getDistanceTraveled());
            stmt.setInt(15, stats.getItemsLooted());
            stmt.setInt(16, stats.getChestsOpened());
            stmt.setLong(17, stats.getFastestWin());
            stmt.setInt(18, stats.getMostKillsInGame());
            stmt.setInt(19, data.getCoins());
            stmt.setLong(20, data.getTotalPlaytime());
            stmt.setLong(21, data.getLastSeen());
            stmt.executeUpdate();
        }
    }

    private void saveAchievements(@NotNull Connection conn, @NotNull PlayerData data) throws SQLException {
        String delete = "DELETE FROM player_achievements WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.executeUpdate();
        }

        if (!data.getUnlockedAchievements().isEmpty()) {
            String insert = "INSERT INTO player_achievements VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                for (String achievementId : data.getUnlockedAchievements()) {
                    stmt.setString(1, data.getUuid().toString());
                    stmt.setString(2, achievementId);
                    stmt.setLong(3, System.currentTimeMillis());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    private void saveCosmetics(@NotNull Connection conn, @NotNull PlayerData data) throws SQLException {
        String delete = "DELETE FROM player_cosmetics WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.executeUpdate();
        }

        if (!data.getOwnedCosmetics().isEmpty()) {
            String insert = "INSERT INTO player_cosmetics VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                for (String cosmeticId : data.getOwnedCosmetics()) {
                    stmt.setString(1, data.getUuid().toString());
                    stmt.setString(2, cosmeticId);
                    stmt.setLong(3, System.currentTimeMillis());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    private void saveEquippedCosmetics(@NotNull Connection conn, @NotNull PlayerData data) throws SQLException {
        String delete = "DELETE FROM player_equipped_cosmetics WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.executeUpdate();
        }

        if (!data.getEquippedCosmetics().isEmpty()) {
            String insert = "INSERT INTO player_equipped_cosmetics VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                for (Map.Entry<CosmeticType, String> entry : data.getEquippedCosmetics().entrySet()) {
                    stmt.setString(1, data.getUuid().toString());
                    stmt.setString(2, entry.getKey().name());
                    stmt.setString(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    private void saveBattlePass(@NotNull Connection conn, @NotNull PlayerData data) throws SQLException {
        String query = """
                    INSERT INTO player_battle_pass VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE xp=VALUES(xp), tier=VALUES(tier), has_premium=VALUES(has_premium)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.setInt(2, data.getBattlePassXP());
            stmt.setInt(3, data.getBattlePassTier());
            stmt.setBoolean(4, data.hasPremiumBattlePass());
            stmt.executeUpdate();
        }
    }

    private void saveCrates(@NotNull Connection conn, @NotNull PlayerData data) throws SQLException {
        String delete = "DELETE FROM player_crates WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.executeUpdate();
        }

        if (!data.getOwnedCrates().isEmpty()) {
            String insert = "INSERT INTO player_crates VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                for (Map.Entry<CrateType, Integer> entry : data.getOwnedCrates().entrySet()) {
                    if (entry.getValue() > 0) {
                        stmt.setString(1, data.getUuid().toString());
                        stmt.setString(2, entry.getKey().name());
                        stmt.setInt(3, entry.getValue());
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            }
        }
    }

    @Override
    public CompletableFuture<List<LeaderboardEntry>> getTopPlayers(String stat, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<LeaderboardEntry> entries = new ArrayList<>();
            String column = switch (stat.toLowerCase()) {
                case "wins" -> "wins";
                case "kd" -> "(kills / NULLIF(deaths, 1))";
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
    public CompletableFuture<Boolean> playerExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT 1 FROM player_stats WHERE uuid = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                return stmt.executeQuery().next();
            } catch (SQLException e) {
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deletePlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "DELETE FROM player_stats WHERE uuid = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                plugin.logSevere("Failed to delete player data: " + e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getTotalPlayers() {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT COUNT(*) FROM player_stats";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                plugin.logSevere("Failed to get total players: " + e.getMessage());
            }
            return 0;
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