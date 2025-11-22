# 🎮 KawaiiRoyale v2.0

<div align="center">

![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)
![Paper](https://img.shields.io/badge/Paper-1.21+-green.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Status](https://img.shields.io/badge/status-production--ready-success.svg)

**Next-Generation Battle Royale Plugin for Minecraft**

*PacketEvents Optimized • Vault Integration • Full Feature Set*

</div>

---

## 🎯 Overview

**KawaiiRoyale** is a production-ready Battle Royale plugin built with modern technologies including PacketEvents for optimal performance, Vault for economy integration, and a comprehensive feature set including achievements, cosmetics, battle pass, and challenges.

### ⚡ Key Features

- ✨ **100% Complete** - All systems fully implemented
- 🚀 **Performance Optimized** - PacketEvents integration for reduced overhead
- 💰 **Economy Integration** - Full Vault support
- 🎨 **Beautiful UX** - MiniMessage gradients, particles, animations
- ⚙️ **High Performance** - Caffeine caching, async operations
- 📊 **Full Statistics** - MySQL/SQLite with connection pooling
- 🗺️ **Smart Zones** - Adaptive shrinking with particle borders
- 🎁 **4-Tier Loot** - Weighted distribution system
- 🏆 **Achievements** - 15+ achievements with rewards
- 💎 **Cosmetics** - Kill messages, victory dances, parachutes
- 📈 **Battle Pass** - 30 tiers with free and premium rewards
- 🎯 **Challenges** - Daily and weekly challenges
- 🎁 **Crates** - 4 rarity tiers with animated openings

---

## 📦 What's New in v2.0

### Performance Improvements
- ✅ **PacketEvents Integration** - Reduced packet overhead
- ✅ **Optimized Networking** - Better player state synchronization
- ✅ **Improved Caching** - Caffeine cache for all data access

### New Features
- ✅ **Vault Integration** - Economy support for rewards
- ✅ **Advanced API** - External plugin integration
- ✅ **Tab Completion** - Full command tab completion
- ✅ **Better GUI System** - Optimized inventory management

### Code Quality
- ✅ **Restructured Packages** - Better organization
- ✅ **JavaDoc Documentation** - Professional documentation
- ✅ **Advanced Java** - Modern Java 21 features
- ✅ **Type Safety** - Records and sealed classes

---

## 📁 Project Structure

```
dev.oumaimaa/
├── api/
│   └── API.java                    - Public API for developers
├── plugin/
│   ├── Main.java                   - Plugin entry point
│   ├── command/
│   │   └── CommandManager.java     - Command system with tab completion
│   ├── config/
│   │   ├── ConfigManager.java      - Configuration management
│   │   └── playerdata/
│   │       ├── PlayerData.java
│   │       ├── PlayerDataManager.java
│   │       ├── PlayerStatistics.java
│   │       └── database/
│   │           ├── DatabaseHandler.java
│   │           ├── MySQLHandler.java
│   │           └── SQLiteHandler.java
│   ├── constant/                   - Enums and constants
│   ├── gui/                        - GUI system
│   ├── lib/
│   │   └── KawaiiRoyalePlaceholder.java
│   ├── listener/                   - Event listeners
│   ├── manager/                    - Core managers
│   │   ├── GameManager.java
│   │   ├── QueueManager.java
│   │   ├── ArenaManager.java
│   │   ├── ZoneManager.java
│   │   ├── LootManager.java
│   │   ├── VaultManager.java       - NEW: Vault integration
│   │   ├── AchievementManager.java
│   │   ├── CosmeticManager.java
│   │   ├── BattlePassManager.java
│   │   ├── ChallengeManager.java
│   │   └── CrateManager.java
│   ├── record/                     - Immutable data classes
│   ├── skeleton/                   - Core game classes
│   └── task/                       - Async tasks
```

---

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.8+
- Paper 1.21+ server
- (Optional) Vault + Economy plugin

### Installation

1. **Build the plugin**:
   ```bash
   mvn clean package
   ```

2. **Install on server**:
   ```bash
   cp target/KawaiiRoyale-2.0.0.jar /path/to/server/plugins/
   ```

3. **Install dependencies** (optional):
   - Vault (for economy features)
   - PlaceholderAPI (for placeholders)

4. **Start server** - Plugin generates default configs

5. **Setup arena**:
   ```
   /kawaii setup battleroyale MyArena
   /setcenter          (at arena center)
   /setlobbybr         (at lobby location)
   /addspawn           (run at each spawn - add 20+)
   /kawaii reload
   ```

6. **Start playing**:
   ```
   /br                 (join Battle Royale)
   ```

---

## 🎮 Game Modes

### 🏆 Battle Royale
- Last player standing wins
- 10-100 players
- No respawns
- Shrinking zone mechanics

### ⚔️ Resurgence
- Top kills win
- 8-50 players
- Unlimited respawns
- Time-based or score limit

---

## 📋 Commands

### Player Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/kawaii` | Main menu | `kawaii.play` |
| `/br` | Join Battle Royale | `kawaii.play` |
| `/rs` | Join Resurgence | `kawaii.play` |
| `/leave` | Leave game | `kawaii.play` |
| `/stats [player]` | View statistics | `kawaii.play` |
| `/leaderboard [type]` | Leaderboards | `kawaii.play` |
| `/achievements` | View achievements | `kawaii.play` |
| `/cosmetics [type]` | Cosmetics shop | `kawaii.play` |
| `/battlepass` | Battle pass | `kawaii.play` |
| `/challenges` | Daily challenges | `kawaii.play` |
| `/crates` | Open crates | `kawaii.play` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/kawaii setup <mode> <name>` | Create arena | `kawaii.admin.setup` |
| `/kawaii reload` | Reload configs | `kawaii.admin.reload` |
| `/kawaii coins <action> <player> <amount>` | Manage coins | `kawaii.admin` |
| `/kawaii achievement give <player> <id>` | Give achievement | `kawaii.admin` |
| `/kawaii cosmetic give <player> <id>` | Give cosmetic | `kawaii.admin` |
| `/kawaii crate give <player> <type> [amount]` | Give crate | `kawaii.admin` |
| `/kawaii bp <action> <player> [value]` | Manage battle pass | `kawaii.admin` |

---

## ⚙️ Configuration

### Main Config (`config.yml`)

Complete configuration with 150+ options including:
- Game modes settings
- Zone configuration
- Loot system
- Performance tuning
- Database settings
- Integration options

### Economy Integration

```yaml
integrations:
  economy:
    enabled: true
    use-vault: true
    
features:
  rewards:
    enabled: true
    use-economy: true
    win-reward: 100
    kill-reward: 10
```

---

## 🎯 Features

### 📊 Statistics System
- Complete player tracking
- MySQL/SQLite support
- HikariCP connection pooling
- Async operations
- Leaderboards

### 🏆 Achievement System
- 15+ achievements
- Multiple categories
- Difficulty tiers
- Coin rewards
- Progress tracking

### 💎 Cosmetic System
- Kill messages
- Victory celebrations
- Parachute colors
- Death effects
- Player titles
- Shop with coins

### 📈 Battle Pass
- 30 tiers
- Free & premium tracks
- XP progression
- Seasonal rewards
- Premium purchase

### 🎯 Daily Challenges
- Auto-generated challenges
- Daily/weekly rotation
- Progress tracking
- Coin & XP rewards
- Multiple difficulties

### 🎁 Crate System
- 4 rarity tiers
- Animated openings
- Purchasable with coins
- Cosmetic rewards
- Economy integration

---

## 🔌 API Usage

### For Developers

```java
// Get plugin instance
Main plugin = (Main) Bukkit.getPluginManager().getPlugin("KawaiiRoyale");

// Check if player in game
boolean inGame = plugin.getGameManager().isInGame(player);

// Get player statistics
PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
int kills = data.getStatistics().getKills();

// Give rewards
plugin.getVaultManager().deposit(player, 100.0);
data.addCoins(50);

// Award achievement
plugin.getAchievementManager().checkAchievement(player, AchievementType.KILLS, 100);
```

---

## 🔧 Technical Details

### Performance Optimizations
- **PacketEvents**: Reduced packet processing overhead
- **Caffeine Cache**: Sub-millisecond data access
- **Async Operations**: Non-blocking I/O for database
- **Connection Pooling**: HikariCP for database connections
- **Smart Caching**: Automatic cache invalidation

### Dependencies
- Paper API 1.21.4
- Adventure API 4.24.0
- PacketEvents 2.5.0
- Vault API 1.7
- Cloud Commands 2.0.0
- Caffeine 3.2.3
- HikariCP 7.0.2
- PlaceholderAPI 2.11.7 (optional)

---

## 📊 PlaceholderAPI

### Available Placeholders

```
%kawaiiroyale_kills%
%kawaiiroyale_deaths%
%kawaiiroyale_wins%
%kawaiiroyale_kd%
%kawaiiroyale_winrate%
%kawaiiroyale_rank_kills%
%kawaiiroyale_top_kills_1%
%kawaiiroyale_ingame%
```

---

## 🐛 Troubleshooting

### Common Issues

**Plugin won't enable**
- Check Java version (21+)
- Verify Paper 1.21+
- Check console errors

**Economy not working**
- Install Vault
- Install economy plugin (EssentialsX, etc.)
- Enable in config.yml

**Performance issues**
- Enable async operations
- Increase database pool size
- Pre-generate arena chunks
- Allocate more server RAM

---

## 📈 Roadmap

### Phase 1: Complete ✅
- Core game systems
- Statistics tracking
- Zone mechanics
- Loot system

### Phase 2: Complete ✅
- Achievements
- Cosmetics
- Battle Pass
- Challenges
- Crates

### Phase 3: Current
- ✅ PacketEvents integration
- ✅ Vault integration
- ✅ API system
- 🔄 Advanced anti-cheat
- 🔄 Team modes

### Phase 4: Planned
- Party system
- Ranked matchmaking
- Tournament system
- Replay system
- Custom weapons

---

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create feature branch
3. Make changes with tests
4. Submit pull request

---

## 📄 License

MIT License - Free to use and modify

---

## 💬 Support

- **Issues**: [GitHub Issues](https://github.com/4K1D3V/KawaiiRoyale/issues)
- **Discord**: [Join Server](#)
- **Documentation**: [Wiki](https://github.com/4K1D3V/KawaiiRoyale/wiki)

---

## 🎉 Credits

- **Author**: oumaimaa
- **APIs**: Paper, Adventure, PacketEvents, Vault
- **Community**: Minecraft Plugin Development Community

---

<div align="center">

**Made with ❤️ by oumaimaa**

⭐ Star this repo if you find it useful!

**Version 2.0.0** - Production Ready with PacketEvents

</div>