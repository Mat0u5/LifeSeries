package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class GivelifeCommand extends Command {

    private Map<UUID, Map<UUID, Long>> soulmateGivelifeRequests = new HashMap<>();

    @Override
    public boolean isAllowed() {
        return seasonConfig.GIVELIFE_COMMAND_ENABLED.get();
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when the givelife command has been enabled in the Life Series config.");
    }

    public List<String> getAdminCommands() {
        return List.of("givelife");
    }

    public List<String> getNonAdminCommands() {
        return List.of("givelife");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("givelife")
                .then(argument("player", EntityArgument.player())
                    .executes(context -> giftLife(context.getSource(), EntityArgument.getPlayer(context, "player")))
                )
        );
    }

    public int giftLife(CommandSourceStack source, ServerPlayer target) {
        if (checkBanned(source)) return -1;

        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;

        String livesOrTime = currentSeason.getSeason() != Seasons.LIMITED_LIFE ? "lives" : "time";

        if (self.ls$isDead()) {
            source.sendFailure(TextUtils.format("You do not have any {} to give", livesOrTime));
            return -1;
        }
        boolean isRevive = target.ls$isDead();
        if (target.ls$isWatcher()) {
            source.sendFailure(Component.nullToEmpty("That player is a Watcher"));
            return -1;
        }
        if (!Season.GIVELIFE_CAN_REVIVE && isRevive) {
            source.sendFailure(Component.nullToEmpty("That player is not alive"));
            return -1;
        }
        if (target == self) {
            source.sendFailure(TextUtils.format("You cannot give {} to yourself", livesOrTime));
            return -1;
        }

        int giveAmount = 1;
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            giveAmount = -LimitedLife.NEW_DEATH_NORMAL.getSeconds();
        }

        Integer currentLives = self.ls$getLives();
        if (currentLives == null || currentLives <= giveAmount) {
            source.sendFailure(TextUtils.format("You cannot give away any more {}", livesOrTime));
            return -1;
        }
        Integer targetLives = target.ls$getLives();
        if (targetLives == null || targetLives >= currentSeason.GIVELIFE_MAX_LIVES) {
            source.sendFailure(TextUtils.format("That player cannot receive any more {}", livesOrTime));
            return -1;
        }
        if (currentSeason instanceof DoubleLife doubleLife) {
            ServerPlayer soulmate = doubleLife.getSoulmate(self);
            if (soulmate != null) {
                if (soulmate.equals(target)) {
                    source.sendFailure(TextUtils.format("You cannot give {} to your soulmate", livesOrTime));
                    return -1;
                }
                boolean success = doubleLifeGiveLife(source, self, soulmate, target);
                if (!success) {
                    return -1;
                }
            }
        }

        Component currentPlayerName = self.getDisplayName();
        self.ls$addLives(-giveAmount);
        livesManager.addToLivesNoUpdate(target, giveAmount);
        AnimationUtils.playTotemAnimation(self);
        TaskScheduler.scheduleTask(Time.seconds(2), () -> livesManager.receiveLifeFromOtherPlayer(currentPlayerName, target, isRevive));

        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(self);
        }

        return 1;
    }

    public boolean doubleLifeGiveLife(CommandSourceStack source, ServerPlayer self, ServerPlayer soulmate, ServerPlayer target) {
        // Check if there already is a request from the soulmate for this target.
        Map<UUID, Long> soulmateRequest = soulmateGivelifeRequests.get(soulmate.getUUID());
        if (soulmateRequest != null) {
            for (Map.Entry<UUID, Long> entry : soulmateRequest.entrySet()) {
                UUID soulmateRequestTarget = entry.getKey();
                long requestTime = entry.getValue();
                if (soulmateRequestTarget.equals(target.getUUID()) && (System.currentTimeMillis() - requestTime <= 60000)) {
                    // If the soulmate already requested a life for this target within the last 60s, give the life.
                    soulmateRequest.remove(soulmateRequestTarget);
                    return true;
                }
            }
        }

        // Add the request for this player
        if (soulmateGivelifeRequests.containsKey(self.getUUID())) {
            soulmateGivelifeRequests.get(self.getUUID()).put(target.getUUID(), System.currentTimeMillis());
        }
        else {
            Map<UUID, Long> request = new HashMap<>();
            request.put(target.getUUID(), System.currentTimeMillis());
            soulmateGivelifeRequests.put(self.getUUID(), request);
        }
        OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("§7Your soulmate must accept your request to give a life to this player."));
        Component message = TextUtils.format("Your soulmate wants to give a life to {}.\nClick {} to accept the request.", target, TextUtils.runCommandText(TextUtils.formatString("/givelife {}", target.getScoreboardName())));
        soulmate.sendSystemMessage(message);
        return false;
    }
}
