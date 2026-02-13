package net.mat0u5.lifeseries.seasons.secretsociety;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class SocietyCommands extends Command {

    public SecretSociety get() {
        return currentSeason.secretSociety;
    }

    @Override
    public boolean isAllowed() {
        return get().SOCIETY_ENABLED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when the Secret Society is enabled in the config.");
    }

    public List<String> getAdminCommands() {
        return List.of("society");
    }

    public List<String> getNonAdminCommands() {
        return List.of("society");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("initiate")
                .executes(context -> initiate(context.getSource()))
        );
        dispatcher.register(
            literal("society")
                .then(literal("success")
                    .executes(context -> societySuccess(context.getSource()))
                    .then(literal("confirm")
                        .requires(PermissionManager::isAdmin)
                        .executes(context -> societySuccessConfirm(context.getSource()))
                    )
                )
                .then(literal("fail")
                    .executes(context -> societyFail(context.getSource()))
                    .then(literal("confirm")
                        .requires(PermissionManager::isAdmin)
                        .executes(context -> societyFailConfirm(context.getSource()))
                    )
                )
                .then(literal("begin")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("secret_word", StringArgumentType.string())
                        .executes(context -> societyBegin(context.getSource(), StringArgumentType.getString(context, "secret_word")))
                    )
                    .executes(context -> societyBegin(context.getSource(), null))
                )
                .then(literal("end")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> societyEnd(context.getSource()))
                )
                .then(literal("members")
                    .requires(PermissionManager::isAdmin)
                        .then(literal("add")
                            .then(argument("player", EntityArgument.player())
                                .executes(context -> membersAdd(context.getSource(), EntityArgument.getPlayer(context, "player")))
                            )
                        )
                        .then(literal("remove")
                            .then(argument("player", EntityArgument.player())
                                .executes(context -> membersRemove(context.getSource(), EntityArgument.getPlayer(context, "player")))
                            )
                        )
                        .then(literal("list")
                            .executes(context -> membersList(context.getSource()))
                        )
                )
        );
    }
    public int membersRemove(CommandSourceStack source, ServerPlayer target) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        if (checkSocietyRunning(source)) return -1;
        if (target == null) return -1;

        if (!society.isMember(target)) {
            source.sendFailure(Component.nullToEmpty("That player is not a Member"));
            return -1;
        }

        society.removeMemberManually(target);
        return 1;
    }

    public int membersAdd(CommandSourceStack source, ServerPlayer target) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        if (checkSocietyRunning(source)) return -1;
        if (target == null) return -1;

        if (society.isMember(target)) {
            source.sendFailure(Component.nullToEmpty("That player already a Member"));
            return -1;
        }

        society.addMemberManually(target);
        return 1;
    }

    public int membersList(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        if (checkSocietyRunning(source)) return -1;

        if (society.members.isEmpty()) {
            source.sendFailure(Component.nullToEmpty("The are no Secret Society members"));
            return -1;
        }

        List<String> societyMembers = new ArrayList<>();
        for (SocietyMember member : society.members) {
            ServerPlayer player = member.getPlayer();
            if (player == null) continue;
            societyMembers.add(player.getScoreboardName());
        }
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SOCIETY_MEMBERS.get(societyMembers));
        return 1;
    }

    public boolean checkSocietyRunning(CommandSourceStack source) {
        SecretSociety society = get();
        if (society == null) return false;
        if (society.societyEnded) {
            source.sendFailure(Component.nullToEmpty("The Secret Society has already ended"));
            if (PermissionManager.isAdmin(source)) {
                OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("§7Use '/society begin' or '/society begin <secret_word>' to start."));
            }
            return true;
        }
        if (!society.societyStarted) {
            source.sendFailure(Component.nullToEmpty("The Secret Society has not started yet"));
            if (PermissionManager.isAdmin(source)) {
                OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("§7Use '/society begin' or '/society begin <secret_word>' to start."));
            }
            return true;
        }
        return false;
    }

    public int societyBegin(CommandSourceStack source, String word) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Starting the Secret Society..."));
        society.startSociety(word);
        return 1;
    }

    public int societyEnd(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Ending the Secret Society..."));
        society.forceEndSociety();
        return 1;
    }

    public int societyFail(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (checkSocietyRunning(source)) return -1;

        SocietyMember member = society.getMember(self);
        if (member == null) {
            source.sendFailure(Component.nullToEmpty("You are not a member of the Secret Society"));
            if (PermissionManager.isAdmin(self)) {
                OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Use the §f\"/society fail §lconfirm\"§l§7 command to bypass this"));
            }
            return -1;
        }
        if (!member.initiated) {
            source.sendFailure(Component.nullToEmpty("You have not been initiated"));
            return -1;
        }

        society.endFail();
        SessionTranscript.societyEndFail(self);
        return 1;
    }

    public int societyFailConfirm(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (checkSocietyRunning(source)) return -1;

        society.endFail();
        SessionTranscript.societyEndFail(self);
        return 1;
    }

    public int societySuccess(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (checkSocietyRunning(source)) return -1;

        SocietyMember member = society.getMember(self);
        if (member == null) {
            source.sendFailure(Component.nullToEmpty("You are not a member of the Secret Society"));
            if (PermissionManager.isAdmin(self)) {
                OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Use the §f\"/society success §lconfirm\"§l§7 command to bypass this"));
            }
            return -1;
        }
        if (!member.initiated) {
            source.sendFailure(Component.nullToEmpty("You have not been initiated"));
            return -1;
        }

        society.endSuccess();
        SessionTranscript.societyEndSuccess(self);
        return 1;
    }

    public int societySuccessConfirm(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (checkSocietyRunning(source)) return -1;

        society.endSuccess();
        SessionTranscript.societyEndSuccess(self);
        return 1;
    }

    public int initiate(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretSociety society = get();
        if (society == null) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (checkSocietyRunning(source)) return -1;

        SocietyMember member = society.getMember(self);
        if (member == null) {
            source.sendFailure(Component.nullToEmpty("You are not a member of the Secret Society"));
            return -1;
        }
        if (member.initiated) {
            source.sendFailure(Component.nullToEmpty("You have already been initiated"));
            return -1;
        }

        society.initiateMember(self);
        return 1;
    }
}
