package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.weapons.ThrownWeaponManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.weapons.ThrownWeapon;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.server.level.ServerPlayer;

public class ThrowSwordsAbility extends Ability {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_THROW_SWORDS_HEARTS);

	public ThrowSwordsAbility() {
		super(Abilities.THROW_SWORDS);
	}

	@Override
	protected int loadHeartsChange() {
		return HEALTH.get();
	}

	@Override
	public void onEnd() {
		ServerPlayer player = getPlayer();
		if (player == null) return;
		ThrownWeapon.returnWeapons(player);
		ThrownWeaponManager.removeThrowableWeaponNBTs(player);
	}
}
