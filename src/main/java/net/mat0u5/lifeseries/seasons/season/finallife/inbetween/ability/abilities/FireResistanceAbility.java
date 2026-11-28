package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.EffectAbility;
import net.minecraft.world.effect.MobEffects;

public class FireResistanceAbility extends EffectAbility {
	public FireResistanceAbility() {
		super(Abilities.FIRE_RESISTANCE, MobEffects.FIRE_RESISTANCE, 0);
	}
}
