package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.DamageReductionAbility;
import net.minecraft.tags.DamageTypeTags;

public class FeatherFallingAbility extends DamageReductionAbility {
	public FeatherFallingAbility() {
		// Feather Falling IV = 48%
		super(Abilities.FEATHER_FALLING, DamageTypeTags.IS_FALL, 0.48F);
	}
}
