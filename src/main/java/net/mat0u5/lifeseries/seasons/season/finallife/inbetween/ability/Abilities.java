package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.SpeedAbility;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum Abilities {
	//TODO this will need to support non-item stack icons
	SPEED(SpeedAbility::new, Items.STONE.getDefaultInstance(), "Speed", "Permanent Speed I.", null);

	private final ItemStack icon;
	private final String title;
	private final String description;
	@Nullable
	private final String additionalDescription;
	private final Supplier<Ability> supplier;

	Abilities(Supplier<Ability> supplier, ItemStack icon, String title, String description, String additionalDescription) {
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
	public ItemStack icon() {
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
