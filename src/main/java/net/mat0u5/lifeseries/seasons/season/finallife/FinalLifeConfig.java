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
			"enchanted_golden_apple",
			"potion",
			"splash_potion",
			"lingering_potion",
			"tipped_arrow"
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
	public final ConfigurableValue<List<Integer>> TRIVIA_DEATH_NUMBERS = ConfigurableValue.register(this, "trivia_death_numbers", List.of(3), ConfigTypes.INTEGER_LIST)
			.bounded(1, null)
			.name("Trivia Death Numbers")
			.description("Controls on which deaths the trivia bot shows up in the In-Between.")
			.setNew(true)
			.group("season.inbetween.trivia");
	public final ConfigurableValue<TriviaGuiType> TRIVIA_GUI_TYPE = ConfigurableValue.register(this, "gui_type", TriviaGuiType.NICE_LIFE)
			.name("Gui Type")
			.description("Controls the trivia gui style.")
			.group("season.inbetween.trivia");
	public final ConfigurableValue<Integer> TRIVIA_QUESTION_TIME = ConfigurableValue.register(this, "trivia_question_time", 68, ConfigTypes.SECONDS)
			.bounded(1, null)
			.name("Trivia Question Time")
			.description("Controls how much time people have to answer the trivia, in seconds.")
			.group("season.inbetween.trivia");
	public final ConfigHeader GROUP_TRIVIA_QUESTIONS = ConfigHeader.register(this, "group_trivia_questions", "Trivia Questions")
			.group("season.inbetween.trivia.questions").groupHeader(true);
	public final ConfigHeader GROUP_ABILITIES = ConfigHeader.register(this, "group_abilities", "Abilities")
			.group("season.abilities").groupHeader(true);
	public final ConfigurableValue<Boolean> ABILITY_AIR_JUMP_ENABLED = ConfigurableValue.register(this, "ability_air_jump_enabled", true)
			.name("Air Jump Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.airjump").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_AIR_JUMP_HEARTS = ConfigurableValue.register(this, "ability_air_jump_hearts", 0, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.airjump");
	public final ConfigurableValue<Boolean> ABILITY_BLAST_RESISTANCE_ENABLED = ConfigurableValue.register(this, "ability_blast_resistance_enabled", true)
			.name("Blast Resistance Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.blastresistance").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_BLAST_RESISTANCE_HEARTS = ConfigurableValue.register(this, "ability_blast_resistance_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.blastresistance");
	public final ConfigurableValue<Boolean> ABILITY_DASH_ENABLED = ConfigurableValue.register(this, "ability_dash_enabled", true)
			.name("Dash Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.dash").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_DASH_HEARTS = ConfigurableValue.register(this, "ability_dash_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.dash");
	public final ConfigurableValue<Boolean> ABILITY_EXTRA_REACH_ENABLED = ConfigurableValue.register(this, "ability_extra_reach_enabled", true)
			.name("Extra Reach Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.extrareach").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_EXTRA_REACH_HEARTS = ConfigurableValue.register(this, "ability_extra_reach_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.extrareach");
	public final ConfigurableValue<Boolean> ABILITY_FEATHER_FALLING_ENABLED = ConfigurableValue.register(this, "ability_feather_falling_enabled", true)
			.name("Feather Falling Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.featherfalling").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_FEATHER_FALLING_HEARTS = ConfigurableValue.register(this, "ability_feather_falling_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.featherfalling");
	public final ConfigurableValue<Boolean> ABILITY_FIRE_RESISTANCE_ENABLED = ConfigurableValue.register(this, "ability_fire_resistance_enabled", true)
			.name("Fire Resistance Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.fireresistance").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_FIRE_RESISTANCE_HEARTS = ConfigurableValue.register(this, "ability_fire_resistance_hearts", 2, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.fireresistance");
	public final ConfigurableValue<Boolean> ABILITY_HASTE_ENABLED = ConfigurableValue.register(this, "ability_haste_enabled", true)
			.name("Haste Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.haste").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_HASTE_HEARTS = ConfigurableValue.register(this, "ability_haste_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.haste");
	public final ConfigurableValue<Boolean> ABILITY_HEALTH_VISION_ENABLED = ConfigurableValue.register(this, "ability_health_vision_enabled", true)
			.name("Health Vision Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.healthvision").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_HEALTH_VISION_HEARTS = ConfigurableValue.register(this, "ability_health_vision_hearts", 4, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.healthvision");
	public final ConfigurableValue<Boolean> ABILITY_KNOCKBACK_RESISTANCE_ENABLED = ConfigurableValue.register(this, "ability_knockback_resistance_enabled", true)
			.name("Knockback Resistance Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.knockbackresistance").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_KNOCKBACK_RESISTANCE_HEARTS = ConfigurableValue.register(this, "ability_knockback_resistance_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.knockbackresistance");
	public final ConfigurableValue<Boolean> ABILITY_MORE_DROPS_ENABLED = ConfigurableValue.register(this, "ability_more_drops_enabled", true)
			.name("More Drops Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.moredrops").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_MORE_DROPS_HEARTS = ConfigurableValue.register(this, "ability_more_drops_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.moredrops");
	public final ConfigurableValue<Boolean> ABILITY_PROJECTILE_PROTECTION_ENABLED = ConfigurableValue.register(this, "ability_projectile_protection_enabled", true)
			.name("Projectile Protection Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.projectileprotection").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_PROJECTILE_PROTECTION_HEARTS = ConfigurableValue.register(this, "ability_projectile_protection_hearts", 2, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.projectileprotection");
	public final ConfigurableValue<Boolean> ABILITY_RESISTANCE_ENABLED = ConfigurableValue.register(this, "ability_resistance_enabled", true)
			.name("Resistance Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.resistance").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_RESISTANCE_HEARTS = ConfigurableValue.register(this, "ability_resistance_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.resistance");
	public final ConfigurableValue<Boolean> ABILITY_SATURATION_ENABLED = ConfigurableValue.register(this, "ability_saturation_enabled", true)
			.name("Saturation Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.saturation").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SATURATION_HEARTS = ConfigurableValue.register(this, "ability_saturation_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.saturation");
	public final ConfigurableValue<Boolean> ABILITY_SECOND_WIND_ENABLED = ConfigurableValue.register(this, "ability_second_wind_enabled", true)
			.name("Second Wind Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.secondwind").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SECOND_WIND_HEARTS = ConfigurableValue.register(this, "ability_second_wind_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.secondwind");
	public final ConfigurableValue<Boolean> ABILITY_SHIELD_BASH_ENABLED = ConfigurableValue.register(this, "ability_shield_bash_enabled", true)
			.name("Shield Bash Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.shieldbash").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SHIELD_BASH_HEARTS = ConfigurableValue.register(this, "ability_shield_bash_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.shieldbash");
	public final ConfigurableValue<Boolean> ABILITY_SMALLER_ENABLED = ConfigurableValue.register(this, "ability_smaller_enabled", true)
			.name("Smaller Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.smaller").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SMALLER_HEARTS = ConfigurableValue.register(this, "ability_smaller_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.smaller");
	public final ConfigurableValue<Boolean> ABILITY_SNAIL_PROTECT_ENABLED = ConfigurableValue.register(this, "ability_snail_protect_enabled", true)
			.name("Snail Protect Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.snailprotect").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SNAIL_PROTECT_HEARTS = ConfigurableValue.register(this, "ability_snail_protect_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.snailprotect");
	public final ConfigurableValue<Boolean> ABILITY_SOULMATE_ENABLED = ConfigurableValue.register(this, "ability_soulmate_enabled", true)
			.name("Soulmate Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.soulmate").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SOULMATE_HEARTS = ConfigurableValue.register(this, "ability_soulmate_hearts", 10, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.soulmate");
	public final ConfigurableValue<Boolean> ABILITY_SPEED_ENABLED = ConfigurableValue.register(this, "ability_speed_enabled", true)
			.name("Speed Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.speed").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_SPEED_HEARTS = ConfigurableValue.register(this, "ability_speed_hearts", 3, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.speed");
	public final ConfigurableValue<Boolean> ABILITY_THROW_SWORDS_ENABLED = ConfigurableValue.register(this, "ability_throw_swords_enabled", true)
			.name("Throw Swords Enabled")
			.description("Controls whether this ability is enabled.")
			.setNew(true)
			.group("season.abilities.throwswords").groupHeader(true);
	public final ConfigurableValue<Integer> ABILITY_THROW_SWORDS_HEARTS = ConfigurableValue.register(this, "ability_throw_swords_hearts", 2, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
			.group("season.abilities.throwswords");
	public final ConfigurableValue<Double> ABILITY_THROW_SWORDS_THROW_RANGE = ConfigurableValue.register(this, "ability_throw_swords_throw_range", 25.0)
			.bounded(0.0, null)
			.name("Throw Range")
			.description("How many blocks a thrown weapon flies before it returns to its thrower.")
			.setNew(true)
			.group("season.abilities.throwswords");
	public final ConfigurableValue<Double> ABILITY_THROW_SWORDS_THROW_CHARGE_TIME = ConfigurableValue.register(this, "ability_throw_swords_throw_charge_time", 0.5)
			.bounded(0.0, null)
			.name("Throw Charge Time")
			.description("How many seconds you have to hold right click before releasing throws the weapon. Do note that changing this will desync the pull animation, which is 0.5s long.")
			.setNew(true)
			.group("season.abilities.throwswords");
	/*
	public final ConfigurableValue<Integer> ABILITY_TRIDENT_HEARTS = ConfigurableValue.register(this, "ability_trident_hearts", 1, ConfigTypes.PLAYER_HEARTS)
			.bounded(-10, 10)
			.name("Trident Ability Hearts")
			.description("Controls how many hearts this ability will add/subtract.")
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
