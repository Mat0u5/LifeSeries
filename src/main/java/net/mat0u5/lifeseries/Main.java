package net.mat0u5.lifeseries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.MainConfig;
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
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.interfaces.IClientHelper;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class Main implements ModInitializer {
	public static final String MOD_VERSION = "dev-1.4.3.16";
	public static final String MOD_ID = "lifeseries";
	public static final String UPDATES_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases";
	public static final boolean DEBUG = false;
	public static final boolean ISOLATED_ENVIRONMENT = false;
	public static final Seasons DEFAULT_SEASON = Seasons.UNASSIGNED;
	public static boolean MOD_DISABLED = false;

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
			ResourceManagerHelper.registerBuiltinResourcePack(IdentifierHelper.mod("lifeseries"), container, Component.nullToEmpty("Main Life Series Resourcepack"), ResourcePackActivationType.ALWAYS_ENABLED);
			ResourceManagerHelper.registerBuiltinResourcePack(IdentifierHelper.mod("minimal_armor"), container, Component.nullToEmpty("Minimal Armor Resourcepack"), ResourcePackActivationType.NORMAL);
			ResourceManagerHelper.registerBuiltinResourcePack(IdentifierHelper.mod("lifeseries_datapack"), container, ResourcePackActivationType.ALWAYS_ENABLED);
		});

		ConfigManager.moveOldMainFileIfExists();
		SnailSkins.createConfig();

		config = new MainConfig();
		MOD_DISABLED = config.getOrCreateProperty("modDisabled", "false").equalsIgnoreCase("true");
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

	public static boolean modDisabled() {
		if (clientHelper != null) {
			if (clientHelper.isReplay()) return true;
			if (clientHelper.serverHandshake() == HandshakeStatus.NOT_RECEIVED) return true;
		}
		return MOD_DISABLED;
	}

	public static boolean modFullyDisabled() {
		if (clientHelper == null) return false;
		return clientHelper.serverHandshake() == HandshakeStatus.NOT_RECEIVED;
	}

	public static void setDisabled(boolean disabled) {
		boolean previouslyDisabled = MOD_DISABLED;
		MOD_DISABLED = disabled;
		config.setProperty("modDisabled", String.valueOf(MOD_DISABLED));

		if (!previouslyDisabled && disabled) {
			changeSeasonTo(Seasons.UNASSIGNED.getId());
		}
		if (!modDisabled()) {
			fullReload();
		}
	}

	public static void fullReload() {
		String season = config.getOrCreateProperty("currentSeries", DEFAULT_SEASON.getId());
		changeSeasonTo(season);
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

		currentSession = new Session();
		currentSession.sessionLength = config.getOrCreateInt("session_length", 144000); //2 hours

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
		SnailSkins.sendTextures();
	}
	public static void reloadEnd() {
		DatapackManager.onReloadEnd();
	}

	public static boolean changeSeasonTo(String changeTo) {
		TaskScheduler.clearTasks();
		config.setProperty("currentSeries", changeTo);
		livesManager.resetAllPlayerLivesInner();
		currentSession.sessionEnd();
		Main.parseSeason(changeTo);
		currentSeason.initialize();
		reloadStart();
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			currentSeason.onPlayerJoin(player);
			currentSeason.onPlayerFinishJoining(player);
			NetworkHandlerServer.tryKickFailedHandshake(player);
			if (!modDisabled()) {
				NetworkHandlerServer.sendStringPacket(player, PacketNames.SEASON_INFO, currentSeason.getSeason().getId());
				NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, SessionTimerStates.NOT_STARTED.getValue());
			}
		}
		SessionTranscript.resetStats();
		return true;
	}

	public static ConfigManager getMainConfig() {
		return config;
	}
}