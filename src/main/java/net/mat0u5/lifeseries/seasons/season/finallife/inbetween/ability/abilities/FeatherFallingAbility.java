package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.DamageReductionAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.tags.DamageTypeTags;

public class FeatherFallingAbility extends DamageReductionAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_FEATHER_FALLING_HEARTS);
	public FeatherFallingAbility() {
		// Feather Falling IV = 48%
		super(Abilities.FEATHER_FALLING, DamageTypeTags.IS_FALL, 0.48F);
	}

	@Override
	protected int loadHeartsChange() {
		return HEALTH.get();
	}
}
