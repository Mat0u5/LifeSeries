package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InBetweenChoices {
	private record BoundedChoice(Abilities ability, Integer minDeaths, Integer maxDeaths) {}
	private static final List<BoundedChoice> registered = new ArrayList<>();

	static {
		register(Abilities.SPEED, null, 2);
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
