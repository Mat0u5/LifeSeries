package net.mat0u5.lifeseries.seasons.season.finallife;

import com.mojang.datafixers.util.Pair;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.matlib.events.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class FinalLife extends Season {
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
		super.onPlayerDisconnect(player);
		finalLifeBoogeymanManager.onPlayerDisconnect(player);
	}

	@Override
	public void switchOutOfSeason(Seasons changedTo) {
		super.switchOutOfSeason(changedTo);
		finalLifeBoogeymanManager.switchOutOfSeason();
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
}
