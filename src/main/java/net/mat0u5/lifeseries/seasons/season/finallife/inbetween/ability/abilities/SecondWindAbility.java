package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class SecondWindAbility extends Ability {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_SECOND_WIND_HEALTH);
	public static final int TRIGGER_HEALTH = 10;
	private static final float INSTANT_HEAL = 10; // After trigger
	private static final float HEARTBEAT_HEAL = 2;
	private static final int HEARTBEATS = 5; //TODO this is unknown, might be until full health
	private static final int HEARTBEAT_INTERVAL = 12; // 0.6 seconds
	private static final int COOLDOWN = 80 * 20; // 80 seconds

	private int cooldown;
	private int heartbeatsLeft;
	private int heartbeatTimer;
	private ServerPlayer heartbeatPlayer; // The heartbeats stop when this player dies or respawns

	public SecondWindAbility() {
		super(Abilities.SECOND_WIND);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}

	@Override
	public void onStart() {
		super.onStart();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		sendCooldown(player);
	}

	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		if (cooldown > 0 && --cooldown == 0) {
			sendCooldown(player);
		}
		if (heartbeatsLeft > 0) {
			if (player != heartbeatPlayer || !player.isAlive()) {
				stopHeartbeats(player);
			}
			else if (--heartbeatTimer <= 0) {
				heartbeat(player);
			}
		}
		float health = player.getHealth();
		if (cooldown == 0 && player.isAlive() && health < TRIGGER_HEALTH) {
			trigger(player);
		}
	}

	@Override
	public void onEnd() {
		super.onEnd();
		ServerPlayer player = getPlayer();
		if (player != null && heartbeatsLeft > 0) {
			stopHeartbeats(player);
		}
		heartbeatsLeft = 0;
		heartbeatPlayer = null;
	}

	private void trigger(ServerPlayer player) {
		player.heal(INSTANT_HEAL);
		cooldown = COOLDOWN;
		heartbeatsLeft = HEARTBEATS;
		heartbeatTimer = HEARTBEAT_INTERVAL;
		heartbeatPlayer = player;
		((ServerLevel) player.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + player.getBbHeight() * 0.6, player.getZ(), 3, 0.5, 0.4, 0.5, 0);
		SimplePackets.SECOND_WIND.sendToClient(List.of("trigger", String.valueOf(COOLDOWN)), player);
	}

	private void heartbeat(ServerPlayer player) {
		heartbeatsLeft--;
		heartbeatTimer = HEARTBEAT_INTERVAL;
		player.heal(HEARTBEAT_HEAL);
		ModifiableSound.FINALLIFE_SECOND_WIND_HEARTBEAT.playWithSource(player, SoundSource.PLAYERS, 1, 1);
		SimplePackets.SECOND_WIND.sendToClient(List.of("heartbeat", String.valueOf(heartbeatsLeft)), player);
		if (heartbeatsLeft == 0) heartbeatPlayer = null;
	}

	private void stopHeartbeats(ServerPlayer player) {
		heartbeatsLeft = 0;
		heartbeatPlayer = null;
		SimplePackets.SECOND_WIND.sendToClient(List.of("stop"), player);
	}

	private void sendCooldown(ServerPlayer player) {
		SimplePackets.SECOND_WIND.sendToClient(List.of("cooldown", String.valueOf(cooldown), String.valueOf(COOLDOWN)), player);
	}
}
