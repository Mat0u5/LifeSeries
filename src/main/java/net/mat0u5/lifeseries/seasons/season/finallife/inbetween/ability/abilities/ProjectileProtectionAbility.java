package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.DamageReductionAbility;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.tags.DamageTypeTags;

public class ProjectileProtectionAbility extends DamageReductionAbility {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_PROJECTILE_PROTECTION_HEARTS);
	public ProjectileProtectionAbility() {
		super(Abilities.PROJECTILE_PROTECTION, DamageTypeTags.IS_PROJECTILE, 0.5F);
	}

	@Override
	protected int loadHeartsChange() {
		return HEALTH.get();
	}
}
