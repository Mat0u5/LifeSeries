package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InBetweenChoices {
	private record BoundedChoice(Abilities ability, Integer minDeaths, Integer maxDeaths) {}
	private static final List<BoundedChoice> registered = new ArrayList<>();

	static {
		register(Abilities.AIR_JUMP, null, null);
		register(Abilities.BLAST_RESISTANCE, null, null);
		register(Abilities.DASH, null, null);
		//? if > 1.20.3 {
		register(Abilities.EXTRA_REACH, null, null);
		//?}
		register(Abilities.FEATHER_FALLING, null, null);
		register(Abilities.FIRE_RESISTANCE, null, null);
		register(Abilities.HASTE, null, null);
		register(Abilities.HEALTH_VISION, null, null);
		register(Abilities.KNOCKBACK_RESISTANCE, null, null);
		register(Abilities.MORE_DROPS, null, null);
		register(Abilities.PROJECTILE_PROTECTION, null, null);
		register(Abilities.RESISTANCE, null, null);
		register(Abilities.SATURATION, null, null);
		register(Abilities.SECOND_WIND, null, null);
		register(Abilities.SHIELD_BASH, null, null);
		//? if > 1.20.3 {
		register(Abilities.SMALLER, null, null);
		//?}
		register(Abilities.SPEED, null, null);
		//register(Abilities.TRIDENT, null, null);

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
