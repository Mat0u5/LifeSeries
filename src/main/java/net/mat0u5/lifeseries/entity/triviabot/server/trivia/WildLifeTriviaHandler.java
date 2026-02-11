package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotPathfinding;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.server;

//? if <= 1.20.3 {
/*import net.minecraft.core.particles.DustParticleOptions;
*///?} else {
import net.minecraft.core.particles.ColorParticleOption;
//?}

//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponents;
//?}

//? if > 1.21.9
import net.minecraft.world.entity.EntityReference;

//? if <= 1.21.9 {
/*import net.minecraft.world.entity.animal.Bee;
import org.apache.http.util.EntityUtils;
import org.joml.Vector3f;
*///?} else {
import net.minecraft.world.entity.animal.bee.Bee;
//?}

public class WildLifeTriviaHandler extends TriviaHandler {
    public WildLifeTriviaHandler(TriviaBot bot) {
        super(bot);
    }

    public static ItemSpawner itemSpawner;
    public int snailTransformation = 0;
    public static int EASY_TIME = 180;
    public static int NORMAL_TIME = 240;
    public static int HARD_TIME = 300;

    public void tick() {
        super.tick();
        if (bot.ranOutOfTime()) {
            snailTransformation++;
        }

        if (bot.tickCount % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
        }

        if (bot.submittedAnswer()) {
            if (bot.answeredRight()) {
                if (bot.getAnalyzingTime() < -80) {
                    bot.setLeaving(true);
                    if (bot.isPassenger()) bot.removeVehicle();
                    bot.noPhysics = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+80) / (20.0f)));
                    bot.setDeltaMovement(0,velocity,0);
                    if (bot.getAnalyzingTime() < -200) bot.serverData.despawn();
                }
            }
            else {
                if (bot.getAnalyzingTime() < -100) {
                    if (bot.isPassenger()) bot.removeVehicle();
                    bot.noPhysics = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+100) / (20.0f)));
                    bot.setDeltaMovement(0,velocity,0);
                    if (bot.getAnalyzingTime() < -200) bot.serverData.despawn();
                }
            }
        }
        else {
            bot.serverData.handleHighVelocity();
            if (bot.interactedWith() && getRemainingTicks() <= 0) {
                if (!bot.ranOutOfTime()) {
                    ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
                    if (boundPlayer != null) {
                        SimplePackets.RESET_TRIVIA.target(boundPlayer).sendToClient();
                    }
                }
                bot.setRanOutOfTime(true);
            }
            if (snailTransformation > 66) {
                transformIntoSnail();
            }
        }
    }

    public InteractionResult interactMob(Player player, InteractionHand hand) {
        if (bot.level().isClientSide()) return InteractionResult.SUCCESS;
        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        if (boundPlayer == null) return InteractionResult.PASS;
        if (boundPlayer.getUUID() != player.getUUID()) return InteractionResult.PASS;
        if (bot.submittedAnswer()) return InteractionResult.PASS;
        if (bot.interactedWith() && getRemainingTicks() <= 0) return InteractionResult.PASS;

        startTrivia(boundPlayer);

        return InteractionResult.SUCCESS;
    }
    public Tuple<Integer, TriviaQuestion> generateTrivia(ServerPlayer boundPlayer) {
        return TriviaWildcard.getTriviaQuestion(boundPlayer);
    }

    public void setTimeBasedOnDifficulty(int difficulty) {
        timeToComplete = difficulty * 60 + 120;
        if (difficulty == 1) timeToComplete = EASY_TIME;
        if (difficulty == 2) timeToComplete = NORMAL_TIME;
        if (difficulty == 3) timeToComplete = HARD_TIME;
    }

    public void transformIntoSnail() {
        if (bot.serverData.getBoundPlayer() != null) {
            Snail triviaSnail = LevelUtils.spawnEntity(MobRegistry.SNAIL, (ServerLevel) bot.level(), bot.blockPosition());
            if (triviaSnail != null) {
                triviaSnail.serverData.setBoundPlayer(bot.serverData.getBoundPlayer());
                triviaSnail.serverData.setFromTrivia();
                //? if <= 1.20.3 {
                /*triviaSnail.playSound(SoundEvents.GENERIC_EXPLODE, 0.5f, 2);
                *///?} else {
                triviaSnail.playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.5f, 2);
                //?}
                ServerLevel level = (ServerLevel) triviaSnail.level();
                Vec3 pos = bot.position();
                level.sendParticles(
                        ParticleTypes.EXPLOSION,
                        pos.x(), pos.y(), pos.z(),
                        10, 0.5, 0.5, 0.5, 0.5
                );
                TriviaWildcard.snails.put(bot.serverData.getBoundPlayer().getUUID(), triviaSnail);
            }
        }
        bot.serverData.despawn();
    }

    public boolean handleAnswer(int answer) {
        if (super.handleAnswer(answer)) {
            bot.setAnalyzingTime(42);
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), bot,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_analyzing")),
                    SoundSource.NEUTRAL, 1f, 1);
            return true;
        }
        return false;
    }

    public void answeredCorrect() {
        super.answeredCorrect();
        TaskScheduler.scheduleTask(145, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(170, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(198, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(213, this::blessPlayer);
        TaskScheduler.scheduleTask(72, () -> {
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), bot,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_correct")),
                    SoundSource.NEUTRAL, 1f, 1);
        });
    }

    public void answeredIncorrect() {
        super.answeredIncorrect();
        TaskScheduler.scheduleTask(210, this::cursePlayer);
        TaskScheduler.scheduleTask(72, () -> {
            PlayerUtils.playSoundWithSourceToPlayers(
                    PlayerUtils.getAllPlayers(), bot,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_incorrect")),
                    SoundSource.NEUTRAL, 1f, 1);
        });
    }

    public void cursePlayer() {
        ServerPlayer player = bot.serverData.getBoundPlayer();
        if (player == null) return;
        player.ls$playNotifySound(SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 0.2f, 1f);
        ServerLevel level = (ServerLevel) bot.level();
        Vec3 pos = bot.position();

        //? if <= 1.20.3 {
        /*level.sendParticles(
                new DustParticleOptions(new Vector3f(166, 17, 17), 1.0F),
                pos.x(), pos.y()+1, pos.z(),
                40, 0.1, 0.25, 0.1, 0.035
        );
        *///?} else {
        level.sendParticles(
                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFFa61111),
                pos.x(), pos.y()+1, pos.z(),
                40, 0.1, 0.25, 0.1, 0.035
        );
        //?}
        int minCurseNum = 0;
        //? if < 1.21 {
        /*minCurseNum = 1;
        *///?}
        int numOfCurses = 9;
        //? if <= 1.20.3 {
        /*numOfCurses--;
        *///?}
        if (CompatibilityManager.voicechatLoaded() && VoicechatMain.isConnectedToSVC(player.getUUID())) numOfCurses = 10;

        Integer punishmentWeight = player.ls$getLives();
        if (punishmentWeight == null) punishmentWeight = 1;
        if (difficulty == 1) punishmentWeight++;
        if (difficulty == 3) punishmentWeight--;
        punishmentWeight = OtherUtils.clamp(punishmentWeight, 1, 4);

        WeightedRandomizer randomizer = new WeightedRandomizer();
        int curse = randomizer.getWeightedRandom(minCurseNum, numOfCurses, punishmentWeight, 4, 1.5);

        //? if <= 1.20.3 {
        /*if (curse >= 4) {
            curse++;
        }
        *///?}

        if (numOfCurses == 9 && curse >= 6) {
            curse++;
        }

        switch (curse) {
            default:
            //? if >= 1.21 {
            case 0:
                curseInfestation(player);
                break;
            //?}
            case 1:
                curseSlipperyGround(player);
                break;
            case 2:
                curseHunger(player);
                break;
            case 3:
                curseBeeswarm(player, bot.blockPosition());
                break;
            //? if > 1.20.3 {
            case 4:
                curseGigantification(player);
                break;
            //?}
            case 5:
                curseMoonjump(player);
                break;
            case 6:
                curseRoboticVoice(player);
                break;
            case 7:
                curseBindingArmor(player);
                break;
            case 8:
                curseRavager(player, bot.blockPosition());
                break;
            case 9:
                curseHearts(player);
                break;
        }
    }

    //? if <= 1.20.3 {
    /*private static final List<MobEffect> blessEffects = List.of(
    *///?} else {
    private static final List<Holder<MobEffect>> blessEffects = List.of(
    //?}
            //? if <= 1.21.4 {
            /*MobEffects.MOVEMENT_SPEED,
            MobEffects.DIG_SPEED,
            MobEffects.DAMAGE_BOOST,
            MobEffects.JUMP,
            MobEffects.DAMAGE_RESISTANCE,
            *///?} else {
            MobEffects.SPEED,
            MobEffects.HASTE,
            MobEffects.STRENGTH,
            MobEffects.JUMP_BOOST,
            MobEffects.RESISTANCE,
            //?}

            MobEffects.REGENERATION,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.NIGHT_VISION,
            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION
    );
    public void blessPlayer() {
        ServerPlayer player = bot.serverData.getBoundPlayer();
        if (player == null) return;
        player.sendSystemMessage(Component.empty());
        for (int i = 0; i < 3; i++) {
            int attempts = 0;
            //? if <= 1.20.3 {
            /*MobEffect effect = null;
            *///?} else {
            Holder<MobEffect> effect = null;
             //?}
            while (effect == null && attempts < 50) {
                attempts++;
                //? if <= 1.20.3 {
                /*MobEffect pickedEffect = blessEffects.get(player.getRandom().nextInt(blessEffects.size()));
                *///?} else {
                Holder<MobEffect> pickedEffect = blessEffects.get(player.getRandom().nextInt(blessEffects.size()));
                //?}
                if (blacklist != null && blacklist.getBannedEffects().contains(pickedEffect)) {
                    continue;
                }
                effect = pickedEffect;
            }
            if (effect == null) continue;
            int amplifier;
            //? if <= 1.21.4 {
            /*if (effect == MobEffects.FIRE_RESISTANCE || effect == MobEffects.WATER_BREATHING || effect == MobEffects.NIGHT_VISION ||
                    effect == MobEffects.REGENERATION || effect == MobEffects.DAMAGE_BOOST || effect == MobEffects.HEALTH_BOOST || effect == MobEffects.DAMAGE_RESISTANCE) {
                amplifier = 0;
            }
            *///?} else {
            if (effect == MobEffects.FIRE_RESISTANCE || effect == MobEffects.WATER_BREATHING || effect == MobEffects.NIGHT_VISION ||
                    effect == MobEffects.REGENERATION || effect == MobEffects.STRENGTH || effect == MobEffects.HEALTH_BOOST || effect == MobEffects.RESISTANCE) {
                amplifier = 0;
            }
            //?}
            else {
                amplifier = player.getRandom().nextInt(4);
            }
            if (Wildcard.isFinale()) {
                player.addEffect(new MobEffectInstance(effect, 12000, amplifier));
            }
            else {
                player.addEffect(new MobEffectInstance(effect, 24000, amplifier));
            }

            String romanNumeral = TextUtils.toRomanNumeral(amplifier + 1);
            //? if <= 1.20.3 {
            /*Component effectName = effect.getDisplayName();
            *///?} else {
            Component effectName = Component.translatable(effect.value().getDescriptionId());
            //?}
            player.sendSystemMessage(TextUtils.formatLoosely(" §a§l+ §7{}§6 {}", effectName, romanNumeral));
        }
        player.sendSystemMessage(Component.empty());
    }

    public void spawnItemForPlayer() {
        if (bot.level().isClientSide()) return;
        if (itemSpawner == null) return;
        if (bot.serverData.getBoundPlayer() == null) return;
        Vec3 playerPos = bot.serverData.getBoundPlayer().position();
        Vec3 pos = bot.position().add(0,1,0);
        Vec3 relativeTargetPos = new Vec3(
                playerPos.x() - pos.x(),
                0,
                playerPos.z() - pos.z()
        );
        Vec3 vector = Vec3.ZERO;
        if (relativeTargetPos.lengthSqr() > 0.0001) {
            vector = relativeTargetPos.normalize().scale(0.3).add(0,0.1,0);
        }

        List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerLevel) bot.level(), bot.serverData.getBoundPlayer(), IdentifierHelper.of("lifeseriesdynamic", "trivia_reward_loottable"), false);
        if (!lootTableItems.isEmpty()) {
            for (ItemStack item : lootTableItems) {
                ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, item, bot.serverData.getBoundPlayer(), vector);
            }
        }
        else {
            ItemStack randomItem = itemSpawner.getRandomItem();
            ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
        }
    }

    public static void initializeItemSpawner() {
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.TRIDENT), 10);
        itemSpawner.addItem(new ItemStack(Items.POWERED_RAIL, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.DIAMOND, 4), 20);
        itemSpawner.addItem(new ItemStack(Items.CREEPER_SPAWN_EGG), 10);
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_CARROT, 8), 10);
        //? if >= 1.21 {
        itemSpawner.addItem(new ItemStack(Items.WIND_CHARGE, 16), 10);
        //?}
        itemSpawner.addItem(new ItemStack(Items.SCULK_SHRIEKER, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SENSOR, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.TNT, 8), 20);
        itemSpawner.addItem(new ItemStack(Items.COBWEB, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.OBSIDIAN, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.PUFFERFISH_BUCKET), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_CHESTPLATE), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_LEGGINGS), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_BOOTS), 10);
        itemSpawner.addItem(new ItemStack(Items.ARROW, 64), 10);
        itemSpawner.addItem(new ItemStack(Items.IRON_BLOCK, 2), 10);

        //? if >= 1.21 {
        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
        mace.setDamageValue(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 5);
        //?}

        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
        itemSpawner.addItem(endCrystal, 10);

        //? if >= 1.20.5 {
        ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
        patat.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§6§l§nThe Sacred Patat"));
        ItemStackUtils.addLoreToItemStack(patat,
                List.of(Component.nullToEmpty("§5§oEating bot might help you. Or maybe not..."))
        );
        itemSpawner.addItem(patat, 1);
        //?}
    }

    /*
        Curses
     */

    public static void curseHunger(ServerPlayer player) {
        MobEffectInstance statusEffectInstance = new MobEffectInstance(MobEffects.HUNGER, 18000, 2);
        player.addEffect(statusEffectInstance);
    }

    public static void curseRavager(ServerPlayer player, BlockPos pos) {
        BlockPos spawnPos = TriviaBotPathfinding.getBlockPosNearPlayer(player, pos, 5);
        LevelUtils.spawnEntity(EntityType.RAVAGER, player.ls$getServerLevel(), spawnPos);
    }

    //? if >= 1.21 {
    public static void curseInfestation(ServerPlayer player) {
        MobEffectInstance statusEffectInstance = new MobEffectInstance(MobEffects.INFESTED, 18000, 0);
        player.addEffect(statusEffectInstance);
    }
    //?}

    public static final List<UUID> cursedGigantificationPlayers = new ArrayList<>();
    //? if > 1.20.3 {
    public static void curseGigantification(ServerPlayer player) {
        cursedGigantificationPlayers.add(player.getUUID());
        SizeShifting.setPlayerSizeUnchecked(player, 4);
    }
    //?}

    public static final List<UUID> cursedSliding = new ArrayList<>();
    public static void curseSlipperyGround(ServerPlayer player) {
        cursedSliding.add(player.getUUID());
    }

    public static void curseBindingArmor(ServerPlayer player) {
        for (ItemStack item : PlayerUtils.getArmorItems(player)) {
            ItemStackUtils.spawnItemForPlayer(player.ls$getServerLevel(), player.position(), item.copy(), player);
        }
        ItemStack head = Items.LEATHER_HELMET.getDefaultInstance();
        ItemStack chest = Items.LEATHER_CHESTPLATE.getDefaultInstance();
        ItemStack legs = Items.LEATHER_LEGGINGS.getDefaultInstance();
        ItemStack boots = Items.LEATHER_BOOTS.getDefaultInstance();
        //? if <= 1.20.5 {
        /*head.enchant(Enchantments.BINDING_CURSE, 1);
        chest.enchant(Enchantments.BINDING_CURSE, 1);
        legs.enchant (Enchantments.BINDING_CURSE, 1);
        boots.enchant(Enchantments.BINDING_CURSE, 1);
        *///?} else {
        head.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        chest.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        legs.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        boots.enchant(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        //?}
        ItemStackUtils.setCustomComponentBoolean(head, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(chest, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(legs, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(boots, "IgnoreBlacklist", true);
        player.setItemSlot(EquipmentSlot.HEAD, head);
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        player.setItemSlot(EquipmentSlot.LEGS, legs);
        player.setItemSlot(EquipmentSlot.FEET, boots);
        player.getInventory().setChanged();
    }

    public static final List<UUID> cursedHeartPlayers = new ArrayList<>();
    public static void curseHearts(ServerPlayer player) {
        cursedHeartPlayers.add(player.getUUID());
        double newHealth = Math.max(player.getMaxHealth()-7, 1);
        AttributeUtils.setMaxPlayerHealth(player, newHealth);
    }

    public static final List<UUID> cursedMoonJumpPlayers = new ArrayList<>();
    public static void curseMoonjump(ServerPlayer player) {
        cursedMoonJumpPlayers.add(player.getUUID());
        //? if >= 1.21.5 {
        AttributeUtils.setJumpStrength(player, 0.85);
        //?} else {
        /*AttributeUtils.setJumpStrength(player, 0.76);
        *///?}
    }

    public static void curseBeeswarm(ServerPlayer player, BlockPos pos) {
        BlockPos spawnPos = TriviaBotPathfinding.getBlockPosNearPlayer(player, pos, 1);
        Bee bee1 = LevelUtils.spawnEntity(EntityType.BEE, player.ls$getServerLevel(), spawnPos);
        Bee bee2 = LevelUtils.spawnEntity(EntityType.BEE, player.ls$getServerLevel(), spawnPos);
        Bee bee3 = LevelUtils.spawnEntity(EntityType.BEE, player.ls$getServerLevel(), spawnPos);
        Bee bee4 = LevelUtils.spawnEntity(EntityType.BEE, player.ls$getServerLevel(), spawnPos);
        Bee bee5 = LevelUtils.spawnEntity(EntityType.BEE, player.ls$getServerLevel(), spawnPos);
        //? if <= 1.21.9 {
        /*if (bee1 != null) bee1.setPersistentAngerTarget(player.getUUID());
        if (bee2 != null) bee2.setPersistentAngerTarget(player.getUUID());
        if (bee3 != null) bee3.setPersistentAngerTarget(player.getUUID());
        if (bee4 != null) bee4.setPersistentAngerTarget(player.getUUID());
        if (bee5 != null) bee5.setPersistentAngerTarget(player.getUUID());
        if (bee1 != null) bee1.setRemainingPersistentAngerTime(1000000);
        if (bee2 != null) bee2.setRemainingPersistentAngerTime(1000000);
        if (bee3 != null) bee3.setRemainingPersistentAngerTime(1000000);
        if (bee4 != null) bee4.setRemainingPersistentAngerTime(1000000);
        if (bee5 != null) bee5.setRemainingPersistentAngerTime(1000000);
        *///?} else {
        if (bee1 != null) bee1.setPersistentAngerTarget(EntityReference.of(player.getUUID()));
        if (bee2 != null) bee2.setPersistentAngerTarget(EntityReference.of(player.getUUID()));
        if (bee3 != null) bee3.setPersistentAngerTarget(EntityReference.of(player.getUUID()));
        if (bee4 != null) bee4.setPersistentAngerTarget(EntityReference.of(player.getUUID()));
        if (bee5 != null) bee5.setPersistentAngerTarget(EntityReference.of(player.getUUID()));
        if (bee1 != null) bee1.setPersistentAngerEndTime(bee1.getAge() + 1000000);
        if (bee2 != null) bee2.setPersistentAngerEndTime(bee2.getAge() + 1000000);
        if (bee3 != null) bee3.setPersistentAngerEndTime(bee3.getAge() + 1000000);
        if (bee4 != null) bee4.setPersistentAngerEndTime(bee4.getAge() + 1000000);
        if (bee5 != null) bee5.setPersistentAngerEndTime(bee5.getAge() + 1000000);
        //?}
    }

    public static final List<UUID> cursedRoboticVoicePlayers = new ArrayList<>();
    public static void curseRoboticVoice(ServerPlayer player) {
        cursedRoboticVoicePlayers.add(player.getUUID());
    }
}
