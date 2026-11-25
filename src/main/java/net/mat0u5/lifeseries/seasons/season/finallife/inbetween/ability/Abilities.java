package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.SpeedAbility;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum Abilities {
	SPEED(SpeedAbility::new, AbilityIcon.effect("speed"), "Speed", "Permanent Speed I.", null);

	private final AbilityIcon icon;
	private final String title;
	private final String description;
	@Nullable
	private final String additionalDescription;
	private final Supplier<Ability> supplier;

	Abilities(Supplier<Ability> supplier, AbilityIcon icon, String title, String description, String additionalDescription) {
		this.supplier = supplier;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.additionalDescription = additionalDescription;
	}

	public Ability createAbility() {
		Ability instance = supplier.get();
		instance.reloadHealthChange();
		return instance;
	}
	public AbilityIcon icon() {
		return this.icon;
	}
	public String title() {
		return this.title;
	}
	public String description() {
		return this.description;
	}
	public String additionalDescription() {
		return this.additionalDescription;
	}
}
