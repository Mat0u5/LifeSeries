package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public abstract class Ability {
	private UUID playerUUID;
	private final Abilities ability;
	private int healthChange;

	public Ability(Abilities ability) {
		this.ability = ability;
	}

	protected abstract int loadHealthChange();

	public int getHealthChange() {
		return this.healthChange;
	}

	public void reloadHealthChange() {
		this.healthChange = this.loadHealthChange();
	}

	public void bindPlayer(ServerPlayer player) {
		bindPlayer(player.getUUID());
	}

	public void bindPlayer(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	/*
	public void saveExtraData(JsonObject json) {}
	public void loadExtraData(JsonObject json) {}
	 */

	public ServerPlayer getPlayer() {
		return PlayerUtils.getPlayer(playerUUID);
	}

	public Abilities ability() {
		return this.ability;
	}

	public void onStart() {
	}

	public void onEnd() {
	}

	public void tick() {
	}

	public enum AbilityType {
		PASSIVE,
		ACTIVE;
	}
}
