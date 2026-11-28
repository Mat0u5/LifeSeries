package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.DamageReductionAbility;
import net.minecraft.tags.DamageTypeTags;

public class ProjectileProtectionAbility extends DamageReductionAbility {
	public ProjectileProtectionAbility() {
		super(Abilities.PROJECTILE_PROTECTION, DamageTypeTags.IS_PROJECTILE, 0.5F);
	}
}
