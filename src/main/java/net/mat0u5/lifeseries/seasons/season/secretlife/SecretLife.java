package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;
//? if >= 1.21.9
/*import net.minecraft.entity.TypedEntityData;*/
//? if <= 1.21.9
import net.minecraft.world.GameRules;
//? if > 1.21.9
/*import net.minecraft.world.rule.GameRules;*/

public class SecretLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /gift, /task, /health";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /gift";
    public static double MAX_HEALTH = 60.0d;
    public static double MAX_KILL_HEALTH = 1000.0d;
    public static boolean ONLY_LOSE_HEARTS_IN_SESSION = false;

    public ItemSpawner itemSpawner;
    SessionAction taskWarningAction = new SessionAction(OtherUtils.minutesToTicks(-5)+1) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Text.literal("Go submit / fail your secret tasks if you haven't!").formatted(Formatting.GRAY));
        }
    };
    SessionAction taskWarningAction2 = new SessionAction(OtherUtils.minutesToTicks(-30)+1) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Text.literal("You better start finishing your secret tasks if you haven't already!").formatted(Formatting.GRAY));
        }
    };

    @Override
    public Seasons getSeason() {
        return Seasons.SECRET_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        TaskManager.initialize();
        return new SecretLifeConfig();
    }

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
    }

    @Override
    public void initialize() {
        super.initialize();
        TaskManager.initialize();
        initializeItemSpawner();
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof SecretLifeConfig config)) return;

        MAX_HEALTH = config.MAX_PLAYER_HEALTH.get(config);
        MAX_KILL_HEALTH = SecretLifeConfig.MAX_PLAYER_KILL_HEALTH.get(config);
        TaskManager.EASY_SUCCESS = SecretLifeConfig.TASK_HEALTH_EASY_PASS.get(config);
        TaskManager.EASY_FAIL = SecretLifeConfig.TASK_HEALTH_EASY_FAIL.get(config);
        TaskManager.HARD_SUCCESS = SecretLifeConfig.TASK_HEALTH_HARD_PASS.get(config);
        TaskManager.HARD_FAIL = SecretLifeConfig.TASK_HEALTH_HARD_FAIL.get(config);
        TaskManager.RED_SUCCESS = SecretLifeConfig.TASK_HEALTH_RED_PASS.get(config);
        TaskManager.RED_FAIL = SecretLifeConfig.TASK_HEALTH_RED_FAIL.get(config);
        TaskManager.ASSIGN_TASKS_MINUTE = SecretLifeConfig.ASSIGN_TASKS_MINUTE.get(config);
        TaskManager.BROADCAST_SECRET_KEEPER = SecretLifeConfig.BROADCAST_SECRET_KEEPER.get(config);
        TaskManager.CONSTANT_TASKS = SecretLifeConfig.CONSTANT_TASKS.get(config);
        TaskManager.PUBLIC_TASKS_ON_SUBMIT = SecretLifeConfig.BROADCAST_TASKS_WHEN_SUBMITTED.get(config);
        ONLY_LOSE_HEARTS_IN_SESSION = SecretLifeConfig.ONLY_LOSE_HEARTS_IN_SESSION.get(config);
    }

    @Override
    public void onPlayerRespawn(ServerPlayerEntity player) {
        super.onPlayerRespawn(player);
        if (giveBookOnRespawn.containsKey(player.getUuid())) {
            ItemStack book = giveBookOnRespawn.get(player.getUuid());
            giveBookOnRespawn.remove(player.getUuid());
            if (book != null) {
                player.getInventory().insertStack(book);
            }
        }
        TaskTypes type = TaskManager.getPlayersTaskType(player);
        if (player.ls$isOnLastLife(false) && TaskManager.submittedOrFailed.contains(player.getUuid()) && type == null) {
            TaskManager.chooseTasks(List.of(player), TaskTypes.RED);
        }
    }

    public void initializeItemSpawner() {
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 10);
        itemSpawner.addItem(new ItemStack(Items.ANCIENT_DEBRIS), 10);
        itemSpawner.addItem(new ItemStack(Items.EXPERIENCE_BOTTLE, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.PUFFERFISH_BUCKET), 10);
        itemSpawner.addItem(new ItemStack(Items.DIAMOND, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.GOLD_BLOCK, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.IRON_BLOCK, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.COAL_BLOCK, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE), 10);
        itemSpawner.addItem(new ItemStack(Items.INFESTED_STONE, 16), 7);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SHRIEKER, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SENSOR, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.TNT, 4), 10);
        itemSpawner.addItem(new ItemStack(Items.OBSIDIAN, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.ARROW, 32), 10);
        itemSpawner.addItem(new ItemStack(Items.WOLF_ARMOR), 10);
        itemSpawner.addItem(new ItemStack(Items.BUNDLE), 10);
        itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.BOOKSHELF, 4), 10);
        itemSpawner.addItem(new ItemStack(Items.SWEET_BERRIES, 16), 10);

        //Potions
        ItemStack pot = new ItemStack(Items.POTION);
        ItemStack pot2 = new ItemStack(Items.POTION);
        ItemStack pot3 = new ItemStack(Items.POTION);
        pot.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.INVISIBILITY));
        pot2.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.SLOW_FALLING));
        pot3.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.FIRE_RESISTANCE));
        itemSpawner.addItem(pot, 10);
        itemSpawner.addItem(pot2, 10);
        itemSpawner.addItem(pot3, 10);

        //Enchanted Books
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.PROTECTION, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FEATHER_FALLING, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.SILK_TOUCH, 1)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FORTUNE, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.LOOTING, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.EFFICIENCY, 4)), 10);


        //Spawn Eggs
        itemSpawner.addItem(new ItemStack(Items.WOLF_SPAWN_EGG), 15);
        itemSpawner.addItem(new ItemStack(Items.PANDA_SPAWN_EGG), 10);
        itemSpawner.addItem(new ItemStack(Items.SNIFFER_SPAWN_EGG), 7);
        itemSpawner.addItem(new ItemStack(Items.TURTLE_SPAWN_EGG), 10);

        ItemStack camel = new ItemStack(Items.CAMEL_SPAWN_EGG);
        ItemStack zombieHorse = new ItemStack(Items.ZOMBIE_HORSE_SPAWN_EGG);
        ItemStack skeletonHorse = new ItemStack(Items.SKELETON_HORSE_SPAWN_EGG);
        NbtCompound nbtCompSkeleton = new NbtCompound();
        nbtCompSkeleton.putInt("Tame", 1);
        nbtCompSkeleton.putString("id", "skeleton_horse");

        NbtCompound nbtCompZombie= new NbtCompound();
        nbtCompZombie.putInt("Tame", 1);
        nbtCompZombie.putString("id", "zombie_horse");

        NbtCompound nbtCompCamel = new NbtCompound();
        nbtCompCamel.putInt("Tame", 1);
        nbtCompCamel.putString("id", "camel");

        //? if <= 1.21.4 {
        NbtCompound saddleItemComp = new NbtCompound();
        saddleItemComp.putInt("Count", 1);
        saddleItemComp.putString("id", "saddle");
        nbtCompSkeleton.put("SaddleItem", saddleItemComp);
        nbtCompZombie.put("SaddleItem", saddleItemComp);
        nbtCompCamel.put("SaddleItem", saddleItemComp);
        //?} else {
        /*NbtCompound equipmentItemComp = new NbtCompound();
        NbtCompound saddleItemComp = new NbtCompound();
        saddleItemComp.putString("id", "saddle");
        equipmentItemComp.put("saddle", saddleItemComp);
        nbtCompSkeleton.put("equipment", equipmentItemComp);
        nbtCompZombie.put("equipment", equipmentItemComp);
        nbtCompCamel.put("equipment", equipmentItemComp);
        *///?}


        NbtComponent nbtSkeleton = NbtComponent.of(nbtCompSkeleton);
        NbtComponent nbtZombie = NbtComponent.of(nbtCompZombie);
        NbtComponent nbtCamel= NbtComponent.of(nbtCompCamel);

        //? if <= 1.21.6 {
        zombieHorse.set(DataComponentTypes.ENTITY_DATA, nbtZombie);
        skeletonHorse.set(DataComponentTypes.ENTITY_DATA, nbtSkeleton);
        camel.set(DataComponentTypes.ENTITY_DATA, nbtCamel);
        //?} else {
        /*zombieHorse.set(DataComponentTypes.ENTITY_DATA, TypedEntityData.create(EntityType.ZOMBIE, nbtZombie.copyNbt()));
        skeletonHorse.set(DataComponentTypes.ENTITY_DATA, TypedEntityData.create(EntityType.SKELETON, nbtSkeleton.copyNbt()));
        camel.set(DataComponentTypes.ENTITY_DATA, TypedEntityData.create(EntityType.CAMEL, nbtCamel.copyNbt()));
        *///?}
        itemSpawner.addItem(zombieHorse, 10);
        itemSpawner.addItem(skeletonHorse, 10);
        itemSpawner.addItem(camel, 10);

        //Other Stuff
        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
        itemSpawner.addItem(endCrystal, 10);

        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
        mace.setDamage(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 3);

        ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
        patat.set(DataComponentTypes.CUSTOM_NAME,Text.of("§6§l§nThe Sacred Patat"));
        ItemStackUtils.addLoreToItemStack(patat,
                List.of(Text.of("§5§oEating this might help you. Or maybe not..."))
        );
        itemSpawner.addItem(patat, 1);
    }

    @Override
    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
        if (player.hasStatusEffect(StatusEffects.HEALTH_BOOST)) {
            player.removeStatusEffect(StatusEffects.HEALTH_BOOST);
        }
        TaskScheduler.scheduleTask(1, () -> syncPlayerHealth(player));
    }

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        super.onPlayerDeath(player, source);
        setPlayerHealth(player, MAX_HEALTH);
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);

        if (TaskManager.tasksChosen && !TaskManager.tasksChosenFor.contains(player.getUuid())) {
            TaskScheduler.scheduleTask(100, () -> TaskManager.chooseTasks(List.of(player), null));
        }
    }

    @Override
    public void assignDefaultLives(ServerPlayerEntity player) {
        setPlayerHealth(player, MAX_HEALTH);
        player.setHealth((float) MAX_HEALTH);
        super.assignDefaultLives(player);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        TaskManager.checkSecretLifePositions();
        super.onPlayerFinishJoining(player);
    }

    @Override
    public boolean sessionStart() {
        if (TaskManager.checkSecretLifePositions()) {
            super.sessionStart();
            SecretLifeCommands.playersGiven.clear();
            TaskManager.tasksChosen = false;
            TaskManager.tasksChosenFor.clear();
            TaskManager.submittedOrFailed.clear();
            TaskScheduler.scheduleTask(1, this::heartsTranscript);
            return true;
        }
        return false;
    }

    @Override
    public void addSessionActions() {
        super.addSessionActions();
        currentSession.addSessionAction(TaskManager.getActionChooseTasks());
        currentSession.addSessionActionIfTime(taskWarningAction);
        currentSession.addSessionActionIfTime(taskWarningAction2);
    }


    @Override
    public void sessionEnd() {
        super.sessionEnd();
        List<String> playersWithTaskBooks = new ArrayList<>();
        for (ServerPlayerEntity player : livesManager.getNonRedPlayers()) {
            if (player.ls$isDead()) continue;
            if (TaskManager.submittedOrFailed.contains(player.getUuid())) continue;
            if (TaskManager.CONSTANT_TASKS) continue;
            playersWithTaskBooks.add(player.getNameForScoreboard());
        }
        if (!playersWithTaskBooks.isEmpty()) {
            boolean isOne = playersWithTaskBooks.size() == 1;
            String playerNames = String.join(", ", playersWithTaskBooks);
            PlayerUtils.broadcastMessageToAdmins(TextUtils.formatLoosely("§4{}§c still {} not submitted / failed a task this session.", playerNames, (isOne?"has":"have")));
        }
    }

    public void heartsTranscript() {
        for (ServerPlayerEntity player : livesManager.getAlivePlayers()) {
            SessionTranscript.logHealth(player, getRoundedHealth(player));
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        super.onPlayerKilledByPlayer(victim, killer);
        if (killer.ls$isOnLastLife(false)) {
            double amountGained = Math.min(Math.max(MAX_KILL_HEALTH, MAX_HEALTH) - getPlayerHealth(killer), 20);
            if (amountGained > 0) {
                addPlayerHealth(killer, amountGained);
                double roundedHearts = Math.ceil(amountGained) / 2.0;
                String text = TextUtils.pluralize(TextUtils.formatString("+{} Heart", roundedHearts), roundedHearts);
                PlayerUtils.sendTitle(killer, Text.literal(text).formatted(Formatting.RED), 0, 40, 20);
            }
        }
    }

    private long ticks = 0;
    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        TaskManager.tick();
        ticks++;
        if (ticks % 20 == 0) {
            checkNaturalRegeneration();
        }
    }

    private Map<UUID, ItemStack> giveBookOnRespawn = new HashMap<>();
    @Override
    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
        super.modifyEntityDrops(entity, damageSource);
        if (entity instanceof ServerPlayerEntity player) {
            boolean dropBook = SecretLifeConfig.PLAYERS_DROP_TASK_ON_DEATH.get(seasonConfig);
            if (dropBook || server == null) return;
            boolean keepInventory = OtherUtils.getBooleanGameRule(PlayerUtils.getServerWorld(player), GameRules.KEEP_INVENTORY);
            if (keepInventory) return;
            giveBookOnRespawn.put(player.getUuid(), TaskManager.getPlayersTaskBook(player));
            TaskManager.removePlayersTaskBook(player);
        }
    }

    public void removePlayerHealth(ServerPlayerEntity player, double health) {
        addPlayerHealth(player,-health);
    }

    public void addPlayerHealth(ServerPlayerEntity player, double health) {
        double currentHealth = AttributeUtils.getMaxPlayerHealth(player);
        setPlayerHealth(player, currentHealth + health);
    }

    public void setPlayerHealth(ServerPlayerEntity player, double health) {
        if (player == null) return;
        if (health < 0.1) health = 0.1;
        if (canChangeHealth() || (health > getPlayerHealth(player))) {
            AttributeUtils.setMaxPlayerHealth(player, health);
        }
        if (health > player.getHealth() && player.isAlive()) {
            player.setHealth((float) health);
        }
    }

    public double getPlayerHealth(ServerPlayerEntity player) {
        return AttributeUtils.getMaxPlayerHealth(player);
    }

    public double getRoundedHealth(ServerPlayerEntity player) {
        return Math.floor(getPlayerHealth(player)*100)/100.0;
    }

    public void syncPlayerHealth(ServerPlayerEntity player) {
        if (player == null) return;
        if (!player.isAlive()) return;
        setPlayerHealth(player, player.getHealth());
    }

    public void syncAllPlayerHealth() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            setPlayerHealth(player, player.getHealth());
        }
    }

    public void resetPlayerHealth(ServerPlayerEntity player) {
        setPlayerHealth(player, MAX_HEALTH);
    }

    public void resetAllPlayerHealth() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            resetPlayerHealth(player);
        }
    }

    public boolean canChangeHealth() {
        if (!ONLY_LOSE_HEARTS_IN_SESSION) return true;
        return currentSession != null && currentSession.statusStarted();
    }

    @Override
    public void sessionChangeStatus(SessionStatus newStatus) {
        super.sessionChangeStatus(newStatus);
        checkNaturalRegeneration();
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.setHealth((float) getPlayerHealth(player));
        }
    }

    public void checkNaturalRegeneration() {
        if (server == null) return;
        boolean naturalRegeneration = false;
        if (ONLY_LOSE_HEARTS_IN_SESSION) {
            if (!currentSession.statusStarted()) {
                naturalRegeneration = true;
            }
        }
        if (server.getOverworld() == null) return;
        //? if <= 1.21.9 {
        server.getOverworld().getGameRules().get(GameRules.NATURAL_REGENERATION).set(naturalRegeneration, server);
         //?} else {
        /*server.getOverworld().getGameRules().setValue(GameRules.NATURAL_HEALTH_REGENERATION, naturalRegeneration, server);
        *///?}
    }
}
