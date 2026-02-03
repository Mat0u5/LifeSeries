package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSession;

//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
//?}
//? if <= 1.21
//import java.util.Optional;
//? if >= 1.21.2 {
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
//?}

public class Hunger extends Wildcard {
    private static final Random rnd = new Random();
    public static int SWITCH_DELAY = 36000;
    public static int shuffleVersion = 0;
    private static boolean shuffledBefore = false;
    private static int lastVersion = -1;
    private static Time timer = Time.zero();

    public static int HUNGER_EFFECT_LEVEL = 3;

    public static double EFFECT_CHANCE = 0.65;
    public static int AVG_EFFECT_DURATION = 10;
    public static double NUTRITION_CHANCE = 0.4;
    public static double SATURATION_CHANCE = 0.5;
    public static double SOUND_CHANCE = 0.01;

    //? if <= 1.20.3 {
    /*private static final List<MobEffect> effects = List.of(
    *///?} else {
    private static final List<Holder<MobEffect>> effects = List.of(
    //?}
            //? if <= 1.21.4 {
            /*MobEffects.MOVEMENT_SPEED
            ,MobEffects.MOVEMENT_SLOWDOWN
            ,MobEffects.DIG_SPEED
            ,MobEffects.DIG_SLOWDOWN
            ,MobEffects.DAMAGE_BOOST
            ,MobEffects.HEAL
            ,MobEffects.HARM
            ,MobEffects.JUMP
            ,MobEffects.CONFUSION
            ,MobEffects.DAMAGE_RESISTANCE
            *///?} else {
            MobEffects.SPEED
            ,MobEffects.SLOWNESS
            ,MobEffects.HASTE
            ,MobEffects.MINING_FATIGUE
            ,MobEffects.STRENGTH
            ,MobEffects.INSTANT_HEALTH
            ,MobEffects.INSTANT_DAMAGE
            ,MobEffects.JUMP_BOOST
            ,MobEffects.NAUSEA
            ,MobEffects.RESISTANCE
            //?}
            ,MobEffects.REGENERATION
            ,MobEffects.FIRE_RESISTANCE
            ,MobEffects.WATER_BREATHING
            ,MobEffects.INVISIBILITY
            ,MobEffects.BLINDNESS
            ,MobEffects.NIGHT_VISION
            ,MobEffects.WEAKNESS
            ,MobEffects.POISON
            ,MobEffects.WITHER
            ,MobEffects.HEALTH_BOOST
            ,MobEffects.ABSORPTION
            ,MobEffects.SATURATION
            ,MobEffects.GLOWING
            ,MobEffects.LEVITATION
            ,MobEffects.LUCK
            ,MobEffects.UNLUCK
            ,MobEffects.SLOW_FALLING
            ,MobEffects.CONDUIT_POWER
            ,MobEffects.DOLPHINS_GRACE
            ,MobEffects.HERO_OF_THE_VILLAGE
            ,MobEffects.DARKNESS
            //? if >= 1.21 {
            ,MobEffects.WIND_CHARGED
            ,MobEffects.WEAVING
            ,MobEffects.OOZING
            ,MobEffects.INFESTED
            //?}
    );

    //? if <= 1.20.3 {
    /*private static final List<MobEffect> levelLimit = List.of(
    *///?} else {
    private static final List<Holder<MobEffect>> levelLimit = List.of(
    //?}
            //? if <= 1.21.4 {
            /*MobEffects.DAMAGE_BOOST,
            MobEffects.HEAL,
            MobEffects.HARM,
            MobEffects.DAMAGE_RESISTANCE,
            *///?} else {
            MobEffects.STRENGTH,
            MobEffects.INSTANT_HEALTH,
            MobEffects.INSTANT_DAMAGE,
            MobEffects.RESISTANCE,
            //?}
            MobEffects.REGENERATION,
            MobEffects.WITHER,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION
    );

    //? if <= 1.20.3 {
    /*private static final List<MobEffect> durationLimit = List.of(
    *///?} else {
    private static final List<Holder<MobEffect>> durationLimit = List.of(
    //?}
            //? if <= 1.21.4 {
            /*MobEffects.HEAL,
            MobEffects.HARM,
            *///?} else {
            MobEffects.INSTANT_HEALTH,
            MobEffects.INSTANT_DAMAGE,
            //?}
            MobEffects.SATURATION
    );

    public static final List<Item> commonItems = Arrays.asList(
            Items.DIRT, Items.STONE, Items.COBBLESTONE, Items.GRAVEL, Items.SAND, Items.NETHERRACK,
            Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG, Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.CHERRY_LOG, Items.CRIMSON_STEM, Items.WARPED_STEM,
            Items.OAK_LEAVES, Items.SPRUCE_LEAVES, Items.BIRCH_LEAVES, Items.JUNGLE_LEAVES, Items.ACACIA_LEAVES, Items.DARK_OAK_LEAVES, Items.MANGROVE_LEAVES, Items.CHERRY_LEAVES, Items.NETHER_WART_BLOCK, Items.WARPED_WART_BLOCK,
            Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS, Items.JUNGLE_PLANKS, Items.ACACIA_PLANKS, Items.DARK_OAK_PLANKS, Items.MANGROVE_PLANKS, Items.CHERRY_PLANKS, Items.CRIMSON_HYPHAE, Items.WARPED_HYPHAE,
            Items.OAK_BUTTON, Items.SPRUCE_BUTTON, Items.BIRCH_BUTTON, Items.JUNGLE_BUTTON, Items.ACACIA_BUTTON, Items.DARK_OAK_BUTTON, Items.MANGROVE_BUTTON, Items.CHERRY_BUTTON, Items.CRIMSON_BUTTON, Items.WARPED_BUTTON,
            Items.PINK_PETALS, Items.IRON_NUGGET, Items.GOLD_NUGGET, Items.STICK, Items.STRING, Items.BONE_MEAL,
            Items.GRASS_BLOCK, Items.COARSE_DIRT, Items.SNOW_BLOCK, Items.DEEPSLATE, Items.CALCITE, Items.TUFF,
            Items.ANDESITE, Items.DIORITE, Items.GRANITE, Items.BASALT, Items.BLACKSTONE, Items.END_STONE,
            Items.SOUL_SAND, Items.SOUL_SOIL, Items.CRIMSON_NYLIUM, Items.WARPED_NYLIUM, Items.CACTUS, Items.SEA_PICKLE,
            Items.KELP, Items.DRIED_KELP_BLOCK, Items.WHEAT_SEEDS
            //? if >= 1.21.5 {
            ,Items.LEAF_LITTER
            //?}
    );

    public static List<String> nonEdibleStr = new ArrayList<>();
    public static List<Item> nonEdible = new ArrayList<>();

    @Override
    public Wildcards getType() {
        return Wildcards.HUNGER;
    }

    @Override
    public void tick() {
        if (currentSession.validTime() || currentSession.getRemainingTime().getTicks() > 6000) {
            int currentVersion = (int) Math.floor((double) currentSession.getPassedTime().getTicks() / (SWITCH_DELAY));
            if (lastVersion != currentVersion) {
                lastVersion = currentVersion;
                newFoodRules();
            }
        }
        timer.tick();
        if (timer.isMultipleOf(Time.seconds(1))) {
            for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
                if (!player.hasEffect(MobEffects.HUNGER)) {
                    addHunger(player);
                }
            }
        }
    }

    @Override
    public void deactivate() {
        shuffledBefore = false;
        TaskScheduler.scheduleTask(1, OtherUtils::reloadServerNoUpdate);
        TaskScheduler.scheduleTask(10, Hunger::updateInventories);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.removeEffect(MobEffects.HUNGER);
        }
        super.deactivate();
    }
    @Override
    public void activate() {
        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            shuffleVersion = rnd.nextInt(0,100);
            shuffledBefore = false;
            lastVersion = -1;
            super.activate();
        });
    }

    public static void newNonEdibleItems(String raw) {
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        nonEdible = new ArrayList<>();
        nonEdibleStr = new ArrayList<>();
        if (!raw.isEmpty()) {
            nonEdibleStr = new ArrayList<>(Arrays.asList(raw.split(",")));
        }
        for (String itemId : nonEdibleStr) {
            if (!itemId.contains(":")) itemId = "minecraft:" + itemId;

            try {
                var id = IdentifierHelper.parse(itemId);
                ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);

                // Check if the block exists in the registry
                //? if <= 1.21 {
                /*Item item = BuiltInRegistries.ITEM.get(key);
                *///?} else {
                Item item = BuiltInRegistries.ITEM.getValue(key);
                 //?}
                if (item != null) {
                    nonEdible.add(item);
                } else {
                    OtherUtils.throwError("[CONFIG] Invalid item: " + itemId);
                }
            } catch (Exception e) {
                OtherUtils.throwError("[CONFIG] Error parsing item ID: " + itemId);
            }
        }
        NetworkHandlerServer.sendUpdatePackets();
        TaskScheduler.scheduleTask(1, OtherUtils::reloadServerNoUpdate);
        TaskScheduler.scheduleTask(5, Hunger::updateInventories);
    }

    public void newFoodRules() {
        List<ServerPlayer> players = PlayerUtils.getAllFunctioningPlayers();
        SessionTranscript.newHungerRule();
        if (shuffledBefore) {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.NOTE_BLOCK_PLING.value());
            PlayerUtils.sendTitleWithSubtitleToPlayers(players, Component.empty(), Component.nullToEmpty("§7Food is about to be randomised..."), 0, 140, 0);
            TaskScheduler.scheduleTask(Time.seconds(2), WildcardManager::showDots);
            TaskScheduler.scheduleTask(Time.seconds(7), () -> {
                addHunger();
                updateInventories();
                PlayerUtils.playSoundToPlayers(players, SoundEvents.ELDER_GUARDIAN_CURSE, 0.2f, 1);
                shuffleVersion++;
            });
        }
        else {
            TaskScheduler.scheduleTask(10, Hunger::updateInventories);
            shuffleVersion++;
        }
        shuffledBefore = true;
        addHunger();
        TaskScheduler.scheduleTask(1, OtherUtils::reloadServerNoUpdate);
        NetworkHandlerServer.sendUpdatePackets();
    }

    public static void updateInventories() {
        PlayerUtils.getAllFunctioningPlayers().forEach(Hunger::updateInventory);
    }

    public static void updateInventory(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;

            //? if < 1.20.5 {
            /*ItemStack newItem = new ItemStack(stack.getItem(), stack.getCount());
            *///?} else {
            stack.set(DataComponents.FOOD, stack.getPrototype().get(DataComponents.FOOD));
            //? if >= 1.21.2 {
            stack.set(DataComponents.CONSUMABLE, stack.getPrototype().get(DataComponents.CONSUMABLE));
             //?}

            DataComponentPatch changes = stack.getComponentsPatch();
            ItemStack newItem = new ItemStack(stack.getItem(), stack.getCount());
            newItem.applyComponentsAndValidate(changes);
            //?}
            inventory.setItem(i, newItem);
        }

        PlayerUtils.updatePlayerInventory(player);
    }

    public static void addHunger() {
        PlayerUtils.getAllFunctioningPlayers().forEach(Hunger::addHunger);
    }
    public static void addHunger(ServerPlayer player) {
        if (player == null) return;
        if (player.isSpectator()) return;
        if (HUNGER_EFFECT_LEVEL <= 0) return;
        MobEffectInstance statusEffectInstance = new MobEffectInstance(MobEffects.HUNGER, -1, HUNGER_EFFECT_LEVEL-1, false, false, false);
        player.addEffect(statusEffectInstance);
    }

    public static void onUseItem(ServerPlayer player) {
        if (!player.hasEffect(MobEffects.HUNGER) && WildcardManager.isActiveWildcard(Wildcards.HUNGER)){
            addHunger(player);
        }
    }

    public static final List<Item> bannedFoodItems = List.of(
            Items.AIR, Items.ENDER_PEARL, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE
            //? if >= 1.21 {
            ,Items.WIND_CHARGE
            //?}
    );

    //? if >= 1.20.5 && <= 1.21 {
    /*public static void defaultFoodComponents(Item item, PatchedDataComponentMap components) {
        if (item == null) return;
        if (bannedFoodItems.contains(item)) return;
        //? if <= 1.20.5 {
        components.set(DataComponents.FOOD, new FoodProperties(0, 0, false, 1.6f, List.of()));
        //?} else {
        /^components.set(DataComponents.FOOD, new FoodProperties(0, 0, false, 1.6f, Optional.empty(), List.of()));
        ^///?}
    }
    *///?} else if > 1.21 {
    public static void defaultFoodComponents(Item item, PatchedDataComponentMap components) {
        if (item == null) return;
        if (bannedFoodItems.contains(item)) return;
        components.set(DataComponents.CONSUMABLE,
                new Consumable(Consumable.DEFAULT_CONSUME_SECONDS, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true, List.of())
        );
        components.set(DataComponents.FOOD, new FoodProperties(0, 0, false));
    }
    //?}

    //? if < 1.20.5 {
    /*public static void finishUsing(Item item, boolean isNormallyEdible, LivingEntity entity) {
    *///?} else {
    public static void finishUsing(Item item, DataComponentMap normalComponents, LivingEntity entity) {
    //?}
        if (!(entity instanceof ServerPlayer player)) return;
        if (item == null) return;
        if (bannedFoodItems.contains(item)) return;
        if (nonEdible.contains(item)) return;

        //? if >= 1.20.5 {
        boolean isNormallyEdible = normalComponents.has(DataComponents.FOOD);
        //?}

        int nutrition = 0;
        int saturation = 0;
        MobEffectInstance effect = null;

        if (isNormallyEdible) {
            effect = new MobEffectInstance(MobEffects.HUNGER, 3600, 7, false, false, false);
        }
        else {
            //Random effect
            int hash = getHash(item);
            Random random = new Random(hash); // Use hash as seed for consistent results

            if (random.nextDouble() < EFFECT_CHANCE) {
                int amplifier = random.nextInt(5); // 0 -> 4
                int duration = (int) Math.ceil(((random.nextInt(20) + 1) * AVG_EFFECT_DURATION) / 10.0);
                //? if <= 1.20.3 {
                /*MobEffect registryEntryEffect = effects.get(random.nextInt(effects.size()));
                if (levelLimit.contains(registryEntryEffect) || commonItems.contains(item)) {
                    amplifier = 0;
                }
                if (durationLimit.contains(registryEntryEffect)) {
                    duration = 1;
                }
                effect = new MobEffectInstance(registryEntryEffect, duration*20, amplifier);
                *///?} else {
                Holder<MobEffect> registryEntryEffect = effects.get(random.nextInt(effects.size()));
                if (levelLimit.contains(registryEntryEffect) || commonItems.contains(item)) {
                    amplifier = 0;
                }
                if (durationLimit.contains(registryEntryEffect)) {
                    duration = 1;
                }
                effect = new MobEffectInstance(registryEntryEffect, duration*20, amplifier);
                //?}
            }

            // Random nutrition and saturation
            if (!commonItems.contains(item)) {
                if (random.nextDouble() < NUTRITION_CHANCE) {
                    nutrition = random.nextInt(8) + 1; // 1 -> 8
                }
                if (random.nextDouble() < SATURATION_CHANCE) {
                    saturation = random.nextInt(4) + 1; // 1 -> 4
                    if (saturation > nutrition) saturation = nutrition;
                }
            }

            // Random Sound
            if (!commonItems.contains(item)) {
                if (random.nextDouble() < SOUND_CHANCE) {
                    List<SoundEvent> allSounds = new ArrayList<>();
                    for (SoundEvent sound : BuiltInRegistries.SOUND_EVENT.stream().toList()) {
                        //? if <= 1.21 {
                        /*if (sound.getLocation().getPath().startsWith("entity.")) {
                            allSounds.add(sound);
                        }
                        *///?} else {
                        if (sound.location().getPath().startsWith("entity.")) {
                            allSounds.add(sound);
                        }
                        //?}
                    }
                    if (!allSounds.isEmpty()) {
                        SoundEvent sound = allSounds.get(random.nextInt(allSounds.size()));
                        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 0.5f, 1f);
                    }
                }
            }
        }

        player.getFoodData().eat(nutrition, saturation);
        if (effect != null) {
            player.addEffect(effect);
        }
    }

    private static int getHash(Item item) {
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
        return Math.abs((itemId.hashCode() + shuffleVersion) * 31);
    }
}