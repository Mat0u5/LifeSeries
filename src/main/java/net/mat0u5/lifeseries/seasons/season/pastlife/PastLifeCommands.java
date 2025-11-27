package net.mat0u5.lifeseries.seasons.season.pastlife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Random;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.currentSession;

public class PastLifeCommands extends Command {
    public Random rnd = new Random();

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.PAST_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when playing Past Life.");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("pastlife")
                    .requires(PermissionManager::isAdmin)
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

    public int pickRandom(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            source.sendFailure(Component.nullToEmpty("The session has not started yet"));
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
            source.sendFailure(Component.nullToEmpty("Picking failed"));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Randomly picking the Boogeyman or the Secret Society..."));
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
            currentSession.getSessionActions().get(currentSession.getSessionActions().size() - 1).sessionMessage = "Randomly Selected Twist";
            return 1;
        }
        return 1;
    }

    public int pickSociety(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            source.sendFailure(Component.nullToEmpty("The session has not started yet"));
            return -1;
        }

        if (!currentSeason.secretSociety.SOCIETY_ENABLED) {
            source.sendFailure(Component.nullToEmpty("The Secret Society is disabled in the config"));
            return -1;
        }

        if (currentSeason.secretSociety.societyEnded) {
            source.sendFailure(Component.nullToEmpty("The Secret Society has already ended"));
            return -1;
        }

        if (currentSeason.secretSociety.societyStarted) {
            source.sendFailure(Component.nullToEmpty("The Secret Society has already started"));
            return -1;
        }

        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionId != null && action.sessionId.equalsIgnoreCase("Begin Secret Society")) {
                source.sendFailure(Component.nullToEmpty("The Secret Society is already queued"));
                return -1;
            }
        }

        currentSeason.secretSociety.addSessionActions();
        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("Added the Secret Society to queued session actions"));
        return 1;
    }

    public int pickBoogeyman(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            source.sendFailure(Component.nullToEmpty("The session has not started yet"));
            return -1;
        }

        if (!currentSeason.boogeymanManager.BOOGEYMAN_ENABLED) {
            source.sendFailure(Component.nullToEmpty("The Boogeyman is disabled in the config"));
            return -1;
        }

        if (currentSeason.boogeymanManager.boogeymanChosen) {
            source.sendFailure(Component.nullToEmpty("The Boogeyman has already been chosen"));
            return -1;
        }

        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionId != null && action.sessionId.equalsIgnoreCase("Choose Boogeymen")) {
                source.sendFailure(Component.nullToEmpty("The Boogeyman is already queued"));
                return -1;
            }
        }

        currentSeason.boogeymanManager.addSessionActions();
        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("Added the Boogeyman to queued session actions"));
        return 1;
    }
}
