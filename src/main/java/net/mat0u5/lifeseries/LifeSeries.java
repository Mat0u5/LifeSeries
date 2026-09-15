package net.mat0u5.lifeseries;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.MainConfig;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.MatLibRegistry;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.registries.ModRegistries;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaSkins;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.seasons.util.SeasonChanger;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.interfaces.ClientAccessor;
import net.mat0u5.lifeseries.utils.other.ModBuiltInPacks;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.matlib.MatLib;
import net.mat0u5.matlib.events.EventFactory;
import net.mat0u5.matlib.events.common.CommonRegistryEvents;
import net.mat0u5.matlib.events.server.ServerLanguageEvents;
import net.mat0u5.matlib.events.server.ServerPackSourceEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class LifeSeries {
	public static final String MOD_VERSION = "1.5.9";
	public static final String MOD_ID = "lifeseries";

	public static final boolean DEBUG = false;
	public static final boolean FORCE_DEV = false;
	public static final boolean ISOLATED_ENVIRONMENT = false;
	public static final Seasons DEFAULT_SEASON = Seasons.UNASSIGNED;
	public static boolean MOD_DISABLED = false;
	@Nullable
	public static volatile MinecraftServer server;
	public static volatile Thread serverThread;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static ConfigManager config;
	private static ClientAccessor clientAccessor;
	public static Season currentSeason;
	public static Session currentSession;
	public static LivesManager livesManager;
	public static Blacklist blacklist;
	public static ConfigManager seasonConfig;

	public static void onInitialize() {
		LOGGER.info("Initializing Life Series [{} {} ({})] with MatLib [{}]...", MatLib.platform().loader().name(), MatLib.platform().mcVersion(), MOD_VERSION, MatLib.MOD_VERSION);

		ServerPackSourceEvents.LOAD_PACK.register(consumer -> ModBuiltInPacks.loadPacks(consumer, PackType.SERVER_DATA));
		ServerLanguageEvents.LOAD_LANG_FILES.register(() -> List.of("/resourcepacks/lifeseries/assets/lifeseries/lang/en_us.json"));
		CommonRegistryEvents.PRE_FREEZE.register(ModRegistries::registerModStuff);
		MatLibRegistry.register();

		config = new MainConfig();
		NetworkHandlerServer.reload();
		ConfigManager.moveOldMainFileIfExists();
		SnailSkins.createConfig();
		TriviaSkins.createConfig();

		MOD_DISABLED = config.getOrCreateProperty("modDisabled", "false").equalsIgnoreCase("true");
		String seasonStr = config.getOrCreateProperty("currentSeries", DEFAULT_SEASON.getId());

		SeasonChanger.initializeSeason(Seasons.getSeasonFromStringName(seasonStr));
		Seasons.getSeasons().forEach(seasons -> seasons.getSeasonInstance().createConfig());

		//? fabric || (forge && > 1.21) {
		MobRegistry.registerAttributes();
		//?}

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

	public static ClientAccessor getClientAccessor() {
		return clientAccessor;
	}

	public static void setClientAccessor(ClientAccessor helper) {
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
		if (clientAccessor == null) return true;
		return clientAccessor != null && clientAccessor.isRunningIntegratedServer();
	}

	public static boolean isLogicalNonDisabled() {
		return isLogicalSide() && !modDisabled();
	}
	public static boolean isClientOrDisabled() {
		return !isLogicalSide() || modDisabled();
	}

	public static boolean isClientPlayer(UUID uuid) {
		return clientAccessor != null && clientAccessor.isMainClientPlayer(uuid);
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

	public static boolean isMainThread() {
		Thread thread = serverThread;
		return thread != null && Thread.currentThread() == thread;
	}

	public static void requireMainThread() {
		if (!isMainThread()) {
			if (VersionControl.isDevVersion()) {
				throw new IllegalStateException("[LifeSeries] requireMainThread fail: " + Thread.currentThread().getName());
			}
			else {
				LifeSeries.LOGGER.error("[LifeSeries] requireMainThread fail", new Throwable());
			}
		}
	}
}