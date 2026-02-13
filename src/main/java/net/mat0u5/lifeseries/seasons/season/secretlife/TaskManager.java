package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.config.StringListManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.Formatted;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;
//? if <= 1.20.3 {
/*import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.FilteredText;
*///?}
//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WrittenBookContent;
//?}

public class TaskManager {
    public static int EASY_SUCCESS = 20;
    public static int EASY_FAIL = 0;
    public static int HARD_SUCCESS = 40;
    public static int HARD_FAIL = -20;
    public static int RED_SUCCESS = 10;
    public static int RED_FAIL = -5;
    public static double ASSIGN_TASKS_MINUTE = 1;
    public static boolean BROADCAST_SECRET_KEEPER = false;
    public static boolean CONSTANT_TASKS = false;
    public static boolean PUBLIC_TASKS_ON_SUBMIT = false;
    public static boolean TASKS_NEED_CONFIRMATION = false;

    public static BlockPos successButtonPos;
    public static BlockPos rerollButtonPos;
    public static BlockPos failButtonPos;
    public static BlockPos itemSpawnerPos;
    public static boolean tasksChosen = false;
    public static List<UUID> tasksChosenFor = new ArrayList<>();
    public static List<UUID> submittedOrFailed = new ArrayList<>();
    public static boolean secretKeeperBeingUsed = false;
    public static int secretKeeperBeingUsedFor = 0;
    public static StringListConfig usedTasksConfig;
    public static SecretLifeLocationConfig locationsConfig;
    public static Map<UUID, Task> preAssignedTasks = new HashMap<>();
    public static Map<UUID, Task> assignedTasks = new HashMap<>();

    public static List<String> easyTasks;
    public static List<String> hardTasks;
    public static List<String> redTasks;
    public static List<String> easyTasks_all;
    public static List<String> hardTasks_all;
    public static List<String> redTasks_all;
    public static final Random rnd = new Random();
    public static List<UUID> pendingConfirmationTasks = new ArrayList<>();

    public static SessionAction getActionChooseTasks() {
        return new SessionAction(Time.minutes(ASSIGN_TASKS_MINUTE), ModifiableText.SESSION_ACTION_TASKS.getString()) {
            @Override
            public void trigger() {
                chooseTasks(livesManager.getAlivePlayers(), null);
                tasksChosen = true;
            }
        };
    }

    public static void initialize() {
        usedTasksConfig = new StringListConfig("./config/lifeseries/main", "DO_NOT_MODIFY_secretlife_used_tasks.properties");
        locationsConfig = new SecretLifeLocationConfig();
        locationsConfig.loadLocations();
        reloadTasks();
    }

    public static void reloadTasks() {
        StringListManager configEasyTasks = new StringListManager("./config/lifeseries/secretlife","easy-tasks.json");
        StringListManager configHardTasks = new StringListManager("./config/lifeseries/secretlife","hard-tasks.json");
        StringListManager configRedTasks = new StringListManager("./config/lifeseries/secretlife","red-tasks.json");
        easyTasks = configEasyTasks.loadStrings();
        hardTasks = configHardTasks.loadStrings();
        redTasks = configRedTasks.loadStrings();
        easyTasks_all = configEasyTasks.loadStrings();
        hardTasks_all = configHardTasks.loadStrings();
        redTasks_all = configRedTasks.loadStrings();
        List<String> alreadySelected = SecretLifeUsedTasks.getUsedTasks(usedTasksConfig);
        for (String selected : alreadySelected) {
            easyTasks.remove(selected);
            hardTasks.remove(selected);
        }
    }

    public static void deleteLocations() {
        locationsConfig.deleteLocations();
    }

    public static Task getRandomTask(ServerPlayer owner, TaskTypes type) {
        String selectedTask = "";

        if (easyTasks.isEmpty()) {
            StringListManager configEasyTasks = new StringListManager("./config/lifeseries/secretlife","easy-tasks.json");
            easyTasks = configEasyTasks.loadStrings();
            SecretLifeUsedTasks.deleteAllTasks(usedTasksConfig, easyTasks);
        }
        if (hardTasks.isEmpty()) {
            StringListManager configHardTasks = new StringListManager("./config/lifeseries/secretlife","hard-tasks.json");
            hardTasks = configHardTasks.loadStrings();
            SecretLifeUsedTasks.deleteAllTasks(usedTasksConfig, hardTasks);
        }

        if (type == TaskTypes.EASY && !easyTasks.isEmpty()) {
            selectedTask = getRandomTask(owner, type, easyTasks);
            if (!selectedTask.isEmpty()) easyTasks.remove(selectedTask);
        }
        else if (type == TaskTypes.HARD && !hardTasks.isEmpty()) {
            selectedTask = getRandomTask(owner, type, hardTasks);
            if (!selectedTask.isEmpty()) hardTasks.remove(selectedTask);
        }
        else if (type == TaskTypes.RED && !redTasks.isEmpty()) {
            selectedTask = getRandomTask(owner, type, redTasks);
        }

        if (type != TaskTypes.RED && !selectedTask.isEmpty()) {
            SecretLifeUsedTasks.addUsedTask(usedTasksConfig, selectedTask);
        }
        return new Task(selectedTask, type);
    }

    public static String getRandomTask(ServerPlayer owner, TaskTypes type, List<String> tasks) {
        List<String> tasksCopy = new ArrayList<>(tasks);
        Collections.shuffle(tasksCopy);
        for (String taskCandidate : tasksCopy) {
            Task testTask = new Task(taskCandidate, type);
            if (testTask.isValid(owner)) {
                return taskCandidate;
            }
        }
        return "";
    }

    public static List<Task> getAllTasks(TaskTypes type) {
        List<Task> result = new ArrayList<>();
        List<String> tasks = easyTasks;
        if (type == TaskTypes.HARD) tasks = hardTasks;
        else if (type == TaskTypes.RED) tasks = redTasks;
        for (String taskStr : tasks) {
            Task task = new Task(taskStr, type);
            result.add(task);
        }
        return result;
    }

    public static ItemStack getTaskBook(ServerPlayer player, Task task) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        //? if < 1.20.5 {
        /*List<FilteredText> lines = task.getBookLines(player);
        book.addTagElement("author", StringTag.valueOf(ModifiableText.SECRETLIFE_TASK_AUTHOR.getString()));
        book.addTagElement("title", StringTag.valueOf(ModifiableText.SECRETLIFE_TASK_NAME.getString(player)));
        ListTag listTag = new ListTag();
        Stream<StringTag> stream = lines.stream().map((filteredTextx) -> StringTag.valueOf(filteredTextx.filteredOrEmpty()));
        Objects.requireNonNull(listTag);
        stream.forEach(listTag::add);
        book.addTagElement("pages", listTag);
        List<String> linesStr = new ArrayList<>();
        for (FilteredText line : lines) {
            linesStr.add(line.filteredOrEmpty());
        }
        *///?} else {
        List<Filterable<Component>> lines = task.getBookLines(player);
        WrittenBookContent bookContent = new WrittenBookContent(
                Filterable.passThrough(ModifiableText.SECRETLIFE_TASK_NAME.getString(player)),
                ModifiableText.SECRETLIFE_TASK_AUTHOR.getString(),
                0,
                lines,
                true
        );

        List<String> linesStr = new ArrayList<>();
        for (Filterable<Component> line : lines) {
            linesStr.add(line.get(true).getString());
        }
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, bookContent);
        //?}
        SessionTranscript.assignTask(player, task, linesStr);

        ItemStackUtils.setCustomComponentBoolean(book, "SecretTask", true);
        ItemStackUtils.setCustomComponentInt(book, "TaskDifficulty", task.getDifficulty());
        return book;
    }

    public static void assignRandomTaskToPlayer(ServerPlayer player, TaskTypes type) {
        if (type != TaskTypes.RED || CONSTANT_TASKS) {
            submittedOrFailed.remove(player.getUUID());
        }

        removePlayersTaskBook(player);
        if (player.ls$isDead()) return;
        Task task;
        if (preAssignedTasks.containsKey(player.getUUID())) {
            task = preAssignedTasks.get(player.getUUID());
            preAssignedTasks.remove(player.getUUID());
        }
        else {
            task = getRandomTask(player, type);
        }
        ItemStack book = getTaskBook(player, task);
        if (!player.addItem(book)) {
            ItemStackUtils.spawnItemForPlayer(player.ls$getServerLevel(), player.position(), book, player);
        }
        assignedTasks.put(player.getUUID(), task);
        DatapackIntegration.setPlayerTask(player, type);
    }

    public static void assignRandomTasks(List<ServerPlayer> allowedPlayers, TaskTypes type) {
        for (ServerPlayer player : allowedPlayers) {
            if (player.ls$isDead()) continue;
            TaskTypes thisType = type;
            if (thisType == null) {
                thisType = TaskTypes.EASY;
                if (player.ls$isOnLastLife(false)) thisType = TaskTypes.RED;
            }
            assignRandomTaskToPlayer(player, thisType);
        }
    }

    public static void chooseTasks(List<ServerPlayer> allowedPlayers, TaskTypes type) {
        secretKeeperBeingUsed = true;
        for (ServerPlayer player : allowedPlayers) {
            if (!tasksChosenFor.contains(player.getUUID())) {
                tasksChosenFor.add(player.getUUID());
            }
        }
        PlayerUtils.sendTitleToPlayers(allowedPlayers, ModifiableText.SECRETLIFE_TASK_TITLE.get(),20,35,0);

        TaskScheduler.scheduleTask(40, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, ModifiableText.COUNTDOWN_RED_3.get(),0,35,0);
        });
        TaskScheduler.scheduleTask(70, () -> {
            PlayerUtils.sendTitleToPlayers(allowedPlayers, ModifiableText.COUNTDOWN_RED_2.get(),0,35,0);
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")));
        });
        TaskScheduler.scheduleTask(105, () -> {
            PlayerUtils.sendTitleToPlayers(allowedPlayers, ModifiableText.COUNTDOWN_RED_1.get(),0,35,0);
        });
        TaskScheduler.scheduleTask(130, () -> {
            for (ServerPlayer player : allowedPlayers) {
                boolean redTask = type == TaskTypes.RED || (type == null && player.ls$isOnLastLife(false));
                AnimationUtils.playSecretLifeTotemAnimation(player, redTask);
            }
        });
        TaskScheduler.scheduleTask(165, () -> {
            assignRandomTasks(allowedPlayers, type);
            secretKeeperBeingUsed = false;
        });
    }

    public static ItemStack getPlayersTaskBook(ServerPlayer player) {
        for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
            if (ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) return item;
        }
        return null;
    }

    public static boolean hasNonRedTaskBook(ServerPlayer player) {
        for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
            if (!ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) continue;
            if (!ItemStackUtils.hasCustomComponentEntry(item,"TaskDifficulty")) continue;
            int difficulty = ItemStackUtils.getCustomComponentInt(item, "TaskDifficulty");
            if (difficulty == 1 || difficulty == 2) return true;
        }
        return false;
    }

    public static boolean removePlayersTaskBook(ServerPlayer player) {
        boolean success = false;
        for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
            if (ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) {
                PlayerUtils.clearItemStack(player, item);
                success = true;
            }
        }
        DatapackIntegration.setPlayerTask(player, null);
        return success;
    }

    public static boolean getPlayerKillPermitted(ServerPlayer player) {
        ItemStack item = getPlayersTaskBook(player);
        if (item == null) return false;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) return false;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"TaskDifficulty")) return false;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"KillPermitted")) return false;
        return ItemStackUtils.getCustomComponentBoolean(item, "KillPermitted");
    }

    public static TaskTypes getPlayersTaskType(ServerPlayer player) {
        ItemStack item = getPlayersTaskBook(player);
        if (item == null) return null;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) return null;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"TaskDifficulty")) return null;
        int difficulty = ItemStackUtils.getCustomComponentInt(item, "TaskDifficulty");
        if (difficulty == 1) return TaskTypes.EASY;
        if (difficulty == 2) return TaskTypes.HARD;
        if (difficulty == 3) return TaskTypes.RED;
        return null;
    }

    public static void addHealthThenItems(ServerPlayer player, int addHealth, TaskTypes taskType) {
        if (server == null) return;
        if (addHealth == 0) {
            secretKeeperBeingUsed = false;
            return;
        }
        secretKeeperBeingUsed = true;
        SecretLife season = (SecretLife) currentSeason;
        double currentHealth = season.getPlayerHealth(player);
        if (currentHealth > SecretLife.MAX_HEALTH) currentHealth = SecretLife.MAX_HEALTH;
        int rounded = (int) Math.floor(currentHealth);
        int remainderToMax = (int) SecretLife.MAX_HEALTH - rounded;

        if (addHealth <= remainderToMax && remainderToMax != 0) {
            season.addPlayerHealth(player, addHealth);
            secretKeeperBeingUsed = false;
        }
        else {
            if (remainderToMax != 0) season.setPlayerHealth(player, SecretLife.MAX_HEALTH);
            int itemsNum = (addHealth - remainderToMax)/2;
            if (itemsNum == 0) {
                secretKeeperBeingUsed = false;
                return;
            }
            Vec3 spawnPos = itemSpawnerPos.getCenter();
            for (int i = 0; i <= itemsNum; i++) {
                if (i == 0) continue;
                TaskScheduler.scheduleTask(3*i, () -> {
                    server.overworld().playSound(null, spawnPos.x(), spawnPos.y(), spawnPos.z(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);

                    List<ItemStack> lootTableItems = new ArrayList<>();

                    if (taskType == TaskTypes.HARD) {
                        lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.overworld(), player, IdentifierHelper.of("lifeseriesdynamic", "task_reward_loottable_hard"), true);
                    }
                    else if (taskType == TaskTypes.RED) {
                        lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.overworld(), player, IdentifierHelper.of("lifeseriesdynamic", "task_reward_loottable_red"), true);
                    }

                    if (taskType == TaskTypes.EASY || lootTableItems.isEmpty()) {
                        lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.overworld(), player, IdentifierHelper.of("lifeseriesdynamic", "task_reward_loottable"), false);
                    }

                    if (!lootTableItems.isEmpty()) {
                        for (ItemStack item : lootTableItems) {
                            ItemStackUtils.spawnItemForPlayer(server.overworld(), spawnPos, item, player);
                        }
                    }
                    else {
                        ItemStack randomItem = season.itemSpawner.getRandomItem();
                        ItemStackUtils.spawnItemForPlayer(server.overworld(), spawnPos, randomItem, player);
                    }

                });
            }
            TaskScheduler.scheduleTask(3*itemsNum+20, () -> secretKeeperBeingUsed = false);
        }
    }

    public static boolean hasSessionStarted(ServerPlayer player) {
        if (currentSession.statusNotStarted()) {
            player.sendSystemMessage(ModifiableText.SESSION_ERROR_START.get());
            return false;
        }
        return true;
    }

    public static boolean isBeingUsed(ServerPlayer player) {
        if (!secretKeeperBeingUsed) return false;
        player.sendSystemMessage(ModifiableText.SECRETLIFE_SECRETKEEPER_INUSE.get());
        return true;
    }

    public static boolean hasTaskBookCheck(ServerPlayer player, boolean sendMessage) {
        TaskTypes type = getPlayersTaskType(player);
        if (type != null) return true;
        if (sendMessage) {
            player.sendSystemMessage(ModifiableText.SECRETLIFE_TASK_MISSING.get());
        }
        return false;
    }

    public static Component getShowTaskMessage(ServerPlayer player) {
        String rawTask = "";

        Task task = null;

        if (hasTaskBookCheck(player, false) && assignedTasks.containsKey(player.getUUID())) {
            task = assignedTasks.get(player.getUUID());
        }
        else if (preAssignedTasks.containsKey(player.getUUID())) {
            task = preAssignedTasks.get(player.getUUID());
        }

        if (task == null) return Component.empty();

        if (!task.formattedTask.isEmpty()) {
            rawTask = task.formattedTask;
        }
        else {
            rawTask = task.rawTask;
        }

        return ModifiableText.SECRETLIFE_TASK_SHOW_PAST.get(TextUtils.selfMessageText(rawTask), player);
    }

    public static void succeedTask(ServerPlayer player, boolean fromCommand) {
        if (server == null) return;
        if (!fromCommand) {
            if (!hasSessionStarted(player)) return;
            if (isBeingUsed(player)) return;
        }
        TaskTypes type = getPlayersTaskType(player);
        if (!hasTaskBookCheck(player, !fromCommand)) return;
        if (!fromCommand) {
            if (TASKS_NEED_CONFIRMATION) {
                if (!pendingConfirmationTasks.contains(player.getUUID())) {
                    pendingConfirmationTasks.add(player.getUUID());
                    PlayerUtils.broadcastMessageToAdmins(ModifiableText.SECRETLIFE_TASK_PENDING.get(player));
                    PlayerUtils.broadcastMessageToAdmins(getShowTaskMessage(player));
                    PlayerUtils.broadcastMessageToAdmins(ModifiableText.SECRETLIFE_TASK_PENDING_ACCEPT.get(TextUtils.runCommandText("/task succeed "+player.getScoreboardName())));
                }
                player.sendSystemMessage(ModifiableText.SECRETLIFE_TASK_PENDING_NOTIFICATION.get());
                return;
            }
        }
        pendingConfirmationTasks.remove(player.getUUID());
        if (BROADCAST_SECRET_KEEPER) {
            PlayerUtils.broadcastMessage(ModifiableText.SECRETLIFE_TASK_SUCCEED.get(player));
        }
        if (PUBLIC_TASKS_ON_SUBMIT) {
            PlayerUtils.broadcastMessage(getShowTaskMessage(player));
        }
        SessionTranscript.successTask(player);
        removePlayersTaskBook(player);
        submittedOrFailed.add(player.getUUID());
        secretKeeperBeingUsed = true;

        Vec3 centerPos = itemSpawnerPos.getCenter();
        AnimationUtils.createGlyphAnimation(server.overworld(), centerPos, 40);
        server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")), SoundSource.PLAYERS, 1.0F, 1.0F);
        TaskScheduler.scheduleTask(Time.seconds(3), () -> {
            //? if < 1.21 {
            /*server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
            *///?} else {
            server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
            //?}
            AnimationUtils.spawnFireworkBall(server.overworld(), centerPos, 40, 0.3, new Vector3f(0, 1, 0));
            if (type == TaskTypes.EASY) {
                showHeartTitle(player, EASY_SUCCESS);
                addHealthThenItems(player, EASY_SUCCESS, type);
            }
            if (type == TaskTypes.HARD) {
                showHeartTitle(player, HARD_SUCCESS);
                addHealthThenItems(player, HARD_SUCCESS, type);
            }
            if (type == TaskTypes.RED) {
                showHeartTitle(player, RED_SUCCESS);
                addHealthThenItems(player, RED_SUCCESS, type);
            }
        });
        DatapackIntegration.EVENT_TASK_SUCCEED.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        chooseNewTaskForPlayerIfNecessary(player);
    }

    public static void rerollTask(ServerPlayer player, boolean fromCommand) {
        if (!fromCommand) {
            if (!hasSessionStarted(player)) return;
            if (isBeingUsed(player)) return;
        }
        TaskTypes type = getPlayersTaskType(player);
        if (!hasTaskBookCheck(player, !fromCommand)) return;
        if (type == TaskTypes.RED) {
            failTask(player, false);
            return;
        }
        if (type == TaskTypes.EASY) {
            removePlayersTaskBook(player);
            if (BROADCAST_SECRET_KEEPER) {
                PlayerUtils.broadcastMessage(ModifiableText.SECRETLIFE_TASK_REROLL.get(player));
            }
            if (PUBLIC_TASKS_ON_SUBMIT) {
                PlayerUtils.broadcastMessage(getShowTaskMessage(player));
            }
            SessionTranscript.rerollTask(player);
            secretKeeperBeingUsed = true;
            TaskTypes newType = TaskTypes.HARD;
            if (player.ls$isOnLastLife(false)) {
                chooseTasks(List.of(player), TaskTypes.RED);
                return;
            }

            PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT1.get(),20,35,0);

            TaskScheduler.scheduleTask(50, () -> {
                PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
                PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT2.get(),20,35,0);
            });
            TaskScheduler.scheduleTask(100, () -> {
                PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
                PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT3.get(),20,35,0);
            });
            TaskScheduler.scheduleTask(150, () -> {
                PlayerUtils.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value());
                PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_TASK_REROLL_PT4.get(),20,30,0);
            });
            TaskScheduler.scheduleTask(200, () -> AnimationUtils.playSecretLifeTotemAnimation(player, false));
            TaskScheduler.scheduleTask(240, () -> {
                assignRandomTaskToPlayer(player, newType);
                secretKeeperBeingUsed = false;
            });
            DatapackIntegration.EVENT_TASK_REROLL.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
            return;
        }
        if (type == TaskTypes.HARD) {
            if (!player.ls$isOnLastLife(true)) {
                player.sendSystemMessage(ModifiableText.SECRETLIFE_TASK_REROLL_HARD_FAIL.get());
            }
            else {
                player.sendSystemMessage(ModifiableText.SECRETLIFE_TASK_REROLL_HARD_FAIL_RED.get());
            }
        }
    }

    public static void failTask(ServerPlayer player, boolean fromCommand) {
        if (server == null) return;
        if (!fromCommand) {
            if (!hasSessionStarted(player)) return;
            if (isBeingUsed(player)) return;
        }
        SecretLife season = (SecretLife) currentSeason;
        TaskTypes type = getPlayersTaskType(player);
        if (!hasTaskBookCheck(player, !fromCommand)) return;
        if (BROADCAST_SECRET_KEEPER) {
            PlayerUtils.broadcastMessage(ModifiableText.SECRETLIFE_TASK_FAIL.get(player));
        }
        if (PUBLIC_TASKS_ON_SUBMIT) {
            PlayerUtils.broadcastMessage(getShowTaskMessage(player));
        }
        SessionTranscript.failTask(player);
        removePlayersTaskBook(player);
        submittedOrFailed.add(player.getUUID());
        secretKeeperBeingUsed = true;

        Vec3 centerPos = itemSpawnerPos.getCenter();
        AnimationUtils.createGlyphAnimation(server.overworld(), centerPos, 40);
        server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")), SoundSource.PLAYERS, 1.0F, 1.0F);
        TaskScheduler.scheduleTask(Time.seconds(3), () -> {
            //? if < 1.21 {
            /*server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 1.0F, 1.0F);
            *///?} else {
            server.overworld().playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.PLAYERS, 1.0F, 1.0F);
            //?}
            AnimationUtils.spawnFireworkBall(server.overworld(), centerPos, 40, 0.3, new Vector3f(1, 0, 0));
            if (type == TaskTypes.EASY) {
                showHeartTitle(player, EASY_FAIL);
                season.removePlayerHealth(player, -EASY_FAIL);
            }
            if (type == TaskTypes.HARD) {
                showHeartTitle(player, HARD_FAIL);
                season.removePlayerHealth(player, -HARD_FAIL);
            }
            if (type == TaskTypes.RED) {
                showHeartTitle(player, RED_FAIL);
                season.removePlayerHealth(player, -RED_FAIL);
            }
            if (!player.ls$isOnLastLife(false)) {
                secretKeeperBeingUsed = false;
            }
        });
        DatapackIntegration.EVENT_TASK_FAIL.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        chooseNewTaskForPlayerIfNecessary(player);
    }

    public static void chooseNewTaskForPlayerIfNecessary(ServerPlayer player) {
        if (currentSession.statusFinished()) return;
        if (player.ls$isOnLastLife(false) || CONSTANT_TASKS) {
            TaskScheduler.scheduleTask(Time.seconds(6), () -> {
                TaskTypes newType = player.ls$isOnLastLife(false) ? TaskTypes.RED : TaskTypes.EASY;
                chooseTasks(List.of(player), newType);
            });
        }
    }

    public static void showHeartTitle(ServerPlayer player, int amount) {
        if (amount == 0) return;
        SecretLife season = (SecretLife) currentSeason;
        if (amount > 0 && season.getPlayerHealth(player) >= SecretLife.MAX_HEALTH) return;
        int healthBefore = Mth.ceil(season.getPlayerHealth(player));
        int finalAmount = amount;
        if (healthBefore + amount <= 0) {
            finalAmount = -healthBefore+1;
            if (healthBefore == 1) finalAmount = 0;
        }
        if (healthBefore + amount > SecretLife.MAX_HEALTH) {
            if (amount > 0) finalAmount = (int) (SecretLife.MAX_HEALTH-healthBefore);
            else finalAmount = amount;
        }
        double finalHearts = (double) finalAmount / 2;
        if (finalHearts == 0) return;

        String finalStr = String.valueOf(finalHearts);
        if (finalAmount%2==0) finalStr = String.valueOf((int)finalHearts);


        ChatFormatting formatting = ChatFormatting.GREEN;
        if (finalAmount < 0) formatting = ChatFormatting.RED;
        else finalStr = "+"+finalStr;
        //TODO test
        PlayerUtils.sendTitle(player, ModifiableText.SECRETLIFE_HEART_GAIN.get(finalStr, "Hearts").copy().withStyle(formatting), 20, 40, 20);
    }

    public static boolean alreadyHasPos(BlockPos pos) {
        if (successButtonPos != null && successButtonPos.equals(pos)) return true;
        if (rerollButtonPos != null && rerollButtonPos.equals(pos)) return true;
        if (failButtonPos != null && failButtonPos.equals(pos)) return true;
        return itemSpawnerPos != null && itemSpawnerPos.equals(pos);
    }

    public static void positionFound(BlockPos pos, boolean fromButton) {
        if (pos == null) return;
        if (alreadyHasPos(pos)) {
            PlayerUtils.broadcastMessage(Component.literal("§c[SecretLife setup] This location is already being used."), 20);
            return;
        }
        if (successButtonPos == null && fromButton) {
            successButtonPos = pos;
            PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife setup 1/4] Location set.\n"));
        }
        else if (rerollButtonPos == null && fromButton) {
            rerollButtonPos = pos;
            PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife setup 2/4] Location set.\n"));
        }
        else if (failButtonPos == null && fromButton) {
            failButtonPos = pos;
            PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife setup 3/4] Location set.\n"));
        }
        if (itemSpawnerPos == null && !fromButton) {
            if (successButtonPos != null && rerollButtonPos != null && failButtonPos != null) {
                itemSpawnerPos = pos;
                PlayerUtils.broadcastMessage(Component.literal("§a[SecretLife] All locations have been set. If you wish to change them in the future, use §2'/task changeLocations'\n"));
                PlayerUtils.broadcastMessage(ModifiableText.SESSION_START_PROMPT.get());
            }
        }
        locationsConfig.saveLocations();
        checkSecretLifePositions();
    }

    public static boolean searchingForLocations = false;
    public static boolean checkSecretLifePositions() {
        if (successButtonPos == null) {
            PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 1/4] Location for the secret keeper task §6§lSUCCESS BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
            searchingForLocations = true;
            return false;
        }
        if (rerollButtonPos == null) {
            PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 2/4] Location for the secret keeper task §6§lRE-ROLL BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
            searchingForLocations = true;
            return false;
        }
        if (failButtonPos == null) {
            PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 3/4] Location for the secret keeper task §6§lFAIL BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
            searchingForLocations = true;
            return false;
        }
        if (itemSpawnerPos == null) {
            PlayerUtils.broadcastMessageToAdmins(Component.literal("§c[SecretLife setup 4/4] Location for the secret keeper task §6§lITEM SPAWN BLOCK§r§c was not found. §nPlease place a bedrock block at the desired spot to mark it."));
            searchingForLocations = true;
            return false;
        }
        searchingForLocations = false;
        return true;
    }

    public static void onBlockUse(ServerPlayer player, ServerLevel level, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        String name = level.getBlockState(pos).getBlock().getName().getString().toLowerCase(Locale.ROOT);
        if (name.contains("button")) {
            if (searchingForLocations) {
                positionFound(pos, true);
            }
            else {
                if (pos.equals(successButtonPos)) {
                    succeedTask(player, false);
                }
                else if (pos.equals(rerollButtonPos)) {
                    rerollTask(player, false);
                }
                else if (pos.equals(failButtonPos)) {
                    failTask(player, false);
                }
            }
        }
        if (!searchingForLocations) return;
        if (successButtonPos == null || rerollButtonPos == null || failButtonPos == null) return;
        BlockPos placePos = pos.relative(hitResult.getDirection());
        TaskScheduler.scheduleTask(1, () -> {
            if (level.getBlockState(placePos).getBlock() == Blocks.BEDROCK) {
                positionFound(placePos, false);
                level.destroyBlock(placePos, false);
            }
        });
    }

    public static void tick() {
        if (secretKeeperBeingUsed) {
            secretKeeperBeingUsedFor++;
        }
        else {
            secretKeeperBeingUsedFor = 0;
        }
        if (secretKeeperBeingUsedFor > 500) {
            secretKeeperBeingUsed = false;
            secretKeeperBeingUsedFor = 0;
            Main.LOGGER.error("Resetting Secret Keeper.");
        }
    }
}
