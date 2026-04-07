package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.utils.other.TextUtils;

public class PastLifeConfig extends SeasonConfig {
    public PastLifeConfig() {
        super(Seasons.PAST_LIFE);
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue = 6;
        BOOGEYMAN.defaultValue = true;
        BOOGEYMAN_ADVANCED_DEATHS.defaultValue = true;
        SECRET_SOCIETY.defaultValue = true;
        super.instantiateProperties();
    }
}
