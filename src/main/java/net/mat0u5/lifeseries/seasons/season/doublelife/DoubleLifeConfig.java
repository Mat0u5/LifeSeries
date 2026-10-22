package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.config.LifeSeriesConfigTypes;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;

import java.util.List;

public class DoubleLifeConfig extends SeasonConfig {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            "enchanting_table",
            //? if >= 1.21
            "mace",
            "end_crystal",
            "#minecraft:head_armor",
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

    public final ConfigurableValue<Boolean> ANNOUNCE_SOULMATES = ConfigurableValue.register(this, "announce_soulmates", false)
            .name("Announce Soulmates")
            .description("Tells you who your soulmate is instead of it saying 'Your soulmate is ????'")
            .group("season");
    public final ConfigHeader GROUP_SOULMATES = ConfigHeader.register(this, "group_soulmates", "Soulmates")
            .description("Manually edit soulmate pairings. Both players need to have joined this server at least once.")
            .group("season.soulmates").groupHeader(true);
    public final ConfigHeader GROUP_SOULBIND = ConfigHeader.register(this, "group_soulbind", "More Soulbind Options")
            .group("season.soulbind").groupHeader(true);
    public final ConfigurableValue<Boolean> BREAKUP_LAST_PAIR_STANDING = ConfigurableValue.register(this, "breakup_last_pair_standing", false)
            .name("Breakup Last Pair Standing")
            .description("Once only two players are left, they will be broken up as soulmates for a final showdown.")
            .group("season");
    public final ConfigurableValue<Boolean> DISABLE_START_TELEPORT = ConfigurableValue.register(this, "disable_start_teleport", false)
            .name("Disable Start Teleport")
            .description("Disables the player spreading over the map when the first session starts.")
            .group("season");

    public final ConfigurableValue<Boolean> SOULBOUND_FOOD = ConfigurableValue.register(this, "soulbound_food", false)
            .name("Soulbound Food")
            .description("Makes your food bar shared with your soulmate, just like the health bar.")
            .group("season.soulbind");
    public final ConfigurableValue<Boolean> SOULBOUND_EFFECTS = ConfigurableValue.register(this, "soulbound_effects", false)
            .name("Soulbound Effects")
            .description("Makes your effects be shared with your soulmate.")
            .group("season.soulbind");
    public final ConfigurableValue<Boolean> SOULBOUND_INVENTORIES = ConfigurableValue.register(this, "soulbound_inventories", false)
            .name("Soulbound Inventories")
            .description("Makes your inventory be shared with your soulmate. \nWARNING: There could be some ways of abusing this (duping etc). Use with caution.")
            .group("season.soulbind");
    public final ConfigurableValue<Boolean> SOULBOUND_BOOGEYMAN = ConfigurableValue.register(this, "soulbound_boogeyman", false)
            .name("Soulbound Boogeymen (If enabled)")
            .description("Makes you become the Boogeyman if your soulmate is one - curing one will cure the other as well.")
            .group("season.soulbind");
    public final ConfigurableValue<Boolean> SOULBOUND_SECRET_SOCIETY = ConfigurableValue.register(this, "soulbound_secret_society", false)
            .name("Soulbound Secret Society (If enabled)")
            .description("Makes you become a member of the Secret Society if your soulmate is one.")
            .group("season.soulbind");
    public final ConfigurableValue<Boolean> SOULBOUND_LIVES = ConfigurableValue.register(this, "soulbound_lives", true, LifeSeriesConfigTypes.BOOLEAN_REVERSE_HEADER)
            .name("Soulbound Lives")
            .description("Controls whether soulmates share their life count.")
            .group("season.soulbind.lives").groupHeader(true);
    public final ConfigurableValue<Boolean> SOULBOUND_LIVES_ASSIGN_MATCH = ConfigurableValue.register(this, "soulbound_lives_assign_match", false)
            .name("Only Assign Soulmates With Same Lives")
            .description("Makes it so that during assignment, only the players with the same amount of lives can become soulmates.")
            .group("season.soulbind.lives");
    public final ConfigurableValue<Boolean> SOULMATES_PVP_ALLOWED = ConfigurableValue.register(this, "soulmates_pvp_allowed", true)
            .name("Soulmates PvP Allowed")
            .description("Controls whether soulmates can hit each other.")
            .group("season");
    public final ConfigurableValue<Double> SOULMATES_ASSIGN_MINUTE = ConfigurableValue.register(this, "soulmates_assign_time", 1.0, ConfigTypes.MINUTES)
            .name("Soulmates Assign Time")
            .description("How many minutes after the session starts the soulmates get assigned.")
            .group("season");

    public final ConfigurableValue<Boolean> SOULMATE_LOCATOR_BAR = ConfigurableValue.register(this, "soulbound_locator_bar", false)
            .name("Soulmate Locator Bar")
            .description("Makes ONLY your soulmate appear on the locator bar.")
            .group("season");

    public DoubleLifeConfig() {
        super(Seasons.DOUBLE_LIFE);
        removeEntry(NO_SEASON_SPECIFIC);
        removeEntry(DEATH_TITLE_MATCH_DEATH_MESSAGE);
        //? if < 1.21.6 {
        //removeEntry(SOULMATE_LOCATOR_BAR);
        //?}

        CUSTOM_ENCHANTER_ALGORITHM.defaultValue(true);
        BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
        LIVES_RANDOMIZE_MINUTE.defaultValue(1.25);
        LIVES_RANDOMIZE_MINUTE.description(LIVES_RANDOMIZE_MINUTE.getDescription() + "\n§cThis time should ALWAYS be after soulmates rolling!");
    }

    public static DoubleLifeConfig get() {
        return getActive(DoubleLifeConfig.class, DoubleLifeConfig::new);
    }
}
