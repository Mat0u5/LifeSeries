package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.ChargeAbility;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.server.level.ServerPlayer;

public class DashAbility extends ChargeAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_DASH_HEALTH);
	private static final double GHOST_VIEW_DISTANCE = 128;

	public DashAbility() {
		super(Abilities.DASH);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}

	// Triggered via packet sent from client, the client itself moves the player
	public void dash(ServerPlayer player) {
		if (!useCharge(player)) return;
		playSound(player, ModifiableSound.FINALLIFE_DASH);
		for (ServerPlayer other : PlayerUtils.getAllPlayers()) {
			if (other == player || other.level() != player.level() || other.distanceToSqr(player) > GHOST_VIEW_DISTANCE * GHOST_VIEW_DISTANCE) continue;
			SimplePackets.DASH_GHOSTS.sendToClient(player.getId(), other);
		}
	}
}
