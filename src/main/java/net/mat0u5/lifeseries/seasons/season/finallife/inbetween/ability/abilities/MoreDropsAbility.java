package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.AbilityManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class MoreDropsAbility extends Ability {
	@Nullable
	public static Entity capturedEntity;

	public MoreDropsAbility() {
		super(Abilities.MORE_DROPS);
	}

	public static boolean hasMoreDrops(@Nullable Entity entity) {
		return entity instanceof ServerPlayer player && AbilityManager.hasAbility(player, Abilities.MORE_DROPS);
	}

	public static int getLootingLevelAdd() {
		return 1;
	}

	public static int getFortuneLevelAdd() {
		return 1;
	}
}
