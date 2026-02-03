package net.mat0u5.lifeseries.seasons.season.thirdlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ThirdLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
        //? if >= 1.21
        "mace",
        "end_crystal",
        "leather_helmet",
        "chainmail_helmet",
        "golden_helmet",
        "iron_helmet",
        "diamond_helmet",
        "netherite_helmet",
        "turtle_helmet",
        //? if >= 1.21.9
        "copper_helmet",
        "elytra"
    );

    public static final List<String> BLACKLISTED_BLOCKS = new ArrayList<>();
    public static final List<String> CLAMPED_ENCHANTMENTS = List.of(
            "sharpness",
            "smite",
            "bane_of_arthropods",
            "fire_aspect",
            "knockback",
            //? if <= 1.20.3 {
            /*"sweeping",
            *///?} else {
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

    public ThirdLifeConfig() {
        super("./config/"+ Main.MOD_ID,"thirdlife.properties");
    }
    public ThirdLifeConfig(String folderPath, String filePath) {
        super(folderPath, filePath);
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        super.instantiateProperties();
    }
}
