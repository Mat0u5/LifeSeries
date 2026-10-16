package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;

public class PastLifeConfig extends SeasonConfig {
    public PastLifeConfig() {
        super(Seasons.PAST_LIFE);
        BLACKLIST_ITEMS.defaultValue(ThirdLifeConfig.BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue(ThirdLifeConfig.BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(ThirdLifeConfig.CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue(6);
        BOOGEYMAN.defaultValue(true);
        BOOGEYMAN_ADVANCED_DEATHS.defaultValue(true);
        SECRET_SOCIETY.defaultValue(true);
    }
}
