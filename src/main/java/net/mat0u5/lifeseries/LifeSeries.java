package net.mat0u5.lifeseries;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.MainConfig;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.platform.Platform;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.util.SeasonChanger;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.interfaces.ClientAccessor;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

//? if fabric {
import net.mat0u5.lifeseries.platform.fabric.FabricPlatform;
//?} neoforge {
/*import net.mat0u5.lifeseries.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import net.mat0u5.lifeseries.platform.forge.ForgePlatform;
 *///?}
//? fabric && <= 1.20.5 {
/*import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.registries.ModRegistries;
*///?}

public class LifeSeries {
	public static final String MOD_VERSION = "1.5.7.14-dev";
	public static final String MOD_ID = "lifeseries";
	private static final Platform PLATFORM = createPlatformInstance();

	public static final boolean DEBUG = false;
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
		LOGGER.info("Initializing Life Series [{} {} ({})]...", platform().loader().name(), platform().mcVersion(), MOD_VERSION);

		config = new MainConfig();
		NetworkHandlerServer.reload();
		ConfigManager.moveOldMainFileIfExists();
		SnailSkins.createConfig();

		MOD_DISABLED = config.getOrCreateProperty("modDisabled", "false").equalsIgnoreCase("true");
		String seasonStr = config.getOrCreateProperty("currentSeries", DEFAULT_SEASON.getId());

		SeasonChanger.initializeSeason(Seasons.getSeasonFromStringName(seasonStr));
		Seasons.getSeasons().forEach(seasons -> seasons.getSeasonInstance().createConfig());

		//? fabric || (forge && > 1.21) {
		MobRegistry.registerAttributes();
		//?}
		//? fabric && <= 1.20.5 {
		/*if (CompatibilityManager.fabricApiLoaded()) {
			ModRegistries.registerModStuff();
		}
		*///?}

		if (!ISOLATED_ENVIRONMENT) {
			UpdateChecker.checkForMajorUpdates();
		}
		NetworkHandlerServer.initializeSimplePacketReceivers();
	}

	public static Platform platform() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? if fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		 *///?}
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