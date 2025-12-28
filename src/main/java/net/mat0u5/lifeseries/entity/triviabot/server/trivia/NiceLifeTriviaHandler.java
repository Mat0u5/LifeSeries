package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class NiceLifeTriviaHandler extends TriviaHandler {
    public static ItemSpawner itemSpawner;
    public NiceLifeTriviaManager.TriviaSpawn spawnInfo;
    public BotState currentState = BotState.LANDING;
    private Time sameStateTime = Time.zero();

    private enum BotState {
        LANDING,
        APPROACHING,
        APPROACHED,
        QUESTION,
        VOTING,
        LEAVING,
        FLYING_UP
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
        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        ServerLevel level = (ServerLevel) bot.level();

        if (bot.waving() > 0) {
            bot.setWaving(bot.waving()-1);
        }

        if (spawnInfo == null || (boundPlayer != null && !boundPlayer.isSleeping())) {
            bot.serverData.despawn();
            return;
        }
        if (currentState == BotState.LANDING) {
            landingTick(level);
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
                        NetworkHandlerServer.sendStringPacket(boundPlayer, PacketNames.RESET_TRIVIA, "true");
                    }

                }
                bot.setRanOutOfTime(true);
                bot.setSubmittedAnswer(true);
                answeredIncorrect();
            }
        }
    }

    public void turnToBed(float turnSpeed) {
        turn(spawnInfo.bedDirection().getOpposite().toYRot(), turnSpeed);
    }

    public void turnFromBed(float turnSpeed) {
        turn(spawnInfo.bedDirection().toYRot(), turnSpeed);
    }

    public void turn(float targetYaw, float turnSpeed) {
        float currentYaw = bot.getYRot();
        float newYaw = Mth.approachDegrees(currentYaw, targetYaw, turnSpeed);
        bot.setYRot(newYaw);
        bot.yRotO = newYaw;
    }

    public void landingTick(ServerLevel level) {
        sameStateTime.tick();
        if (bot.blockPosition().getY() < spawnInfo.spawnPos().getY()) {
            bot.setDeltaMovement(0, 0,0);
            bot.setPos(bot.position().x, spawnInfo.spawnPos().getY(), bot.position().z);
            changeStateTo(BotState.APPROACHING);
            for (BlockPos pos : BlockPos.betweenClosed(spawnInfo.spawnPos().above(), spawnInfo.bedPos())) {
                level.destroyBlock(pos, true);
            }
        }
        else {
            bot.setDeltaMovement(0, -0.25,0);
            NiceLifeTriviaManager.breakBlocksAround(level, bot.blockPosition(), spawnInfo.bedPos().getY());
        }
    }

    public void approachingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();

        turnToBed(20);

        Vec3 botPos = bot.position();

        if (bot.waving() == -1) {
            Vec3 bedVector = Vec3.atBottomCenterOf(spawnInfo.bedPos()).subtract(Vec3.atBottomCenterOf(spawnInfo.spawnPos()));
            if (bedVector.length() > 4) {
                Vec3 middlePos = Vec3.atBottomCenterOf(spawnInfo.spawnPos()).add(bedVector.scale(0.4));
                boolean atMiddlePos = botPos.distanceTo(middlePos) <= 0.1;
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

        Vec3 bedPos = Vec3.atBottomCenterOf(spawnInfo.bedPos());
        double speedX = bedPos.x() - botPos.x();
        double speedZ = bedPos.z() - botPos.z();
        double maxSpeed = 0.08;
        if (speedX > maxSpeed) speedX = maxSpeed;
        if (speedX < -maxSpeed) speedX = -maxSpeed;
        if (speedZ > maxSpeed) speedZ = maxSpeed;
        if (speedZ < -maxSpeed) speedZ = -maxSpeed;

        Vec3 speed = new Vec3(speedX, 0,speedZ);
        bot.setDeltaMovement(speed);

        boolean atPos = botPos.distanceTo(bedPos) <= 0.1;
        if (atPos || sameStateTime.isLarger(Time.seconds(10))) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, spawnInfo.bedPos());
            }
            changeStateTo(BotState.APPROACHED);
        }
    }

    public void approachedTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();
        bot.setDeltaMovement(0, 0, 0);
        if (sameStateTime.getTicks() > 78) {
            changeStateTo(BotState.QUESTION);
            startTrivia(boundPlayer);
        }
    }

    public void questionTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();
        bot.setDeltaMovement(0, 0, 0);
    }

    public void flyingUpTick(ServerLevel level) {
        bot.setLeaving(true);
        turnToBed(20);
        sameStateTime.tick();
        if (bot.isPassenger()) bot.removeVehicle();
        bot.noPhysics = true;
        if (sameStateTime.getTicks() < 10) {
            bot.setDeltaMovement(0,0, 0);
            return;
        }
        float velocity = Math.min(0.3f, 0.25f * Math.abs((sameStateTime.getTicks()-10) / (20.0f)));
        bot.setDeltaMovement(0,velocity,0);
        if (sameStateTime.isLarger(Time.seconds(10))) bot.serverData.despawn();
    }

    public void leavingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateTime.tick();

        turnFromBed(20);

        Vec3 botPos = bot.position();
        Vec3 leavePos = Vec3.atBottomCenterOf(spawnInfo.spawnPos());
        Vec3 bedPos = Vec3.atBottomCenterOf(spawnInfo.bedPos());
        double speedX = leavePos.x() - botPos.x();
        double speedZ = leavePos.z() - botPos.z();
        double maxSpeed = 0.08;
        if (speedX > maxSpeed) speedX = maxSpeed;
        if (speedX < -maxSpeed) speedX = -maxSpeed;
        if (speedZ > maxSpeed) speedZ = maxSpeed;
        if (speedZ < -maxSpeed) speedZ = -maxSpeed;

        Vec3 speed = new Vec3(speedX, 0,speedZ);
        bot.setDeltaMovement(speed);
        boolean atPos = botPos.distanceTo(leavePos) <= 0.1;
        if (atPos || sameStateTime.isLarger(Time.seconds(10))) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, spawnInfo.spawnPos());
            }
            changeStateTo(BotState.FLYING_UP);
        }
    }

    public void votingTick(ServerLevel level) {
        sameStateTime.tick();
        bot.setDeltaMovement(0, 0, 0);
        Time remainingVotingTime = NiceLifeTriviaManager.VOTING_TIME.diff(sameStateTime);
        NetworkHandlerServer.sendNumberPacket(bot.serverData.getBoundPlayer(), PacketNames.VOTING_TIME, remainingVotingTime.getSeconds());
        if (sameStateTime.isLarger(NiceLifeTriviaManager.VOTING_TIME)) {
            NetworkHandlerServer.sendNumberPacket(bot.serverData.getBoundPlayer(), PacketNames.VOTING_TIME, 0);
        }
    }

    public void changeStateTo(BotState newState) {
        currentState = newState;
        sameStateTime = Time.zero();
        if (newState == BotState.APPROACHING) {
            turnToBed(1000);
        }
        if (newState == BotState.APPROACHED) {
            bot.setWaving(78);
        }
        if (newState == BotState.FLYING_UP) {
            NetworkHandlerServer.sendStringPacket(bot.serverData.getBoundPlayer(), PacketNames.HIDE_SLEEP_DARKNESS, "false");
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
        //TODO items
        //TaskScheduler.scheduleTask(145, this::spawnItemForPlayer);
        //TaskScheduler.scheduleTask(170, this::spawnItemForPlayer);
        //TaskScheduler.scheduleTask(198, this::spawnItemForPlayer);
        //TaskScheduler.scheduleTask(213, this::blessPlayer);
        SoundEvent sound = OtherUtils.getRandomSound("nicelife_santabot_correct", 1, 6);
        TaskScheduler.scheduleTask(174, () -> {
            PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 1f, 1);
        });
        TaskScheduler.scheduleTask(174+140, () -> {
            bot.setAnalyzingTime(-1); //To stop the animation
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
        NiceLifeTriviaManager.TriviaVoteType voteType = NiceLifeTriviaManager.voteType;
        if (voteType == NiceLifeTriviaManager.TriviaVoteType.NONE) return false;
        List<String> availableForVoting = new ArrayList<>();

        // The first element of the list is the voting name.
        if (voteType == NiceLifeTriviaManager.TriviaVoteType.NICE_LIST) {
            availableForVoting.add("Vote for who's been nice");
        }
        else {
            availableForVoting.add("Vote for who's been naughty");
        }

        for (ServerPlayer player : livesManager.getAlivePlayers()) {
            if (voteType == NiceLifeTriviaManager.TriviaVoteType.NICE_LIST) {
                if (player != boundPlayer && player.ls$isOnAtLeastLives(2, false)) {
                    availableForVoting.add(player.getScoreboardName());
                }
            }
            else {
                availableForVoting.add(player.getScoreboardName());
            }
        }
        if (availableForVoting.isEmpty()) return false;

        NetworkHandlerServer.sendStringListPacket(boundPlayer, PacketNames.VOTING_SCREEN, availableForVoting);
        return true;
    }

    public void answeredIncorrect() {
        super.answeredIncorrect();
        int startingDelay = bot.ranOutOfTime() ? 0 : 174;
        //TaskScheduler.scheduleTask(210, this::cursePlayer);
        SoundEvent sound = OtherUtils.getRandomSound("nicelife_santabot_incorrect", 1, 6);
        TaskScheduler.scheduleTask(startingDelay, () -> {
            PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 1f, 1);
        });
        TaskScheduler.scheduleTask(startingDelay+160, () -> {
            bot.setAnalyzingTime(-1); //To stop the animation
            changeStateTo(BotState.LEAVING);
        });
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
        //TODO velocity + dependent on success/fail

        List<ItemStack> lootTableItems = ItemSpawner.getRandomItemsFromLootTable(server, (ServerLevel) bot.level(), bot.serverData.getBoundPlayer(), IdentifierHelper.of("lifeseriesdynamic", "nicelife_trivia_reward_loottable"), false);
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
        //TODO loot table
        //TODO finish items
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
    }
}
