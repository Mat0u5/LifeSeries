package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.config.StringListManager;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerListReference;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

//? if <= 1.20.3 {
/*import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.FilteredText;
*///?}
//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WrittenBookContent;

import static net.mat0u5.lifeseries.LifeSeries.currentSession;
//?}
//? if < 1.20.5
//import java.util.stream.Stream;

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

    // Fraction of the tasks to keep as used when resetting happens.
    public static double RECENT_TASK_PERCENTAGE = 0.5;
    public static boolean tasksChosen = false;
    public static Set<UUID> tasksChosenFor = new HashSet<>();
    public static Set<UUID> submittedOrFailed = new HashSet<>();
    public static StringListConfig usedTasksConfig;
    public static SecretLifeLocationConfig locationsConfig;
    public static Map<UUID, Task> preAssignedTasks = new HashMap<>();
    public static Map<UUID, Task> assignedTasks = new HashMap<>();
    public static Task finalTask = new Task("Win Secret Life.", TaskTypes.FINALE);

    public static List<String> easyTasks;
    public static List<String> hardTasks;
    public static List<String> redTasks;
    public static List<String> easyTasks_all;
    public static List<String> hardTasks_all;
    public static List<String> redTasks_all;
    public static final Random rnd = new Random();

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

        if (currentSession.isFinale() || type == TaskTypes.FINALE) {
            return finalTask;
        }

        String selectedTask = "";

        if (easyTasks.isEmpty()) {
            StringListManager configEasyTasks = new StringListManager("./config/lifeseries/secretlife","easy-tasks.json");
            easyTasks = configEasyTasks.loadStrings();
            SecretLifeUsedTasks.deleteAllTasks(usedTasksConfig, easyTasks, RECENT_TASK_PERCENTAGE);
            List<String> stillUsed = SecretLifeUsedTasks.getUsedTasks(usedTasksConfig);
            easyTasks.removeAll(stillUsed);
        }
        if (hardTasks.isEmpty()) {
            StringListManager configHardTasks = new StringListManager("./config/lifeseries/secretlife","hard-tasks.json");
            hardTasks = configHardTasks.loadStrings();
            SecretLifeUsedTasks.deleteAllTasks(usedTasksConfig, hardTasks, RECENT_TASK_PERCENTAGE);
            List<String> stillUsed = SecretLifeUsedTasks.getUsedTasks(usedTasksConfig);
            hardTasks.removeAll(stillUsed);
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
        if (player == null) return;
        UUID uuid = SubInManager.getOrSub(player);
        if (type != TaskTypes.RED || CONSTANT_TASKS) {
            submittedOrFailed.remove(uuid);
        }

        removePlayersTaskBook(player);
        if (((IPlayer) player).ls$isDead()) return;
        Task task;
        if (preAssignedTasks.containsKey(uuid)) {
            task = preAssignedTasks.get(uuid);
            preAssignedTasks.remove(uuid);
        }
        else {
            task = getRandomTask(player, type);
        }
        ItemStack book = getTaskBook(player, task);
        if (!player.addItem(book)) {
            ItemStackUtils.spawnItemForPlayer(((IPlayer) player).ls$getServerLevel(), player.position(), book, player);
        }
        assignedTasks.put(uuid, task);
        DatapackIntegration.setPlayerTask(player, type);
    }

    public static void assignRandomTasks(List<ServerPlayer> allowedPlayers, TaskTypes type) {
        for (ServerPlayer player : allowedPlayers) {
            if (((IPlayer) player).ls$isDead()) continue;
            TaskTypes thisType = type;
            if (thisType == null) {
                thisType = TaskTypes.EASY;
                if (((IPlayer) player).ls$isOnLastLife(false)) thisType = TaskTypes.RED;
            }
            assignRandomTaskToPlayer(player, thisType);
        }
    }

    public static void chooseTasks(List<ServerPlayer> allowedPlayers, TaskTypes type) {
        if (currentSession.isFinale()) type = TaskTypes.FINALE;
        SecretKeeper.secretKeeperBeingUsed = true;
        for (ServerPlayer player : allowedPlayers) {
            UUID uuid = SubInManager.getOrSub(player);
			tasksChosenFor.add(uuid);
        }
        PlayerUtils.sendTitleToPlayers(allowedPlayers, ModifiableText.SECRETLIFE_TASK_TITLE.get(),20,35,0);
        PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("secretlife_task")));

        PlayerListReference ref = PlayerListReference.of(allowedPlayers);
        TaskScheduler.scheduleTask(50, () -> {
            var newList = ref.get();
            PlayerUtils.playSoundToPlayers(newList, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(newList, ModifiableText.COUNTDOWN_RED_3.get(),0,35,0);
        });
        TaskScheduler.scheduleTask(80, () -> {
            var newList = ref.get();
            PlayerUtils.playSoundToPlayers(newList, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(newList, ModifiableText.COUNTDOWN_RED_2.get(),0,35,0);
        });
        TaskScheduler.scheduleTask(115, () -> {
            var newList = ref.get();
            PlayerUtils.playSoundToPlayers(newList, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(newList, ModifiableText.COUNTDOWN_RED_1.get(),0,35,0);
        });
        TaskTypes finalType = type;
        TaskScheduler.scheduleTask(140, () -> {
            for (ServerPlayer player : ref.get()) {
                boolean redTask = (finalType == TaskTypes.RED || finalType == TaskTypes.FINALE) || (finalType == null && ((IPlayer) player).ls$isOnLastLife(false));
                AnimationUtils.playSecretLifeTotemAnimation(player, redTask);
            }
        });
        TaskScheduler.scheduleTask(175, () -> {
            assignRandomTasks(ref.get(), finalType);
            SecretKeeper.secretKeeperBeingUsed = false;
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
        if (difficulty == 4) return TaskTypes.FINALE;
        return null;
    }
}
