package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.DamageReductionAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.tags.DamageTypeTags;

public class BlastResistanceAbility extends DamageReductionAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_BLAST_RESISTANCE_HEALTH);
	public BlastResistanceAbility() {
		super(Abilities.BLAST_RESISTANCE, DamageTypeTags.IS_EXPLOSION, 0.75F);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}
}
