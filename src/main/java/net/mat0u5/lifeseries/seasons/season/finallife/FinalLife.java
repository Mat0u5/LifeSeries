package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.boogeyman.BoogeymanWeaponManager;
import net.mat0u5.lifeseries.seasons.season.finallife.boogeyman.FinalLifeBoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.finallife.boogeyman.ThrownBoogeymanWeapon;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.matlib.events.EventResult;
import net.mat0u5.matlib.utils.player.ScoreboardUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class FinalLife extends Season {
	public static final String DEATHS_SCOREBOARD = "FinalLifeDeaths";
	public FinalLifeBoogeymanManager finalLifeBoogeymanManager = new FinalLifeBoogeymanManager();

	@Override
	public Seasons getSeason() {
		return Seasons.FINAL_LIFE;
	}

	@Override
	public SeasonConfig createConfig() {
		return new FinalLifeConfig();
	}

	@Override
	public LivesManager createLivesManager() {
		return new FinalLifeLivesManager();
	}

	@Override
	public void reload() {
		super.reload();
		finalLifeBoogeymanManager.onReload();
	}

	@Override
	public String getTeamForPlayer(ServerPlayer player) {
		if (finalLifeBoogeymanManager.isBoogeyman(player)) {
			return FinalLifeBoogeymanManager.TEAM_NAME;
		}
		return super.getTeamForPlayer(player);
	}

	@Override
	public void createTeams() {
		super.createTeams();
		finalLifeBoogeymanManager.createTeamsIfNecessary();
	}

	@Override
	public void createScoreboards() {
		super.createScoreboards();
		finalLifeBoogeymanManager.createScoreboardsIfNecessary();
		ScoreboardUtils.createObjective(DEATHS_SCOREBOARD, DEATHS_SCOREBOARD, ObjectiveCriteria.DEATH_COUNT);
	}

	@Override
	public void onPlayerJoin(ServerPlayer player) {
		finalLifeBoogeymanManager.onPlayerJoin(player);
		super.onPlayerJoin(player);
	}

	@Override
	public boolean sessionStart() {
		finalLifeBoogeymanManager.onSessionStart();
		super.sessionStart();
		return true;
	}
	@Override
	public void sessionEnd() {
		finalLifeBoogeymanManager.onSessionEnd();
		super.sessionEnd();
	}

	@Override
	public void onPlayerDisconnect(ServerPlayer player) {
		ThrownBoogeymanWeapon.returnWeapons(player);
		InBetweenManager.onPlayerDisconnect(player);
		super.onPlayerDisconnect(player);
		finalLifeBoogeymanManager.onPlayerDisconnect(player);
	}

	@Override
	public void switchOutOfSeason(Seasons changedTo) {
		ScoreboardUtils.removeObjective(DEATHS_SCOREBOARD);
		createScoreboards();
		ThrownBoogeymanWeapon.returnAllWeapons();
		BoogeymanWeaponManager.removeAllBoogeymanWeaponNBTs();
		InBetweenManager.exitInBetweenAllPlayers();
		super.switchOutOfSeason(changedTo);
		finalLifeBoogeymanManager.switchOutOfSeason();
	}

	@Override
	public void tick(MinecraftServer server) {
		super.tick(server);
		finalLifeBoogeymanManager.tick();
		BoogeymanWeaponManager.onTick();
		InBetweenManager.tick();
	}

	@Override
	public void onPlayerRespawn(ServerPlayer player) {
		super.onPlayerRespawn(player);
		InBetweenManager.onPlayerRespawn(player);
	}

	@Override
	public void onPlayerDeath(ServerPlayer player, DamageSource source) {
		InBetweenManager.onPlayerDeath(player);
		super.onPlayerDeath(player, source);
	}

	@Override
	public EventResult onPrePlayerDamage(ServerPlayer player, DamageSource source, float amount) {
		EventResult res = InBetweenManager.onPrePlayerDamage(player, source);
		if (!res.isPass()) return res;
		return super.onPrePlayerDamage(player, source, amount);
	}

	@Override
	public EventResult onPlayerPreDeath(ServerPlayer player, DamageSource source) {
		EventResult res = super.onPlayerPreDeath(player, source);
		if (res.isPass()) {
			return finalLifeBoogeymanManager.onPlayerPreDeath(player, source);
		}
		return res;
	}

	@Override
	public boolean isAllowedToAttack(ServerPlayer attacker, ServerPlayer victim, boolean allowSelfDefense) {
		if (finalLifeBoogeymanManager.isBoogeyman(attacker)) {
			return true;
		}
		return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
	}

	@Override
	public boolean shouldHideEquipment(ServerPlayer player, EquipmentSlot slot, ItemStack stack) {
		if (stack != null && !stack.isEmpty()) {
			if (finalLifeBoogeymanManager.isBoogeyman(player) && OtherUtils.isArmorSlot(slot) && FinalLifeBoogeymanManager.BOOGEYMAN_HIDE_ARMOR.get()) {
				return true;
			}
		}
		return super.shouldHideEquipment(player, slot, stack);
	}

	public int getDeaths(ServerPlayer player) {
		Integer deathsNum = ScoreboardUtils.getScore(player, DEATHS_SCOREBOARD);
		if (deathsNum == null) {
			setDeaths(player, 0);
			return 0;
		}
		return deathsNum;
	}

	public void setDeaths(ServerPlayer player, int deaths) {
		ScoreboardUtils.setScore(player, DEATHS_SCOREBOARD, deaths);
	}
}
