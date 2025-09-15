package net.mat0u5.lifeseries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.MainConfig;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.dependencies.PolymerDependency;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.ModRegistries;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.interfaces.IClientHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class Main implements ModInitializer {
	public static final String MOD_VERSION = "dev-1.4.0.3";
	public static final String MOD_ID = "lifeseries";
	public static final String LATEST_UPDATE_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases/latest";
	public static final String ALL_UPDATES_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases";
	public static final boolean DEBUG = false;
	public static final boolean ISOLATED_ENVIRONMENT = false;
	public static final Seasons DEFAULT_SEASON = Seasons.UNASSIGNED;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static ConfigManager config;
	public static IClientHelper clientHelper;

	@Nullable
	public static MinecraftServer server;
	public static Season currentSeason;
	public static Session currentSession;
	public static LivesManager livesManager;
	public static Blacklist blacklist;
	public static ConfigManager seasonConfig;
	public static final List<String> ALLOWED_SEASON_NAMES = Seasons.getSeasonIds();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Life Series...");
		FabricLoader.getInstance().getModContainer(Main.MOD_ID).ifPresent(container -> {
			ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "lifeseries_datapack"), container, ResourcePackActivationType.ALWAYS_ENABLED);
		});

		ConfigManager.moveOldMainFileIfExists();
		SnailSkinsServer.createConfig();

		if (DependencyManager.polymerLoaded()) {
			PolymerDependency.onInitialize();
		}

		config = new MainConfig();
		String season = config.getOrCreateProperty("currentSeries", DEFAULT_SEASON.getId());

		parseSeason(season);
		Seasons.getSeasons().forEach(seasons -> seasons.getSeasonInstance().createConfig());

		ModRegistries.registerModStuff();
		if (!ISOLATED_ENVIRONMENT) {
			UpdateChecker.checkForMajorUpdates();
		}

		NetworkHandlerServer.registerPackets();
		NetworkHandlerServer.registerServerReceiver();
	}


	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	public static void setClientHelper(IClientHelper helper) {
		clientHelper = helper;
	}

	public static boolean isLogicalSide() {
		if (!isClient()) return true;
		return clientHelper != null && clientHelper.isRunningIntegratedServer();
	}

	public static boolean isClientPlayer(UUID uuid) {
		if (!isClient()) return false;
		return clientHelper != null && clientHelper.isMainClientPlayer(uuid);
	}

	public static void parseSeason(String seasonStr) {
		currentSeason = Seasons.getSeasonFromStringName(seasonStr).getSeasonInstance();

		Integer currentSessionLength = (currentSession == null) ? null : currentSession.sessionLength;
		currentSession = new Session();
		currentSession.sessionLength = currentSessionLength;

		livesManager = currentSeason.livesManager;
		seasonConfig = currentSeason.createConfig();
		blacklist = currentSeason.createBlacklist();
	}

	public static void reloadStart() {
		if (Events.skipNextTickReload) return;
		softReloadStart();
		DatapackManager.onReloadStart();
	}

	public static void softReloadStart() {
		softestReloadStart();
		SnailSkinsServer.sendStoredImages();
	}

	public static void softestReloadStart() {
		if (currentSeason.getSeason() == Seasons.SECRET_LIFE) {
			TaskManager.initialize();
		}
		if (currentSeason.getSeason() == Seasons.DOUBLE_LIFE && currentSeason instanceof DoubleLife doubleLife) {
			doubleLife.loadSoulmates();
		}
		seasonConfig.loadProperties();
		blacklist.reloadBlacklist();
		currentSeason.reload();
		NetworkHandlerServer.sendUpdatePackets();
		PlayerUtils.resendCommandTrees();
	}
	public static void reloadEnd() {
		DatapackManager.onReloadEnd();
	}

	public static boolean changeSeasonTo(String changeTo) {
		if (Seasons.getSeasonFromStringName(changeTo) == Seasons.WILD_LIFE) {
			if (!DependencyManager.checkWildLifeDependencies()) return false;
		}

		config.setProperty("currentSeries", changeTo);
		livesManager.resetAllPlayerLivesInner();
		currentSession.sessionEnd();
		Main.parseSeason(changeTo);
		currentSeason.initialize();
		reloadStart();
		for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
			currentSeason.onPlayerJoin(player);
			currentSeason.onPlayerFinishJoining(player);
			NetworkHandlerServer.tryKickFailedHandshake(player);
			NetworkHandlerServer.sendStringPacket(player, PacketNames.SEASON_INFO, currentSeason.getSeason().getId());
			NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, SessionTimerStates.NOT_STARTED.getValue());
		}
		SessionTranscript.resetStats();
		return true;
	}
}