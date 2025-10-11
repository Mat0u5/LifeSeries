package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.compatibilities.DependencyManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.WeightedRandomizer;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;
//? if <= 1.21.6
import net.minecraft.particle.EntityEffectParticleEffect;
//? if >= 1.21.9
/*import net.minecraft.particle.TintedParticleEffect;*/

public class TriviaHandler {
    private TriviaBot bot;
    public TriviaHandler(TriviaBot bot) {
        this.bot = bot;
    }

    public static ItemSpawner itemSpawner;
    public int difficulty = 0;
    public long interactedAt = 0;
    public int timeToComplete = 0;
    public TriviaQuestion question;


    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (bot.getBotWorld().isClient()) return ActionResult.PASS;
        ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
        if (boundPlayer == null) return ActionResult.PASS;
        if (boundPlayer.getUuid() != player.getUuid()) return ActionResult.PASS;
        if (bot.submittedAnswer()) return ActionResult.PASS;
        if (bot.interactedWith() && getRemainingTime() <= 0) return ActionResult.PASS;

        if (!bot.interactedWith() || question == null) {
            interactedAt = System.currentTimeMillis();
            difficulty = 1+bot.getRandom().nextInt(3);
            timeToComplete = difficulty * 60 + 120;
            if (difficulty == 1) timeToComplete = TriviaBot.EASY_TIME;
            if (difficulty == 2) timeToComplete = TriviaBot.NORMAL_TIME;
            if (difficulty == 3) timeToComplete = TriviaBot.HARD_TIME;
            question = TriviaWildcard.getTriviaQuestion(difficulty);
        }
        NetworkHandlerServer.sendTriviaPacket(boundPlayer, question.getQuestion(), difficulty, interactedAt, timeToComplete, question.getAnswers());
        bot.setInteractedWith(true);

        return ActionResult.PASS;
    }

    public void transformIntoSnail() {
        if (bot.serverData.getBoundPlayer() != null) {
            Snail triviaSnail = MobRegistry.SNAIL.spawn((ServerWorld) bot.getBotWorld(), bot.getBlockPos(), SpawnReason.COMMAND);
            if (triviaSnail != null) {
                triviaSnail.serverData.setBoundPlayer(bot.serverData.getBoundPlayer());
                triviaSnail.serverData.setFromTrivia();
                triviaSnail.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 0.5f, 2);
                ServerWorld world = (ServerWorld) triviaSnail.getSnailWorld();
                Vec3d pos = WorldUtils.getEntityPos(bot);
                world.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        pos.getX(), pos.getY(), pos.getZ(),
                        10, 0.5, 0.5, 0.5, 0.5
                );
                TriviaWildcard.snails.put(bot.serverData.getBoundPlayer().getUuid(), triviaSnail);
            }
        }
        bot.serverData.despawn();
    }

    public int getRemainingTime() {
        int timeSinceStart = (int) Math.ceil((System.currentTimeMillis() - interactedAt) / 1000.0);
        return timeToComplete - timeSinceStart;
    }

    public long getRemainingTimeMs() {
        long timeSinceStart = System.currentTimeMillis() - interactedAt;
        return (timeToComplete * 1000L) - timeSinceStart;
    }

    public void handleAnswer(int answer) {
        if (bot.getBotWorld().isClient()) return;
        if (bot.submittedAnswer()) return;
        bot.setSubmittedAnswer(true);
        bot.setAnalyzingTime(42);
        PlayerUtils.playSoundWithSourceToPlayers(
                PlayerUtils.getAllPlayers(), bot,
                SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_analyzing")),
                SoundCategory.NEUTRAL, 1f, 1);
        if (answer == question.getCorrectAnswerIndex()) {
            answeredCorrect();
            TaskScheduler.scheduleTask(72, () -> {
                PlayerUtils.playSoundWithSourceToPlayers(
                        PlayerUtils.getAllPlayers(), bot,
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_correct")),
                        SoundCategory.NEUTRAL, 1f, 1);
            });
        }
        else {
            answeredIncorrect();
            TaskScheduler.scheduleTask(72, () -> {
                PlayerUtils.playSoundWithSourceToPlayers(
                        PlayerUtils.getAllPlayers(), bot,
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_incorrect")),
                        SoundCategory.NEUTRAL, 1f, 1);
            });
        }
    }

    public void answeredCorrect() {
        bot.setAnsweredRight(true);
        TaskScheduler.scheduleTask(145, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(170, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(198, this::spawnItemForPlayer);
        TaskScheduler.scheduleTask(213, this::blessPlayer);
    }

    public void answeredIncorrect() {
        bot.setAnsweredRight(false);
        TaskScheduler.scheduleTask(210, this::cursePlayer);
    }

    public void cursePlayer() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player == null) return;
        player.playSoundToPlayer(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 0.2f, 1f);
        ServerWorld world = (ServerWorld) bot.getBotWorld();
        Vec3d pos = WorldUtils.getEntityPos(bot);

        //? if <= 1.21.6 {
        world.spawnParticles(
                EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0xFFa61111),
                pos.getX(), pos.getY()+1, pos.getZ(),
                40, 0.1, 0.25, 0.1, 0.035
        );
        //?} else {
        /*world.spawnParticles(
                TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0xFFa61111),
                pos.getX(), pos.getY()+1, pos.getZ(),
                40, 0.1, 0.25, 0.1, 0.035
        );
        *///?}
        int numOfCurses = 9;
        if (DependencyManager.voicechatLoaded() && VoicechatMain.isConnectedToSVC(player.getUuid())) numOfCurses = 10;

        Integer punishmentWeight = livesManager.getPlayerLives(player);
        if (punishmentWeight == null) punishmentWeight = 1;
        if (difficulty == 1) punishmentWeight++;
        if (difficulty == 3) punishmentWeight--;
        punishmentWeight = Math.clamp(punishmentWeight, 1, 4);

        WeightedRandomizer randomizer = new WeightedRandomizer();
        int curse = randomizer.getWeightedRandom(0, numOfCurses, punishmentWeight, 4, 1.5);

        if (numOfCurses == 9 && curse >= 6) {
            curse++;
        }

        switch (curse) {
            default:
            case 0:
                curseInfestation(player);
                break;
            case 1:
                curseSlipperyGround(player);
                break;
            case 2:
                curseHunger(player);
                break;
            case 3:
                curseBeeswarm(player);
                break;
            case 4:
                curseGigantification(player);
                break;
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
                curseRavager(player);
                break;
            case 9:
                curseHearts(player);
                break;
        }
    }

    private static final List<RegistryEntry<StatusEffect>> blessEffects = List.of(
            StatusEffects.SPEED,
            StatusEffects.HASTE,
            StatusEffects.STRENGTH,
            StatusEffects.JUMP_BOOST,
            StatusEffects.REGENERATION,
            StatusEffects.RESISTANCE,
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.WATER_BREATHING,
            StatusEffects.NIGHT_VISION,
            StatusEffects.HEALTH_BOOST,
            StatusEffects.ABSORPTION
    );
    public void blessPlayer() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player == null) return;
        player.sendMessage(Text.empty());
        for (int i = 0; i < 3; i++) {
            RegistryEntry<StatusEffect> effect = blessEffects.get(player.getRandom().nextInt(blessEffects.size()));
            int amplifier;
            if (effect == StatusEffects.FIRE_RESISTANCE || effect == StatusEffects.WATER_BREATHING || effect == StatusEffects.NIGHT_VISION ||
                    effect == StatusEffects.REGENERATION || effect == StatusEffects.STRENGTH || effect == StatusEffects.HEALTH_BOOST || effect == StatusEffects.RESISTANCE) {
                amplifier = 0;
            }
            else {
                amplifier = player.getRandom().nextInt(4);
            }
            if (WildcardManager.isActiveWildcard(Wildcards.CALLBACK)) {
                player.addStatusEffect(new StatusEffectInstance(effect, 12000, amplifier));
            }
            else {
                player.addStatusEffect(new StatusEffectInstance(effect, 24000, amplifier));
            }

            String romanNumeral = TextUtils.toRomanNumeral(amplifier + 1);
            Text effectName = Text.translatable(effect.value().getTranslationKey());
            player.sendMessage(TextUtils.formatLoosely(" §a§l+ §7{}§6 {}", effectName, romanNumeral));
        }
        player.sendMessage(Text.empty());
    }

    public void spawnItemForPlayer() {
        if (bot.getBotWorld().isClient()) return;
        if (itemSpawner == null) return;
        if (bot.serverData.getBoundPlayer() == null) return;
        Vec3d playerPos = WorldUtils.getEntityPos(bot.serverData.getBoundPlayer());
        Vec3d pos = WorldUtils.getEntityPos(bot).add(0,1,0);
        Vec3d relativeTargetPos = new Vec3d(
                playerPos.getX() - pos.getX(),
                0,
                playerPos.getZ() - pos.getZ()
        );
        Vec3d vector = Vec3d.ZERO;
        if (relativeTargetPos.lengthSquared() > 0.0001) {
            vector = relativeTargetPos.normalize().multiply(0.3).add(0,0.1,0);
        }

        List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerWorld) bot.getBotWorld(), bot.serverData.getBoundPlayer(), Identifier.of("lifeseriesdynamic", "trivia_reward_loottable"));
        if (!lootTableItems.isEmpty()) {
            for (ItemStack item : lootTableItems) {
                ItemStackUtils.spawnItemForPlayerWithVelocity((ServerWorld) bot.getBotWorld(), pos, item, bot.serverData.getBoundPlayer(), vector);
            }
        }
        else {
            ItemStack randomItem = itemSpawner.getRandomItem();
            ItemStackUtils.spawnItemForPlayerWithVelocity((ServerWorld) bot.getBotWorld(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
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
        itemSpawner.addItem(new ItemStack(Items.WIND_CHARGE, 16), 10);
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

        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
        mace.setDamage(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 5);

        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        ItemStackUtils.setCustomComponentBoolean(endCrystal, "IgnoreBlacklist", true);
        itemSpawner.addItem(endCrystal, 10);

        ItemStack patat = new ItemStack(Items.POISONOUS_POTATO);
        patat.set(DataComponentTypes.CUSTOM_NAME, Text.of("§6§l§nThe Sacred Patat"));
        ItemStackUtils.addLoreToItemStack(patat,
                List.of(Text.of("§5§oEating bot might help you. Or maybe not..."))
        );
        itemSpawner.addItem(patat, 1);
    }

    /*
        Curses
     */

    public void curseHunger(ServerPlayerEntity player) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.HUNGER, 18000, 2);
        player.addStatusEffect(statusEffectInstance);
    }

    public void curseRavager(ServerPlayerEntity player) {
        BlockPos spawnPos = TriviaBotPathfinding.getBlockPosNearPlayer(player, bot.getBlockPos(), 5);
        EntityType.RAVAGER.spawn(PlayerUtils.getServerWorld(player), spawnPos, SpawnReason.COMMAND);
    }

    public void curseInfestation(ServerPlayerEntity player) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.INFESTED, 18000, 0);
        player.addStatusEffect(statusEffectInstance);
    }

    public static final List<UUID> cursedGigantificationPlayers = new ArrayList<>();
    public void curseGigantification(ServerPlayerEntity player) {
        cursedGigantificationPlayers.add(player.getUuid());
        SizeShifting.setPlayerSizeUnchecked(player, 4);
    }

    public static final List<UUID> cursedSliding = new ArrayList<>();
    public void curseSlipperyGround(ServerPlayerEntity player) {
        cursedSliding.add(player.getUuid());
    }

    public void curseBindingArmor(ServerPlayerEntity player) {
        for (ItemStack item : PlayerUtils.getArmorItems(player)) {
            ItemStackUtils.spawnItemForPlayer(PlayerUtils.getServerWorld(player), WorldUtils.getEntityPos(player), item.copy(), player);
        }
        ItemStack head = Items.LEATHER_HELMET.getDefaultStack();
        ItemStack chest = Items.LEATHER_CHESTPLATE.getDefaultStack();
        ItemStack legs = Items.LEATHER_LEGGINGS.getDefaultStack();
        ItemStack boots = Items.LEATHER_BOOTS.getDefaultStack();
        head.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        chest.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        legs.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        boots.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
        ItemStackUtils.setCustomComponentBoolean(head, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(chest, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(legs, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(boots, "IgnoreBlacklist", true);
        player.equipStack(EquipmentSlot.HEAD, head);
        player.equipStack(EquipmentSlot.CHEST, chest);
        player.equipStack(EquipmentSlot.LEGS, legs);
        player.equipStack(EquipmentSlot.FEET, boots);
        player.getInventory().markDirty();
    }

    public static final List<UUID> cursedHeartPlayers = new ArrayList<>();
    public void curseHearts(ServerPlayerEntity player) {
        cursedHeartPlayers.add(player.getUuid());
        double newHealth = Math.max(player.getMaxHealth()-7, 1);
        AttributeUtils.setMaxPlayerHealth(player, newHealth);
    }

    public static final List<UUID> cursedMoonJumpPlayers = new ArrayList<>();
    public void curseMoonjump(ServerPlayerEntity player) {
        cursedMoonJumpPlayers.add(player.getUuid());
        AttributeUtils.setJumpStrength(player, 0.76);
    }

    public void curseBeeswarm(ServerPlayerEntity player) {
        BlockPos spawnPos = TriviaBotPathfinding.getBlockPosNearPlayer(player, bot.getBlockPos(), 1);
        BeeEntity bee1 = EntityType.BEE.spawn((ServerWorld) bot.getBotWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee2 = EntityType.BEE.spawn((ServerWorld) bot.getBotWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee3 = EntityType.BEE.spawn((ServerWorld) bot.getBotWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee4 = EntityType.BEE.spawn((ServerWorld) bot.getBotWorld(), spawnPos, SpawnReason.COMMAND);
        BeeEntity bee5 = EntityType.BEE.spawn((ServerWorld) bot.getBotWorld(), spawnPos, SpawnReason.COMMAND);
        if (bee1 != null) bee1.setAngryAt(player.getUuid());
        if (bee2 != null) bee2.setAngryAt(player.getUuid());
        if (bee3 != null) bee3.setAngryAt(player.getUuid());
        if (bee4 != null) bee4.setAngryAt(player.getUuid());
        if (bee5 != null) bee5.setAngryAt(player.getUuid());
        if (bee1 != null) bee1.setAngerTime(1000000);
        if (bee2 != null) bee2.setAngerTime(1000000);
        if (bee3 != null) bee3.setAngerTime(1000000);
        if (bee4 != null) bee4.setAngerTime(1000000);
        if (bee5 != null) bee5.setAngerTime(1000000);
    }

    public static final List<UUID> cursedRoboticVoicePlayers = new ArrayList<>();
    public void curseRoboticVoice(ServerPlayerEntity player) {
        cursedRoboticVoicePlayers.add(player.getUuid());
    }
}
