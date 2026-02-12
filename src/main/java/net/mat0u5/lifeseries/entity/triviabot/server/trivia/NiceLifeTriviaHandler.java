package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeVotingManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
//?}

public class NiceLifeTriviaHandler extends TriviaHandler {
    public static ItemSpawner itemSpawner;
    public NiceLifeTriviaManager.TriviaSpawn spawnInfo;
    public BotState currentState = BotState.LANDING;
    private Time sameStateTime = Time.zero();
    private static Vec3 botPosOffset = new Vec3(0, 0, 0);

    public enum BotState {
        LANDING,
        LANDED,
        APPROACHING,
        APPROACHED,
        QUESTION,
        VOTING,
        LEAVING,
        FLYING_UP,
        FINISHED
    }

    public NiceLifeTriviaHandler(TriviaBot bot) {
        super(bot);
    }
    public Tuple<Integer, TriviaQuestion> generateTrivia(ServerPlayer boundPlayer) {
        return NiceLifeTriviaManager.getTriviaQuestion(boundPlayer);
    }

    public void setTimeBasedOnDifficulty(int difficulty) {
        timeToComplete = NiceLifeTriviaManager.QUESTION_TIME;
    }

    public void tick() {
        super.tick();
        bot.pathfinding.noPathfinding = true;
        bot.noPhysics = true;
        bot.setNoGravity(true);

        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        ServerLevel level = (ServerLevel) bot.level();

        if (bot.waving() > 0) {
            bot.setWaving(bot.waving()-1);
        }

        if (spawnInfo == null || boundPlayer == null) {
            bot.serverData.despawn();
            return;
        }
        if (boundPlayer != null && !boundPlayer.isSleeping() && currentState != BotState.LEAVING && currentState != BotState.FLYING_UP && currentState != BotState.FINISHED) {
            NiceLifeTriviaManager.incorrectAnswers.add(bot.serverData.getBoundPlayerUUID());
            changeStateTo(BotState.LEAVING);
        }

        if (currentState == BotState.LANDING) {
            landingTick(level);
            bot.setGliding(true);
        }
        else {
            bot.setGliding(false);
        }

        if (currentState == BotState.LANDED) {
            landedTick(level, boundPlayer);
        }

        if (currentState == BotState.APPROACHING) {
            approachingTick(level, boundPlayer);
        }
        if (currentState == BotState.APPROACHED) {
            approachedTick(level, boundPlayer);
        }
        if (currentState == BotState.QUESTION) {
            questionTick(level, boundPlayer);
        }
        if (currentState == BotState.VOTING) {
            votingTick(level);
        }
        if (currentState == BotState.LEAVING) {
            leavingTick(level, boundPlayer);
        }
        if (currentState == BotState.FLYING_UP) {
            flyingUpTick(level);
        }

        if (bot.tickCount % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
        }

        if (!bot.submittedAnswer()) {
            bot.serverData.handleHighVelocity();
            if (bot.interactedWith() && getRemainingTicks() <= 0) {
                if (!bot.ranOutOfTime()) {
                    if (boundPlayer != null) {
                        SimplePackets.RESET_TRIVIA.target(boundPlayer).sendToClient();
                    }
                    TaskScheduler.scheduleTask(40, () -> {
                        bot.setSubmittedAnswer(true);
                        bot.setAnalyzingTime(0);
                        answeredIncorrect();
                    });
                }
                bot.setRanOutOfTime(true);
            }
        }

        NiceLifeTriviaManager.preparingForSpawn = false;
    }

    public void turnToBed(float turnSpeed) {
        turn(spawnInfo.bedDirection().getOpposite().toYRot(), turnSpeed);
    }

    public void turnFromBed(float turnSpeed) {
        turn(spawnInfo.bedDirection().toYRot(), turnSpeed);
    }

    public void turn(float targetYaw, float turnSpeed) {
        float currentYaw = bot.getYRot();

        float delta = targetYaw - currentYaw;
        while (delta > 180.0f) delta -= 360.0f;
        while (delta < -180.0f) delta += 360.0f;
        float turnAmount = Math.max(-turnSpeed, Math.min(turnSpeed, delta));
        float newYaw = currentYaw + turnAmount;
        while (newYaw > 180.0f) newYaw -= 360.0f;
        while (newYaw < -180.0f) newYaw += 360.0f;

        if (turnSpeed >= 180) {
            newYaw = targetYaw;
        }

        bot.setYRot(newYaw);
        bot.setYBodyRot(newYaw);
        bot.setYHeadRot(newYaw);
    }

    public void landingTick(ServerLevel level) {
        sameStateTime.tick();
        turnToBed(1000);
        if (bot.position().y() < (spawnInfo.spawnPos().getY()+botPosOffset.y) || sameStateTime.isLarger(Time.seconds(30))) {
            bot.setDeltaMovement(0, 0,0);
            bot.setPos(bot.position().x, spawnInfo.spawnPos().getY()+botPosOffset.y, bot.position().z);
            changeStateTo(BotState.LANDED);
            for (BlockPos pos : BlockPos.betweenClosed(spawnInfo.spawnPos().above(), spawnInfo.bedPos())) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof BedBlock) continue;
                if (state.getBlock().defaultDestroyTime() == -1) {
                    //Unbreakable blocks
                    continue;
                }
                if (state.getCollisionShape(level, pos).isEmpty()) {
                    continue;
                }
                level.destroyBlock(pos, NiceLifeTriviaManager.BREAKING_DROPS_RESOURCES);
            }
        }
        else {
            bot.setDeltaMovement(0, -0.25,0);
            NiceLifeTriviaManager.breakBlocksAround(level, bot.blockPosition(), spawnInfo.bedPos().getY());
        }
    }

    public void landedTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();
        turnToBed(1000);
        bot.setDeltaMovement(0, 0, 0);

        if (sameStateTime.getTicks() >= 55) {
            changeStateTo(BotState.APPROACHING);
        }
    }

    public void approachingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();

        turnToBed(20);

        Vec3 botPos = bot.position();

        if (bot.waving() == -1) {
            Vec3 bedVector = Vec3.atBottomCenterOf(spawnInfo.bedPos()).subtract(Vec3.atBottomCenterOf(spawnInfo.spawnPos()));
            if (bedVector.length() > 4) {
                Vec3 middlePos = Vec3.atBottomCenterOf(spawnInfo.spawnPos()).add(botPosOffset).add(bedVector.scale(0.4));
                boolean atMiddlePos = botPos.distanceTo(middlePos) <= 0.2;
                if (atMiddlePos) {
                    bot.setDeltaMovement(0, 0, 0);
                    bot.setWaving(78);
                    return;
                }
            }
        }
        else if (bot.waving() > 4) {
            bot.setDeltaMovement(0, 0, 0);
            return;
        }

        Vec3 bedPos = Vec3.atBottomCenterOf(spawnInfo.bedPos()).add(botPosOffset);
        double speedX = bedPos.x() - botPos.x();
        double speedZ = bedPos.z() - botPos.z();
        double maxSpeed = 0.08;
        if (speedX > maxSpeed) speedX = maxSpeed;
        if (speedX < -maxSpeed) speedX = -maxSpeed;
        if (speedZ > maxSpeed) speedZ = maxSpeed;
        if (speedZ < -maxSpeed) speedZ = -maxSpeed;

        Vec3 speed = new Vec3(speedX, 0,speedZ);
        bot.setDeltaMovement(speed);

        boolean atPos = botPos.distanceTo(bedPos) <= 0.2;
        if (atPos || sameStateTime.isLarger(Time.seconds(10))) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, Vec3.atBottomCenterOf(spawnInfo.bedPos()).add(botPosOffset));
            }
            changeStateTo(BotState.APPROACHED);
        }
    }

    public void approachedTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();
        bot.setDeltaMovement(0, 0, 0);
        turnToBed(20);
        if (sameStateTime.getTicks() > 78) {
            changeStateTo(BotState.QUESTION);
            startTrivia(boundPlayer);
        }
    }

    public void questionTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();
        bot.setDeltaMovement(0, 0, 0);
        turnToBed(20);
        if (sameStateTime.getSeconds() >= NiceLifeTriviaManager.QUESTION_TIME + 35) {
            changeStateTo(BotState.LEAVING);
        }
    }

    public void flyingUpTick(ServerLevel level) {
        bot.setLeaving(true);
        turnToBed(40);
        sameStateTime.tick();
        if (bot.isPassenger()) bot.removeVehicle();
        bot.noPhysics = true;
        if (sameStateTime.getTicks() < 12) {
            bot.setDeltaMovement(0, 0, 0);
            return;
        }
        float velocity = 0.12f * Math.abs((sameStateTime.getTicks()-12) / (20.0f));
        if (sameStateTime.getTicks() >= 42) {
            velocity *= 2f;
        }

        bot.setDeltaMovement(0,velocity,0);
        if (sameStateTime.isLarger(Time.seconds(10))) {
            changeStateTo(BotState.FINISHED);
            bot.serverData.despawn();
        }
    }

    public void leavingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();

        turnFromBed(20);

        Vec3 botPos = bot.position();
        Vec3 leavePos = Vec3.atBottomCenterOf(spawnInfo.spawnPos()).add(botPosOffset);
        Vec3 bedPos = Vec3.atBottomCenterOf(spawnInfo.bedPos()).add(botPosOffset);
        double speedX = leavePos.x() - botPos.x();
        double speedZ = leavePos.z() - botPos.z();
        double maxSpeed = 0.08;
        if (speedX > maxSpeed) speedX = maxSpeed;
        if (speedX < -maxSpeed) speedX = -maxSpeed;
        if (speedZ > maxSpeed) speedZ = maxSpeed;
        if (speedZ < -maxSpeed) speedZ = -maxSpeed;

        Vec3 speed = new Vec3(speedX, 0,speedZ);
        bot.setDeltaMovement(speed);
        boolean atPos = botPos.distanceTo(leavePos) <= 0.2;
        if (atPos || sameStateTime.isLarger(Time.seconds(10))) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, Vec3.atBottomCenterOf(spawnInfo.spawnPos()).add(botPosOffset));
            }
            changeStateTo(BotState.FLYING_UP);
        }
    }

    public void votingTick(ServerLevel level) {
        sameStateTime.tick();
        turnToBed(20);
        bot.setDeltaMovement(0, 0, 0);
        Time remainingVotingTime = NiceLifeVotingManager.VOTING_TIME.diff(sameStateTime);
        SimplePackets.VOTING_TIME.target(bot.serverData.getBoundPlayer()).sendToClient(remainingVotingTime.getSeconds());
        if (sameStateTime.isLarger(NiceLifeVotingManager.VOTING_TIME)) {
            SimplePackets.VOTING_TIME.target(bot.serverData.getBoundPlayer()).sendToClient(0);
            changeStateTo(BotState.LEAVING);
        }
    }

    public void changeStateTo(BotState newState) {
        currentState = newState;
        sameStateTime = Time.zero();
        if (newState == BotState.APPROACHING || newState == BotState.LANDED) {
            turnToBed(1000);
        }
        if (newState == BotState.APPROACHED) {
            bot.setWaving(78);
        }
        if (newState == BotState.LEAVING) {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_turn"));
            PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
            TaskScheduler.scheduleTask(40, () -> {
                for (ItemEntity item : droppedItems) {
                    if (item == null) continue;
                    item.setPickUpDelay(0);
                }
            });
        }
        if (newState == BotState.FLYING_UP) {
            SimplePackets.HIDE_SLEEP_DARKNESS.target(bot.serverData.getBoundPlayer()).sendToClient(false);
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_away"));
            PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
        }
    }

    public boolean handleAnswer(int answer) {
        if (super.handleAnswer(answer)) {
            bot.setAnalyzingTime(87);
            PlayerUtils.playSoundToPlayer(
                    bot.serverData.getBoundPlayer(),
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_analyzing")), 1f, 1);
            return true;
        }
        return false;
    }

    public void answeredCorrect() {
        super.answeredCorrect();

        NiceLifeTriviaManager.correctAnswers.add(bot.serverData.getBoundPlayerUUID());

        TaskScheduler.scheduleTask(174+87, () -> spawnItemForPlayer(true));
        TaskScheduler.scheduleTask(174+107, () -> spawnItemForPlayer(true));
        TaskScheduler.scheduleTask(174+126, () -> spawnItemForPlayer(true));

        SoundEvent sound = OtherUtils.getRandomSound("nicelife_santabot_correct", 1, 6);
        TaskScheduler.scheduleTask(174, () -> {
            PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
        });
        TaskScheduler.scheduleTask(174+140, () -> {
            if (startVoting()) {
                changeStateTo(BotState.VOTING);
            }
            else {
                changeStateTo(BotState.LEAVING);
            }
        });
    }
    public boolean startVoting() {
        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        if (boundPlayer == null) return false;
        NiceLifeVotingManager.VoteType voteType = NiceLifeVotingManager.voteType;
        if (voteType != NiceLifeVotingManager.VoteType.NICE_LIST && voteType != NiceLifeVotingManager.VoteType.NAUGHTY_LIST) return false;
        List<String> availableForVoting = new ArrayList<>();


        for (ServerPlayer player : livesManager.getAlivePlayers()) {
            if (voteType == NiceLifeVotingManager.VoteType.NICE_LIST) {
                if (player != boundPlayer) {
                    availableForVoting.add(player.getScoreboardName());
                }
            }
            else {
                if (player.ls$isOnAtLeastLives(2, false) || NiceLifeVotingManager.REDS_ON_NAUGHTY_LIST) {
                    availableForVoting.add(player.getScoreboardName());
                }
            }
        }
        if (availableForVoting.isEmpty()) return false;

        String screenName = (voteType == NiceLifeVotingManager.VoteType.NICE_LIST) ? "Vote for who's been nice" : "Vote for who's been naughty";
        NetworkHandlerServer.sendVoteScreenPacket(boundPlayer, screenName, true, false, true, availableForVoting);
        NiceLifeVotingManager.allowedToVote.add(boundPlayer.getUUID());
        //TODO add the voting sound - "nicelife_santabot_vote"
        SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_suspense"));
        PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
        return true;
    }

    public void answeredIncorrect() {
        super.answeredIncorrect();

        NiceLifeTriviaManager.incorrectAnswers.add(bot.serverData.getBoundPlayerUUID());

        int delay = bot.ranOutOfTime() ? 0 : 174;
        TaskScheduler.scheduleTask(delay+115, () -> spawnItemForPlayer(false));
        TaskScheduler.scheduleTask(delay+135, () -> spawnItemForPlayer(false));
        SoundEvent sound = OtherUtils.getRandomSound("nicelife_santabot_incorrect", 1, 6);
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
        });
        TaskScheduler.scheduleTask(delay+160, () -> {
            changeStateTo(BotState.LEAVING);
        });
    }

    public List<ItemEntity> droppedItems = new ArrayList<>();
    public void spawnItemForPlayer(boolean success) {
        if (bot.level().isClientSide()) return;
        if (itemSpawner == null) return;
        if (bot.serverData.getBoundPlayer() == null) return;
        Vec3 pos = bot.position().add(0,1,0);
        //? if <= 1.21 {
        /*Vec3 towardsArm = Vec3.atLowerCornerOf(spawnInfo.bedDirection().getCounterClockWise().getNormal());
        *///?} else {
        Vec3 towardsArm = spawnInfo.bedDirection().getCounterClockWise().getUnitVec3();
        //?}
        pos = pos.add(towardsArm.scale(0.6));
        Vec3 playerPos = bot.serverData.getBoundPlayer().position().add(towardsArm.scale(0.3));

        Vec3 relativeTargetPos = new Vec3(
                playerPos.x() - pos.x(),
                0,
                playerPos.z() - pos.z()
        );
        Vec3 vector = Vec3.ZERO;
        if (relativeTargetPos.lengthSqr() > 0.0001) {
            if (success) {
                vector = relativeTargetPos.normalize().scale(0.05).add(0,0.43,0);
            }
            else {
                vector = relativeTargetPos.normalize().scale(0.25).add(0,0.1,0);
            }
        }

        if (success) {
            List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerLevel) bot.level(), bot.serverData.getBoundPlayer(), IdentifierHelper.of("lifeseriesdynamic", "nicelife_trivia_reward_loottable"), false);
            if (!lootTableItems.isEmpty()) {
                for (ItemStack item : lootTableItems) {
                    ItemEntity itemEntity = ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, item, bot.serverData.getBoundPlayer(), vector);
                    itemEntity.setNeverPickUp();
                    droppedItems.add(itemEntity);
                }
            }
            else {
                ItemStack randomItem = itemSpawner.getRandomItem();
                ItemEntity itemEntity = ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);
                itemEntity.setNeverPickUp();
                droppedItems.add(itemEntity);
            }
        }
        else {
            ItemStack coal = Items.COAL.getDefaultInstance();
            ItemEntity itemEntity = ItemStackUtils.spawnItemForPlayerWithVelocity((ServerLevel) bot.level(), pos, coal, bot.serverData.getBoundPlayer(), vector);
            itemEntity.setNeverPickUp();
            droppedItems.add(itemEntity);
        }
    }

    public static void initializeItemSpawner() {
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.DIAMOND_BLOCK, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_CARROT, 32), 10);
        itemSpawner.addItem(new ItemStack(Items.CREEPER_SPAWN_EGG, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.PUFFERFISH_BUCKET, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.DIAMOND, 3), 20);
        itemSpawner.addItem(new ItemStack(Items.IRON_BLOCK, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 1), 20);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SENSOR, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SHRIEKER, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.TNT, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.OBSIDIAN, 20), 10);
        itemSpawner.addItem(new ItemStack(Items.ARROW, 32), 10);

        itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.SHULKER_BOX, 1), 10);

        itemSpawner.addItem(new ItemStack(Items.BIRCH_LOG, 64), 10);
        itemSpawner.addItem(new ItemStack(Items.OAK_LOG, 64), 10);
        itemSpawner.addItem(new ItemStack(Items.MANGROVE_LOG, 64), 10);

        itemSpawner.addItem(new ItemStack(Items.COOKED_BEEF, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.BLAZE_ROD, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.REDSTONE, 32), 10);
        itemSpawner.addItem(new ItemStack(Items.VINDICATOR_SPAWN_EGG, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.EVOKER_SPAWN_EGG, 1), 10);

        itemSpawner.addItem(new ItemStack(Items.BREWING_STAND, 1), 10);
        itemSpawner.addItem(new ItemStack(Items.ENCHANTING_TABLE, 1), 10);

        //Enchanted Books
        //? if <= 1.20.3 {
        /*itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FALL_PROTECTION, 3))), 10);
        *///?} else if <= 1.20.5 {
        /*itemSpawner.addItem(Objects.requireNonNull(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FEATHER_FALLING, 3))), 10);
        *///?} else {
        itemSpawner.addItem(Objects.requireNonNull(ItemStackUtils.createEnchantedBook(Enchantments.FEATHER_FALLING, 3)), 10);
        //?}

        //Potions
        ItemStack pot = new ItemStack(Items.POTION);
        ItemStack pot2 = new ItemStack(Items.POTION);
        //? if <= 1.20.3 {
        /*PotionUtils.setCustomEffects(pot, Potions.INVISIBILITY.getEffects());
        PotionUtils.setCustomEffects(pot2, Potions.SLOW_FALLING.getEffects());
        *///?} else {
        pot.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.INVISIBILITY));
        pot2.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOW_FALLING));
        //?}
        itemSpawner.addItem(pot, 10);
        itemSpawner.addItem(pot2, 10);

        //? if >= 1.21 {
        itemSpawner.addItem(new ItemStack(Items.WIND_CHARGE, 6), 10);
        itemSpawner.addItem(new ItemStack(Items.BREEZE_ROD, 1), 10);

        ItemStack mace = new ItemStack(Items.MACE);
        ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
        ItemStackUtils.setCustomComponentBoolean(mace, "NoModifications", true);
        mace.setDamageValue(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 5);
        //?}
        //? if >= 1.21.6 {
        itemSpawner.addItem(new ItemStack(Items.DRIED_GHAST, 1), 10);
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



        //Camel spawn egg
        ItemStack camel = new ItemStack(Items.CAMEL_SPAWN_EGG);

        CompoundTag nbtCompCamel = new CompoundTag();
        nbtCompCamel.putInt("Tame", 1);
        nbtCompCamel.putString("id", "camel");

        //? if <= 1.21.4 {
        /*CompoundTag saddleItemComp = new CompoundTag();
        saddleItemComp.putInt("Count", 1);
        saddleItemComp.putString("id", "saddle");
        nbtCompCamel.put("SaddleItem", saddleItemComp);
        *///?} else {
        CompoundTag equipmentItemComp = new CompoundTag();
        CompoundTag saddleItemComp = new CompoundTag();
        saddleItemComp.putString("id", "saddle");
        equipmentItemComp.put("saddle", saddleItemComp);
        nbtCompCamel.put("equipment", equipmentItemComp);
        //?}


        //? if < 1.20.5 {
        /*camel.setTag(nbtCompCamel);
        *///?} else {
        CustomData nbtCamel= CustomData.of(nbtCompCamel);
        //?}

        //? if >=1.20.5 && <= 1.21.6 {
        /*camel.set(DataComponents.ENTITY_DATA, nbtCamel);
        *///?} else if > 1.21.6 {
        camel.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.CAMEL, nbtCamel.copyTag()));
        //?}
        itemSpawner.addItem(camel, 10);
    }
}
