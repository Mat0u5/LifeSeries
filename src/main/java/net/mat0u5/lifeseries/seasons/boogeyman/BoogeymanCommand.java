package net.mat0u5.lifeseries.seasons.boogeyman;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.CustomCommand;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLife;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeBoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLifeBoogeymanManager;
import net.mat0u5.lifeseries.seasons.util.WatcherManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class BoogeymanCommand extends CustomCommand {

    @Override
    public boolean isAllowed() {
        return isAllowedNormal() || isAllowedFinalLife();
    }

    public boolean isAllowedNormal() {
        return getBM().BOOGEYMAN_ENABLED.get();
    }

    public boolean isAllowedFinalLife() {
        return currentSeason instanceof FinalLife finalLife && finalLife.finalLifeBoogeymanManager.isEnabled();
    }

    public boolean checkBannedNormal(CommandSourceStack source) {
        if (super.checkBanned(source)) return true;
        if (isAllowedNormal()) {
            return false;
        }
        else {
            sendCommandFailure(source, getBannedTextNormal());
            return true;
        }
    }

    public boolean checkBannedFinalLife(CommandSourceStack source) {
        if (super.checkBanned(source)) return true;
        if (isAllowedFinalLife()) {
            return false;
        }
        else {
            sendCommandFailure(source, getBannedTextFinalLife());
            return true;
        }
    }

    @Override
    public Component getBannedText() {
        return getBannedTextNormal();
    }

    public Component getBannedTextNormal() {
        if (isAllowedFinalLife()) {
            return Component.nullToEmpty("This command is only available when the boogeyman has been enabled in the Life Series config.\n\nFinal Life has different Boogeyman behavior. Use the '/boogeyman finalLife ...' command instead.");
        }
        return Component.nullToEmpty("This command is only available when the boogeyman has been enabled in the Life Series config.");
    }

    public Component getBannedTextFinalLife() {
        return Component.nullToEmpty("This command is only available in Final Life when the Final Life Boogeyman has been enabled in the config.");
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
                    .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                    .executes(context -> boogeyClear(
                        context.getSource()
                    ))
                )
                .then(literal("list")
                    .requires(source -> {
                        if (!isAllowedNormal()) return false;
                        if (PermissionManager.isAdmin(source)) return true;
                        ServerPlayer self = source.getPlayer();
						return self != null && WatcherManager.WATCHERS_SEE_BOOGEY_AND_SOCIETY.get() && ((IPlayer) self).ls$isWatcher();
					})
                    .executes(context -> boogeyList(
                        context.getSource()
                    ))
                )
                .then(literal("count")
                    .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                    .executes(context -> boogeyCount(
                        context.getSource()
                    ))
                )
                .then(literal("add")
                    .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> addBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("remove")
                    .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> removeBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("reset")
                        .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                        .then(argument("player", EntityArgument.players())
                                .executes(context -> resetBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
                .then(literal("cure")
                    .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> cureBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("fail")
                        .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                        .then(argument("player", EntityArgument.players())
                                .executes(context -> failBoogey(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
                .then(literal("selfFail")
                        .requires(source -> isAllowedNormal())
                        .then(literal("confirm")
                                .executes(context -> selfFailBoogey(context.getSource(), true))
                        )
                        .executes(context -> selfFailBoogey(context.getSource(), false))
                )
                .then(literal("randomize")
                    .requires(source -> isAllowedNormal() && PermissionManager.isAdmin(source))
                    .executes(context -> boogeyChooseRandom(
                        context.getSource()
                    ))
                )
                .then(literal("finalLife")
                        .requires(source -> isAllowedFinalLife() && PermissionManager.isAdmin(source))
                        .then(literal("add")
                                .then(argument("player", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(fl_getBoogeySuggestions(true), builder))
                                        .executes(context -> fl_addBoogey(context.getSource(), StringArgumentType.getString(context, "player")))
                                )
                        )
                        .then(literal("remove")
                                .then(argument("player", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(fl_getBoogeySuggestions(false), builder))
                                        .executes(context -> fl_removeBoogey(context.getSource(), StringArgumentType.getString(context, "player")))
                                )
                        )
                        .then(literal("list")
                                .executes(context -> fl_listBoogey(context.getSource()))
                        )
                        .then(literal("clear")
                                .executes(context -> fl_clearBoogey(context.getSource()))
                        )
                )
        );
    }

    public BoogeymanManager getBM() {
        return currentSeason.boogeymanManager;
    }

    public int boogeyCheck(CommandSourceStack source) {
        if (checkBannedNormal(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(self)) {
            sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY.get());
            return -1;
        }
        Boogeyman boogeyman = bm.getBoogeyman(self);
        if (boogeyman != null) {
            if (boogeyman.failed) {
                sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_ALREADY_FAILED.get());
                return 1;

            }
            else if (boogeyman.cured) {
                sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_ALREADY_CURED.get());
                return 1;
            }
        }
        if (bm instanceof PastLifeBoogeymanManager) {
            sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_IS.get());
        }

        bm.messageBoogeyman(boogeyman, self);

        return 1;
    }

    public int selfFailBoogey(CommandSourceStack source, boolean confirm) {
        if (checkBannedNormal(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (!bm.isBoogeyman(self)) {
            sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY.get());
            return -1;
        }
        Boogeyman boogeyman = bm.getBoogeyman(self);
        if (boogeyman != null) {
            if (boogeyman.cured) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_ALREADY_CURED.get());
                return -1;
            }
            if (boogeyman.failed) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_ALREADY_FAILED.get());
                return -1;
            }
        }

        if (!confirm) {
            sendCommandFailure(source, ModifiableText.BOOGEYMAN_SELFFAIL_WARNING.get());
            return -1;
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME.get()) {
            sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_SELFFAIL.get());
        }
        else {
            PlayerUtils.broadcastMessage(ModifiableText.BOOGEYMAN_FAIL_SELFFAIL.get(self));
        }
        bm.playerFailBoogeymanManually(self, false);

        return 1;
    }

    public int failBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.playerFailBoogeymanManually(player, true);
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME.get()) {
            if (targets.size() == 1) {
                sendCommandFeedback(source, ModifiableText.BOOGEYMAN_FAIL_OTHER_SINGLE.get(targets.iterator().next()));
            }
            else {
                sendCommandFeedback(source, ModifiableText.BOOGEYMAN_FAIL_OTHER_MULTIPLE.get(targets.size()));
            }
        }

        return 1;
    }
    public int resetBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.reset(player);
        }

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.BOOGEYMAN_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.BOOGEYMAN_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int cureBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.cure(player);
        }

        if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME.get()) {
            if (targets.size() == 1) {
                sendCommandFeedback(source, ModifiableText.BOOGEYMAN_CURE_SINGLE.get(targets.iterator().next()));
            }
            else {
                sendCommandFeedback(source, ModifiableText.BOOGEYMAN_CURE_MULTIPLE.get(targets.size()));
            }
        }

        return 1;
    }

    public int addBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (bm.isBoogeyman(target)) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_IS.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            if (!bm.isBoogeyman(player)) {
                bm.addBoogeymanManually(player);
            }
        }
        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.BOOGEYMAN_ADD_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.BOOGEYMAN_ADD_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int removeBoogey(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!bm.isBoogeyman(target)) {
                sendCommandFailure(source, ModifiableText.BOOGEYMAN_ERROR_NOTBOOGEY_OTHER.get());
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
            bm.removeBoogeymanManually(player);
        }
        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.BOOGEYMAN_REMOVE_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.BOOGEYMAN_REMOVE_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int boogeyList(CommandSourceStack source) {
        if (checkBannedNormal(source)) return -1;
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

        sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_REMAINING.get(allBoogeymen));
        sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_CURED.get(curedBoogeymen));
        sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_FAILED.get(failedBoogeymen));
        return 1;
    }

    public int boogeyCount(CommandSourceStack source) {
        if (checkBannedNormal(source)) return -1;
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

        sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_REMAINING.get(allBoogeymen));
        sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_CURED.get(curedBoogeymen));
        sendCommandFeedbackQuiet(source, ModifiableText.BOOGEYMAN_LIST_FAILED.get(failedBoogeymen));
        return 1;
    }

    public int boogeyClear(CommandSourceStack source) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        bm.resetBoogeymen();
        sendCommandFeedback(source, ModifiableText.BOOGEYMAN_CLEAR.get());
        return 1;
    }

    public int boogeyChooseRandom(CommandSourceStack source) {
        if (checkBannedNormal(source)) return -1;
        BoogeymanManager bm = getBM();
        if (bm == null) return -1;

        sendCommandFeedback(source, ModifiableText.BOOGEYMAN_RANDOMIZE.get());

        bm.resetBoogeymen();
        bm.prepareToChooseBoogeymen();

        return 1;
    }


    /*
        Final Life Boogeyman
     */

    public FinalLifeBoogeymanManager fl_getBM() {
        if (currentSeason instanceof FinalLife finalLife) {
            return finalLife.finalLifeBoogeymanManager;
        }
        return null;
    }

    public List<String> fl_getBoogeySuggestions(boolean add) {
        FinalLifeBoogeymanManager bm = fl_getBM();
        if (bm == null) return List.of();
        return add ? bm.getAddSuggestions() : bm.getRemoveSuggestions();
    }

    public int fl_addBoogey(CommandSourceStack source, String targetName) {
        if (checkBannedFinalLife(source)) return -1;
        FinalLifeBoogeymanManager bm = fl_getBM();
        if (bm == null) return -1;
        if (!bm.isEnabled()) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_DISABLED.get());
            return -1;
        }

        UUID target = bm.resolvePlayer(targetName);
        if (target == null) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_UNKNOWN.get(targetName));
            return -1;
        }

        if (bm.isBoogeyman(target)) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_ALREADY.get());
            return -1;
        }

        String realName = FinalLifeBoogeymanManager.getRealName(target);
        bm.addBoogeyman(target);
        sendCommandFeedbackQuiet(source, ModifiableText.FINALLIFE_BOOGEYMAN_ADD.get(realName));

        return 1;
    }

    public int fl_removeBoogey(CommandSourceStack source, String targetName) {
        if (checkBannedFinalLife(source)) return -1;
        FinalLifeBoogeymanManager bm = fl_getBM();
        if (bm == null) return -1;
        if (!bm.isEnabled()) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_DISABLED.get());
            return -1;
        }

        UUID target = bm.resolvePlayer(targetName);
        if (target == null) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_UNKNOWN.get(targetName));
            return -1;
        }

        if (!bm.isBoogeyman(target)) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_NOT.get());
            return -1;
        }

        String realName = FinalLifeBoogeymanManager.getRealName(target);
        bm.removeBoogeyman(target);
        sendCommandFeedbackQuiet(source, ModifiableText.FINALLIFE_BOOGEYMAN_REMOVE.get(realName));

        return 1;
    }

    public int fl_listBoogey(CommandSourceStack source) {
        if (checkBannedFinalLife(source)) return -1;
        FinalLifeBoogeymanManager bm = fl_getBM();
        if (bm == null) return -1;
        if (!bm.isEnabled()) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_DISABLED.get());
            return -1;
        }

        if (!bm.anyBoogeymen()) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_NONE.get());
            return -1;
        }

        List<Component> boogeymen = new ArrayList<>();
        for (UUID uuid : bm.getBoogeymen()) {
            boogeymen.add(Component.literal(FinalLifeBoogeymanManager.getRealName(uuid)));
        }
        sendCommandFeedbackQuiet(source, ModifiableText.FINALLIFE_BOOGEYMAN_LIST.get(boogeymen));

        return 1;
    }

    public int fl_clearBoogey(CommandSourceStack source) {
        if (checkBannedFinalLife(source)) return -1;
        FinalLifeBoogeymanManager bm = fl_getBM();
        if (bm == null) return -1;
        if (!bm.isEnabled()) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_DISABLED.get());
            return -1;
        }

        if (!bm.anyBoogeymen()) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_NONE.get());
            return -1;
        }

        bm.clearBoogeymen();
        sendCommandFeedback(source, ModifiableText.FINALLIFE_BOOGEYMAN_COMMAND_CLEAR.get());
        return 1;
    }
}
