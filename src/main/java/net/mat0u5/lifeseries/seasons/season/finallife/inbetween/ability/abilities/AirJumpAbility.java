package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.ChargeAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public class AirJumpAbility extends ChargeAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_AIR_JUMP_HEALTH);
	private static final double SAFE_FALL_DISTANCE = 5;
	private static final int LANDED_TICKS = 10;

	private boolean airJumped;
	private int ticksOnGround;

	public AirJumpAbility() {
		super(Abilities.AIR_JUMP);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}

	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (!airJumped || player == null) return;
		if (player.onGround() || player.isInWater()) {
			ticksOnGround++;
			if (ticksOnGround >= LANDED_TICKS) {
				airJumped = false;
			}
		}
		else {
			ticksOnGround = 0;
		}
	}

	// Triggered via packet sent from client
	public void airJump(ServerPlayer player) {
		if (!useCharge(player)) return;
		playSound(player, ModifiableSound.FINALLIFE_AIR_JUMP);
		player.resetFallDistance();
		airJumped = true;
		ticksOnGround = 0;
	}

	@Override
	public boolean preventDamage(DamageSource source) {
		ServerPlayer player = getPlayer();
		if (!airJumped || player == null || !source.is(DamageTypeTags.IS_FALL)) return false;
		airJumped = false;
		return player.fallDistance < SAFE_FALL_DISTANCE;
	}
}
