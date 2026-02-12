package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.NicknameManager;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class LifeSkinsCommand extends Command {
    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Component getBannedText() {
        return Component.empty();
    }

    public List<String> getAdminCommands() {
        if (Main.DEBUG) return List.of("lifeskins");
        return List.of("");
    }

    public List<String> getNonAdminCommands() {
        return List.of("");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (Main.DEBUG) {

        dispatcher.register(Commands.literal("lifeskins")
                .requires(PermissionManager::isAdmin)
                        .then(literal("modify")
                                .then(literal("skin")
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .then(Commands.argument("username", StringArgumentType.string())
                                                                .executes(context -> setSkin(context.getSource(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, "username")))
                                                        )
                                                )
                                        )

                                        .then(Commands.literal("reset")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> setSkin(context.getSource(), EntityArgument.getPlayer(context, "player"), null))
                                                )
                                        )
                                )
                                .then(literal("username")
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .then(Commands.argument("username", StringArgumentType.string())
                                                                .executes(context -> setUsername(context.getSource(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, "username")))
                                                        )
                                                )
                                        )

                                        .then(Commands.literal("reset")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> setUsername(context.getSource(), EntityArgument.getPlayer(context, "player"), null))
                                                )
                                        )
                                )
                                .then(literal("nickname")
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .then(Commands.argument("nickname", StringArgumentType.string())
                                                                .executes(context -> setNickname(context.getSource(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, "nickname")))
                                                        )
                                                )
                                        )

                                        .then(Commands.literal("reset")
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> setNickname(context.getSource(), EntityArgument.getPlayer(context, "player"), null))
                                                )
                                        )
                                )
                        )

        );

        }
    }

    public int setSkin(CommandSourceStack source, ServerPlayer player, String username) {
        if (checkBanned(source)) return -1;
        ProfileManager.ProfileChange skinChange = (username == null) ? ProfileManager.ProfileChange.ORIGINAL : ProfileManager.ProfileChange.SET.withInfo(username);
        ProfileManager.modifyProfile(player, skinChange, ProfileManager.ProfileChange.NONE).thenAccept(success -> {
            if (success) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s skin to {}", player, username));
            }
            else {
                source.sendFailure(Component.literal("Something went wrong"));
            }
        });
        return 1;
    }

    public int setUsername(CommandSourceStack source, ServerPlayer player, String username) {
        if (checkBanned(source)) return -1;
        ProfileManager.ProfileChange nameChange = (username == null) ? ProfileManager.ProfileChange.ORIGINAL : ProfileManager.ProfileChange.SET.withInfo(username);
        ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.NONE, nameChange).thenAccept(success -> {
            if (success) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s username to {}", player, username));
            }
            else {
                source.sendFailure(Component.literal("Something went wrong"));
            }
        });
        return 1;
    }

    public int setNickname(CommandSourceStack source, ServerPlayer player, String nickname) {
        if (checkBanned(source)) return -1;
        if (nickname != null) {
            NicknameManager.setNickname(player, nickname);
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s nickname to {}", player, nickname));
        }
        else {
            NicknameManager.removeNickname(player);
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {}'s nickname", player));
        }
        return 1;
    }

}
