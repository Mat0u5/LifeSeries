package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.EffectAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.world.effect.MobEffects;

public class SpeedAbility extends EffectAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_SPEED_HEALTH);
	public SpeedAbility() {
		super(Abilities.SPEED, MobEffects.SPEED, 0);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}
}
