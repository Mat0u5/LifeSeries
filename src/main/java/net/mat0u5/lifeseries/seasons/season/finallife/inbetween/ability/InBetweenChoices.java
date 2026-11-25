package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InBetweenChoices {
	private record BoundedChoice(Abilities ability, Integer minDeaths, Integer maxDeaths) {}
	private static final List<BoundedChoice> registered = new ArrayList<>();

	static {
		register(Abilities.AIR_JUMP, null, 3);
		register(Abilities.BLAST_RESISTANCE, null, 3);
		register(Abilities.DASH, null, 3);
		register(Abilities.EXTRA_REACH, null, 3);
		register(Abilities.FEATHER_FALLING, null, 3);
		register(Abilities.FIRE_RESISTANCE, null, 3);
		register(Abilities.HASTE, null, 3);
		register(Abilities.HEALTH_VISION, null, 3);
		register(Abilities.KNOCKBACK_RESISTANCE, null, 3);
		register(Abilities.MORE_DROPS, null, 3);
		register(Abilities.PROJECTILE_PROTECTION, null, 3);
		register(Abilities.RESISTANCE, null, 3);
		register(Abilities.SATURATION, null, 3);
		//register(Abilities.SECOND_WIND, null, 3);
		register(Abilities.SHIELD_BASH, null, 3);
		register(Abilities.SMALLER, null, 3);
		register(Abilities.SPEED, null, 3);
		//register(Abilities.TRIDENT, 3, null);

		//TODO modify the death ranges.
	}

	public static void register(Abilities ability, Integer minDeaths, Integer maxDeaths) {
		registered.add(new BoundedChoice(ability, minDeaths, maxDeaths));
	}

	public static List<InBetweenChoice> getPossibleChoices(ServerPlayer player, int deathNum) {
		List<InBetweenChoice> choices = new ArrayList<>();
		for (BoundedChoice boundedChoice : registered) {
			Integer minDeaths = boundedChoice.minDeaths();
			Integer maxDeaths = boundedChoice.maxDeaths();
			if (minDeaths != null && deathNum < minDeaths) continue;
			if (maxDeaths != null && deathNum > maxDeaths) continue;
			if (AbilityManager.hasAbility(player, boundedChoice.ability())) continue;
			choices.add(InBetweenChoice.of(boundedChoice.ability()));
		}
		Collections.shuffle(choices);
		return choices;
	}
}
