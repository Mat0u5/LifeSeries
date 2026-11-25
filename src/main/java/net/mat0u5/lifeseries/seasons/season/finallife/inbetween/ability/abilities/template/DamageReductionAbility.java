package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public abstract class DamageReductionAbility extends Ability {
	private final TagKey<DamageType> damageTypes;
	private final float reduction;

	public DamageReductionAbility(Abilities ability, TagKey<DamageType> damageTypes, float reduction) {
		super(ability);
		this.damageTypes = damageTypes;
		this.reduction = reduction;
	}

	@Override
	public float modifyDamage(DamageSource source, float amount) {
		if (!source.is(damageTypes)) return amount;
		return amount * (1 - reduction);
	}
}
