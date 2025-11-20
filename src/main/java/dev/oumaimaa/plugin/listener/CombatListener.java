package dev.oumaimaa.plugin.listener;

import dev.oumaimaa.Main;
import dev.oumaimaa.plugin.constant.AchievementType;
import dev.oumaimaa.plugin.constant.ChallengeType;
import dev.oumaimaa.plugin.skeleton.Game;
import dev.oumaimaa.plugin.skeleton.GamePlayer;
import dev.oumaimaa.plugin.config.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Combat listener with Phase 3 integration
 */
public class CombatListener implements Listener {
    private final Main plugin;

    public CombatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        Player killer = victim.getKiller();

        Game game = plugin.getGameManager().getPlayerGame(victim);
        if (game == null) return;

        GamePlayer victimGP = plugin.getGameManager().getGamePlayer(victim);

        if (killer != null && plugin.getGameManager().isInGame(killer)) {
            GamePlayer killerGP = plugin.getGameManager().getGamePlayer(killer);

            killerGP.addKill();

            PlayerData killerData = plugin.getPlayerDataManager().getPlayerData(killer);
            killerData.getStatistics().addKill(1);
            killerData.addCoins(plugin.getConfigManager().getMainConfig().getInt("rewards.kill-coins", 10));

            plugin.getBattlePassManager().addXP(killer, 50, "Kill");

            int totalKills = killerData.getStatistics().getKills();
            int gameKills = game.getKills(killer.getUniqueId());
            plugin.getAchievementManager().checkAchievement(killer, AchievementType.KILLS, totalKills);
            plugin.getAchievementManager().checkAchievement(killer, AchievementType.KILLS_IN_GAME, gameKills);
            plugin.getChallengeManager().updateProgress(killer, ChallengeType.KILLS, 1);

            game.eliminatePlayer(victimGP, killerGP);
        } else {
            game.eliminatePlayer(victimGP, null);
        }

        PlayerData victimData = plugin.getPlayerDataManager().getPlayerData(victim);
        victimData.getStatistics().addDeath(1);
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        Game game = plugin.getGameManager().getPlayerGame(victim);
        if (game == null) return;

        double damage = event.getFinalDamage();

        PlayerData attackerData = plugin.getPlayerDataManager().getPlayerData(attacker);
        attackerData.getStatistics().setDamageDealt(
                attackerData.getStatistics().getDamageDealt() + damage
        );

        PlayerData victimData = plugin.getPlayerDataManager().getPlayerData(victim);
        victimData.getStatistics().setDamageTaken(
                victimData.getStatistics().getDamageTaken() + damage
        );

        plugin.getAchievementManager().checkAchievement(
                attacker,
                AchievementType.DAMAGE_DEALT,
                (int) attackerData.getStatistics().getDamageDealt()
        );

        plugin.getAchievementManager().checkAchievement(
                victim,
                AchievementType.DAMAGE_TAKEN,
                (int) victimData.getStatistics().getDamageTaken()
        );

        plugin.getChallengeManager().updateProgress(
                attacker,
                ChallengeType.DAMAGE_DEALT,
                (int) damage
        );

        plugin.getChallengeManager().updateProgress(
                victim,
                ChallengeType.DAMAGE_TAKEN,
                (int) damage
        );
    }
}