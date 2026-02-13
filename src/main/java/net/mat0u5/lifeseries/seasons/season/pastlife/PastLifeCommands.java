package net.mat0u5.lifeseries.seasons.season.pastlife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
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

    public List<String> getAdminCommands() {
        return List.of("pastlife");
    }

    public List<String> getNonAdminCommands() {
        return List.of();
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
            OtherUtils.sendCommandFailure(source, ModifiableText.SESSION_ERROR_NOTSTARTED.get());
            return -1;
        }

        boolean bannedSociety = !currentSeason.secretSociety.SOCIETY_ENABLED || currentSeason.secretSociety.societyStarted || currentSeason.secretSociety.societyEnded;
        boolean bannedBoogeyman = !currentSeason.boogeymanManager.BOOGEYMAN_ENABLED || currentSeason.boogeymanManager.boogeymanChosen;
        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionMessage != null && action.sessionMessage.equalsIgnoreCase("Begin Secret Society")) {
                bannedSociety = true;
            }
            if (action.sessionMessage != null && action.sessionMessage.equalsIgnoreCase("Choose Boogeymen")) {
                bannedBoogeyman = true;
            }
        }

        if (bannedBoogeyman && bannedSociety) {
            OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_FAIL.get());
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, ModifiableText.PASTLIFE_TWIST_RANDOM.get());
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
            OtherUtils.sendCommandFailure(source, ModifiableText.SESSION_ERROR_NOTSTARTED.get());
            return -1;
        }

        if (!currentSeason.secretSociety.SOCIETY_ENABLED) {
            OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_SOCIETY_ERROR_DISABLED.get());
            return -1;
        }

        if (currentSeason.secretSociety.societyEnded) {
            OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_SOCIETY_ERROR_ENDED.get());
            return -1;
        }

        if (currentSeason.secretSociety.societyStarted) {
            OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_SOCIETY_ERROR_STARTED.get());
            return -1;
        }

        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionMessage != null && action.sessionMessage.equalsIgnoreCase("Begin Secret Society")) {
                OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_SOCIETY_ERROR_QUEUED.get());
                return -1;
            }
        }

        currentSeason.secretSociety.addSessionActions();
        OtherUtils.sendCommandFeedback(source, ModifiableText.PASTLIFE_TWIST_SOCIETY.get());
        return 1;
    }

    public int pickBoogeyman(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.statusStarted()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SESSION_ERROR_NOTSTARTED.get());
            return -1;
        }

        if (!currentSeason.boogeymanManager.BOOGEYMAN_ENABLED) {
            OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_BOOGEYMAN_ERROR_DISABLED.get());
            return -1;
        }

        if (currentSeason.boogeymanManager.boogeymanChosen) {
            OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_BOOGEYMAN_ERROR_CHOSEN.get());
            return -1;
        }

        for (SessionAction action : currentSession.getSessionActions()) {
            if (action.sessionMessage != null && action.sessionMessage.equalsIgnoreCase("Choose Boogeymen")) {
                OtherUtils.sendCommandFailure(source, ModifiableText.PASTLIFE_TWIST_BOOGEYMAN_ERROR_QUEUED.get());
                return -1;
            }
        }

        currentSeason.boogeymanManager.addSessionActions();
        OtherUtils.sendCommandFeedback(source, ModifiableText.PASTLIFE_TWIST_BOOGEYMAN.get());
        return 1;
    }
}
