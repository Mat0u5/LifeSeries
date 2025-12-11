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
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

//? if <= 1.20.5 {
/*import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
*///?}
//? if <= 1.20.3 {
/*import net.minecraft.world.item.alchemy.PotionUtils;
*///?} else {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
//?}

//? if >= 1.21.9 {
/*import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
*///?}

//? if <= 1.21.9
import net.minecraft.world.level.GameRules;
//? if > 1.21.9
/*import net.minecraft.world.level.gamerules.GameRules;*/

public class SecretLife extends Season {
    public static double MAX_HEALTH = 60.0d;
    public static double MAX_KILL_HEALTH = 1000.0d;
    public static boolean ONLY_LOSE_HEARTS_IN_SESSION = false;

    public ItemSpawner itemSpawner;
    SessionAction taskWarningAction = new SessionAction(Time.minutes(-5).add(Time.seconds(1))) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Component.literal("Go submit / fail your secret tasks if you haven't!").withStyle(ChatFormatting.GRAY));
        }
    };
    SessionAction taskWarningAction2 = new SessionAction(Time.minutes(-30).add(Time.seconds(1))) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Component.literal("You better start finishing your secret tasks if you haven't already!").withStyle(ChatFormatting.GRAY));
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
    public void initialize() {
        super.initialize();
        TaskManager.initialize();
        initializeItemSpawner();
    }

    @Override
    public void reload() {
        super.reload();
        MAX_HEALTH = seasonConfig.MAX_PLAYER_HEALTH.get(seasonConfig);
        MAX_KILL_HEALTH = SecretLifeConfig.MAX_PLAYER_KILL_HEALTH.get(seasonConfig);
        TaskManager.EASY_SUCCESS = SecretLifeConfig.TASK_HEALTH_EASY_PASS.get(seasonConfig);
        TaskManager.EASY_FAIL = SecretLifeConfig.TASK_HEALTH_EASY_FAIL.get(seasonConfig);
        TaskManager.HARD_SUCCESS = SecretLifeConfig.TASK_HEALTH_HARD_PASS.get(seasonConfig);
        TaskManager.HARD_FAIL = SecretLifeConfig.TASK_HEALTH_HARD_FAIL.get(seasonConfig);
        TaskManager.RED_SUCCESS = SecretLifeConfig.TASK_HEALTH_RED_PASS.get(seasonConfig);
        TaskManager.RED_FAIL = SecretLifeConfig.TASK_HEALTH_RED_FAIL.get(seasonConfig);
        TaskManager.ASSIGN_TASKS_MINUTE = SecretLifeConfig.ASSIGN_TASKS_MINUTE.get(seasonConfig);
        TaskManager.BROADCAST_SECRET_KEEPER = SecretLifeConfig.BROADCAST_SECRET_KEEPER.get(seasonConfig);
        TaskManager.CONSTANT_TASKS = SecretLifeConfig.CONSTANT_TASKS.get(seasonConfig);
        TaskManager.PUBLIC_TASKS_ON_SUBMIT = SecretLifeConfig.BROADCAST_TASKS_WHEN_SUBMITTED.get(seasonConfig);
        ONLY_LOSE_HEARTS_IN_SESSION = SecretLifeConfig.ONLY_LOSE_HEARTS_IN_SESSION.get(seasonConfig);
        TaskManager.TASKS_NEED_CONFIRMATION = SecretLifeConfig.TASKS_NEED_CONFIRMATION.get(seasonConfig);
    }

    @Override
    public void onPlayerRespawn(ServerPlayer player) {
        super.onPlayerRespawn(player);
        if (giveBookOnRespawn.containsKey(player.getUUID())) {
            ItemStack book = giveBookOnRespawn.get(player.getUUID());
            giveBookOnRespawn.remove(player.getUUID());
            if (book != null) {
                player.getInventory().add(book);
            }
        }
        TaskTypes type = TaskManager.getPlayersTaskType(player);
        if (player.ls$isOnLastLife(false) && TaskManager.submittedOrFailed.contains(player.getUUID()) && type == null) {
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
        //? if >= 1.20.5 {
        itemSpawner.addItem(new ItemStack(Items.WOLF_ARMOR), 10);
        //?}
        itemSpawner.addItem(new ItemStack(Items.BUNDLE), 10);
        itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.BOOKSHELF, 4), 10);
        itemSpawner.addItem(new ItemStack(Items.SWEET_BERRIES, 16), 10);

        //Potions
        ItemStack pot = new ItemStack(Items.POTION);
        ItemStack pot2 = new ItemStack(Items.POTION);
        ItemStack pot3 = new ItemStack(Items.POTION);
        //? if <= 1.20.3 {
        /*PotionUtils.setCustomEffects(pot, Potions.INVISIBILITY.getEffects());
        PotionUtils.setCustomEffects(pot2, Potions.SLOW_FALLING.getEffects());
        PotionUtils.setCustomEffects(pot3, Potions.FIRE_RESISTANCE.getEffects());
        *///?} else {
        pot.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY));
        pot2.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOW_FALLING));
        pot3.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.FIRE_RESISTANCE));
        //?}
        itemSpawner.addItem(pot, 10);
        itemSpawner.addItem(pot2, 10);
        itemSpawner.addItem(pot3, 10);

        //Enchanted Books
        //? if <= 1.20.3 {
        /*itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.ALL_DAMAGE_PROTECTION, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FALL_PROTECTION, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.SILK_TOUCH, 1))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.BLOCK_FORTUNE, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.MOB_LOOTING, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.BLOCK_EFFICIENCY, 4))), 10);
        *///?} else if <= 1.20.5 {
        /*itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.PROTECTION, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FEATHER_FALLING, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.SILK_TOUCH, 1))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FORTUNE, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.LOOTING, 3))), 10);
        itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.EFFICIENCY, 4))), 10);
        *///?} else {
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.PROTECTION, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FEATHER_FALLING, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.SILK_TOUCH, 1)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FORTUNE, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.LOOTING, 3)), 10);
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.EFFICIENCY, 4)), 10);
        //?}


        //Spawn Eggs
        itemSpawner.addItem(new ItemStack(Items.WOLF_SPAWN_EGG), 15);
        itemSpawner.addItem(new ItemStack(Items.PANDA_SPAWN_EGG), 10);
        itemSpawner.addItem(new ItemStack(Items.SNIFFER_SPAWN_EGG), 7);
        itemSpawner.addItem(new ItemStack(Items.TURTLE_SPAWN_EGG), 10);

        ItemStack camel = new ItemStack(Items.CAMEL_SPAWN_EGG);
        ItemStack zombieHorse = new ItemStack(Items.ZOMBIE_HORSE_SPAWN_EGG);
        ItemStack skeletonHorse = new ItemStack(Items.SKELETON_HORSE_SPAWN_EGG);
        CompoundTag nbtCompSkeleton = new CompoundTag();
        nbtCompSkeleton.putInt("Tame", 1);
        nbtCompSkeleton.putString("id", "skeleton_horse");

        CompoundTag nbtCompZombie= new CompoundTag();
        nbtCompZombie.putInt("Tame", 1);
        nbtCompZombie.putString("id", "zombie_horse");

        CompoundTag nbtCompCamel = new CompoundTag();
        nbtCompCamel.putInt("Tame", 1);
        nbtCompCamel.putString("id", "camel");

        //? if <= 1.21.4 {
        CompoundTag saddleItemComp = new CompoundTag();
        saddleItemComp.putInt("Count", 1);
        saddleItemComp.putString("id", "saddle");
        nbtCompSkeleton.put("SaddleItem", saddleItemComp);
        nbtCompZombie.put("SaddleItem", saddleItemComp);
        nbtCompCamel.put("SaddleItem", saddleItemComp);
        //?} else {
        /*CompoundTag equipmentItemComp = new CompoundTag();
        CompoundTag saddleItemComp = new CompoundTag();
        saddleItemComp.putString("id", "saddle");
        equipmentItemComp.put("saddle", saddleItemComp);
        nbtCompSkeleton.put("equipment", equipmentItemComp);
        nbtCompZombie.put("equipment", equipmentItemComp);
        nbtCompCamel.put("equipment", equipmentItemComp);
        *///?}


        //? if < 1.20.5 {
        /*zombieHorse.setTag(nbtCompZombie);
        skeletonHorse.setTag(nbtCompSkeleton);
        camel.setTag(nbtCompCamel);
        *///?} else {
        CustomData nbtSkeleton = CustomData.of(nbtCompSkeleton);
        CustomData nbtZombie = CustomData.of(nbtCompZombie);
        CustomData nbtCamel= CustomData.of(nbtCompCamel);
        //?}

        //? if >=1.20.5 && <= 1.21.6 {
        zombieHorse.set(DataComponents.ENTITY_DATA, nbtZombie);
        skeletonHorse.set(DataComponents.ENTITY_DATA, nbtSkeleton);
        camel.set(DataComponents.ENTITY_DATA, nbtCamel);
        //?} else if > 1.21.6 {
        /*zombieHorse.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.ZOMBIE, nbtZombie.copyTag()));
        skeletonHorse.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.SKELETON, nbtSkeleton.copyTag()));
        camel.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.CAMEL, nbtCamel.copyTag()));
        *///?}
        itemSpawner.addItem(zombieHorse, 10);
        itemSpawner.addItem(skeletonHorse, 10);
        itemSpawner.addItem(camel, 10);

        //Other Stuff
        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
        itemSpawner.addItem(endCrystal, 10);

        //? if >= 1.21 {
        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
        mace.setDamageValue(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 3);
        //?}

        //? if >= 1.20.5 {
        ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
        patat.set(DataComponents.CUSTOM_NAME,Component.nullToEmpty("§6§l§nThe Sacred Patat"));
        ItemStackUtils.addLoreToItemStack(patat,
                List.of(Component.nullToEmpty("§5§oEating this might help you. Or maybe not..."))
        );
        itemSpawner.addItem(patat, 1);
        //?}
    }

    @Override
    public void onPlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfo ci) {
        super.onPlayerDamage(player, source, amount, ci);
        if (player.hasEffect(MobEffects.HEALTH_BOOST)) {
            player.removeEffect(MobEffects.HEALTH_BOOST);
        }
        TaskScheduler.scheduleTask(1, () -> syncPlayerHealth(player));
    }

    @Override
    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        super.onPlayerDeath(player, source);
        setPlayerHealth(player, MAX_HEALTH);
    }

    @Override
    public void onPlayerJoin(ServerPlayer player) {
        super.onPlayerJoin(player);

        if (TaskManager.tasksChosen && !TaskManager.tasksChosenFor.contains(player.getUUID())) {
            TaskScheduler.scheduleTask(Time.seconds(5), () -> TaskManager.chooseTasks(List.of(player), null));
        }
    }

    @Override
    public void assignDefaultLives(ServerPlayer player) {
        setPlayerHealth(player, MAX_HEALTH);
        player.setHealth((float) MAX_HEALTH);
        super.assignDefaultLives(player);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayer player) {
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
        for (ServerPlayer player : livesManager.getNonRedPlayers()) {
            if (player.ls$isDead()) continue;
            if (TaskManager.submittedOrFailed.contains(player.getUUID())) continue;
            if (TaskManager.CONSTANT_TASKS) continue;
            playersWithTaskBooks.add(player.getScoreboardName());
        }
        if (!playersWithTaskBooks.isEmpty()) {
            boolean isOne = playersWithTaskBooks.size() == 1;
            String playerNames = String.join(", ", playersWithTaskBooks);
            PlayerUtils.broadcastMessageToAdmins(TextUtils.formatLoosely("§4{}§c still {} not submitted / failed a task this session.", playerNames, (isOne?"has":"have")));
        }
    }

    public void heartsTranscript() {
        for (ServerPlayer player : livesManager.getAlivePlayers()) {
            SessionTranscript.logHealth(player, getRoundedHealth(player));
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        super.onPlayerKilledByPlayer(victim, killer);
        if (killer.ls$isOnLastLife(false)) {
            double amountGained = Math.min(Math.max(MAX_KILL_HEALTH, MAX_HEALTH) - getPlayerHealth(killer), 20);
            if (amountGained > 0) {
                addPlayerHealth(killer, amountGained);
                double roundedHearts = Math.ceil(amountGained) / 2.0;
                String text = TextUtils.pluralize(TextUtils.formatString("+{} Heart", roundedHearts), roundedHearts);
                PlayerUtils.sendTitle(killer, Component.literal(text).withStyle(ChatFormatting.RED), 0, 40, 20);
            }
        }
    }

    private Time timer = Time.zero();
    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        TaskManager.tick();
        timer.tick();
        if (timer.isMultipleOf(Time.seconds(1))) {
            checkNaturalRegeneration();
        }
    }

    private Map<UUID, ItemStack> giveBookOnRespawn = new HashMap<>();
    @Override
    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource, CallbackInfo ci) {
        super.modifyEntityDrops(entity, damageSource, ci);
        if (entity instanceof ServerPlayer player) {
            boolean dropBook = SecretLifeConfig.PLAYERS_DROP_TASK_ON_DEATH.get(seasonConfig);
            if (dropBook || server == null) return;
            //? if <= 1.21.9 {
            boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.RULE_KEEPINVENTORY);
            //?} else {
            /*boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.KEEP_INVENTORY);
            *///?}
            if (keepInventory) return;
            giveBookOnRespawn.put(player.getUUID(), TaskManager.getPlayersTaskBook(player));
            TaskManager.removePlayersTaskBook(player);
        }
    }

    public void removePlayerHealth(ServerPlayer player, double health) {
        addPlayerHealth(player,-health);
    }

    public void addPlayerHealth(ServerPlayer player, double health) {
        double currentHealth = AttributeUtils.getMaxPlayerHealth(player);
        setPlayerHealth(player, currentHealth + health);
    }

    public void setPlayerHealth(ServerPlayer player, double health) {
        if (player == null) return;
        if (health < 0.1) health = 0.1;
        if (canChangeHealth() || (health > getPlayerHealth(player))) {
            AttributeUtils.setMaxPlayerHealth(player, health);
        }
        if (health > player.getHealth() && player.isAlive()) {
            player.setHealth((float) health);
        }
    }

    public double getPlayerHealth(ServerPlayer player) {
        return AttributeUtils.getMaxPlayerHealth(player);
    }

    public double getRoundedHealth(ServerPlayer player) {
        return Math.floor(getPlayerHealth(player)*100)/100.0;
    }

    public void syncPlayerHealth(ServerPlayer player) {
        if (player == null) return;
        if (!player.isAlive()) return;
        setPlayerHealth(player, player.getHealth());
    }

    public void syncAllPlayerHealth() {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            setPlayerHealth(player, player.getHealth());
        }
    }

    public void resetPlayerHealth(ServerPlayer player) {
        setPlayerHealth(player, MAX_HEALTH);
    }

    public void resetAllPlayerHealth() {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
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
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
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
        if (server.overworld() == null) return;
        //? if <= 1.21.9 {
        server.overworld().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(naturalRegeneration, server);
         //?} else {
        /*server.overworld().getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, naturalRegeneration, server);
        *///?}
    }
}
