package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Triple;
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
import java.util.*;

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
        if (LifeSeries.DEBUG) return List.of("lifeskins");
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

        sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_INFO.get(TextUtils.openURLText("https://mat0u5.github.io/LifeSeries-docs/features/lifeskins")));

        return 1;
    }
    public int listLifeSkins(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        Map<String, Map<String, Triple<Boolean, File, Integer>>> skinsCache = LifeSkinsManager.getCache();
        if (skinsCache.isEmpty()) {
            sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_LIST_EMPTY.get());
            lifeSkinsInfo(source);
            return -1;
        }
        else {
            sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_LIST.get());
            for (Map.Entry<String, Map<String, Triple<Boolean, File, Integer>>> holderskins : skinsCache.entrySet()) {
                String name = holderskins.getKey();
                List<String> skins = new ArrayList<>();
                for (Map.Entry<String, Triple<Boolean, File, Integer>> skin : holderskins.getValue().entrySet()) {
                    String append = "";
                    if (skin.getValue() != null && skin.getValue().x) {
                        append = " (slim)";
                    }
                    skins.add(skin.getKey().replaceAll("lives_", "") + append);
                }
                Collections.sort(skins);
                sendCommandFeedbackQuiet(source, ModifiableText.LIFESKINS_LIST_PERSON.get(name, skins));
            }
        }

        return 1;
    }

    public int reloadLifeSkins(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        LifeSkinsManager.reloadAll();
        sendCommandFeedback(source, ModifiableText.LIFESKINS_RELOAD_ALL.get());

        return 1;
    }

    public int setSkin(CommandSourceStack source, ServerPlayer player, String username) {
        if (checkBanned(source)) return -1;
        if (username != null && username.length() > 16) {
            sendCommandFailure(source, ModifiableText.LIFESKINS_USERNAME_INVALID.get());
            return -1;
        }
        Component playerName = player.getDisplayName();
        ProfileManager.ProfileChange skinChange = (username == null) ? ProfileManager.ProfileChange.original() : ProfileManager.ProfileChange.set(username);
        ProfileManager.modifyProfile(player, skinChange, ProfileManager.ProfileChange.none()).thenAccept(success -> {
            if (success) {
                if (username != null) {
                    sendCommandFeedback(source, ModifiableText.LIFESKINS_SKIN_SET.get(playerName, username));
                    ProfileManager.manualSkins.put(player.getUUID(), username);
                }
                else {
                    ProfileManager.manualSkins.remove(player.getUUID());
                    LifeSkinsManager.refreshLifeSkin(player);
                    sendCommandFeedback(source, ModifiableText.LIFESKINS_SKIN_RESET.get(playerName));
                }
            }
            else {
                sendCommandFailure(source, ModifiableText.MOD_ERROR_GENERAL.get());
                ProfileManager.manualSkins.remove(player.getUUID());
            }
        });
        return 1;
    }

    public int setUsername(CommandSourceStack source, ServerPlayer player, String username) {
        if (checkBanned(source)) return -1;
        if (username != null && username.length() > 16) {
            sendCommandFailure(source, ModifiableText.LIFESKINS_USERNAME_INVALID.get());
            return -1;
        }
        Component playerName = player.getDisplayName();
        ProfileManager.ProfileChange nameChange = (username == null) ? ProfileManager.ProfileChange.original() : ProfileManager.ProfileChange.set(username);
        ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.none(), nameChange).thenAccept(success -> {
            if (success) {
                if (username != null) {
                    sendCommandFeedback(source, ModifiableText.LIFESKINS_USERNAME_SET.get(playerName, username));
                }
                else {
                    sendCommandFeedback(source, ModifiableText.LIFESKINS_USERNAME_RESET.get(playerName));
                }
            }
            else {
                sendCommandFailure(source, ModifiableText.MOD_ERROR_GENERAL.get());
            }
        });
        return 1;
    }

    public int setNickname(CommandSourceStack source, ServerPlayer player, String nickname) {
        if (checkBanned(source)) return -1;
        Component playerName = player.getDisplayName();
        if (nickname != null) {
            NicknameManager.setNickname(player, nickname);
            sendCommandFeedback(source, ModifiableText.LIFESKINS_NICKNAME_SET.get(playerName, nickname));
        }
        else {
            NicknameManager.removeNickname(player);
            sendCommandFeedback(source, ModifiableText.LIFESKINS_NICKNAME_RESET.get(playerName));
        }
        return 1;
    }

}
