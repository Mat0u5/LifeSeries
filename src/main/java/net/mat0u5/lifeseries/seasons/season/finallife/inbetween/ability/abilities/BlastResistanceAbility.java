package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.DamageReductionAbility;
import net.minecraft.tags.DamageTypeTags;

public class BlastResistanceAbility extends DamageReductionAbility {
	public BlastResistanceAbility() {
		super(Abilities.BLAST_RESISTANCE, DamageTypeTags.IS_EXPLOSION, 0.75F);
	}
}
