package net.mat0u5.lifeseries.seasons.season.nicelife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class NiceLifeCommands extends Command {
    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.NICE_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when playing Nice Life.");
    }

    public List<String> getAdminCommands() {
        return List.of("vote");
    }
    public List<String> getNonAdminCommands() {
        return List.of("vote");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("vote")
                        .executes(context -> this.vote(context.getSource()))
        );
    }

    private int vote(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;

        if (!NiceLifeVotingManager.niceListMembers.contains(self.getUUID())) {
            source.sendFailure(Component.literal("You are not on the nice list"));
            return -1;
        }
        if (NiceLifeVotingManager.voteType != NiceLifeVotingManager.VoteType.NICE_LIST_LIFE) {
            source.sendFailure(Component.literal("Nice list voting is not in progress"));
            return -1;
        }
        if (self.ls$isDead()) {
            source.sendFailure(Component.literal("Dead players cannot vote"));
            return -1;
        }
        if (self.ls$isWatcher()) {
            source.sendFailure(Component.literal("Watchers cannot vote"));
            return -1;
        }

        boolean success = NiceLifeVotingManager.openNiceListLifeVote(self);

        if (!success) {
            source.sendFailure(Component.literal("There are no players to vote for"));
            return -1;
        }

        return 1;
    }
}
