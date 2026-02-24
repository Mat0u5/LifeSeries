package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static net.mat0u5.lifeseries.Main.*;

public class LifeSeriesCommand extends Command {

    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("");
    }

    public List<String> getAdminCommands() {
        return List.of("lifeseries");
    }

    public List<String> getNonAdminCommands() {
        return List.of("lifeseries");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
          var lifeseriesTree = literal("lifeseries")
                .executes(context -> defaultCommand(context.getSource()))
                .then(literal("worlds")
                        .requires(PermissionManager::isAdmin)
                        .executes(context -> getWorlds(context.getSource()))
                )
                .then(literal("credits")
                        .executes(context -> getCredits(context.getSource()))
                )
                .then(literal("discord")
                    .executes(context -> getDiscord(context.getSource()))
                )
                .then(literal("getSeries")
                    .executes(context -> getSeason(context.getSource()))
                )
                .then(literal("version")
                    .executes(context -> getVersion(context.getSource()))
                )
                .then(literal("config")
                        .executes(context -> config(context.getSource()))
                        .then(literal("set")
                                .requires(PermissionManager::isAdmin)
                                .then(argument("key", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(seasonConfig.getAvailableConfigKeys(), builder))
                                        .then(argument("value", StringArgumentType.greedyString())
                                                .executes(context -> configSet(context.getSource(), StringArgumentType.getString(context, "key"), StringArgumentType.getString(context, "value")))
                                        )
                                )
                        )
                )
                .then(literal("wiki")
                        .executes(context -> wiki(context.getSource()))
                )
                .then(literal("help")
                        .executes(context -> wiki(context.getSource()))
                )
                .then(literal("reload")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> reload(context.getSource()))
                )
                .then(literal("chooseSeries")
                        .requires(source -> PermissionManager.isAdmin(source) && (NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer()) || (source.getEntity() == null)))
                        .executes(context -> chooseSeason(context.getSource()))
                )
                .then(literal("setSeries")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("season", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ALLOWED_SEASON_NAMES, builder))
                        .executes(context -> setSeason(
                            context.getSource(), StringArgumentType.getString(context, "season"), false)
                        )
                        .then(literal("confirm")
                            .executes(context -> setSeason(
                                context.getSource(), StringArgumentType.getString(context, "season"), true)
                            )
                        )
                    )
                )
                .then(literal("enable")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> enableOrDisable(context.getSource(), false))
                )
                .then(literal("disable")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> enableOrDisable(context.getSource(), true))
                );
        dispatcher.register(lifeseriesTree);
        dispatcher.register(literal("ls").redirect(lifeseriesTree.build()));
    }

    private int enableOrDisable(CommandSourceStack source, boolean disabled) {
        OtherUtils.sendCommandFeedback(source, ModifiableText.SERIES_DISABLE.get(disabled ? "disabled" : "enabled"));
        Main.setDisabled(disabled);
        return 1;
    }

    public int chooseSeason(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            OtherUtils.sendCommandFailure(source, Component.nullToEmpty("You must have the Life Series mod installed §nclient-side§c to open the season selection GUI."));
            OtherUtils.sendCommandFailure(source, Component.nullToEmpty("Use the '/lifeseries setSeries <season>' command instead."));
            return -1;
        }
        OtherUtils.sendCommandFeedback(source, ModifiableText.SEASON_SELECTION_GUI.get());
        SimplePackets.SELECT_SEASON.target(source.getPlayer()).sendToClient(currentSeason.getSeason().getId());
        return 1;
    }

    public int setSeason(CommandSourceStack source, String setTo, boolean confirmed) {
        if (checkBanned(source)) return -1;
        if (!ALLOWED_SEASON_NAMES.contains(setTo)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SEASON_INVALID.get());
            OtherUtils.sendCommandFailure(source, ModifiableText.SEASON_INVALID_HELP.get(ALLOWED_SEASON_NAMES));
            return -1;
        }
        if (confirmed) {
            setSeasonFinal(source, setTo);
        }
        else {
            if (currentSeason.getSeason() == Seasons.UNASSIGNED) {
                setSeasonFinal(source, setTo);
            }
            else {
                OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SEASON_SELECT_WARNING.get());
            }
        }
        return 1;
    }

    public void setSeasonFinal(CommandSourceStack source, String setTo) {
        boolean prevTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;
        if (Main.changeSeasonTo(setTo)) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SEASON_CHANGING.get(setTo));
            PlayerUtils.broadcastMessage(ModifiableText.SEASON_CHANGED.get(setTo));
            boolean currentTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;
            if (prevTickFreeze != currentTickFreeze) {
                OtherUtils.setFreezeGame(currentTickFreeze);
            }
        }
    }

    public int config(CommandSourceStack source) {
        //if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) {
            return -1;
        }
        if (!NetworkHandlerServer.wasHandshakeSuccessful(self)) {
            OtherUtils.sendCommandFailure(source, Component.nullToEmpty("You must have the Life Series mod installed §nclient-side§c to open the config GUI."));
            OtherUtils.sendCommandFailure(source, Component.nullToEmpty("Either install the mod on the client on modify the config folder."));
            return -1;
        }

        SimplePackets.CLEAR_CONFIG.target(self).sendToClient();
        if (PermissionManager.isAdmin(self) && currentSeason.getSeason() != Seasons.UNASSIGNED) {
            Main.seasonConfig.sendConfigTo(self);
            OtherUtils.sendCommandFeedback(source, ModifiableText.CONFIG_GUI_OPENING.get());
        }
        else {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.CONFIG_GUI_OPENING.get());
        }
        SimplePackets.OPEN_CONFIG.target(self).sendToClient();
        return 1;
    }

    public int configSet(CommandSourceStack source, String key, String value) {
        if (checkBanned(source)) return -1;

        seasonConfig.setProperty(key, value);
        ConfigManager.onUpdatedUnknown(key, value);

        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.CONFIG_SET.get(key));

        NetworkHandlerServer.updatedConfigThisTick = true;
        if (DefaultConfigValues.RELOAD_NEEDED.contains(key)) {
            NetworkHandlerServer.configNeedsReload = true;
        }
        return 1;
    }

    public int getWorlds(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component worldSavesText = TextUtils.format("§7If you want to play on the exact same world seeds as Grian did, click {}§7 to open a dropbox where you can download the pre-made worlds.", TextUtils.openURLText("https://www.dropbox.com/scl/fo/jk9fhqx0jjbgeo2qa6v5i/AOZZxMx6S7MlS9HrIRJkkX4?rlkey=2khwcnf2zhgi6s4ik01e3z9d0&st=ghw1d8k6&dl=0"));
        OtherUtils.sendCommandFeedbackQuiet(source, worldSavesText);
        return 1;
    }

    public int defaultCommand(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        getDiscord(source);
        return 1;
    }

    public int wiki(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component text = TextUtils.format("§7Click {}§7 to open the Life Series Mod wiki", TextUtils.openURLText("https://mat0u5.github.io/LifeSeries-docs"));
        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getDiscord(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component text = TextUtils.format("§7Click {}§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)", TextUtils.openURLText("https://discord.gg/QWJxfb4zQZ"));
        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getSeason(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SEASON_GET.get(currentSeason.getSeason().getId()));
        if (source.getPlayer() != null) {
            currentSeason.sendSetSeasonPacket(source.getPlayer());
        }
        return 1;
    }

    public int getVersion(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.MOD_VERSION.get(Main.MOD_VERSION));
        return 1;
    }

    public int reload(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, ModifiableText.MOD_RELOAD.get());
        OtherUtils.reloadServer();
        return 1;
    }

    public int getCredits(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("§7The Life Series was originally created by §fGrian§7" +
                ", and this mod, created by §fMat0u5§7, aims to recreate every single season one-to-one."));
        OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("§7This mod uses sounds created by §fOli (TheOrionSound)§7, and uses recreated snail model (first created by §fDanny§7), and a recreated trivia bot model (first created by §fHoffen§7)."));
        return 1;
    }
}
