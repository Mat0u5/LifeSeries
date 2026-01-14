package net.mat0u5.lifeseries.seasons.season.nicelife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        return List.of("vote", "nicelife");
    }
    public List<String> getNonAdminCommands() {
        return List.of("vote");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("vote")
                        .executes(context -> vote(context.getSource()))
                        .then(literal("forceNext")
                                .requires(PermissionManager::isAdmin)
                                .then(argument("type", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("none", "nicelist", "naughtylist"), builder))
                                        .executes(context -> forceNextVote(context.getSource(), StringArgumentType.getString(context, "type")))
                                )
                        )
        );

        dispatcher.register(
                literal("nicelife")
                        .requires(PermissionManager::isAdmin)
                        .then(literal("wakeUp")
                                        .executes(context -> wakeup(context.getSource()))
                                        .then(argument("players", EntityArgument.players())
                                                .executes(context -> wakeup(context.getSource(), EntityArgument.getPlayers(context, "players")))
                                        )
                        )
                        .then(literal("skipNight")
                                        .executes(context -> skipNight(context.getSource()))
                        )
        );
    }

    private int forceNextVote(CommandSourceStack source, String type) {
        if (checkBanned(source)) return -1;

        switch (type) {
            case "none":
                NiceLifeVotingManager.forcedTriviaVote = Optional.of(NiceLifeVotingManager.VoteType.NONE);
                break;
            case "nicelist":
                NiceLifeVotingManager.forcedTriviaVote = Optional.of(NiceLifeVotingManager.VoteType.NICE_LIST);
                break;
            case "naughtylist":
                NiceLifeVotingManager.forcedTriviaVote = Optional.of(NiceLifeVotingManager.VoteType.NAUGHTY_LIST);
                break;
            default:
                source.sendFailure(Component.nullToEmpty("Vote type not found"));
                return -1;
        }

        OtherUtils.sendCommandFeedback(source, TextUtils.format("Next midnight vote will be '{}'", type));
        return 1;
    }

    private int wakeup(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;

        if (self.isSleeping()) {
            self.stopSleepInBed(false, true);
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Woke up {}", self));
        }
        else {
            source.sendFailure(Component.nullToEmpty("You are not sleeping"));
            return -1;
        }
        NetworkHandlerServer.sendStringPacket(self, PacketNames.REMOVE_SLEEP_SCREENS, "");

        return 1;
    }

    private int wakeup(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        for (ServerPlayer player : targets) {
            if (player.isSleeping()) {
                player.stopSleepInBed(false, true);
            }
            NetworkHandlerServer.sendStringPacket(player, PacketNames.REMOVE_SLEEP_SCREENS, "");
        }


        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Woke up {}", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Woke up {} players", targets.size()));
        }

        return 1;
    }

    private int skipNight(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (currentSeason instanceof NiceLife niceLife) {
            //? if <= 1.21.4 {
            if (!source.getServer().overworld().isNight()) {
            //?} else {
            /*if (!source.getServer().overworld().isDarkOutside()) {
            *///?}
                source.sendFailure(Component.nullToEmpty("It is not night time"));
                return -1;
            }
            niceLife.sleepThroughNight();
        }
        return 1;
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
