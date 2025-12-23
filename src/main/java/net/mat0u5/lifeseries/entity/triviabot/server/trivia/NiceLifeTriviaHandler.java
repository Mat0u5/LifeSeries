package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Tuple;
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

import java.util.List;

import static net.mat0u5.lifeseries.Main.server;

public class NiceLifeTriviaHandler extends TriviaHandler {
    public static ItemSpawner itemSpawner;
    public NiceLifeTriviaManager.TriviaSpawn spawnInfo;
    public BotState currentState = BotState.LANDING;
    private int sameStateForTicks = 0;

    private enum BotState {
        LANDING,
        APPROACHING,
        APPROACHED,
        ANALYZING,
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
        if (spawnInfo == null) {
            bot.serverData.despawn();
            return;
        }
        bot.pathfinding.noPathfinding = true;
        bot.noPhysics = true;
        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        ServerLevel level = (ServerLevel) bot.level();
        if (currentState == BotState.LANDING) {
            landingTick(level);
        }
        if (currentState == BotState.APPROACHING) {
            approachingTick(level, boundPlayer);
        }
        if (currentState == BotState.APPROACHED) {
            approachedTick(level);
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
        if (currentState == BotState.APPROACHING || currentState == BotState.LEAVING) {
            turn(bot.getDeltaMovement(), 20.0F);
        }
    }

    public void turn(Vec3 movement, float turnSpeed) {
        float targetYaw = (float)(Math.atan2(movement.z, movement.x) * (180F / Math.PI)) - 90F;
        float currentYaw = bot.getYRot();
        float newYaw = Mth.approachDegrees(currentYaw, targetYaw, turnSpeed);
        bot.setYRot(newYaw);
        bot.yRotO = newYaw;
    }

    public void landingTick(ServerLevel level) {
        sameStateForTicks++;
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
            NiceLifeTriviaManager.breakBlocksAround(level, bot.blockPosition());
        }
    }

    public void approachingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateForTicks++;
        Vec3 botPos = bot.position();
        //? if <= 1.20.5 {
        /*Vec3 bedPos = spawnInfo.bedPos().getCenter();//TODO test
         *///?} else {
        Vec3 bedPos = spawnInfo.bedPos().getBottomCenter();
        //?}
        double speedX = bedPos.x() - botPos.x();
        double speedZ = bedPos.z() - botPos.z();
        double maxSpeed = 0.08;
        if (speedX > maxSpeed) speedX = maxSpeed;
        if (speedX < -maxSpeed) speedX = -maxSpeed;
        if (speedZ > maxSpeed) speedZ = maxSpeed;
        if (speedZ < -maxSpeed) speedZ = -maxSpeed;

        Vec3 speed = new Vec3(speedX, 0,speedZ);
        bot.setDeltaMovement(speed);
        if (sameStateForTicks == 1) turn(speed, 1000);
        boolean atPos = botPos.distanceTo(bedPos) <= 0.1;
        if (atPos || sameStateForTicks >= 200) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, spawnInfo.bedPos());
            }
            changeStateTo(BotState.APPROACHED);
            startTrivia(boundPlayer);
        }
    }

    public void approachedTick(ServerLevel level) {
        sameStateForTicks++;
        bot.setDeltaMovement(0, 0, 0);
    }

    public void flyingUpTick(ServerLevel level) {
        if (sameStateForTicks == 0) {
            NetworkHandlerServer.sendStringPacket(bot.serverData.getBoundPlayer(), PacketNames.HIDE_SLEEP_DARKNESS, "false");//TODO move elsewhere
        }
        sameStateForTicks++;
        if (bot.isPassenger()) bot.removeVehicle();
        bot.noPhysics = true;
        float velocity = Math.min(0.3f, 0.25f * Math.abs(sameStateForTicks / (20.0f)));
        bot.setDeltaMovement(0,velocity,0);
        if (sameStateForTicks > 200) bot.serverData.despawn();
    }

    public void leavingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateForTicks++;
        Vec3 botPos = bot.position();
        //? if <= 1.20.5 {
        /*Vec3 leavePos = spawnInfo.spawnPos().getCenter();//TODO test
         *///?} else {
        Vec3 leavePos = spawnInfo.spawnPos().getBottomCenter();
        //?}
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
        if (atPos || sameStateForTicks >= 200) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, spawnInfo.spawnPos());
            }
            changeStateTo(BotState.FLYING_UP);
        }
    }

    public void changeStateTo(BotState newState) {
        currentState = newState;
        sameStateForTicks = 0;
    }

    public boolean handleAnswer(int answer) {
        if (super.handleAnswer(answer)) {
            bot.setAnalyzingTime(42);//TODO time
            PlayerUtils.playSoundToPlayer(
                    bot.serverData.getBoundPlayer(),
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_analyzing")), 1f, 1);//TODO sound
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
        TaskScheduler.scheduleTask(72, () -> {
            PlayerUtils.playSoundToPlayer(
                    bot.serverData.getBoundPlayer(),
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_correct")), 1f, 1);//TODO sound
        });
        TaskScheduler.scheduleTask(250, () -> { //TODO delay
            changeStateTo(BotState.LEAVING);
        });
    }

    public void answeredIncorrect() {
        super.answeredIncorrect();
        int startingDelay = bot.ranOutOfTime() ? 0 : 72;
        //TaskScheduler.scheduleTask(210, this::cursePlayer);
        TaskScheduler.scheduleTask(startingDelay, () -> {
            PlayerUtils.playSoundToPlayer(
                    bot.serverData.getBoundPlayer(),
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_incorrect")), 1f, 1);//TODO sound
        });
        TaskScheduler.scheduleTask(startingDelay+200, () -> { //TODO delay
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
