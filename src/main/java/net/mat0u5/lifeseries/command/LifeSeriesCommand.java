package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.mat0u5.lifeseries.Main.ALLOWED_SEASON_NAMES;
import static net.mat0u5.lifeseries.Main.currentSeason;

public class LifeSeriesCommand extends Command {

    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Text getBannedText() {
        return Text.of("");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("lifeseries")
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
                        .suggests((context, builder) -> CommandSource.suggestMatching(ALLOWED_SEASON_NAMES, builder))
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
                )
        );
    }

    private int enableOrDisable(ServerCommandSource source, boolean disabled) {
        OtherUtils.sendCommandFeedback(source, TextUtils.format("The Life Series has been {}", disabled ? "disabled" : "enabled"));
        Main.setDisabled(disabled);
        return 1;
    }

    public int chooseSeason(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            source.sendError(Text.of("You must have the Life Series mod installed §nclient-side§r to open the season selection GUI."));
            source.sendError(Text.of("Use the '/lifeseries setSeries <season>' command instead."));
            return -1;
        }
        OtherUtils.sendCommandFeedback(source, Text.of("§7Opening the season selection GUI..."));
        NetworkHandlerServer.sendStringPacket(source.getPlayer(), PacketNames.SELECT_SEASON, currentSeason.getSeason().getId());
        return 1;
    }

    public int setSeason(ServerCommandSource source, String setTo, boolean confirmed) {
        if (checkBanned(source)) return -1;
        if (!ALLOWED_SEASON_NAMES.contains(setTo)) {
            source.sendError(Text.of("That is not a valid season!"));
            source.sendError(TextUtils.formatPlain("You must choose one of the following: {}", ALLOWED_SEASON_NAMES));
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
                OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7WARNING: you have already selected a season, changing it might cause some saved data to be lost (lives, ...)"));
                OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7If you are sure, use '§f/lifeseries setSeries <season> confirm§7'"));
            }
        }
        return 1;
    }

    public void setSeasonFinal(ServerCommandSource source, String setTo) {
        if (Main.changeSeasonTo(setTo)) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Changing the season to {}§7...", setTo));
            PlayerUtils.broadcastMessage(TextUtils.format("Successfully changed the season to {}",setTo).formatted(Formatting.GREEN));
        }
    }

    public int config(ServerCommandSource source) {
        //if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) {
            return -1;
        }
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            source.sendError(Text.of("You must have the Life Series mod installed §nclient-side§r to open the config GUI."));
            source.sendError(Text.of("Either install the mod on the client on modify the config folder."));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, Text.of("§7Opening the config GUI..."));
        NetworkHandlerServer.sendStringPacket(source.getPlayer(), PacketNames.OPEN_CONFIG,"");
        return 1;
    }

    public int getWorlds(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        Text worldSavesText = TextUtils.format("§7If you want to play on the exact same world seeds as Grian did, click {}§7 to open a dropbox where you can download the pre-made worlds.", TextUtils.openURLText("https://www.dropbox.com/scl/fo/jk9fhqx0jjbgeo2qa6v5i/AOZZxMx6S7MlS9HrIRJkkX4?rlkey=2khwcnf2zhgi6s4ik01e3z9d0&st=ghw1d8k6&dl=0"));
        OtherUtils.sendCommandFeedbackQuiet(source, worldSavesText);
        return 1;
    }

    public int defaultCommand(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        getDiscord(source);
        return 1;
    }

    public int getDiscord(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        Text text = TextUtils.format("§7Click {}§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)\"", TextUtils.openURLText("https://discord.gg/QWJxfb4zQZ"));
        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getSeason(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Current season: {}", currentSeason.getSeason().getId()));
        if (source.getPlayer() != null) {
            NetworkHandlerServer.sendStringPacket(source.getPlayer(), PacketNames.SEASON_INFO, currentSeason.getSeason().getId());
        }
        return 1;
    }

    public int getVersion(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Mod version: {}",Main.MOD_VERSION));
        return 1;
    }

    public int reload(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, Text.of("§7Reloading the Life Series..."));
        OtherUtils.reloadServer();
        return 1;
    }

    public int getCredits(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7The Life Series was originally created by §fGrian§7" +
                ", and this mod, created by §fMat0u5§7, aims to recreate every single season one-to-one."));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7This mod uses sounds created by §fOli (TheOrionSound)§7, and uses recreated snail model (first created by §fDanny§7), and a recreated trivia bot model (first created by §fHoffen§7)."));
        return 1;
    }
}
