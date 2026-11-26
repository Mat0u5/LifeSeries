package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.LifeSeriesConfigTypes;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window.InBetweenFileSync;
import net.mat0u5.lifeseries.utils.enums.TriviaGuiType;
import net.mat0u5.matlib.config.entry.ConfigGuiEntry;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Objects;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class FinalLifeConfig extends SeasonConfig {
	public static final List<String> BLACKLISTED_ITEMS = List.of(
			"lectern",
			"bookshelf",
			//? if >= 1.21
			"mace",
			"end_crystal",
			//? if >= 1.21.11
			"#minecraft:spears",
			"elytra",
			"golden_apple",
			"enchanted_golden_apple"
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


	public final ConfigurableValue<Boolean> FINAL_LIFE_BOOGEYMAN = ConfigurableValue.register(this, "final_life_boogeyman", true)
			.name("Final Life Boogeyman")
			.description("Controls whether the Final Life Boogeyman is activated")
			.setNew(true)
			.setRequiresReload(true)
			.group("season.boogeyman").groupHeader(true);
	public final ConfigurableValue<List<String>> FINAL_LIFE_BOOGEYMAN_LIST = ConfigurableValue.register(this, "final_life_boogeyman_list", List.of(), ConfigTypes.STRING_LIST)
			.name("Boogeyman List")
			.description("List of active boogeyman UUIDs")
			.setNew(true)
			.group("season.boogeyman");
	public final ConfigurableValue<Boolean> FINAL_LIFE_BOOGEYMAN_HIDE_IN_TAB = ConfigurableValue.register(this, "final_life_boogeyman_hide_in_tab", true)
			.name("Hide in Tab")
			.description("Controls whether the secret boogeyman player is shown in the tab list")
			.setNew(true)
			.group("season.boogeyman");
	public final ConfigurableValue<Boolean> FINAL_LIFE_BOOGEYMAN_HIDE_ARMOR = ConfigurableValue.register(this, "final_life_boogeyman_hide_armor", true)
			.name("Hide Armor")
			.description("Controls whether the armor of the boogeyman shows up.")
			.setNew(true)
			.serverShared()
			.group("season.boogeyman");
	public final ConfigurableValue<Boolean> FINAL_LIFE_BOOGEYMAN_THROWABLE_WEAPONS = ConfigurableValue.register(this, "final_life_boogeyman_throwable_weapons", true)
			.name("Throwable Weapons")
			.description("Controls whether the boogeyman can throw their swords and axes.")
			.setNew(true)
			.group("season.boogeyman.throwable").groupHeader(true);
	public final ConfigurableValue<Double> FINAL_LIFE_BOOGEYMAN_THROW_RANGE = ConfigurableValue.register(this, "final_life_boogeyman_throw_range", 25.0)
			.name("Throw Range")
			.description("How many blocks a thrown weapon flies before it returns to the boogeyman.")
			.setNew(true)
			.group("season.boogeyman.throwable");
	public final ConfigurableValue<Double> FINAL_LIFE_BOOGEYMAN_THROW_CHARGE_TIME = ConfigurableValue.register(this, "final_life_boogeyman_throw_charge_time", 0.5)
			.name("Throw Charge Time")
			.description("How many seconds the boogeyman has to hold right click before releasing throws the weapon. Do note that changing this will desync the pull animation, which is 0.5s long.")
			.setNew(true)
			.group("season.boogeyman.throwable");
	public final ConfigurableValue<Boolean> FINAL_LIFE_BOOGEYMAN_SPEED = ConfigurableValue.register(this, "final_life_boogeyman_speed", true)
			.name("Speed Boost")
			.description("Controls whether the boogeyman has a permanent speed effect.")
			.setNew(true)
			.group("season.boogeyman");

	public final ConfigurableValue<Boolean> IN_BETWEEN = ConfigurableValue.register(this, "in_between", true)
			.name("The In-Between")
			.description("Controls whether players who die are sent to the In-Between before respawning.")
			.setNew(true)
			.group("season.inbetween").groupHeader(true);
	public final ConfigurableValue<Boolean> IN_BETWEEN_ISOLATE_CHAT = ConfigurableValue.register(this, "in_between_isolate_chat", true)
			.name("Isolate Chat")
			.description("Controls whether the In-Between players have isolated chat.")
			.setNew(true)
			.serverShared()
			.group("season.inbetween");
	public final ConfigurableValue<Boolean> IN_BETWEEN_WINDOWS = ConfigurableValue.register(this, "in_between_windows", true)
			.name("Memory Windows")
			.description("Controls whether the memory windows show up in the In-Between.")
			.setNew(true)
			.group("season.inbetween");
	public final ConfigurableValue<Boolean> IN_BETWEEN_TRIVIA = ConfigurableValue.register(this, "in_between_trivia", true)
			.name("Enable Trivia")
			.description("Controls whether the trivia bot shows up in the In-Between.")
			.setNew(true)
			.group("season.inbetween.trivia").groupHeader(true);
	public final ConfigurableValue<List<String>> TRIVIA_DEATH_NUMBERS = ConfigurableValue.register(this, "trivia_death_numbers", List.of("3"), ConfigTypes.STRING_LIST)
			.name("Trivia Death Numbers")
			.description("Controls on which deaths the trivia bot shows up in the In-Between.")
			.setNew(true)
			.group("season.inbetween.trivia");
	public final ConfigurableValue<TriviaGuiType> TRIVIA_GUI_TYPE = ConfigurableValue.register(this, "gui_type", TriviaGuiType.NICE_LIFE)
			.name("Gui Type")
			.description("Controls the trivia gui style.")
			.group("season.inbetween.trivia");
	public final ConfigurableValue<Integer> TRIVIA_QUESTION_TIME = ConfigurableValue.register(this, "trivia_question_time", 68, ConfigTypes.SECONDS)
			.name("Trivia Question Time")
			.description("Controls how much time people have to answer the trivia, in seconds.")
			.group("season.inbetween.trivia");
	public final ConfigHeader GROUP_TRIVIA_QUESTIONS = ConfigHeader.register(this, "group_trivia_questions", "Trivia Questions")
			.group("season.inbetween.trivia.questions").groupHeader(true);
	public final ConfigHeader GROUP_ABILITIES = ConfigHeader.register(this, "group_abilities", "Abilities")
			.group("season.abilities").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_AIR_JUMP_HEALTH = ConfigurableValue.register(this, "ability_air_jump_health", 0, ConfigTypes.HEARTS)
			.name("Air Jump Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_BLAST_RESISTANCE_HEALTH = ConfigurableValue.register(this, "ability_blast_resistance_health", 2, ConfigTypes.HEARTS)
			.name("Blast Resistance Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_DASH_HEALTH = ConfigurableValue.register(this, "ability_dash_health", 2, ConfigTypes.HEARTS)
			.name("Dash Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_EXTRA_REACH_HEALTH = ConfigurableValue.register(this, "ability_extra_reach_health", 2, ConfigTypes.HEARTS)
			.name("Extra Reach Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_FEATHER_FALLING_HEALTH = ConfigurableValue.register(this, "ability_feather_falling_health", 2, ConfigTypes.HEARTS)
			.name("Feather Falling Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_FIRE_RESISTANCE_HEALTH = ConfigurableValue.register(this, "ability_fire_resistance_health", 4, ConfigTypes.HEARTS)
			.name("Fire Resistance Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_HASTE_HEALTH = ConfigurableValue.register(this, "ability_haste_health", 6, ConfigTypes.HEARTS)
			.name("Haste Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_HEALTH_VISION_HEALTH = ConfigurableValue.register(this, "ability_health_vision_health", 8, ConfigTypes.HEARTS)
			.name("Health Vision Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_KNOCKBACK_RESISTANCE_HEALTH = ConfigurableValue.register(this, "ability_knockback_resistance_health", 6, ConfigTypes.HEARTS)
			.name("Knockback Resistance Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_MORE_DROPS_HEALTH = ConfigurableValue.register(this, "ability_more_drops_health", 6, ConfigTypes.HEARTS)
			.name("More Drops Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_PROJECTILE_PROTECTION_HEALTH = ConfigurableValue.register(this, "ability_projectile_protection_health", 4, ConfigTypes.HEARTS)
			.name("Projectile Protection Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_RESISTANCE_HEALTH = ConfigurableValue.register(this, "ability_resistance_health", 2, ConfigTypes.HEARTS)
			.name("Resistance Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_SATURATION_HEALTH = ConfigurableValue.register(this, "ability_saturation_health", 2, ConfigTypes.HEARTS)
			.name("Saturation Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_SECOND_WIND_HEALTH = ConfigurableValue.register(this, "ability_second_wind_health", 6, ConfigTypes.HEARTS)
			.name("Second Wind Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_SHIELD_BASH_HEALTH = ConfigurableValue.register(this, "ability_shield_bash_health", 6, ConfigTypes.HEARTS)
			.name("Shield Bash Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_SMALLER_HEALTH = ConfigurableValue.register(this, "ability_smaller_health", 6, ConfigTypes.HEARTS)
			.name("Smaller Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	public final ConfigurableValue<Integer> ABILITY_SPEED_HEALTH = ConfigurableValue.register(this, "ability_speed_health", 6, ConfigTypes.HEARTS)
			.name("Speed Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	/*
	public final ConfigurableValue<Integer> ABILITY_TRIDENT_HEALTH = ConfigurableValue.register(this, "ability_trident_health", 2, ConfigTypes.HEARTS)
			.name("Trident Ability Health")
			.description("Controls how much health this ability will add/subtract.")
			.group("season.abilities");
	 */


	protected FinalLifeConfig() {
		super(Seasons.FINAL_LIFE);
		removeEntry(NO_SEASON_SPECIFIC);
		BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
		BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
		BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
		BLACKLIST_RECIPES.defaultValue(List.of("enchanting_table"));
		DEFAULT_LIVES.defaultValue(6);
		DISABLE_HELMET_RENDERING.defaultValue(true);
		ENABLE_START_TELEPORT.defaultValue(true);
		FINAL_LIFE_BOOGEYMAN_HIDE_ARMOR.onChange((oldValue, newValue) -> {
			if (isActive()) PlayerUtils.resendVisibleEquipment();
		});
		IN_BETWEEN_WINDOWS.onChange((oldValue, newValue) -> { if (isActive()) InBetweenFileSync.onReload(); });
		IN_BETWEEN_TRIVIA.onChange((oldValue, newValue) -> { if (isActive()) InBetweenFileSync.onReload(); });
		TRIVIA_DEATH_NUMBERS.onChange((oldValue, newValue) -> { if (isActive()) InBetweenFileSync.onReload(); });
	}

	public static FinalLifeConfig get() {
		return getActive(FinalLifeConfig.class, FinalLifeConfig::new);
	}

	@Override
	public void onDynamicEntryUpdate(ServerPlayer player, ConfigGuiEntry entry) {
		super.onDynamicEntryUpdate(player, entry);
		String typeId = entry.type.id();
		String key = entry.key;
		if (typeId.equals(LifeSeriesConfigTypes.MODIFIABLE_TEXT.id()) && !entry.args.isEmpty()) {
			if (Objects.equals(key, ModifiableText.FINALLIFE_BOOGEYMAN_USERNAME.getConfigKey())) {
				if (currentSeason instanceof FinalLife finalLife) {
					finalLife.finalLifeBoogeymanManager.reloadDisguises();
				}
			}
		}
	}
}
