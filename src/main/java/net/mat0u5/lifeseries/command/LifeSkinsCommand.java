package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.LifeSkinsManager;
import net.mat0u5.lifeseries.utils.player.NicknameManager;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return List.of();
    }

    public List<String> getNonAdminCommands() {
        return List.of();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
                .then(literal("reload")
                        .executes(context -> reloadLifeSkins(context.getSource()))
                )
                .then(literal("list")
                        .executes(context -> listLifeSkins(context.getSource()))
                )
                .then(literal("info")
                        .executes(context -> lifeSkinsInfo(context.getSource()))
                )
                .executes(context -> lifeSkinsInfo(context.getSource()))
        );
    }

    public int lifeSkinsInfo(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        //TODO remove /dev/ when out of development
        //TODO update URL
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_INFO.get(TextUtils.openURLText("https://mat0u5.github.io/LifeSeries-docs/dev/features/life-skins")));

        return 1;
    }
    public int listLifeSkins(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        Map<String, Map<Integer, File>> skinsCache = LifeSkinsManager.getCache();
        if (skinsCache.isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_LIST_EMPTY.get());
            lifeSkinsInfo(source);
            return -1;
        }
        else {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_LIST.get());
            for (Map.Entry<String, Map<Integer, File>> holderskins : skinsCache.entrySet()) {
                String name = holderskins.getKey();
                List<String> skins = new ArrayList<>();
                for (Integer skin : holderskins.getValue().keySet()) {
                    skins.add(String.valueOf(skin));
                }
                OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_LIST_PERSON.get(name, skins));
            }
        }

        return 1;
    }
    public int reloadLifeSkins(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        LifeSkinsManager.reloadAll();
        OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_RELOAD.get());

        return 1;
    }

    public int setSkin(CommandSourceStack source, ServerPlayer player, String username) {
        if (checkBanned(source)) return -1;
        ProfileManager.ProfileChange skinChange = (username == null) ? ProfileManager.ProfileChange.ORIGINAL : ProfileManager.ProfileChange.SET.withInfo(username);
        ProfileManager.modifyProfile(player, skinChange, ProfileManager.ProfileChange.NONE).thenAccept(success -> {
            if (success) {
                if (username != null) {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_SKIN_SET.get(player, username));
                }
                else {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_SKIN_RESET.get(player));
                }
            }
            else {
                OtherUtils.sendCommandFailure(source, ModifiableText.MOD_ERROR_GENERAL.get());
            }
        });
        return 1;
    }

    public int setUsername(CommandSourceStack source, ServerPlayer player, String username) {
        if (checkBanned(source)) return -1;
        ProfileManager.ProfileChange nameChange = (username == null) ? ProfileManager.ProfileChange.ORIGINAL : ProfileManager.ProfileChange.SET.withInfo(username);
        ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.NONE, nameChange).thenAccept(success -> {
            if (success) {
                if (username != null) {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_USERNAME_SET.get(player, username));
                }
                else {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_USERNAME_RESET.get(player));
                }
            }
            else {
                OtherUtils.sendCommandFailure(source, ModifiableText.MOD_ERROR_GENERAL.get());
            }
        });
        return 1;
    }

    public int setNickname(CommandSourceStack source, ServerPlayer player, String nickname) {
        if (checkBanned(source)) return -1;
        if (nickname != null) {
            NicknameManager.setNickname(player, nickname);
            OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_NICKNAME_SET.get(player, nickname));
        }
        else {
            NicknameManager.removeNickname(player);
            OtherUtils.sendCommandFeedback(source, ModifiableText.LIFESKINS_NICKNAME_RESET.get(player));
        }
        return 1;
    }

}
