package net.mat0u5.lifeseries.client.utils;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.utils.enums.MinimalArmorBehavior;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.client.utils.ClientResourcePacks;

import static net.mat0u5.lifeseries.client.LifeSeriesClient.clientConfig;

public class LifeSeriesResourcePacks {
	private static final String MINIMAL_ARMOR_RESOURCEPACK = "lifeseries:minimal_armor";
	private static final String NICELIFE_NETHER_RESOURCEPACK = "lifeseries:nicelife";

	public static void checkClientPacks() {
		boolean enableMinimalArmorPack = true;
		MinimalArmorBehavior armorbehavior = clientConfig.MINIMAL_ARMOR.get();
		if (armorbehavior == MinimalArmorBehavior.FALSE) enableMinimalArmorPack = false;
		if (armorbehavior == MinimalArmorBehavior.MATCH_SERVER) enableMinimalArmorPack = LifeSeriesClient.SERVER_MINIMAL_ARMOR;

		ClientResourcePacks.setClientResourcepack(MINIMAL_ARMOR_RESOURCEPACK, enableMinimalArmorPack);
		ClientResourcePacks.setClientResourcepack(NICELIFE_NETHER_RESOURCEPACK, (LifeSeries.isSeason(Seasons.NICE_LIFE) && LifeSeriesClient.NICELIFE_SNOWY_NETHER));
	}
}
