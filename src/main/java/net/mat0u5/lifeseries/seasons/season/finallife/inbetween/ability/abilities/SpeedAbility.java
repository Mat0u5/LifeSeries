package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class SpeedAbility extends Ability {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_SPEED_HEALTH);


	public SpeedAbility() {
		super(Abilities.SPEED);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}

	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null || player.isSpectator()) return;
		MobEffectInstance effect = player.getEffect(MobEffects.SPEED);
		if (effect != null && (effect.getAmplifier() > 0 || !effect.endsWithin(20))) return;
		player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0, false, false, false));
	}

	@Override
	public void onEnd() {
		ServerPlayer player = getPlayer();
		if (player == null) return;
		MobEffectInstance effect = player.getEffect(MobEffects.SPEED);
		if (effect != null && effect.getAmplifier() == 0 && effect.endsWithin(100) && !effect.showIcon()) {
			player.removeEffect(MobEffects.SPEED);
		}
	}
}
