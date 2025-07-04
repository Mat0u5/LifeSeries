package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.config.StringListManager;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class TaskManager {
    public static int EASY_SUCCESS = 20;
    public static int EASY_FAIL = 0;
    public static int HARD_SUCCESS = 40;
    public static int HARD_FAIL = -20;
    public static int RED_SUCCESS = 10;
    public static int RED_FAIL = -5;

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

    public static SessionAction actionChooseTasks = new SessionAction(
            OtherUtils.minutesToTicks(1),"§7Assign Tasks §f[00:01:00]", "Assign Tasks"
    ) {
        @Override
        public void trigger() {
            chooseTasks(currentSeason.getAlivePlayers(), null);
            tasksChosen = true;
        }
    };
    public static List<String> easyTasks;
    public static List<String> hardTasks;
    public static List<String> redTasks;
    public static final Random rnd = new Random();

    public static void initialize() {
        usedTasksConfig = new StringListConfig("./config/lifeseries/main", "DO_NOT_MODIFY_secretlife_used_tasks.properties");
        locationsConfig = new SecretLifeLocationConfig();
        locationsConfig.loadLocations();
        StringListManager configEasyTasks = new StringListManager("./config/lifeseries/secretlife","easy-tasks.json");
        StringListManager configHardTasks = new StringListManager("./config/lifeseries/secretlife","hard-tasks.json");
        StringListManager configRedTasks = new StringListManager("./config/lifeseries/secretlife","red-tasks.json");
        easyTasks = configEasyTasks.loadStrings();
        hardTasks = configHardTasks.loadStrings();
        redTasks = configRedTasks.loadStrings();
        List<String> alreadySelected = SecretLifeUsedTasks.getUsedTasks(usedTasksConfig);
        for (String selected : alreadySelected) {
            easyTasks.remove(selected);
            hardTasks.remove(selected);
        }
    }

    public static void deleteLocations() {
        locationsConfig.deleteLocations();
    }

    public static Task getRandomTask(TaskType type) {
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

        if (type == TaskType.EASY && !easyTasks.isEmpty()) {
            selectedTask = easyTasks.get(rnd.nextInt(easyTasks.size()));
            easyTasks.remove(selectedTask);
        }
        else if (type == TaskType.HARD && !hardTasks.isEmpty()) {
            selectedTask = hardTasks.get(rnd.nextInt(hardTasks.size()));
            hardTasks.remove(selectedTask);
        }
        else if (type == TaskType.RED && !redTasks.isEmpty()) {
            selectedTask = redTasks.get(rnd.nextInt(redTasks.size()));
        }
        if (type != TaskType.RED && !selectedTask.isEmpty()) {
            SecretLifeUsedTasks.addUsedTask(usedTasksConfig, selectedTask);
        }
        return new Task(selectedTask, type);
    }

    public static List<Task> getAllTasks(TaskType type) {
        List<Task> result = new ArrayList<>();
        List<String> tasks = easyTasks;
        if (type == TaskType.HARD) tasks = hardTasks;
        else if (type == TaskType.RED) tasks = redTasks;
        for (String taskStr : tasks) {
            Task task = new Task(taskStr, type);
            result.add(task);
        }
        return result;
    }

    public static ItemStack getTaskBook(ServerPlayerEntity player, Task task) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        List<RawFilteredPair<Text>> lines = task.getBookLines();
        WrittenBookContentComponent bookContent = new WrittenBookContentComponent(
            RawFilteredPair.of("§c"+player.getNameForScoreboard()+"'s Secret Task"),
                "Secret Keeper",
                0,
                lines,
                true
        );

        List<String> linesStr = new ArrayList<>();
        for (RawFilteredPair<Text> line : lines) {
            linesStr.add(line.get(true).getString());
        }
        book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, bookContent);
        SessionTranscript.assignTask(player, task, linesStr);

        ItemStackUtils.setCustomComponentBoolean(book, "SecretTask", true);
        ItemStackUtils.setCustomComponentInt(book, "TaskDifficulty", task.getDifficulty());
        ItemStackUtils.setCustomComponentBoolean(book, "KillPermitted", task.killPermitted());
        return book;
    }

    public static void assignRandomTaskToPlayer(ServerPlayerEntity player, TaskType type) {
        removePlayersTaskBook(player);
        if (!currentSeason.isAlive(player)) return;
        Task task;
        if (preAssignedTasks.containsKey(player.getUuid())) {
            task = preAssignedTasks.get(player.getUuid());
            preAssignedTasks.remove(player.getUuid());
        }
        else {
            task = getRandomTask(type);
        }
        ItemStack book = getTaskBook(player, task);
        if (!player.giveItemStack(book)) {
            ItemStackUtils.spawnItemForPlayer(PlayerUtils.getServerWorld(player), player.getPos(), book, player);
        }
    }

    public static void assignRandomTasks(List<ServerPlayerEntity> allowedPlayers, TaskType type) {
        for (ServerPlayerEntity player : allowedPlayers) {
            if (!currentSeason.isAlive(player)) continue;
            TaskType thisType = type;
            if (thisType == null) {
                thisType = TaskType.EASY;
                if (currentSeason.isOnLastLife(player, false)) thisType = TaskType.RED;
            }
            assignRandomTaskToPlayer(player, thisType);
        }
    }

    public static void chooseTasks(List<ServerPlayerEntity> allowedPlayers, TaskType type) {
        secretKeeperBeingUsed = true;
        for (ServerPlayerEntity player : allowedPlayers) {
            if (!tasksChosenFor.contains(player.getUuid())) {
                tasksChosenFor.add(player.getUuid());
            }
        }
        PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("Your secret is...").formatted(Formatting.RED),20,35,0);

        TaskScheduler.scheduleTask(40, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("3").formatted(Formatting.RED),0,35,0);
        });
        TaskScheduler.scheduleTask(70, () -> {
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("2").formatted(Formatting.RED),0,35,0);
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.of(Identifier.ofVanilla("secretlife_task")));
        });
        TaskScheduler.scheduleTask(105, () -> {
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("1").formatted(Formatting.RED),0,35,0);
        });
        TaskScheduler.scheduleTask(130, () -> {
            for (ServerPlayerEntity player : allowedPlayers) {
                AnimationUtils.playTotemAnimation(player);
            }
        });
        TaskScheduler.scheduleTask(165, () -> {
            assignRandomTasks(allowedPlayers, type);
            secretKeeperBeingUsed = false;
        });
    }

    public static ItemStack getPlayersTaskBook(ServerPlayerEntity player) {
        for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
            if (ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) return item;
        }
        return null;
    }

    public static boolean hasNonRedTaskBook(ServerPlayerEntity player) {
        for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
            if (!ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) continue;
            if (!ItemStackUtils.hasCustomComponentEntry(item,"TaskDifficulty")) continue;
            int difficulty = ItemStackUtils.getCustomComponentInt(item, "TaskDifficulty");
            if (difficulty == 1 || difficulty == 2) return true;
        }
        return false;
    }

    public static boolean removePlayersTaskBook(ServerPlayerEntity player) {
        boolean success = false;
        for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
            if (ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) {
                PlayerUtils.clearItemStack(player, item);
                success = true;
            }
        }
        return success;
    }

    public static boolean getPlayerKillPermitted(ServerPlayerEntity player) {
        ItemStack item = getPlayersTaskBook(player);
        if (item == null) return false;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) return false;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"TaskDifficulty")) return false;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"KillPermitted")) return false;
        return ItemStackUtils.getCustomComponentBoolean(item, "KillPermitted");
    }

    public static TaskType getPlayersTaskType(ServerPlayerEntity player) {
        ItemStack item = getPlayersTaskBook(player);
        if (item == null) return null;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"SecretTask")) return null;
        if (!ItemStackUtils.hasCustomComponentEntry(item,"TaskDifficulty")) return null;
        int difficulty = ItemStackUtils.getCustomComponentInt(item, "TaskDifficulty");
        if (difficulty == 1) return TaskType.EASY;
        if (difficulty == 2) return TaskType.HARD;
        if (difficulty == 3) return TaskType.RED;
        return null;
    }

    public static void addHealthThenItems(ServerPlayerEntity player, int addHealth) {
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
            Vec3d spawnPos = itemSpawnerPos.toCenterPos();
            for (int i = 0; i <= itemsNum; i++) {
                if (i == 0) continue;
                TaskScheduler.scheduleTask(3*i, () -> {
                    server.getOverworld().playSound(null, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, server.getOverworld(), player, Identifier.of("lifeseriesdynamic", "task_reward_loottable"));
                    if (!lootTableItems.isEmpty()) {
                        for (ItemStack item : lootTableItems) {
                            ItemStackUtils.spawnItemForPlayer(server.getOverworld(), spawnPos, item, player);
                        }
                    }
                    else {
                        ItemStack randomItem = season.itemSpawner.getRandomItem();
                        ItemStackUtils.spawnItemForPlayer(server.getOverworld(), spawnPos, randomItem, player);
                    }

                });
            }
            TaskScheduler.scheduleTask(3*itemsNum+20, () -> secretKeeperBeingUsed = false);
        }
    }

    public static boolean hasSessionStarted(ServerPlayerEntity player) {
        if (currentSession.statusNotStarted()) {
            player.sendMessage(Text.of("§cThe session has not started yet."));
            return false;
        }
        return true;
    }

    public static boolean isBeingUsed(ServerPlayerEntity player) {
        if (!secretKeeperBeingUsed) return false;
        player.sendMessage(Text.of("§cSomeone else is using the Secret Keeper right now."));
        return true;
    }

    public static boolean hasTaskBookCheck(ServerPlayerEntity player, TaskType type) {
        if (type != null) return true;
        player.sendMessage(Text.of("§cYou do not have a secret task book in your inventory."));
        return false;
    }

    public static void succeedTask(ServerPlayerEntity player) {
        if (server == null) return;
        if (!hasSessionStarted(player)) return;
        if (isBeingUsed(player)) return;
        SecretLife season = (SecretLife) currentSeason;
        TaskType type = getPlayersTaskType(player);
        if (!hasTaskBookCheck(player, type)) return;
        SessionTranscript.successTask(player);
        removePlayersTaskBook(player);
        submittedOrFailed.add(player.getUuid());
        secretKeeperBeingUsed = true;

        Vec3d centerPos = itemSpawnerPos.toCenterPos();
        AnimationUtils.createGlyphAnimation(server.getOverworld(), centerPos, 40);
        server.getOverworld().playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(), SoundEvent.of(Identifier.of("minecraft","secretlife_task")), SoundCategory.PLAYERS, 1.0F, 1.0F);
        TaskScheduler.scheduleTask(60, () -> {
            server.getOverworld().playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(), SoundEvents.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, SoundCategory.PLAYERS, 1.0F, 1.0F);
            AnimationUtils.spawnFireworkBall(server.getOverworld(), centerPos, 40, 0.3, new Vector3f(0, 1, 0));
            if (type == TaskType.EASY) {
                showHeartTitle(player, EASY_SUCCESS);
                addHealthThenItems(player, EASY_SUCCESS);
            }
            if (type == TaskType.HARD) {
                showHeartTitle(player, HARD_SUCCESS);
                addHealthThenItems(player, HARD_SUCCESS);
            }
            if (type == TaskType.RED) {
                showHeartTitle(player, RED_SUCCESS);
                addHealthThenItems(player, RED_SUCCESS);
            }
        });
        if (season.isOnLastLife(player, false)) {
            TaskScheduler.scheduleTask(120, () -> {
                chooseTasks(List.of(player), TaskType.RED);
            });
        }
    }

    public static void rerollTask(ServerPlayerEntity player) {
        if (!hasSessionStarted(player)) return;
        if (isBeingUsed(player)) return;
        SecretLife season = (SecretLife) currentSeason;
        TaskType type = getPlayersTaskType(player);
        if (!hasTaskBookCheck(player, type)) return;
        if (type == TaskType.RED) {
            failTask(player);
            return;
        }
        if (type == TaskType.EASY) {
            removePlayersTaskBook(player);
            SessionTranscript.rerollTask(player);
            secretKeeperBeingUsed = true;
            TaskType newType = TaskType.HARD;
            if (season.isOnLastLife(player, false)) {
                chooseTasks(List.of(player), TaskType.RED);
                return;
            }

            PlayerUtils.playSoundToPlayers(List.of(player), SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitle(player, Text.literal("The reward is more").formatted(Formatting.DARK_GREEN).formatted(Formatting.BOLD),20,35,0);

            TaskScheduler.scheduleTask(50, () -> {
                PlayerUtils.playSoundToPlayers(List.of(player), SoundEvents.UI_BUTTON_CLICK.value());
                PlayerUtils.sendTitle(player, Text.literal("The risk is great").formatted(Formatting.GREEN).formatted(Formatting.BOLD),20,35,0);
            });
            TaskScheduler.scheduleTask(100, () -> {
                PlayerUtils.playSoundToPlayers(List.of(player), SoundEvents.UI_BUTTON_CLICK.value());
                PlayerUtils.sendTitle(player, Text.literal("Let me open the door").formatted(Formatting.YELLOW).formatted(Formatting.BOLD),20,35,0);
            });
            TaskScheduler.scheduleTask(150, () -> {
                PlayerUtils.playSoundToPlayers(List.of(player), SoundEvents.UI_BUTTON_CLICK.value());
                PlayerUtils.sendTitle(player, Text.literal("Accept your fate").formatted(Formatting.RED).formatted(Formatting.BOLD),20,30,0);
            });
            TaskScheduler.scheduleTask(200, () -> AnimationUtils.playTotemAnimation(player));
            TaskScheduler.scheduleTask(240, () -> {
                assignRandomTaskToPlayer(player, newType);
                secretKeeperBeingUsed = false;
            });
            return;
        }
        if (type == TaskType.HARD) {
            if (!season.isOnLastLife(player, true)) {
                player.sendMessage(Text.of("§cYou cannot re-roll a Hard task."));
            }
            else {
                player.sendMessage(Text.of("§cYou cannot re-roll a Hard task. If you want your red task instead, click the Fail button."));
            }
        }
    }

    public static void failTask(ServerPlayerEntity player) {
        if (server == null) return;
        if (!hasSessionStarted(player)) return;
        if (isBeingUsed(player)) return;
        SecretLife season = (SecretLife) currentSeason;
        TaskType type = getPlayersTaskType(player);
        if (!hasTaskBookCheck(player, type)) return;
        SessionTranscript.failTask(player);
        removePlayersTaskBook(player);
        submittedOrFailed.add(player.getUuid());
        secretKeeperBeingUsed = true;

        Vec3d centerPos = itemSpawnerPos.toCenterPos();
        AnimationUtils.createGlyphAnimation(server.getOverworld(), centerPos, 40);
        server.getOverworld().playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(), SoundEvent.of(Identifier.of("minecraft","secretlife_task")), SoundCategory.PLAYERS, 1.0F, 1.0F);
        TaskScheduler.scheduleTask(60, () -> {
            server.getOverworld().playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(), SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, SoundCategory.PLAYERS, 1.0F, 1.0F);
            AnimationUtils.spawnFireworkBall(server.getOverworld(), centerPos, 40, 0.3, new Vector3f(1, 0, 0));
            if (type == TaskType.EASY) {
                showHeartTitle(player, EASY_FAIL);
                season.removePlayerHealth(player, -EASY_FAIL);
            }
            if (type == TaskType.HARD) {
                showHeartTitle(player, HARD_FAIL);
                season.removePlayerHealth(player, -HARD_FAIL);
            }
            if (type == TaskType.RED) {
                showHeartTitle(player, RED_FAIL);
                season.removePlayerHealth(player, -RED_FAIL);
            }
            if (!season.isOnLastLife(player, false)) {
                secretKeeperBeingUsed = false;
            }
        });
        if (season.isOnLastLife(player, false)) {
            TaskScheduler.scheduleTask(120, () -> chooseTasks(List.of(player), TaskType.RED));
        }
    }

    public static void showHeartTitle(ServerPlayerEntity player, int amount) {
        if (amount == 0) return;
        SecretLife season = (SecretLife) currentSeason;
        if (amount > 0 && season.getPlayerHealth(player) >= SecretLife.MAX_HEALTH) return;
        int healthBefore = MathHelper.ceil(season.getPlayerHealth(player));
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


        Formatting formatting = Formatting.GREEN;
        if (finalAmount < 0) formatting = Formatting.RED;
        else finalStr = "+"+finalStr;
        PlayerUtils.sendTitle(player, Text.literal(finalStr+" Hearts").formatted(formatting), 20, 40, 20);
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
            OtherUtils.broadcastMessage(Text.literal("§c[SecretLife setup] This location is already being used."), 20);
            return;
        }
        if (successButtonPos == null && fromButton) {
            successButtonPos = pos;
            OtherUtils.broadcastMessage(Text.literal("§a[SecretLife setup 1/4] Location set.\n"));
        }
        else if (rerollButtonPos == null && fromButton) {
            rerollButtonPos = pos;
            OtherUtils.broadcastMessage(Text.literal("§a[SecretLife setup 2/4] Location set.\n"));
        }
        else if (failButtonPos == null && fromButton) {
            failButtonPos = pos;
            OtherUtils.broadcastMessage(Text.literal("§a[SecretLife setup 3/4] Location set.\n"));
        }
        if (itemSpawnerPos == null && !fromButton) {
            if (successButtonPos != null && rerollButtonPos != null && failButtonPos != null) {
                itemSpawnerPos = pos;
                OtherUtils.broadcastMessage(Text.literal("§a[SecretLife] All locations have been set. If you wish to change them in the future, use §2'/secretlife changeLocations'\n"));

                OtherUtils.broadcastMessage(Text.of("\nUse §b'/session timer set <time>'§f to set the desired session time."));
                OtherUtils.broadcastMessage(Text.of("After that, use §b'/session start'§f to start the session."));
            }
        }
        locationsConfig.saveLocations();
        checkSecretLifePositions();
    }

    public static boolean searchingForLocations = false;
    public static boolean checkSecretLifePositions() {
        if (successButtonPos == null) {
            OtherUtils.broadcastMessage(Text.literal("§c[SecretLife setup 1/4] Location for the secret keeper task §6§lSUCCESS BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
            searchingForLocations = true;
            return false;
        }
        if (rerollButtonPos == null) {
            OtherUtils.broadcastMessage(Text.literal("§c[SecretLife setup 2/4] Location for the secret keeper task §6§lRE-ROLL BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
            searchingForLocations = true;
            return false;
        }
        if (failButtonPos == null) {
            OtherUtils.broadcastMessage(Text.literal("§c[SecretLife setup 3/4] Location for the secret keeper task §6§lFAIL BUTTON§r§c was not found. §nThe next button you click will be set as the location."));
            searchingForLocations = true;
            return false;
        }
        if (itemSpawnerPos == null) {
            OtherUtils.broadcastMessage(Text.literal("§c[SecretLife setup 4/4] Location for the secret keeper task §6§lITEM SPAWN BLOCK§r§c was not found. §nPlease place a bedrock block at the desired spot to mark it."));
            searchingForLocations = true;
            return false;
        }
        searchingForLocations = false;
        return true;
    }

    public static void onBlockUse(ServerPlayerEntity player, ServerWorld world, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        String name = world.getBlockState(pos).getBlock().getName().getString().toLowerCase();
        if (name.contains("button")) {
            if (searchingForLocations) {
                positionFound(pos, true);
            }
            else {
                if (pos.equals(successButtonPos)) {
                    succeedTask(player);
                }
                else if (pos.equals(rerollButtonPos)) {
                    rerollTask(player);
                }
                else if (pos.equals(failButtonPos)) {
                    failTask(player);
                }
            }
        }
        if (!searchingForLocations) return;
        if (successButtonPos == null || rerollButtonPos == null || failButtonPos == null) return;
        BlockPos placePos = pos.offset(hitResult.getSide());
        TaskScheduler.scheduleTask(1, () -> {
            String name2 = world.getBlockState(placePos).getBlock().getName().getString().toLowerCase();
            if (name2.contains("bedrock")) {
                positionFound(placePos, false);
                world.breakBlock(placePos, false);
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
