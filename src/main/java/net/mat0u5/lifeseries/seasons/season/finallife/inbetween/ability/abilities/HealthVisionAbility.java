package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

// Drawn client-side, the server sends players' health
public class HealthVisionAbility extends Ability {
	private List<String> lastSent = List.of();

	public HealthVisionAbility() {
		super(Abilities.HEALTH_VISION);
	}

	@Override
	public void onStart() {
		super.onStart();
		lastSent = List.of();
	}

	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		List<String> healthValues = new ArrayList<>();
		for (ServerPlayer other : PlayerUtils.getAllPlayers()) {
			healthValues.add(other.getId() + ";" + Mth.ceil(other.getHealth()));
		}
		if (healthValues.equals(lastSent)) return;
		lastSent = healthValues;
		SimplePackets.HEALTH_VISION.sendToClient(healthValues, player);
	}

	@Override
	public void onEnd() {
		super.onEnd();
		ServerPlayer player = getPlayer();
		if (player != null) SimplePackets.HEALTH_VISION.sendToClient(List.of(), player);
		lastSent = List.of();
	}
}
