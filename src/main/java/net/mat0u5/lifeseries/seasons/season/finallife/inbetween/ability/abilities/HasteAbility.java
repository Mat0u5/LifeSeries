package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.EffectAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.world.effect.MobEffects;

public class HasteAbility extends EffectAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_HASTE_HEARTS);
	public HasteAbility() {
		super(Abilities.HASTE, MobEffects.HASTE, 1);
	}

	@Override
	protected int loadHeartsChange() {
		return HEALTH.get();
	}
}
