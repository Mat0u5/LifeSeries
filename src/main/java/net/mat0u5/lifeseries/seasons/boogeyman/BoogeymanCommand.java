package net.mat0u5.lifeseries.seasons.boogeyman;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLifeBoogeymanManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when the boogeyman has been enabled in the Life Series config.");
    }

    public List<String> getAdminCommands() {
        return List.of("boogeyman");
    }

    public List<String> getNonAdminCommands() {
        return List.of("boogeyman");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("boogeyman")
                .executes(context -> boogeyCheck(
                        context.getSource()
                ))
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
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> addBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("remove")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> removeBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("reset")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgument.players())
                                .executes(context -> resetBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
                .then(literal("cure")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> cureBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("fail")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgument.players())
                                .executes(context -> failBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
                .then(literal("selfFail")
                        .then(literal("confirm")
                                .executes(context -> selfFailBoogey(context.getSource(), true))
                        )
                        .executes(context -> selfFailBoogey(context.getSource(), false))
                )
                .then(literal("randomize")
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

    public int boogeyCheck(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(self)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY.get());
            return -1;
        }
        Boogeyman boogeyman = bm.getBoogeyman(self);
        if (boogeyman != null) {
            if (boogeyman.failed) {
                OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_ALREADY_FAILED.get());
                return 1;

            }
            else if (boogeyman.cured) {
                OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_ALREADY_CURED.get());
                return 1;
            }
        }
        if (bm instanceof PastLifeBoogeymanManager) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_IS.get());
        }

        bm.messageBoogeyman(boogeyman, self);

        return 1;
    }

    public int selfFailBoogey(CommandSourceStack source, boolean confirm) {
        if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(self)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY.get());
            return -1;
        }
        Boogeyman boogeyman = bm.getBoogeyman(self);
        if (boogeyman != null) {
            if (boogeyman.cured) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_ALREADY_CURED.get());
                return -1;
            }
            if (boogeyman.failed) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_ALREADY_FAILED.get());
                return -1;
            }
        }

        if (!confirm) {
            OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_SELFFAIL_WARNING.get());
            return -1;
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_SELFFAIL.get());
        }
        else {
            PlayerUtils.broadcastMessage(ModifiableText.BOOGEYMAN_FAIL_SELF.get(self));
        }
        bm.playerFailBoogeymanManually(self, false);

        return 1;
    }

    public int failBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.playerFailBoogeymanManually(player, true);
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_FAIL_OTHER_SINGLE.get(targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_FAIL_OTHER_MULTIPLE.get(targets.size()));
            }
        }

        return 1;
    }
    public int resetBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.reset(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int cureBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.cure(player);
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_CURE_SINGLE.get(targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_CURE_MULTIPLE.get(targets.size()));
            }
        }

        return 1;
    }

    public int addBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (bm.isBoogeyman(target)) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_IS.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            if (!bm.isBoogeyman(player)) {
                bm.addBoogeymanManually(player);
            }
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_ADD_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_ADD_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int removeBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                OtherUtils.sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.removeBoogeymanManually(player);
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_REMOVE_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_REMOVE_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int boogeyList(CommandSourceStack source) {
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

        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_REMAINING.get(allBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_CURED.get(curedBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_FAILED.get(failedBoogeymen));
        return 1;
    }

    public int boogeyCount(CommandSourceStack source) {
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

        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_REMAINING.get(allBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_CURED.get(curedBoogeymen));
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_FAILED.get(failedBoogeymen));
        return 1;
    }

    public int boogeyClear(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        bm.resetBoogeymen();
        OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_CLEAR.get());
        return 1;
    }

    public int boogeyChooseRandom(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        OtherUtils.sendCommandFeedback(source, ModifiableText.BOOGEYMAN_RANDOMIZE.get());

        bm.resetBoogeymen();
        bm.prepareToChooseBoogeymen();

        return 1;
    }
}
