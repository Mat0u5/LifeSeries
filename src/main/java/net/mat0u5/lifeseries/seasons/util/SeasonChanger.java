package net.mat0u5.lifeseries.seasons.util;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaSkins;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.*;

public class SeasonChanger {
	public record ChangeSeasonArgs(boolean openGui, boolean showChatMessage, boolean changeLives, boolean changeSession) {}
	public static ChangeSeasonArgs DEFAULT_SEASON_ARGS = new ChangeSeasonArgs(true, true, true, true);
	public static List<String> AVAILABLE_SEASON_ARGS = List.of("-noGui", "-noChatFeedback", "-keepLives", "-keepSession");

	public static ChangeSeasonArgs parseChangeSeasonArgs(String argsStr) {
		boolean openGui = !argsStr.contains("-noGui");
		boolean showChatMessage = !argsStr.contains("-noChatFeedback");
		boolean changeLives = !argsStr.contains("-keepLives");
		boolean changeSession = !argsStr.contains("-keepSession");
		return new ChangeSeasonArgs(openGui, showChatMessage, changeLives, changeSession);
	}

	public static void resetSeason() {
		String seasonStr = LifeSeries.getMainConfig().getOrCreateProperty("currentSeries", DEFAULT_SEASON.getId());
		Seasons season = Seasons.getSeasonFromStringName(seasonStr);
		changeSeasonTo(season);
	}

	public static void initializeSeason(Seasons season) {
		initializeSeason(season, DEFAULT_SEASON_ARGS);
	}
	public static void initializeSeason(Seasons season, ChangeSeasonArgs args) {
		currentSeason = season.getSeasonInstance();

		if (args.changeSession()) {
			currentSession = new Session();
			currentSession.loadSessionLength();
		}

		livesManager = currentSeason.livesManager;
		seasonConfig = currentSeason.createConfig();
		blacklist = currentSeason.createBlacklist();
	}

	public static void reloadConfig() {
		currentSeason.reloadStart();
		seasonConfig.loadProperties();
		LifeSeries.getMainConfig().loadProperties();
		blacklist.reloadBlacklist();
		currentSeason.reload();
		NetworkHandlerServer.sendUpdatePackets();
		PlayerUtils.resendCommandTrees();
		SnailSkins.sendTextures();
		TriviaSkins.sendTextures();
	}

	public static boolean changeSeasonTo(Seasons season) {
		return changeSeasonTo(season, DEFAULT_SEASON_ARGS);
	}

	public static boolean changeSeasonTo(Seasons season, ChangeSeasonArgs args) {
		TaskScheduler.clearTasks();
		LifeSeries.getMainConfig().setProperty("currentSeries", season.getId());
		if (args.changeLives()) livesManager.resetAllPlayerLivesInner();
		currentSeason.seasonSwitched(season);
		currentSeason.boogeymanManager.resetBoogeymen();
		currentSeason.secretSociety.forceEndSociety();
		if (args.changeSession()) currentSession.sessionEnd();
		initializeSeason(season, args);
		currentSeason.initialize();
		reloadConfig();
		DatapackManager.onReloadStart();
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			currentSeason.onPlayerJoin(player);
			currentSeason.onPlayerFinishJoining(player, args.showChatMessage());
			NetworkHandlerServer.tryKickFailedHandshake(player);
			if (!modDisabled()) {
				if (args.openGui()) currentSeason.sendSetSeasonPacket(player);
				if (args.changeSession()) SimplePackets.SESSION_TIMER.sendToClient((long) SessionTimerStates.NOT_STARTED.getValue(), player);
			}
		}
		SessionTranscript.resetStats();
		return true;
	}

	public static void preChangeEvent(Seasons preSeason, Seasons postSeason) {
		DatapackIntegration.EVENT_SEASON_CHANGE_PRE.trigger(List.of(
				new DatapackIntegration.Events.MacroEntry("PreviousSeasonIndex", String.valueOf(preSeason.getIndex())),
				new DatapackIntegration.Events.MacroEntry("NextSeasonIndex", String.valueOf(postSeason.getIndex()))
		));
	}

	public static void postChangeEvent(Seasons preSeason, Seasons postSeason) {
		DatapackIntegration.EVENT_SEASON_CHANGE_POST.trigger(List.of(
				new DatapackIntegration.Events.MacroEntry("PreviousSeasonIndex", String.valueOf(preSeason.getIndex())),
				new DatapackIntegration.Events.MacroEntry("NextSeasonIndex", String.valueOf(postSeason.getIndex()))
		));
	}
}
