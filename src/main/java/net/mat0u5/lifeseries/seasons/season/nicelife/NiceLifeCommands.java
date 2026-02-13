package net.mat0u5.lifeseries.seasons.season.nicelife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

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
        return List.of("vote", "nicelife", "nicelist", "naughtylist");
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
        dispatcher.register(
                literal("nicelist")
                        .requires(PermissionManager::isAdmin)
                        .then(literal("end")
                                .executes(context -> niceListEnd(
                                        context.getSource()
                                ))
                        )
                        .then(literal("list")
                                .executes(context -> niceList(
                                        context.getSource()
                                ))
                        )
                        .then(literal("add")
                                .then(argument("player", EntityArgument.players())
                                        .executes(context -> niceListAdd(context.getSource(), EntityArgument.getPlayers(context, "player")))
                                )
                        )
                        .then(literal("remove")
                                .then(argument("player", EntityArgument.players())
                                        .executes(context -> niceListRemove(context.getSource(), EntityArgument.getPlayers(context, "player")))
                                )
                        )
        );
        dispatcher.register(
                literal("naughtylist")
                        .requires(PermissionManager::isAdmin)
                        .then(literal("end")
                                .executes(context -> naughtyListEnd(
                                        context.getSource()
                                ))
                        )
                        .then(literal("list")
                                .executes(context -> naughtyList(
                                        context.getSource()
                                ))
                        )
                        .then(literal("add")
                                .then(argument("player", EntityArgument.players())
                                        .executes(context -> naughtyListAdd(context.getSource(), EntityArgument.getPlayers(context, "player")))
                                )
                        )
                        .then(literal("remove")
                                .then(argument("player", EntityArgument.players())
                                        .executes(context -> naughtyListRemove(context.getSource(), EntityArgument.getPlayers(context, "player")))
                                )
                        )
        );

    }

    private int niceListEnd(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (NiceLifeVotingManager.voteType != NiceLifeVotingManager.VoteType.NICE_LIST_LIFE) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_NICELIST_ERROR.get());
            return -1;
        }
        NiceLifeVotingManager.endNiceList();
        return 1;
    }

    private int niceList(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (NiceLifeVotingManager.voteType != NiceLifeVotingManager.VoteType.NICE_LIST_LIFE) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_NICELIST_ERROR.get());
            return -1;
        }

        List<ServerPlayer> niceListPlayers = new ArrayList<>();
        for (UUID uuid : NiceLifeVotingManager.niceListMembers) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player != null) {
                niceListPlayers.add(player);
            }
        }
        if (niceListPlayers.isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.NICELIFE_NICELIST_EMPTY.get());
            return 1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.NICELIFE_NICELIST_LIST.get(niceListPlayers));
        return 1;
    }

    private int niceListAdd(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        if (NiceLifeVotingManager.voteType != NiceLifeVotingManager.VoteType.NICE_LIST_LIFE) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_NICELIST_ERROR.get());
            return -1;
        }

        for (ServerPlayer player : targets) {
            NiceLifeVotingManager.manuallyAddNiceListMember(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NICELIST_ADD_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NICELIST_ADD_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    private int niceListRemove(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        if (NiceLifeVotingManager.voteType != NiceLifeVotingManager.VoteType.NICE_LIST_LIFE) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_NICELIST_ERROR.get());
            return -1;
        }

        for (ServerPlayer player : targets) {
            NiceLifeVotingManager.manuallyRemoveNiceListMember(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NICELIST_REMOVE_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NICELIST_REMOVE_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    private int naughtyListEnd(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        NiceLifeVotingManager.endNaughtyList();
        return 1;
    }

    private int naughtyList(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        List<ServerPlayer> naughtyListPlayers = new ArrayList<>();
        for (UUID uuid : NiceLifeVotingManager.naughtyListMembers) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player != null) {
                naughtyListPlayers.add(player);
            }
        }
        if (naughtyListPlayers.isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.NICELIFE_NAUGHTYLIST_EMPTY.get());
            return 1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.NICELIFE_NAUGHTYLIST_LIST.get(naughtyListPlayers));
        return 1;
    }

    private int naughtyListAdd(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        for (ServerPlayer player : targets) {
            NiceLifeVotingManager.manuallyAddNaughtyListMember(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NAUGHTYLIST_ADD_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NAUGHTYLIST_ADD_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    private int naughtyListRemove(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        for (ServerPlayer player : targets) {
            NiceLifeVotingManager.manuallyRemoveNaughtyListMember(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NAUGHTYLIST_REMOVE_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_NAUGHTYLIST_REMOVE_MULTIPLE.get(targets.size()));
        }

        return 1;
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
                OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_VOTE_ERROR_UNKNOWN.get());
                return -1;
        }

        OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_VOTE_SET.get(type));
        return 1;
    }

    private int wakeup(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;

        if (self.isSleeping()) {
            self.stopSleepInBed(false, true);
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_WAKEUP_SINGLE.get(self));
        }
        else {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_NOTSLEEPING.get());
            return -1;
        }
        SimplePackets.REMOVE_SLEEP_SCREENS.target(self).sendToClient();

        return 1;
    }

    private int wakeup(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        for (ServerPlayer player : targets) {
            if (player.isSleeping()) {
                player.stopSleepInBed(false, true);
            }
            SimplePackets.REMOVE_SLEEP_SCREENS.target(player).sendToClient();
        }


        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_WAKEUP_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.NICELIFE_WAKEUP_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    private int skipNight(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (currentSeason instanceof NiceLife niceLife) {
            //? if <= 1.21.4 {
            /*if (!source.getServer().overworld().isNight()) {
            *///?} else {
            if (!source.getServer().overworld().isDarkOutside()) {
            //?}
                OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_NOT_NIGHT.get());
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
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_VOTE_ERROR_NICELIST_MISSING.get());
            return -1;
        }
        if (NiceLifeVotingManager.voteType != NiceLifeVotingManager.VoteType.NICE_LIST_LIFE) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_VOTE_ERROR_NICELIST_PROGRESS.get());
            return -1;
        }
        if (self.ls$isDead()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_VOTE_ERROR_DEAD.get());
            return -1;
        }
        if (self.ls$isWatcher()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_VOTE_ERROR_WATCHER.get());
            return -1;
        }

        boolean success = NiceLifeVotingManager.openNiceListLifeVote(self);

        if (!success) {
            OtherUtils.sendCommandFailure(source, ModifiableText.NICELIFE_VOTE_ERROR_TARGET.get());
            return -1;
        }

        return 1;
    }
}
