package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.AbilityManager;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.server.level.ServerPlayer;

public class SaturationAbility extends Ability {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_SATURATION_HEALTH);
	public SaturationAbility() {
		super(Abilities.SATURATION);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}

	public static int modifyTickTimer(ServerPlayer player, int foodLevel, int tickTimer) {
		if (tickTimer <= 0 || foodLevel < 18) return tickTimer;
		if (!AbilityManager.hasAbility(player, Abilities.SATURATION)) return tickTimer;
		return tickTimer + 1;
	}
}
