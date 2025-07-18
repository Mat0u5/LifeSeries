package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;

import static net.mat0u5.lifeseries.Main.ALLOWED_SEASON_NAMES;
import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LifeSeriesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("lifeseries")
                .executes(context -> defaultCommand(context.getSource()))
                .then(literal("worlds")
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
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .executes(context -> config(context.getSource()))
                )
                .then(literal("reload")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> reload(context.getSource()))
                )
                .then(literal("chooseSeries")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .executes(context -> chooseSeason(context.getSource()))
                )
                .then(literal("setSeries")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
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
        );

        if (VersionControl.isDevVersion()) {
            dispatcher.register(
                literal("ls")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(literal("test")
                        .executes(context -> test(context.getSource()))
                    )
                    .then(literal("test1")
                        .executes(context -> test1(context.getSource()))
                    )
                    .then(literal("test2")
                        .executes(context -> test2(context.getSource()))
                    )
                    .then(literal("test3")
                        .executes(context -> test3(context.getSource()))
                    )
            );
        }

    }

    public static int chooseSeason(ServerCommandSource source) {
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            source.sendError(Text.of("§cYou must have the Life Series mod installed §nclient-side§c to open the season selection GUI."));
            source.sendError(Text.of("§7Use the §f'/lifeseries setSeries <season>'§7 command instead."));
            return -1;
        }
        OtherUtils.sendCommandFeedback(source, Text.of("§7Opening the season selection GUI..."));
        NetworkHandlerServer.sendStringPacket(source.getPlayer(), "select_season", Seasons.getStringNameFromSeason(currentSeason.getSeason()));
        return 1;
    }

    public static int setSeason(ServerCommandSource source, String setTo, boolean confirmed) {
        if (!ALLOWED_SEASON_NAMES.contains(setTo)) {
            source.sendError(Text.of("That is not a valid season!"));
            source.sendError(Text.literal("You must choose one of the following: ").append(Text.literal(String.join(", ", ALLOWED_SEASON_NAMES)).formatted(Formatting.GRAY)));
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
                OtherUtils.sendCommandFeedbackQuiet(source, Text.of("WARNING: you have already selected a season, changing it might cause some saved data to be lost (lives, ...)"));
                OtherUtils.sendCommandFeedbackQuiet(source, Text.literal("If you are sure, use '")
                        .append(Text.literal("/lifeseries chooseSeries").formatted(Formatting.GRAY))
                        .append(Text.literal(" confirm").formatted(Formatting.GREEN)).append(Text.of("'")));
            }
        }
        return 1;
    }

    public static void setSeasonFinal(ServerCommandSource source, String setTo) {
        if (Main.changeSeasonTo(setTo)) {
            OtherUtils.sendCommandFeedback(source, Text.literal("Changing the season to " + setTo + "..."));
            OtherUtils.broadcastMessage(Text.literal("Successfully changed the season to " + setTo + ".").formatted(Formatting.GREEN));
        }
    }

    public static int config(ServerCommandSource source) {
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7 The Life Series config folder is located server-side at §a" + new File("./config/lifeseries").getAbsolutePath()));
        if (source.getPlayer() == null) {
            return -1;
        }

        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            source.sendError(Text.of("§cYou must have the Life Series mod installed §nclient-side§c to open the config GUI."));
            source.sendError(Text.of("§cEither install the mod on the client on modify the config folder (see above)."));
            return -1;
        }
        else {
            OtherUtils.sendCommandFeedback(source, Text.of("§7Opening the config GUI..."));
            NetworkHandlerServer.sendStringPacket(source.getPlayer(), "open_config","");
        }
        return 1;
    }

    public static int getWorlds(ServerCommandSource source) {
        Text worldSavesText = Text.literal("§7Additionally, if you want to play on the exact same worlds as Grian did, click ").append(
                Text.literal("here")
                        .styled(style -> style
                                .withColor(Formatting.BLUE)
                                .withClickEvent(TextUtils.openURLClickEvent("https://www.dropbox.com/scl/fo/jk9fhqx0jjbgeo2qa6v5i/AOZZxMx6S7MlS9HrIRJkkX4?rlkey=2khwcnf2zhgi6s4ik01e3z9d0&st=ghw1d8k6&dl=0"))
                                .withUnderline(true)
                        )).append(Text.of("§7 to open a dropbox where you can download the pre-made worlds."));
        OtherUtils.sendCommandFeedbackQuiet(source, worldSavesText);
        return 1;
    }

    public static int defaultCommand(ServerCommandSource source) {
        getDiscord(source);
        return 1;
    }

    public static int getDiscord(ServerCommandSource source) {
        Text text = Text.literal("§7Click ").append(
                Text.literal("here")
                        .styled(style -> style
                                .withColor(Formatting.BLUE)
                                .withClickEvent(TextUtils.openURLClickEvent("https://discord.gg/QWJxfb4zQZ"))
                                .withUnderline(true)
                        )).append(Text.of("§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)"));
        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public static int getSeason(ServerCommandSource source) {
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Current season: "+ Seasons.getStringNameFromSeason(currentSeason.getSeason())));
        if (source.getPlayer() != null) {
            NetworkHandlerServer.sendStringPacket(source.getPlayer(), "season_info", Seasons.getStringNameFromSeason(currentSeason.getSeason()));
        }
        return 1;
    }

    public static int getVersion(ServerCommandSource source) {
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Mod version: "+ Main.MOD_VERSION));
        return 1;
    }

    public static int reload(ServerCommandSource source) {
        OtherUtils.sendCommandFeedback(source, Text.of("Reloading the Life Series!"));
        OtherUtils.reloadServer();
        return 1;
    }

    public static int getCredits(ServerCommandSource source) {
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7The Life Series was originally created by §fGrian§7" +
                ", and this mod, created by §fMat0u5§7, aims to recreate every single season one-to-one."));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7This mod uses sounds created by §fOli (TheOrionSound)§7, and uses recreated snail model (first created by §fDanny§7), and a recreated trivia bot model (first created by §fHoffen§7)."));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7This mod bundles other mods to improve the experience, such as §fPolymer§7, §fBlockbench Import Library§7, §fCardinal Components API§7, and supports client-side configuration with §fCloth Config§7."));
        return 1;
    }

    public static int test(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        WildcardManager.showCryptTitle("A wildcard is active!");
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command"));

        return 1;
    }

    public static int test1(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        WildcardManager.showRainbowCryptTitle("All wildcards are active!");
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command 1"));

        return 1;
    }

    public static int test2(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command 2"));

        return 1;
    }
    public static int test3(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command 3"));

        return 1;
    }
}
