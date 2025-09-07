package net.mat0u5.lifeseries.seasons.season.pastlife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Random;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PastLifeCommands {
    public static Random rnd = new Random();

    public static boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.PAST_LIFE;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when playing Past Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("pastlife")
                .requires(source -> isAllowed() && (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(literal("boogeyman")
                        .executes(context -> pickBoogeyman(context.getSource()))
                    )
                    .then(literal("society")
                        .executes(context -> pickSociety(context.getSource()))
                    )
                    .then(literal("pickRandom")
                        .executes(context -> pickRandom(context.getSource()))
                    )
        );
    }

    public static int pickRandom(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            source.sendError(Text.of("The session has not started yet"));
            return -1;
        }

        boolean bannedSociety = !currentSeason.secretSociety.SOCIETY_ENABLED || currentSeason.secretSociety.societyStarted || currentSeason.secretSociety.societyEnded;
        boolean bannedBoogeyman = !currentSeason.boogeymanManager.BOOGEYMAN_ENABLED || currentSeason.boogeymanManager.boogeymanChosen;
        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionId != null && action.sessionId.equalsIgnoreCase("Begin Secret Society")) {
                bannedSociety = true;
            }
            if (action.sessionId != null && action.sessionId.equalsIgnoreCase("Choose Boogeymen")) {
                bannedBoogeyman = true;
            }
        }

        if (bannedBoogeyman && bannedSociety) {
            source.sendError(Text.of("Picking failed"));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, Text.of("§7Randomly picking the Boogeyman or the Secret Society..."));
        if (!bannedBoogeyman && bannedSociety) {
            currentSeason.boogeymanManager.addSessionActions();
            return 1;
        }

        if (bannedBoogeyman && !bannedSociety) {
            currentSeason.secretSociety.addSessionActions();
            return 1;
        }

        if (!bannedBoogeyman && !bannedSociety) {
            if (rnd.nextInt(2) == 0) {
                currentSeason.boogeymanManager.addSessionActions();
            }
            else {
                currentSeason.secretSociety.addSessionActions();
            }
            return 1;
        }
        return 1;
    }

    public static int pickSociety(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            source.sendError(Text.of("The session has not started yet"));
            return -1;
        }

        if (!currentSeason.secretSociety.SOCIETY_ENABLED) {
            source.sendError(Text.of("The Secret Society is disabled in the config"));
            return -1;
        }

        if (currentSeason.secretSociety.societyEnded) {
            source.sendError(Text.of("The Secret Society has already ended"));
            return -1;
        }

        if (currentSeason.secretSociety.societyStarted) {
            source.sendError(Text.of("The Secret Society has already started"));
            return -1;
        }

        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionId != null && action.sessionId.equalsIgnoreCase("Begin Secret Society")) {
                source.sendError(Text.of("The Secret Society is already queued"));
                return -1;
            }
        }

        currentSeason.secretSociety.addSessionActions();
        OtherUtils.sendCommandFeedback(source, Text.of("Added the Secret Society to queued session actions"));
        return 1;
    }

    public static int pickBoogeyman(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            source.sendError(Text.of("The session has not started yet"));
            return -1;
        }

        if (!currentSeason.boogeymanManager.BOOGEYMAN_ENABLED) {
            source.sendError(Text.of("The Boogeyman is disabled in the config"));
            return -1;
        }

        if (currentSeason.boogeymanManager.boogeymanChosen) {
            source.sendError(Text.of("The Boogeyman has already been chosen"));
            return -1;
        }

        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionId != null && action.sessionId.equalsIgnoreCase("Choose Boogeymen")) {
                source.sendError(Text.of("The Boogeyman is already queued"));
                return -1;
            }
        }

        currentSeason.boogeymanManager.addSessionActions();
        OtherUtils.sendCommandFeedback(source, Text.of("Added the Boogeyman to queued session actions"));
        return 1;
    }
}
