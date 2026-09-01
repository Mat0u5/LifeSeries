package net.mat0u5.lifeseries.seasons.season.secretlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.ActionText;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;
import static net.mat0u5.lifeseries.LifeSeries.currentSession;

public class SecretLifeCommands extends Command {

    @Override
    public boolean isAllowed() {
        return LifeSeries.isSeason(Seasons.SECRET_LIFE);
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
                    .then(literal("succeed")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> succeedTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("fail")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> failTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("reroll")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> rerollTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("randomize")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> assignTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("clear")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.players())
                                    .executes(context -> clearTask(
                                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                                    )
                            )
                    )
                    .then(literal("set")
                            .requires(PermissionManager::isAdmin)
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
                    .then(literal("append")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.players())
                                    .then(argument("string", StringArgumentType.greedyString())
                                            .executes(context -> appendTask(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(context, "player"),
                                                            StringArgumentType.getString(context, "string")
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(literal("get")
                            .requires(PermissionManager::isAdmin)
                            .then(argument("player", EntityArgument.player())
                                    .executes(context -> getTask(
                                            context.getSource(), EntityArgument.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("changeLocations")
                            .requires(PermissionManager::isAdmin)
                            .executes(context -> changeLocations(
                                    context.getSource())
                            )
                    )
                    .then(literal("resetUsed")
                            .requires(PermissionManager::isAdmin)
                            .executes(context -> resetUsedTasks(
                                    context.getSource())
                            )
                    )
                    .then(literal("guess")
                            .then(argument("player", EntityArgument.player())
                                    .then(argument("task", StringArgumentType.greedyString())
                                            .executes(context -> guessTask(
                                                            context.getSource(),
                                                            EntityArgument.getPlayer(context, "player"),
                                                            StringArgumentType.getString(context, "task")
                                                    )
                                            )
                                    )
                            )
                            .then(literal("decide")
                                    .then(argument("player", EntityArgument.player())
                                            .then(literal("correct")
                                                    .executes(context -> guessTaskDecision(
                                                                    context.getSource(),
                                                                    EntityArgument.getPlayer(context, "player"), true
                                                            )
                                                    )
                                            )
                                            .then(literal("wrong")
                                                    .executes(context -> guessTaskDecision(
                                                                    context.getSource(),
                                                                    EntityArgument.getPlayer(context, "player"), false
                                                            )
                                                    )
                                            )
                                    )
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
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgument.players())
                            .executes(context -> resetGift(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
        );
    }

    public int guessTaskDecision(CommandSourceStack source, ServerPlayer target, boolean decision) {
        if (checkBanned(source)) return -1;
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;

        if (Objects.equals(self.getUUID(), target.getUUID())) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_ERROR_MISSING.get(target));
            return -1;
        }

        if (!TaskManager.GUESS_TASKS) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR.get());
            return -1;
        }

        if (TaskManager.getPlayersTaskBook(self) == null) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_ERROR_BOOK.get());
            return -1;
        }

        if (!TaskManager.taskGuesses.containsKey(target.getUUID())) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_ERROR_MISSING.get(target));
            return -1;
        }

        List<TaskManager.TaskGuess> taskGuesses = TaskManager.taskGuesses.get(target.getUUID());
        for (TaskManager.TaskGuess taskGuess : taskGuesses) {
            if (Objects.equals(taskGuess.player, self.getUUID())) {
                if (taskGuess.confirmed != null) {
                    break;
                }
                taskGuess.confirmed = decision;
                List<ServerPlayer> allPlayers = TaskManager.GUESS_TASKS_PUBLIC ? PlayerUtils.getAllPlayers() : new ArrayList<>(List.of(self, target));

                if (decision) {
                    PlayerUtils.broadcastMessage(allPlayers, ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_CORRECT.get(target, self));
                    PlayerUtils.broadcastMessage(List.of(self), ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_CORRECT_SELF.get());

                }
                else {
                    PlayerUtils.broadcastMessage(allPlayers, ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_WRONG.get(target, self));
                }
                return 1;
            }
        }


        sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_DECISION_ERROR_MISSING.get(target));
        return -1;
    }

    public int guessTask(CommandSourceStack source, ServerPlayer target, String guessedTaskStr) {
        if (checkBanned(source)) return -1;
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;

        if (!TaskManager.GUESS_TASKS) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR.get());
            return -1;
        }

        if (Objects.equals(self.getUUID(), target.getUUID())) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_SELF.get());
            return -1;
        }

        if (!TaskManager.tasksChosen) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_TASKS.get());
            return -1;
        }

        if (((IPlayer) self).ls$isDead()) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_GUESSER_DEAD.get());
            return -1;
        }
        if (((IPlayer) target).ls$isDead()) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_GUESSED_DEAD.get());
            return -1;
        }

        Integer selfLives = ((IPlayer) self).ls$getLives();
        Integer targetLives = ((IPlayer) target).ls$getLives();
        if (selfLives == null || targetLives == null) return -1;

        if (selfLives < TaskManager.GUESS_TASKS_LIFE_GUESSER_MIN) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_GUESSER_MIN.get(TaskManager.GUESS_TASKS_LIFE_GUESSER_MIN));
            return -1;
        }
        if (selfLives > TaskManager.GUESS_TASKS_LIFE_GUESSER_MAX) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_GUESSER_MAX.get(TaskManager.GUESS_TASKS_LIFE_GUESSER_MAX));
            return -1;
        }
        if (targetLives < TaskManager.GUESS_TASKS_LIFE_GUESSED_MIN) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_GUESSED_MIN.get(TaskManager.GUESS_TASKS_LIFE_GUESSED_MIN));
            return -1;
        }
        if (!TaskManager.taskGuesses.containsKey(self.getUUID())) {
            TaskManager.taskGuesses.put(self.getUUID(), new ArrayList<>());
        }

        List<TaskManager.TaskGuess> taskGuesses = TaskManager.taskGuesses.get(self.getUUID());
        for (TaskManager.TaskGuess taskGuess : taskGuesses) {
            if (Objects.equals(taskGuess.player, target.getUUID())) {
                sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_GUESSER_ALREADY.get());
                return -1;
            }
        }

        if (TaskManager.getPlayersTaskBook(target) == null) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_NO_BOOK.get(target));
            return -1;
        }

        if (!TaskManager.hasNonRedTaskBook(target)) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_GUESS_ERROR_RED.get());
            return -1;
        }

        taskGuesses.add(new TaskManager.TaskGuess(target.getUUID(), null));

        List<ServerPlayer> allPlayers = TaskManager.GUESS_TASKS_PUBLIC ? PlayerUtils.getAllPlayers() : new ArrayList<>(List.of(self, target));
        List<ServerPlayer> otherPlayers = new ArrayList<>(allPlayers);
        otherPlayers.remove(target);

        PlayerUtils.broadcastMessage(List.of(target), ModifiableText.SECRETLIFE_TASK_GUESS_HEADER.get(self));
        PlayerUtils.broadcastMessage(otherPlayers, ModifiableText.SECRETLIFE_TASK_GUESS_HEADER_PUBLIC.get(self, target));

        PlayerUtils.broadcastMessage(allPlayers, ModifiableText.SECRETLIFE_TASK_GUESS_BODY.get(guessedTaskStr));

        PlayerUtils.broadcastMessage(List.of(target), ModifiableText.SECRETLIFE_TASK_GUESS_PROMPT.get());
        PlayerUtils.broadcastMessage(otherPlayers, ModifiableText.SECRETLIFE_TASK_GUESS_PROMPT_PUBLIC.get(target));

        Component correctClick = new ActionText(Component.literal("§a[CORRECT]")).runCommand("Click if the task guess is correct", "/task guess decide " + self.getScoreboardName() + " correct").get();
        Component wrongClick = new ActionText(Component.literal("§c[WRONG]")).runCommand("Click if the task guess is wrong", "/task guess decide " + self.getScoreboardName() + " wrong").get();

        PlayerUtils.broadcastMessage(List.of(target), ModifiableText.SECRETLIFE_TASK_GUESS_VERDICT.get(correctClick, wrongClick));
        return 1;
    }

    public int getTask(CommandSourceStack source, ServerPlayer player) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;
        UUID uuid = SubInManager.getOrSub(player);

        boolean hasPreassignedTask = TaskManager.preAssignedTasks.containsKey(uuid);
        boolean hasTaskBook = SecretKeeper.hasTaskBookCheck(player, false);

        if (!hasTaskBook && !hasPreassignedTask) {
            source.sendSystemMessage(ModifiableText.SECRETLIFE_TASK_MISSING_OTHER.get(player));
            return -1;
        }

        String rawTask = "";
        Task task = null;

        if (hasTaskBook) {
            sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_TASK_PRESENT.get(player));
            if (TaskManager.assignedTasks.containsKey(uuid)) {
                task = TaskManager.assignedTasks.get(uuid);
            }
        }
        else {
            //Pre-assigned task
            sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_TASK_PREASSIGNED.get(player));
            task = TaskManager.preAssignedTasks.get(uuid);
        }

        if (task == null) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_READFAIL.get(player));
            return -1;
        }

        if (!task.formattedTask.isEmpty()) {
            rawTask = task.formattedTask;
        }
        else {
            rawTask = task.rawTask;
        }

        if (!rawTask.isEmpty()) {
            sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_TASK_SHOW.get(ActionText.hereTextRunCommand("Click to show task", "/selfmsg " + rawTask)));
        }

        return 1;
    }

    public int appendTask(CommandSourceStack source, Collection<ServerPlayer> targets, String append) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        append = "\n"+append;

        for (ServerPlayer player : targets) {
            UUID uuid = SubInManager.getOrSub(player);

            boolean inSession = TaskManager.tasksChosen && !currentSession.statusFinished();
            TaskTypes taskType = TaskManager.getPlayersTaskType(player);
            if (inSession && TaskManager.assignedTasks.containsKey(uuid) && TaskManager.removePlayersTaskBook(player)) {
                Task task = TaskManager.assignedTasks.get(uuid);
                task.rawTask += append;
                TaskManager.setPlayerTask(player, taskType, task);
                AnimationUtils.playSecretLifeTotemAnimation(player, (taskType == TaskTypes.RED || taskType == TaskTypes.FINALE));
                ModifiableSound.SECRETLIFE_TASK_TOTEM.play(player);
            }
            else {
                TaskManager.appendTask.put(uuid, append);
            }
        }

        sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_APPEND.get());

        return 1;
    }

    public int setTask(CommandSourceStack source, Collection<ServerPlayer> targets, String type, String task) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;

        TaskTypes taskType = TaskTypes.EASY;

        if (type.equalsIgnoreCase("hard")) taskType = TaskTypes.HARD;
        if (type.equalsIgnoreCase("red")) taskType = TaskTypes.RED;

        task = task.replaceAll("\\\\n","\n");

        for (ServerPlayer player : targets) {
            UUID uuid = SubInManager.getOrSub(player);
            TaskManager.preAssignedTasks.put(uuid, new Task(task, taskType));

            boolean inSession = TaskManager.tasksChosen && !currentSession.statusFinished();
            if (TaskManager.removePlayersTaskBook(player) || inSession) {
                TaskManager.assignRandomTaskToPlayer(player, taskType);
                AnimationUtils.playSecretLifeTotemAnimation(player, (taskType == TaskTypes.RED || taskType == TaskTypes.FINALE));
                ModifiableSound.SECRETLIFE_TASK_TOTEM.play(player);
                if (targets.size() == 1) {
                    sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET.get(player));
                }
            }
            else if (targets.size() == 1) {
                sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_PREASSIGN.get(player));
            }
        }

        if (targets.size() != 1) {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int changeLocations(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_LOCATIONS.get());
        TaskManager.deleteLocations();
        SecretKeeper.checkSecretLifePositions();
        return 1;
    }

    public int resetUsedTasks(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SecretLifeUsedTasks.deleteAllTasks(TaskManager.usedTasksConfig);
        sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_RESET_USED.get());
        return 1;
    }

    public int clearTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;
        List<ServerPlayer> affected = new ArrayList<>();
        for (ServerPlayer player : targets) {
            if (TaskManager.removePlayersTaskBook(player)) {
                affected.add(player);
            }
        }

        if (affected.isEmpty()) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_ERROR_BOOK_MISSING.get());
            return -1;
        }
        if (affected.size() == 1) {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REMOVE_SINGLE.get(affected.get(0)));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REMOVE_MULTIPLE.get(affected.size()));
        }
        return 1;
    }

    public int assignTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;

        List<ServerPlayer> players = new ArrayList<>();
        for (ServerPlayer player : targets) {
            if (((IPlayer) player).ls$isAlive()) {
                players.add(player);
            }
        }

        if (players.isEmpty()) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_TASK_SET_DEAD.get());
            return -1;
        }

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET_RANDOM_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SET_RANDOM_MULTIPLE.get(targets.size()));
        }

        TaskManager.chooseTasks(players, null);

        return 1;
    }

    public int succeedTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SUCCESS_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_SUCCESS_MULTIPLE.get(targets.size()));
        }

        for (ServerPlayer player : targets) {
            SecretKeeper.clickSucceed(player, true);
        }

        return 1;
    }

    public int failTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_FAIL_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_FAIL_MULTIPLE.get(targets.size()));
        }

        for (ServerPlayer player : targets) {
            SecretKeeper.clickFail(player, true);
        }

        return 1;
    }

    public int rerollTask(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!SecretKeeper.checkSecretLifePositions()) return -1;

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REROLL_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_TASK_REROLL_MULTIPLE.get(targets.size()));
        }

        for (ServerPlayer player : targets) {
            SecretKeeper.clickReroll(player, true);
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
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_GIVEHEART_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_GIVEHEART_RESET_MULTIPLE.get(targets.size()));
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
            sendCommandFailure(source, ModifiableText.SECRETLIFE_GIVEHEART_ERROR_SELF.get());
            return -1;
        }
        if (playersGiven.contains(self.getUUID())) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_GIVEHEART_ERROR_MULTIPLE.get());
            return -1;
        }
        if (((IPlayer) target).ls$isDead()) {
            sendCommandFailure(source, ModifiableText.SECRETLIFE_GIVEHEART_ERROR_DEAD.get());
            return -1;
        }
        if (!currentSession.statusStarted()) {
            sendCommandFailure(source, ModifiableText.SESSION_ERROR_START.get());
            return -1;
        }
        playersGiven.add(self.getUUID());
        secretLife.addPlayerHealth(target, 2, true);
        Component senderMessage = ModifiableText.SECRETLIFE_GIVEHEART_SEND.get(target);
        Component recipientMessage = ModifiableText.SECRETLIFE_GIVEHEART_RECEIVE.get(self);
        SessionTranscript.giftHeart(self, target);

        ((IPlayer) self).ls$message(senderMessage);
        PlayerUtils.sendTitle(target, recipientMessage, 20, 20, 20);
        ((IPlayer) target).ls$message(recipientMessage);
        AnimationUtils.createSpiral(target, 40);

        ModifiableSound.SECRETLIFE_GIFT_HEART.play(List.of(self,target));

        return 1;
    }

    public int showHealth(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        final ServerPlayer self = source.getPlayer();

        if (self == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeason;

        if (((IPlayer) self).ls$isDead()) {
            sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_SELF_DEAD.get());
            return -1;
        }

        double playerHealth = secretLife.getRoundedHealth(self);
        sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_SELF.get(playerHealth));

        return 1;
    }

    public int getHealthFor(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null) return -1;

        if (targets.size() > 1) {
            sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_LIST.get());
        }

        for (ServerPlayer player : targets) {
            SecretLife secretLife = (SecretLife) currentSeason;
            if (((IPlayer) player).ls$isDead()) {
                sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_OTHER_DEAD.get(player));
                continue;
            }

            double playerHealth = secretLife.getRoundedHealth(player);
            sendCommandFeedbackQuiet(source, ModifiableText.SECRETLIFE_HEALTH_GET_OTHER.get(player, playerHealth));
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
                secretLife.setPlayerHealth(player, amount, true);
            }
            if (targets.size() == 1) {
                sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_SET_SINGLE.get(targets.iterator().next(), amount));
            }
            else {
                sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_SET_MULTIPLE.get(targets.size(), amount));
            }
        }
        else {
            for (ServerPlayer player : targets) {
                secretLife.addPlayerHealth(player, amount, true);
            }
            String addOrRemove = amount >= 0 ? "Added" : "Removed";
            String toOrFrom = amount >= 0 ? "to" : "from";
            if (targets.size() == 1) {
                sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_MODIFY_SINGLE.get(addOrRemove, Math.abs(amount), toOrFrom, targets.iterator().next()));
            }
            else {
                sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_MODIFY_MULTIPLE.get(addOrRemove, Math.abs(amount), toOrFrom, targets.size()));
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
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.SECRETLIFE_HEALTH_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }
}
