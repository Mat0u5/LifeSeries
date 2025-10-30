package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class GivelifeCommand extends Command {

    private Map<UUID, Map<UUID, Long>> soulmateGivelifeRequests = new HashMap<>();

    @Override
    public boolean isAllowed() {
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) return false;
        return seasonConfig.GIVELIFE_COMMAND_ENABLED.get(seasonConfig);
    }

    @Override
    public Text getBannedText() {
        return Text.of("This command is only available when the givelife command has been enabled in the Life Series config.");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("givelife")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> giftLife(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                )
        );
    }

    public int giftLife(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;

        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;
        if (self.ls$isDead()) {
            source.sendError(Text.of("You do not have any lives to give"));
            return -1;
        }
        boolean isRevive = target.ls$isDead();
        if (!Season.GIVELIFE_CAN_REVIVE && isRevive) {
            source.sendError(Text.of("That player is not alive"));
            return -1;
        }
        if (target == self) {
            source.sendError(Text.of("You cannot give a life to yourself"));
            return -1;
        }
        Integer currentLives = self.ls$getLives();
        if (currentLives == null || currentLives <= 1) {
            source.sendError(Text.of("You cannot give away your last life"));
            return -1;
        }
        Integer targetLives = target.ls$getLives();
        if (targetLives == null || targetLives >= currentSeason.GIVELIFE_MAX_LIVES) {
            source.sendError(Text.of("That player cannot receive any more lives"));
            return -1;
        }
        if (currentSeason instanceof DoubleLife doubleLife) {
            ServerPlayerEntity soulmate = doubleLife.getSoulmate(self);
            if (soulmate != null) {
                if (soulmate.equals(target)) {
                    source.sendError(Text.of("You cannot give a life to your soulmate"));
                    return -1;
                }
                boolean success = doubleLifeGiveLife(source, self, soulmate, target);
                if (!success) {
                    return -1;
                }
            }
        }

        Text currentPlayerName = self.getStyledDisplayName();
        self.ls$removeLife();
        livesManager.addToLifeNoUpdate(target);
        AnimationUtils.playTotemAnimation(self);
        TaskScheduler.scheduleTask(40, () -> livesManager.receiveLifeFromOtherPlayer(currentPlayerName, target, isRevive));

        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(self);
        }

        return 1;
    }

    public boolean doubleLifeGiveLife(ServerCommandSource source, ServerPlayerEntity self, ServerPlayerEntity soulmate, ServerPlayerEntity target) {
        // Check if there already is a request from the soulmate for this target.
        Map<UUID, Long> soulmateRequest = soulmateGivelifeRequests.get(soulmate.getUuid());
        if (soulmateRequest != null) {
            for (Map.Entry<UUID, Long> entry : soulmateRequest.entrySet()) {
                UUID soulmateRequestTarget = entry.getKey();
                long requestTime = entry.getValue();
                if (soulmateRequestTarget.equals(target.getUuid()) && (System.currentTimeMillis() - requestTime <= 60000)) {
                    // If the soulmate already requested a life for this target within the last 60s, give the life.
                    soulmateRequest.remove(soulmateRequestTarget);
                    return true;
                }
            }
        }

        // Add the request for this player
        if (soulmateGivelifeRequests.containsKey(self.getUuid())) {
            soulmateGivelifeRequests.get(self.getUuid()).put(target.getUuid(), System.currentTimeMillis());
        }
        else {
            Map<UUID, Long> request = new HashMap<>();
            request.put(target.getUuid(), System.currentTimeMillis());
            soulmateGivelifeRequests.put(self.getUuid(), request);
        }
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7Your soulmate must accept your request to give a life to this player."));
        Text message = TextUtils.format("Your soulmate wants to give a life to {}.\nClick {} to accept the request.", target, TextUtils.runCommandText(TextUtils.formatString("/givelife {}", target.getNameForScoreboard())));
        soulmate.sendMessage(message);
        return false;
    }
}
