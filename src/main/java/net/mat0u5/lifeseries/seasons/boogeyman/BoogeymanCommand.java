package net.mat0u5.lifeseries.seasons.boogeyman;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class BoogeymanCommand extends Command {

    @Override
    public boolean isAllowed() {
        return getBM().BOOGEYMAN_ENABLED;
    }

    @Override
    public Text getBannedText() {
        return Text.of("This command is only available when the boogeyman has been enabled in the Life Series config.");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("boogeyman")
                .then(literal("clear")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> boogeyClear(
                        context.getSource()
                    ))
                )
                .then(literal("list")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> boogeyList(
                        context.getSource()
                    ))
                )
                .then(literal("count")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> boogeyCount(
                        context.getSource()
                    ))
                )
                .then(literal("add")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgumentType.players())
                        .executes(context -> addBoogey(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
                    )
                )
                .then(literal("remove")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgumentType.players())
                        .executes(context -> removeBoogey(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
                    )
                )
                .then(literal("cure")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgumentType.players())
                        .executes(context -> cureBoogey(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
                    )
                )
                .then(literal("fail")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgumentType.players())
                                .executes(context -> failBoogey(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
                        )
                )
                .then(literal("selfFail")
                        .then(literal("confirm")
                                .executes(context -> selfFailBoogey(context.getSource(), true))
                        )
                        .executes(context -> selfFailBoogey(context.getSource(), false))
                )
                .then(literal("chooseRandom")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> boogeyChooseRandom(
                        context.getSource()
                    ))
                )

        );
    }

    public BoogeymanManager getBM() {
        return currentSeason.boogeymanManager;
    }

    public int selfFailBoogey(ServerCommandSource source, boolean confirm) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(self)) {
            source.sendError(Text.of("You are not a Boogeyman"));
            return -1;
        }
        if (!confirm) {
            source.sendError(Text.of("Warning: This will cause you to fail as the Boogeyman"));
            source.sendError(Text.of("Run \"/boogeyman selfFail §lconfirm§r\" to confirm this action."));
            return -1;
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7Failing as the Boogeyman..."));
        }
        else {
            PlayerUtils.broadcastMessage(TextUtils.format("{}§7 voulentarily failed themselves as the Boogeyman. They have been consumed by the curse.", self));
        }
        bm.playerFailBoogeymanManually(self, false);

        return 1;
    }

    public int failBoogey(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayerEntity target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                source.sendError(Text.of("That player is not a Boogeyman"));
                return -1;
            }
        }

        for (ServerPlayerEntity player : targets) {
            bm.playerFailBoogeymanManually(player, true);
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Failing Boogeyman for {}§7...", targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Failing Boogeyman for {} targets§7...", targets.size()));
            }
        }

        return 1;
    }

    public int cureBoogey(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayerEntity target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                source.sendError(Text.of("That player is not a Boogeyman"));
                return -1;
            }
        }

        for (ServerPlayerEntity player : targets) {
            bm.cure(player);
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Curing {}§7...", targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Curing {} targets§7...", targets.size()));
            }
        }

        return 1;
    }

    public int addBoogey(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayerEntity target = targets.iterator().next();
            if (bm.isBoogeyman(target)) {
                source.sendError(Text.of("That player is already a Boogeyman"));
                return -1;
            }
        }

        for (ServerPlayerEntity player : targets) {
            bm.addBoogeymanManually(player);
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is now a Boogeyman", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} targets are now Boogeymen", targets.size()));
        }

        return 1;
    }

    public int removeBoogey(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayerEntity target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                source.sendError(Text.of("That player is not a Boogeyman"));
                return -1;
            }
        }

        for (ServerPlayerEntity player : targets) {
            bm.removeBoogeymanManually(player);
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is no longer a Boogeyman", targets.iterator().next()));

        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("{} targets are no longer Boogeymen", targets.size()));
        }

        return 1;
    }

    public int boogeyList(ServerCommandSource source) {
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

        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Remaining Boogeymen: {}", allBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Cured Boogeymen: {}", curedBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Failed Boogeymen: {}", failedBoogeymen));
        return 1;
    }

    public int boogeyCount(ServerCommandSource source) {
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

        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Remaining Boogeymen: {}", allBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Cured Boogeymen: {}", curedBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Failed Boogeymen: {}", failedBoogeymen));
        return 1;
    }

    public int boogeyClear(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        bm.resetBoogeymen();
        OtherUtils.sendCommandFeedback(source, Text.of("All Boogeymen have been cleared"));
        return 1;
    }

    public int boogeyChooseRandom(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("§7Choosing random Boogeymen..."));

        bm.resetBoogeymen();
        bm.prepareToChooseBoogeymen();

        return 1;
    }
}
