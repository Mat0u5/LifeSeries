package net.mat0u5.lifeseries;

import com.google.auto.service.AutoService;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.MainConfig;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.Registries;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaSkins;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.seasons.util.SeasonChanger;
import net.mat0u5.matlib.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.interfaces.LifeSeriesClientAccessor;
import net.mat0u5.lifeseries.utils.other.ModBuiltInPacks;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.matlib.MatLib;
import net.mat0u5.matlib.api.MatLibInitializer;
import net.mat0u5.matlib.events.server.ServerLanguageEvents;
import net.mat0u5.matlib.events.server.ServerPackSourceEvents;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@AutoService(MatLibInitializer.class)
public class LifeSeries implements MatLibInitializer {
	public static final String MOD_VERSION = "1.5.9";
	public static final String MOD_ID = "lifeseries";

	public static final boolean DEBUG = false;
	public static final boolean FORCE_DEV = false;
	public static final boolean ISOLATED_ENVIRONMENT = false;
	public static final Seasons DEFAULT_SEASON = Seasons.UNASSIGNED;
	public static boolean MOD_DISABLED = false;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static ConfigManager config;
	private static LifeSeriesClientAccessor clientAccessor;
	public static Season currentSeason;
	public static Session currentSession;
	public static LivesManager livesManager;
	public static Blacklist blacklist;
	public static ConfigManager seasonConfig;

	@Override
	public void onRegister() {
		ServerPackSourceEvents.LOAD_PACK.register(consumer -> ModBuiltInPacks.loadPacks(consumer, PackType.SERVER_DATA));
		ServerLanguageEvents.LOAD_LANG_FILES.register(() -> List.of("/resourcepacks/lifeseries/assets/lifeseries/lang/en_us.json"));
		Registries.register();
	}
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Life Series [{} {} ({})] with MatLib [{}]...", MatLib.platform().loader().name(), MatLib.platform().mcVersion(), MOD_VERSION, MatLib.MOD_VERSION);

		config = new MainConfig();
		NetworkHandlerServer.reload();
		ConfigManager.moveOldMainFileIfExists();
		SnailSkins.createConfig();
		TriviaSkins.createConfig();

		MOD_DISABLED = config.getOrCreateProperty("modDisabled", "false").equalsIgnoreCase("true");
		String seasonStr = config.getOrCreateProperty("currentSeries", DEFAULT_SEASON.getId());

		SeasonChanger.initializeSeason(Seasons.getSeasonFromStringName(seasonStr));
		Seasons.getSeasons().forEach(seasons -> seasons.getSeasonInstance().createConfig());

		if (!ISOLATED_ENVIRONMENT) {
			UpdateChecker.checkForMajorUpdates();
		}
		NetworkHandlerServer.initializeSimplePacketReceivers();
	}

	public static boolean modDisabled() {
		if (clientAccessor != null) {
			if (clientAccessor.isReplay()) return true;
			if (clientAccessor.serverHandshake() == HandshakeStatus.NOT_RECEIVED) return true;
			return clientAccessor.isDisabledServerSide();
		}
		return MOD_DISABLED;
	}

	public static boolean modFullyDisabled() {
		if (clientAccessor == null) return false;
		return clientAccessor.serverHandshake() == HandshakeStatus.NOT_RECEIVED;
	}

	public static void setDisabled(boolean disabled) {
		boolean previouslyDisabled = MOD_DISABLED;
		MOD_DISABLED = disabled;
		config.setProperty("modDisabled", String.valueOf(MOD_DISABLED));

		if (!previouslyDisabled && disabled) {
			SeasonChanger.changeSeasonTo(Seasons.UNASSIGNED);
		}
		if (!modDisabled()) {
			SeasonChanger.resetSeason();
		}
		SimplePackets.MOD_DISABLED.sendToAllClients(LifeSeries.MOD_DISABLED);
	}

	public static boolean hasClient() {
		return clientAccessor != null;
	}

	public static LifeSeriesClientAccessor getClientAccessor() {
		return clientAccessor;
	}

	public static void setClientAccessor(LifeSeriesClientAccessor helper) {
		clientAccessor = helper;
	}

	public static Seasons getSeason() {
		if (!isLogicalSide() && clientAccessor != null) {
			return clientAccessor.getCurrentSeason();
		}
		return currentSeason.getSeason();
	}

	public static boolean isSeason(Seasons season) {
		return getSeason() == season;
	}

	public static boolean isLogicalSide() {
		return MatLib.isLogicalSide();
	}

	public static boolean isLogicalNonDisabled() {
		return isLogicalSide() && !modDisabled();
	}
	public static boolean isClientOrDisabled() {
		return !isLogicalSide() || modDisabled();
	}

	public static ConfigManager getMainConfig() {
		return config;
	}

	public static Season currentSeason() {
		return currentSeason;
	}

	public static Session currentSession() {
		return currentSession;
	}
}