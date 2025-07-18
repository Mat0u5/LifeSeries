package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BoogeymanCommand {

    public static boolean isAllowed() {
        return getBM().BOOGEYMAN_ENABLED;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when the boogeyman has been enabled in the Life Series config."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("boogeyman")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null) || !currentSeason.isAlive(source.getPlayer())))
                .then(literal("clear")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> boogeyClear(
                        context.getSource()
                    ))
                )
                .then(literal("list")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null) || !currentSeason.isAlive(source.getPlayer())))
                    .executes(context -> boogeyList(
                        context.getSource()
                    ))
                )
                .then(literal("add")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> addBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("remove")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> removeBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("cure")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> cureBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("fail")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> failBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                        )
                )
                .then(literal("chooseRandom")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> boogeyChooseRandom(
                        context.getSource()
                    ))
                )

        );
    }

    public static BoogeymanManager getBM() {
        return currentSeason.boogeymanManagerNew;
    }

    public static int failBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is not a boogeyman!"));
            return -1;
        }
        OtherUtils.sendCommandFeedback(source, Text.of("Failing boogeyman for " + target.getName().getString() + "..."));
        bm.playerFailBoogeyman(target);

        return 1;
    }

    public static int cureBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is not a boogeyman!"));
            return -1;
        }
        bm.cure(target);

        OtherUtils.sendCommandFeedback(source, Text.literal("").append(target.getStyledDisplayName()).append(Text.of(" is now cured.")));

        return 1;
    }

    public static int addBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;

        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is already a boogeyman!"));
            return -1;
        }
        bm.addBoogeymanManually(target);

        OtherUtils.sendCommandFeedback(source, Text.literal("").append(target.getStyledDisplayName()).append(Text.of(" is now a boogeyman.")));
        return 1;
    }

    public static int removeBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;

        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is not a boogeyman!"));
            return -1;
        }
        bm.removeBoogeymanManually(target);

        OtherUtils.sendCommandFeedback(source, Text.literal("").append(target.getStyledDisplayName()).append(Text.of(" is no longer a boogeyman.")));
        return 1;
    }

    public static int boogeyList(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        List<String> boogeymen = new ArrayList<>();
        for (Boogeyman boogeyman : bm.boogeymen) {
            boogeymen.add(boogeyman.name);
        }

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Current boogeymen: ["+String.join(", ",boogeymen)+"]"));
        return 1;
    }

    public static int boogeyClear(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        bm.resetBoogeymen();
        OtherUtils.sendCommandFeedback(source, Text.of("All boogeymen have been cleared"));
        return 1;
    }

    public static int boogeyChooseRandom(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("Choosing random boogeymen..."));

        bm.resetBoogeymen();
        bm.prepareToChooseBoogeymen();

        return 1;
    }
}
