package net.mat0u5.lifeseries.series.secretlife;

import net.mat0u5.lifeseries.resources.config.ConfigManager;
import net.mat0u5.lifeseries.series.*;
import net.mat0u5.lifeseries.utils.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.seriesConfig;

public class SecretLife extends Series {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /gift, /task, /health, /secretlife";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /gift";
    public static final String RESOURCEPACK_SECRETLIFE_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-secretlife-4b42f33cede049a0b747e7bd807488b5c8cae2ce/RP.zip";
    public static final String RESOURCEPACK_SECRETLIFE_SHA ="e175488de7a0545265f8f8dc078325b4745970d6";
    public static double MAX_HEALTH = 60.0d;
    public ItemSpawner itemSpawner;
    SessionAction taskWarningAction = new SessionAction(OtherUtils.minutesToTicks(-5)+1) {
        @Override
        public void trigger() {
            OtherUtils.broadcastMessage(Text.literal("Go submit / fail your secret tasks if you haven't!").formatted(Formatting.GRAY));
        }
    };
    SessionAction taskWarningAction2 = new SessionAction(OtherUtils.minutesToTicks(-30)+1) {
        @Override
        public void trigger() {
            OtherUtils.broadcastMessage(Text.literal("You better start finishing your secret tasks if you haven't already!").formatted(Formatting.GRAY));
        }
    };

    @Override
    public SeriesList getSeries() {
        return SeriesList.SECRET_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new SecretLifeConfig();
    }

    @Override
    public void initialize() {
        super.initialize();
        NO_HEALING = true;
        TaskManager.initialize();
        initializeItemSpawner();
    }

    @Override
    public void reload() {
        super.reload();
        MAX_HEALTH = seriesConfig.getOrCreateDouble("max_player_health", 60.0d);
        TaskManager.EASY_SUCCESS = seriesConfig.getOrCreateInt("task_health_easy_pass", 20);
        TaskManager.EASY_FAIL = seriesConfig.getOrCreateInt("task_health_easy_fail", 0);
        TaskManager.HARD_SUCCESS = seriesConfig.getOrCreateInt("task_health_hard_pass", 40);
        TaskManager.HARD_FAIL = seriesConfig.getOrCreateInt("task_health_hard_fail", -20);
        TaskManager.RED_SUCCESS = seriesConfig.getOrCreateInt("task_health_red_pass", 10);
        TaskManager.RED_FAIL = seriesConfig.getOrCreateInt("task_health_red_fail", -5);
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
        TaskType type = TaskManager.getPlayersTaskType(player);
        if (isOnLastLife(player, false) && TaskManager.submittedOrFailed.contains(player.getUuid()) && type == null) {
            TaskManager.chooseTasks(List.of(player), TaskType.RED);
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

        zombieHorse.set(DataComponentTypes.ENTITY_DATA, nbtZombie);
        skeletonHorse.set(DataComponentTypes.ENTITY_DATA, nbtSkeleton);
        camel.set(DataComponentTypes.ENTITY_DATA, nbtCamel);
        itemSpawner.addItem(zombieHorse, 10);
        itemSpawner.addItem(skeletonHorse, 10);
        itemSpawner.addItem(camel, 10);

        //Other Stuff
        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
        itemSpawner.addItem(endCrystal, 10);

        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoMending", true);
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
        if (!hasAssignedLives(player)) {
            int lives = seriesConfig.getOrCreateInt("default_lives", 3);
            setPlayerLives(player, lives);
            setPlayerHealth(player, MAX_HEALTH);
            player.setHealth((float) MAX_HEALTH);
        }

        if (TaskManager.tasksChosen && !TaskManager.tasksChosenFor.contains(player.getUuid())) {
            TaskScheduler.scheduleTask(100, () -> TaskManager.chooseTasks(List.of(player), null));
        }
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        TaskManager.checkSecretLifePositions();
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Secret Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Secret Life non-admin commands: §r"+COMMANDS_TEXT));
        }
        super.onPlayerFinishJoining(player);
    }

    @Override
    public boolean sessionStart() {
        if (TaskManager.checkSecretLifePositions()) {
            if (super.sessionStart()) {
                activeActions.addAll(
                        List.of(TaskManager.actionChooseTasks, taskWarningAction, taskWarningAction2)
                );
                SecretLifeCommands.playersGiven.clear();
                TaskManager.tasksChosen = false;
                TaskManager.tasksChosenFor.clear();
                TaskManager.submittedOrFailed.clear();
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        List<String> playersWithTaskBooks = new ArrayList<>();
        for (ServerPlayerEntity player : getNonRedPlayers()) {
            if (!isAlive(player)) continue;
            if (TaskManager.submittedOrFailed.contains(player.getUuid())) continue;
            playersWithTaskBooks.add(player.getNameForScoreboard());
        }
        if (!playersWithTaskBooks.isEmpty()) {
            boolean isOne = playersWithTaskBooks.size() == 1;
            String playerNames = String.join(", ", playersWithTaskBooks);
            OtherUtils.broadcastMessageToAdmins(Text.of("§4"+playerNames+"§c still " + (isOne?"has":"have") + " not submitted / failed any tasks this session."));
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        if (isAllowedToAttack(killer, victim)) {
            if (currentSeries.isOnLastLife(killer, false)) {
                addPlayerHealth(killer, 20);
                PlayerUtils.sendTitle(killer, Text.literal("+10 Hearts").formatted(Formatting.RED), 0, 40, 20);
            }
            return;
        }
        OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"
                +killer.getNameForScoreboard() + "§7, who is not §cred name§7."));
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (currentSeries.isOnLastLife(attacker, false)) return true;
        return attacker.getPrimeAdversary() == victim && (currentSeries.isOnLastLife(victim, false));
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        TaskManager.tick();
    }

    private Map<UUID, ItemStack> giveBookOnRespawn = new HashMap<>();
    @Override
    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
        super.modifyEntityDrops(entity, damageSource);
        if (entity instanceof ServerPlayerEntity player) {
            boolean dropBook = seriesConfig.getOrCreateBoolean("players_drop_task_on_death", false);
            if (dropBook) return;
            boolean keepInventory = player.server.getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
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
        AttributeUtils.setMaxPlayerHealth(player, health);
        if (health > player.getHealth() && !player.isDead()) {
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
        if (player.isDead()) return;
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
}
package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails;

import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.client.ClientResourcePacks;
import net.mat0u5.lifeseries.network.packets.ImagePayload;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SnailSkinsClient {
    private static final String CMD = "{\n  \"parent\": \"minecraft:item/generated\",\n  \"textures\": {\n    \"layer0\": \"minecraft:item/golden_horse_armor\"\n  },\n  \"overrides\": [\n__REPLACE__\n  ]\n}";
    private static final String ITEMS_ENTRY = "{\"model\":{\"model\":\"snailtextures:item/snail/__REPLACE__\",\"tints\":[{\"default\":16777215,\"type\":\"minecraft:dye\"}],\"type\":\"minecraft:model\"}}";
    private static final String BODY_1 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[9.0,13.0,12.0],\"to\":[10.0,16.0,13.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.0,12.5,3.5,12.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[3.0,12.0,3.5,13.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[2.5,12.0,3.0,13.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,2.5,12.5,4.0]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.5,12.0,4.0,12.5]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[2.0,12.0,2.5,13.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[10.0,13.0,13.0]}},{\"from\":[6.0,8.0,11.0],\"to\":[10.0,13.0,13.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,10.0,10.0,9.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[10.0,6.5,11.0,9.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[10.0,4.0,11.0,6.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[8.0,8.0,10.0,10.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,10.0,10.0,11.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[6.0,8.0,8.0,10.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[7.0,8.0,12.0]}},{\"from\":[6.0,8.0,9.0],\"to\":[10.0,10.0,11.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,5.0,11.0,4.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[9.0,11.5,10.0,12.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[8.0,11.5,9.0,12.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[8.0,10.5,10.0,11.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,5.0,11.0,6.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[6.0,10.5,8.0,11.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[7.0,8.0,10.0]}},{\"from\":[6.0,13.0,12.0],\"to\":[7.0,16.0,13.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.0,12.5,3.5,12.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[3.0,12.0,3.5,13.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[2.5,12.0,3.0,13.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,2.5,12.5,4.0]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.5,12.0,4.0,12.5]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[2.0,12.0,2.5,13.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[7.0,13.0,13.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_2 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[4.0,9.0,4.0],\"to\":[12.0,17.0,12.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.0,12.0,0.0,8.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.0,4.0,8.0,8.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,4.0,4.0,8.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[4.0,0.0,8.0,4.0]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,0.0,8.0,4.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,4.0,4.0]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[6.0,9.5,5.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_3 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[6.0,8.0,12.0],\"to\":[10.0,10.0,14.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,12.0,10.0,11.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,1.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,0.5,13.0,1.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[11.0,8.0,13.0,9.0]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[8.0,11.5,6.0,12.5]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[11.0,7.0,13.0,8.0]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[7.0,8.0,13.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_4 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[6.01,8.01,3.5],\"to\":[9.99,9.99,11.5],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[6.0,12.0,4.0,8.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[11.0,6.0,15.0,7.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[11.0,6.0,15.0,7.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,0.0,14.0,0.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[10.0,4.0,8.0,8.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,12.0,2.0,12.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[8.0,8.5,5.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_5 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[6.0,8.0,8.0],\"to\":[10.0,10.0,12.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[6.0,10.0,4.0,8.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,6.0,15.0,7.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,6.0,15.0,7.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.0,0.0,14.0,0.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[10.0,4.0,8.0,6.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,12.0,2.0,12.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[8.0,8.5,9.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_6 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[7.5,18.0,7.5],\"to\":[8.5,20.0,8.5],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.5,2.5,13.0,3.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.5,2.5,13.0,3.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.5,2.5,13.0,3.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.5,2.5,13.0,3.0]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.5,2.5,13.0,3.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[12.5,2.5,13.0,3.0]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[8.5,18.0,8.5]}},{\"from\":[6.0,17.0,6.0],\"to\":[10.0,18.0,10.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,11.0,12.0,9.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,10.5,16.0,11.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,9.5,16.0,10.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,10.0,16.0,10.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,9.0,12.0,11.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,9.0,16.0,9.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[7.0,17.0,7.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_7 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[5.0,20.01,5.0],\"to\":[11.0,20.01,11.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[16.0,6.0,13.0,3.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,3.0,0.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,3.0,0.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,3.0,0.0]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[16.0,3.0,13.0,6.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,3.0,0.0]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[5.0,20.01,5.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_8 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[0.0,21.0,4.0],\"to\":[16.0,21.6,20.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,11.0,12.0,9.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,10.5,16.0,11.0]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,9.5,16.0,10.0]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,10.0,16.0,10.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,9.0,12.0,11.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.0,9.0,16.0,9.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[1.0,21.0,5.0]}},{\"from\":[0.010000229,20.5,4.01],\"to\":[15.99,21.0,4.51],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[1.0,20.5,5.0]}},{\"from\":[0.010000229,20.5,19.49],\"to\":[15.99,21.0,19.99],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[1.0,20.5,20.5]}},{\"from\":[0.0,20.5,4.0],\"to\":[0.5,21.0,20.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[1.0,20.5,5.0]}},{\"from\":[15.5,20.5,4.0],\"to\":[16.0,21.0,20.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,14.75,0.25]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[14.5,0.0,15.5,0.25]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[16.5,20.5,5.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";
    private static final String BODY_9 = "{\"textures\":{\"0\":\"snailtextures:item/snail/texture__REPLACE__\",\"particle\":\"snailtextures:item/snail/texture__REPLACE__\"},\"elements\":[{\"from\":[6.0,17.0,15.0],\"to\":[7.0,21.0,15.1],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[6.0,17.0,15.0]}},{\"from\":[9.0,17.0,15.0],\"to\":[10.0,21.0,15.1],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[9.0,17.0,15.0]}},{\"from\":[9.0,17.0,9.0],\"to\":[10.0,21.0,9.1],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[9.0,17.0,9.0]}},{\"from\":[6.0,17.0,9.0],\"to\":[7.0,21.0,9.1],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[6.0,17.0,9.0]}},{\"from\":[5.0,17.0,13.0],\"to\":[5.1,21.0,14.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[5.0,17.0,13.0]}},{\"from\":[5.0,17.0,10.0],\"to\":[5.1,21.0,11.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[5.0,17.0,10.0]}},{\"from\":[11.0,17.0,10.0],\"to\":[11.1,21.0,11.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[11.0,17.0,10.0]}},{\"from\":[11.0,17.0,13.0],\"to\":[11.1,21.0,14.0],\"faces\":{\"up\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"west\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"east\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.0,2.5]},\"south\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]},\"down\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[0.0,0.0,0.5,0.0]},\"north\":{\"texture\":\"#0\",\"tintindex\":0,\"uv\":[13.0,0.5,13.5,2.5]}},\"rotation\":{\"axis\":\"y\",\"angle\":0.0,\"origin\":[11.0,17.0,13.0]}}],\"display\":{\"head\":{\"rotation\":[0.0,180.0,0.0]}}}";

    public static int skinReloadTicks = 0;
    public static void handleSnailSkin(ImagePayload payload) {

        int index = payload.index();
        int maxIndex = payload.maxIndex();

        String imageName = "texture"+index;
        byte[] imageBytes = payload.bytes();

        Main.LOGGER.info("Added dynamic image: " + imageName);
        MinecraftClient client = MinecraftClient.getInstance();
        skinReloadTicks = 30;
        client.execute(() -> addImage(imageName, imageBytes, index, maxIndex));
    }

    public static final String PACK_NAME = "[Life Series Mod] Snail Textures";
    private static Path resourcePackPath;
    private static boolean packInitialized = false;

    public static void initialize() {
        if (packInitialized) return;

        try {
            File resourcePacksFolder = new File(MinecraftClient.getInstance().runDirectory, "resourcepacks");
            resourcePackPath = resourcePacksFolder.toPath().resolve(PACK_NAME);

            Path assetsDir = resourcePackPath.resolve("assets").resolve("snailtextures");
            Path itemsDir = assetsDir.resolve("items");
            Path modelsDir = assetsDir.resolve("models").resolve("item").resolve("snail");
            Path texturesDir = assetsDir.resolve("textures").resolve("item").resolve("snail");
            Path cmdDir = resourcePackPath.resolve("assets").resolve("minecraft").resolve("models").resolve("item");

            Files.createDirectories(assetsDir);
            Files.createDirectories(itemsDir);
            Files.createDirectories(modelsDir);
            Files.createDirectories(texturesDir);
            Files.createDirectories(cmdDir);

            Files.writeString(cmdDir.resolve("golden_horse_armor.json"), CMD);

            Path packMcmetaPath = resourcePackPath.resolve("pack.mcmeta");
            String packMcmetaContent = "{\"pack\":{\"description\":\"Life Series Snails\",\"pack_format\":34}}";
            Files.writeString(packMcmetaPath, packMcmetaContent);

            packInitialized = true;

            Main.LOGGER.info("Initialized dynamic resource pack at: " + resourcePackPath);
        } catch (IOException e) {
            e.printStackTrace();
            Main.LOGGER.info("Failed to initialize dynamic resource pack: " + e.getMessage());
        }
    }

    public static CompletableFuture<Void> addImage(String imageName, byte[] imageData, int index, int maxIndex) {
        if (!packInitialized) {
            initialize();
        }

        return CompletableFuture.runAsync(() -> {
            try {
                // Convert byte array to image
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
                if (image == null) {
                    throw new IOException("Failed to decode image data");
                }

                File resourcePacksFolder = new File(MinecraftClient.getInstance().runDirectory, "resourcepacks");
                resourcePackPath = resourcePacksFolder.toPath().resolve(PACK_NAME);
                Path texturesDir = resourcePackPath.resolve("assets").resolve("snailtextures").resolve("textures").resolve("item").resolve("snail");
                String textureName = imageName + ".png";
                Path targetPath = texturesDir.resolve(textureName);

                // Create parent directories
                Files.createDirectories(targetPath.getParent());

                // Write the image
                ImageIO.write(image, "PNG", targetPath.toFile());

                Main.LOGGER.info("Added image to resource pack: " + targetPath);

                int modelDataStart = 10000 + index*10;

                //Add the items files
                Path assetsDir = resourcePackPath.resolve("assets").resolve("snailtextures");
                Path itemsDir = assetsDir.resolve("items");
                Files.createDirectories(itemsDir);
                Files.writeString(itemsDir.resolve("body1_"+(modelDataStart+1)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body1_"+(modelDataStart+1)));
                Files.writeString(itemsDir.resolve("body2_"+(modelDataStart+2)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body2_"+(modelDataStart+2)));
                Files.writeString(itemsDir.resolve("body3_"+(modelDataStart+3)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body3_"+(modelDataStart+3)));
                Files.writeString(itemsDir.resolve("body4_"+(modelDataStart+4)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body4_"+(modelDataStart+4)));
                Files.writeString(itemsDir.resolve("body5_"+(modelDataStart+5)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body5_"+(modelDataStart+5)));
                Files.writeString(itemsDir.resolve("body6_"+(modelDataStart+6)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body6_"+(modelDataStart+6)));
                Files.writeString(itemsDir.resolve("body7_"+(modelDataStart+7)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body7_"+(modelDataStart+7)));
                Files.writeString(itemsDir.resolve("body8_"+(modelDataStart+8)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body8_"+(modelDataStart+8)));
                Files.writeString(itemsDir.resolve("body9_"+(modelDataStart+9)+".json"), ITEMS_ENTRY.replaceAll("__REPLACE__", "body9_"+(modelDataStart+9)));

                //Add the model file
                Path modelsDir = assetsDir.resolve("models").resolve("item").resolve("snail");
                Files.createDirectories(modelsDir);
                Files.writeString(modelsDir.resolve("body1_"+(modelDataStart+1)+".json"), BODY_1.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body2_"+(modelDataStart+2)+".json"), BODY_2.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body3_"+(modelDataStart+3)+".json"), BODY_3.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body4_"+(modelDataStart+4)+".json"), BODY_4.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body5_"+(modelDataStart+5)+".json"), BODY_5.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body6_"+(modelDataStart+6)+".json"), BODY_6.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body7_"+(modelDataStart+7)+".json"), BODY_7.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body8_"+(modelDataStart+8)+".json"), BODY_8.replaceAll("__REPLACE__", String.valueOf(index)));
                Files.writeString(modelsDir.resolve("body9_"+(modelDataStart+9)+".json"), BODY_9.replaceAll("__REPLACE__", String.valueOf(index)));

                //Add the custom model data file
                Path cmdDir = resourcePackPath.resolve("assets").resolve("minecraft").resolve("models").resolve("item");
                Files.createDirectories(cmdDir);
                List<String> replaceCMD = new ArrayList<>();
                for (int i = 0; i <= maxIndex; i++) {
                    for (int y = 1; y < 10; y++) {
                        int newModelData = 10000 + i*10 + y;
                        replaceCMD.add("\t{\"model\": \"snailtextures:item/snail/body"+y+"_"+newModelData+"\",\"predicate\": {\"custom_model_data\": "+newModelData + "}}");
                    }
                }
                Files.writeString(cmdDir.resolve("golden_horse_armor.json"), CMD.replaceAll("__REPLACE__", String.join(",\n",replaceCMD)));



            } catch (IOException e) {
                e.printStackTrace();
                Main.LOGGER.info("Failed to add image to resource pack: " + e.getMessage());
            }
        });
    }
}
package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.ImagePayload;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mat0u5.lifeseries.Main.currentSeries;

public class SnailSkinsServer {
    public static void sendImageToClient(ServerPlayerEntity player, String name, int index, int maxIndex, Path imagePath) {
        try {
            // Read the image file
            BufferedImage image = ImageIO.read(Files.newInputStream(imagePath));

            // Check if the image is 32x32
            if (image.getWidth() != 32 || image.getHeight() != 32) {
                Main.LOGGER.error("Image must be 32x32 pixels");
                return;
            }

            // Convert the image to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            // Create ImagePayload using your packet format
            ImagePayload payload = new ImagePayload(name, index, maxIndex, imageBytes);

            NetworkHandlerServer.sendImagePacket(player, payload);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> indexedSkins = new HashMap<>();
    public static int currentIndex = 0;

    public static void sendStoredImages(List<ServerPlayerEntity> players) {
        if (currentSeries.getSeries() != SeriesList.WILD_LIFE) return;
        File folder = new File("./config/lifeseries/wildlife/snailskins/");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", folder);
                return;
            }
        }
        File[] files = folder.listFiles();
        if (files == null) return;
        int maxIndex = 0;
        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName().toLowerCase();
            if (name.equalsIgnoreCase("example.png")) continue;
            if (!name.endsWith(".png")) continue;
            String replacedName = name.toLowerCase().replaceAll(".png","");
            if (!indexedSkins.containsKey(replacedName)) {
                indexedSkins.put(replacedName, currentIndex);
                currentIndex++;
            }
            int imageIndex = indexedSkins.get(replacedName);
            maxIndex = Math.max(maxIndex,imageIndex);
        }
        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName().toLowerCase();
            if (name.equalsIgnoreCase("example.png")) continue;
            if (!name.endsWith(".png")) continue;
            String replacedName = name.toLowerCase().replaceAll(".png","");
            int imageIndex = indexedSkins.get(replacedName);
            for (ServerPlayerEntity player : players) {
                sendImageToClient(player, "snail_skin", imageIndex, maxIndex, file.toPath());
            }
        }
    }

    public static List<String> getAllSkins() {
        List<String> result = new ArrayList<>();
        File folder = new File("./config/lifeseries/wildlife/snailskins/");
        File[] files = folder.listFiles();
        if (files == null) return result;
        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName().toLowerCase();
            if (name.equalsIgnoreCase("example.png")) continue;
            if (!name.endsWith(".png")) continue;
            String replacedName = name.replaceAll(".png","");
            result.add(replacedName);
        }
        return result;
    }

    public static void sendStoredImages() {
        sendStoredImages(PlayerUtils.getAllPlayers());
    }

    public static void createConfig() {
        File folder = new File("./config/lifeseries/wildlife/snailskins/");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", folder);
                return;
            }
        }
        ResourceHandler handler = new ResourceHandler();

        Path modelResult = new File("./config/lifeseries/wildlife/snailskins/snail.bbmodel").toPath();
        handler.copyBundledSingleFile("/model/" + Main.MOD_ID + "/snail.bbmodel", modelResult);

        Path textureResult = new File("./config/lifeseries/wildlife/snailskins/example.png").toPath();
        handler.copyBundledSingleFile("/model/" + Main.MOD_ID + "/texture/example.png", textureResult);
    }
}
package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.resources.config.StringListConfig;
import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class Snails extends Wildcard {
    public static StringListConfig snailNameConfig;

    public static Map<UUID, Snail> snails = new HashMap<>();
    public static Map<UUID, String> snailNames = new HashMap<>();
    long ticks = 0;

    @Override
    public Wildcards getType() {
        return Wildcards.SNAILS;
    }

    @Override
    public void activate() {
        snails.clear();
        for (ServerPlayerEntity player : currentSeries.getAlivePlayers()) {
            spawnSnailFor(player);
        }
        loadSnailNames();
        if (!currentSession.statusStarted()) {
            OtherUtils.broadcastMessageToAdmins(Text.of("§7Use the §f'/snail ...'§7 command to modify snail names and to get info on how to change snail textures."));
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        snails.clear();
        killAllSnails();
        super.deactivate();
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks % 100 == 0) {
            for (ServerPlayerEntity player : currentSeries.getAlivePlayers()) {
                UUID playerUUID = player.getUuid();
                if (snails.containsKey(playerUUID)) {
                    Snail snail = snails.get(playerUUID);
                    if (snail == null || snail.isDead() || snail.isRemoved()) {
                        snails.remove(playerUUID);
                        spawnSnailFor(player);
                    }
                }
                else {
                    spawnSnailFor(player);
                }
            }
        }
    }

    public static void spawnSnailFor(ServerPlayerEntity player) {
        BlockPos pos = Snail.getBlockPosNearTarget(player, 20);
        if (pos == null) pos = player.getBlockPos().add(0,20,0);
        spawnSnailFor(player, pos);
    }

    public static void spawnSnailFor(ServerPlayerEntity player, BlockPos pos) {
        if (player == null || pos == null) return;
        Snail snail = MobRegistry.SNAIL.spawn(player.getServerWorld(), pos, SpawnReason.COMMAND);
        if (snail != null) {
            snail.setBoundPlayer(player);
            snail.updateSkin(player);
            snails.put(player.getUuid(), snail);
        }
    }

    public static void killAllSnails() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof Snail snail && !snail.fromTrivia) {
                        toKill.add(entity);
                    }

                if (entity instanceof PathFinder) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void reloadSnailNames() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.updateSnailName();
        }
    }

    public static void reloadSnailSkins() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.updateSkin(snail.getActualBoundPlayer());
        }
    }

    public static void setSnailName(ServerPlayerEntity player, String name) {
        snailNames.put(player.getUuid(), name);
        reloadSnailNames();
        saveSnailNames();
    }

    public static void resetSnailName(ServerPlayerEntity player) {
        snailNames.remove(player.getUuid());
        reloadSnailNames();
        saveSnailNames();
    }

    public static String getSnailName(ServerPlayerEntity player) {
        if (snailNames.containsKey(player.getUuid())) {
            return snailNames.get(player.getUuid());
        }
        return player.getNameForScoreboard()+"'s Snail";
    }

    public static void saveSnailNames() {
        if (snailNameConfig == null) loadConfig();
        List<String> names = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : snailNames.entrySet()) {
            names.add(entry.getKey().toString()+"_"+entry.getValue().replaceAll("_",""));
        }
        snailNameConfig.save(names);
    }

    public static void loadSnailNames() {
        if (snailNameConfig == null) loadConfig();
        HashMap<UUID, String> newNames = new HashMap<>();
        for (String entry : snailNameConfig.load()) {
            if (!entry.contains("_")) continue;
            String[] split = entry.split("_");
            if (split.length != 2) continue;
            try {
                UUID uuid = UUID.fromString(split[0]);
                newNames.put(uuid, split[1]);
            } catch(Exception ignored) {}
        }
        snailNames = newNames;
    }

    public static void loadConfig() {
        snailNameConfig = new StringListConfig("./config/lifeseries/main", "DO_NOT_MODIFY_wildlife_snailnames.properties");
    }
}
