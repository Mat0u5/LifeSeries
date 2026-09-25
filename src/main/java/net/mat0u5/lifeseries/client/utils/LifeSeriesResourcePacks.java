package net.mat0u5.lifeseries.client.utils;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.config.ClientConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.client.utils.ClientResourcePacks;

import static net.mat0u5.lifeseries.client.LifeSeriesClient.clientConfig;

public class LifeSeriesResourcePacks {
	private static final String MINIMAL_ARMOR_RESOURCEPACK = "lifeseries:minimal_armor";
	private static final String NICELIFE_ARMOR_RESOURCEPACK = "lifeseries:nicelife";

	public static void checkClientPacks() {
		ClientResourcePacks.setClientResourcepack(MINIMAL_ARMOR_RESOURCEPACK, ClientConfig.MINIMAL_ARMOR.get(clientConfig));
		ClientResourcePacks.setClientResourcepack(NICELIFE_ARMOR_RESOURCEPACK, (LifeSeries.isSeason(Seasons.NICE_LIFE) && LifeSeriesClient.NICELIFE_SNOWY_NETHER));
	}
}
