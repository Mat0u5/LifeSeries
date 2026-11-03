package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;

import java.util.List;

public class FinalLifeConfig extends SeasonConfig {
	public static final List<String> BLACKLISTED_ITEMS = List.of(
			"lectern",
			"bookshelf",
			//? if >= 1.21
			"mace",
			"end_crystal",
			"#minecraft:head_armor",
			//? if >= 1.21.11
			"#minecraft:spears",
			"elytra"
	);

	public static final List<String> BLACKLISTED_BLOCKS = List.of(
			"lectern",
			"bookshelf"
	);
	public static final List<String> CLAMPED_ENCHANTMENTS = List.of(
			"sharpness",
			"smite",
			"bane_of_arthropods",
			"fire_aspect",
			"knockback",
			//? if <= 1.20.3 {
			//"sweeping",
			//?} else {
			"sweeping_edge",
			//?}

			"power",
			"punch",

			"protection",
			"projectile_protection",
			"blast_protection",
			"fire_protection",
			"feather_falling",
			"thorns",

			//? if >= 1.21 {
			"breach",
			"density",
			"wind_burst",
			//?}

			"multishot",
			"piercing",
			"quick_charge"
	);

	protected FinalLifeConfig() {
		super(Seasons.FINAL_LIFE);
		BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
		BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
		BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
		BLACKLIST_RECIPES.defaultValue(List.of("enchanting_table"));
		DEFAULT_LIVES.defaultValue(6);
	}
}
