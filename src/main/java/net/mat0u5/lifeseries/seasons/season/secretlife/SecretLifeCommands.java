package net.mat0u5.lifeseries.seasons.season.secretlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.currentSession;

public class SecretLifeCommands extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.SECRET_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when playing Secret Life.");
    }

    public List<String> getAdminCommands() {
        return List.of("health", "task", "gift");
    }

    public List<String> getNonAdminCommands() {
        return List.of("health", "gift");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
            literal("health")
                .executes(context -> showHealth(context.getSource()))
                .then(literal("sync")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> syncHealth(
                        context.getSource())
                    )
                )
                .then(literal("add")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> healthManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"), 1, false)
                        )
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgument.getPlayers(context, "player"), DoubleArgumentType.getDouble(context, "amount"), false)
                            )
                        )
                    )
                )
                .then(literal("remove")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> healthManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"), -1, false)
                        )
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgument.getPlayers(context, "player"), -DoubleArgumentType.getDouble(context, "amount"), false)
                            )
                        )
                    )
                )
                .then(literal("set")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgument.getPlayers(context, "player"), DoubleArgumentType.getDouble(context, "amount"), true)
                            )
                        )
                    )
                )
                .then(literal("get")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> getHealthFor(
                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                        )
                    )
                )
                .then(literal("reset")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> resetHealth(
                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                        )
                    )
                )
        );
        dispatcher.register(
            literal("task")
                    .requires(PermissionManager::isAdmin)
                    .then(literal("succeed")
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> succeedTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("fail")
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> failTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("reroll")
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> rerollTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("randomize")
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> assignTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("clear")
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> clearTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("set")
                            .then(argument("player", EntityArgument.players())
                                    .then(argument("type", StringArgumentType.string())
                                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("easy","hard","red"), builder))
                                            .then(argument("task", StringArgumentType.greedyString())
                                                    .executes(context -> setTask(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(context, "player"),
                                                            StringArgumentType.getString(context, "type"),
                                                            StringArgumentType.getString(context, "task")
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(literal("get")
                            .then(argument("player", EntityArgument.player())
                                    .executes(context -> getTask(
                                            context.getSource(), EntityArgument.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("changeLocations")
                            .executes(context -> changeLocations(
                                    context.getSource())
                            )
                    )
        );
        dispatcher.register(
            literal("gift")
                .then(argument("player", EntityArgument.player())
                    .executes(context -> gift(
                        context.getSource(), EntityArgument.getPlayer(context, "player"))
                    )
                )
                .then(literal("reset")
                        .then(argument("player", EntityArgument.players())
                            .executes(context -> resetGift(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
        );
    }

    public int getTask(CommandSourceStack source, ServerPlayer player) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;

        boolean hasPreassignedTask = TaskManager.preAssignedTasks.containsKey(player.getUUID());
        boolean hasTaskBook = TaskManager.hasTaskBookCheck(player, false);

        if (!hasTaskBook && !hasPreassignedTask) {
            source.sendSystemMessage(ModifiableText.SECRETLIFE_TASK_MISSING_OTHER.get(player));
            return -1;
        }

        String rawTask = "";
        Task task = null;

        if (hasTaskBook) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_TASK_PRESENT.get(player));
            if (TaskManager.assignedTasks.containsKey(player.getUUID())) {
                task = TaskManager.assignedTasks.get(player.getUUID());
            }
        }
        else {
            //Pre-assigned task
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_TASK_PREASSIGNED.get(player));
            task = TaskManager.preAssignedTasks.get(player.getUUID());
        }

        if (task == null) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_READFAIL.get(player));
            return -1;
        }

        if (!task.formattedTask.isEmpty()) {
            rawTask = task.formattedTask;
        }
        else {
            rawTask = task.rawTask;
        }

        if (!rawTask.isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_TASK_SHOW.get(TextUtils.selfMessageText(rawTask)));
        }

        return 1;
    }

    public int setTask(CommandSourceStack source, Collection<ServerPlayer> targets, String type, String task) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;

        TaskTypes taskType = TaskTypes.EASY;

        if (type.equalsIgnoreCase("hard")) taskType = TaskTypes.HARD;
        if (type.equalsIgnoreCase("red")) taskType = TaskTypes.RED;

        task = task.replaceAll("\\\\n","\n");

        for (ServerPlayer player : targets) {
            TaskManager.preAssignedTasks.put(player.getUUID(), new Task(task, taskType));

            boolean inSession = TaskManager.tasksChosen && !currentSession.statusFinished();
            if (TaskManager.removePlayersTaskBook(player) || inSession) {
                TaskManager.assignRandomTaskToPlayer(player, taskType);
                AnimationUtils.playSecretLifeTotemAnimation(player, taskType == TaskTypes.RED);
                if (targets.size() == 1) {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET.get(player));
                }
            }
            else if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_PREASSIGN.get(player));
            }
        }

        if (targets.size() != 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int changeLocations(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_LOCATIONS.get());
        TaskManager.deleteLocations();
        TaskManager.checkSecretLifePositions();
        return 1;
    }

    public int clearTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;
        List<ServerPlayer> affected = new ArrayList<>();
        for (ServerPlayer player : targets) {
            if (TaskManager.removePlayersTaskBook(player)) {
                affected.add(player);
            }
        }

        if (affected.isEmpty()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_ERROR_BOOK_MISSING.get());
            return -1;
        }
        if (affected.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REMOVE_SINGLE.get(affected.get(0)));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REMOVE_MULTIPLE.get(affected.size()));
        }
        return 1;
    }

    public int assignTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET_RANDOM_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET_RANDOM_MULTIPLE.get(targets.size()));
        }

        TaskManager.chooseTasks(targets.stream().toList(), null);

        return 1;
    }

    public int succeedTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SUCCESS_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SUCCESS_MULTIPLE.get(targets.size()));
        }

        for (ServerPlayer player : targets) {
            TaskManager.succeedTask(player, true);
        }

        return 1;
    }

    public int failTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_FAIL_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_FAIL_MULTIPLE.get(targets.size()));
        }

        for (ServerPlayer player : targets) {
            TaskManager.failTask(player, true);
        }

        return 1;
    }

    public int rerollTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!TaskManager.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REROLL_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REROLL_MULTIPLE.get(targets.size()));
        }

        for (ServerPlayer player : targets) {
            TaskManager.rerollTask(player, true);
        }

        return 1;
    }

    public static final List<UUID> playersGiven = new ArrayList<>();
    public int resetGift(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        for (ServerPlayer player : targets) {
            playersGiven.remove(player.getUUID());
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_GIVEHEART_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_GIVEHEART_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }
    public int gift(CommandSourceStack source, ServerPlayer target) {
        if (checkBanned(source)) return -1;
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;
        SecretLife secretLife = (SecretLife) currentSeason;

        if (target == self) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SECRETLIFE_GIVEHEART_ERROR_SELF.get());
            return -1;
        }
        if (playersGiven.contains(self.getUUID())) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SECRETLIFE_GIVEHEART_ERROR_MULTIPLE.get());
            return -1;
        }
        if (target.ls$isDead()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SECRETLIFE_GIVEHEART_ERROR_DEAD.get());
            return -1;
        }
        if (!currentSession.statusStarted()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SESSION_ERROR_START.get());
            return -1;
        }
        playersGiven.add(self.getUUID());
        secretLife.addPlayerHealth(target, 2);
        Component senderMessage = ModifiableText.SECRETLIFE_GIVEHEART_SEND.get(target);
        Component recipientMessage = ModifiableText.SECRETLIFE_GIVEHEART_RECEIVE.get(self);
        SessionTranscript.giftHeart(self, target);

        self.ls$message(senderMessage);
        PlayerUtils.sendTitle(target, recipientMessage, 20, 20, 20);
        target.ls$message(recipientMessage);
        AnimationUtils.createSpiral(target, 40);

        PlayerUtils.playSoundToPlayers(List.of(self,target), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_life")));

        return 1;
    }

    public int showHealth(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        final ServerPlayer self = source.getPlayer();

        if (self == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;

        if (self.ls$isDead()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_SELF_DEAD.get());
            return -1;
        }

        double playerHealth = secretLife.getRoundedHealth(self);
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_SELF.get(playerHealth));

        return 1;
    }

    public int getHealthFor(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null) return -1;

        if (targets.size() > 1) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_LIST.get());
        }

        for (ServerPlayer player : targets) {
            SecretLife secretLife = (SecretLife) currentSeason;
            if (player.ls$isDead()) {
                OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_OTHER_DEAD.get(player));
                continue;
            }

            double playerHealth = secretLife.getRoundedHealth(player);
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_OTHER.get(player, playerHealth));
        }

        return 1;
    }

    public int syncHealth(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretLife secretLife = (SecretLife) currentSeason;
        secretLife.syncAllPlayerHealth();
        return 1;
    }

    public int healthManager(CommandSourceStack source, Collection<ServerPlayer> targets, double amount, boolean setNotGive) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;
        if (setNotGive) {
            for (ServerPlayer player : targets) {
                secretLife.setPlayerHealth(player, amount);
            }
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_SET_SINGLE.get(targets.iterator().next(), amount));
            }
            else {
                OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_SET_MULTIPLE.get(targets.size(), amount));
            }
        }
        else {
            for (ServerPlayer player : targets) {
                secretLife.addPlayerHealth(player, amount);
            }
            String addOrRemove = amount >= 0 ? "Added" : "Removed";
            String toOrFrom = amount >= 0 ? "to" : "from";
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_MODIFY_SINGLE.get(addOrRemove, Math.abs(amount), toOrFrom, targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_MODIFY_MULTIPLE.get(addOrRemove, Math.abs(amount), toOrFrom, targets.size()));
            }
        }

        return 1;
    }

    public int resetHealth(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        for (ServerPlayer player : targets) {
            SecretLife secretLife = (SecretLife) currentSeason;
            secretLife.resetPlayerHealth(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }
}
