package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;

public class EmptyAbility extends Ability {
	public EmptyAbility() {
		super(Abilities.EMPTY);
	}
	public EmptyAbility(Abilities ability) {
		super(ability);
	}

	@Override
	protected int loadHeartsChange() {
		return 0;
	}
}
