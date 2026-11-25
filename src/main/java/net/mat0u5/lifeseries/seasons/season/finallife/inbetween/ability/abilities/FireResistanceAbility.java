package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.EffectAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.world.effect.MobEffects;

public class FireResistanceAbility extends EffectAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_FIRE_RESISTANCE_HEALTH);
	public FireResistanceAbility() {
		super(Abilities.FIRE_RESISTANCE, MobEffects.FIRE_RESISTANCE, 0);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}
}
