package net.mat0u5.lifeseries.seasons.boogeyman;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BoogeymanCommand {

    public static boolean isAllowed() {
        return getBM().BOOGEYMAN_ENABLED;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when the quizmaster has been enabled in the Life Series config."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("boogeyman")
                .requires(PermissionManager::isAdmin)
                .then(literal("clear")
                    .executes(context -> boogeyClear(
                        context.getSource()
                    ))
                )
                .then(literal("list")
                    .executes(context -> boogeyList(
                        context.getSource()
                    ))
                )
                .then(literal("count")
                    .executes(context -> boogeyCount(
                        context.getSource()
                    ))
                )
                .then(literal("add")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> addBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("remove")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> removeBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("cure")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> cureBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("fail")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> failBoogey(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                        )
                )
                .then(literal("chooseRandom")
                    .executes(context -> boogeyChooseRandom(
                        context.getSource()
                    ))
                )

        );
    }

    public static BoogeymanManager getBM() {
        return currentSeason.boogeymanManager;
    }

    public static int failBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is not a Quizmaster"));
            return -1;
        }
        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Failing Quizmaster for {}§7...", target));
        }
        bm.playerFailBoogeymanManually(target);

        return 1;
    }

    public static int cureBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is not a Quizmaster"));
            return -1;
        }
        bm.cure(target);

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Curing {}§7...", target));
        }

        return 1;
    }

    public static int addBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;

        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is already a Quizmaster"));
            return -1;
        }
        bm.addBoogeymanManually(target);

        OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is now a Quizmaster", target));
        return 1;
    }

    public static int removeBoogey(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;

        if (target == null) return -1;

        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(target)) {
            source.sendError(Text.of("That player is not a Quizmaster"));
            return -1;
        }
        bm.removeBoogeymanManually(target);

        OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is no longer a Quizmaster", target));
        return 1;
    }

    public static int boogeyList(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        List<String> allBoogeymen = new ArrayList<>();
        List<String> curedBoogeymen = new ArrayList<>();
        List<String> failedBoogeymen = new ArrayList<>();
        for (Boogeyman boogeyman : bm.boogeymen) {
            if (boogeyman.cured) {
                curedBoogeymen.add(boogeyman.name);
            }
            else if (boogeyman.failed) {
                failedBoogeymen.add(boogeyman.name);
            }
            else {
                allBoogeymen.add(boogeyman.name);
            }
        }

        if (allBoogeymen.isEmpty()) allBoogeymen.add("§7None");
        if (curedBoogeymen.isEmpty()) curedBoogeymen.add("§7None");
        if (failedBoogeymen.isEmpty()) failedBoogeymen.add("§7None");

        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Remaining Quizmasters: {}", allBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Cured Quizmasters: {}", curedBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Failed Quizmasters: {}", failedBoogeymen));
        return 1;
    }

    public static int boogeyCount(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        int allBoogeymen = 0;
        int curedBoogeymen = 0;
        int failedBoogeymen = 0;
        for (Boogeyman boogeyman : bm.boogeymen) {
            if (boogeyman.cured) {
                curedBoogeymen++;
            }
            else if (boogeyman.failed) {
                failedBoogeymen++;
            }
            else {
                allBoogeymen++;
            }
        }

        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Remaining Quizmasters: {}", allBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Cured Quizmasters: {}", curedBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Failed Quizmasters: {}", failedBoogeymen));
        return 1;
    }

    public static int boogeyClear(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        bm.resetBoogeymen();
        OtherUtils.sendCommandFeedback(source, Text.of("All Quizmasters have been cleared"));
        return 1;
    }

    public static int boogeyChooseRandom(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("§7Choosing random Quizmaster..."));

        bm.resetBoogeymen();
        bm.prepareToChooseBoogeymen();

        return 1;
    }
}
