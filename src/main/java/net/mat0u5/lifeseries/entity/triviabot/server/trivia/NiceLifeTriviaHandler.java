package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class NiceLifeTriviaHandler extends TriviaHandler {
    public static ItemSpawner itemSpawner;
    public NiceLifeTriviaManager.TriviaSpawn spawnInfo;
    public BotState currentState = BotState.LANDING;
    private int sameStateForTicks = 0;

    private enum BotState {
        LANDING,
        APPROACHING,
        APPROACHED
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

        if (bot.tickCount % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
        }

        if (bot.submittedAnswer()) {
            if (bot.answeredRight()) {
                if (bot.getAnalyzingTime() < -80) {
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
                    if (boundPlayer != null) {
                        NetworkHandlerServer.sendStringPacket(boundPlayer, PacketNames.RESET_TRIVIA, "true");
                    }
                }
                bot.setRanOutOfTime(true);
            }
        }
    }

    public void landingTick(ServerLevel level) {
        sameStateForTicks++;
        bot.setDeltaMovement(0, -0.25,0);
        if (bot.blockPosition().getY() <= spawnInfo.spawnPos().getY()) {
            currentState = BotState.APPROACHING;
            sameStateForTicks = 0;
            for (BlockPos pos : BlockPos.betweenClosed(spawnInfo.spawnPos().above(), spawnInfo.bedPos())) {
                level.destroyBlock(pos, true);
            }
        }
        NiceLifeTriviaManager.breakBlocksAround(level, bot.blockPosition());
    }

    public void approachingTick(ServerLevel level, ServerPlayer boundPlayer) {
        sameStateForTicks++;
        Vec3 botPos = bot.position();
        //? if <= 1.20.5 {
        /*Vec3 bedPos = spawnInfo.spawnPos().getCenter();//TODO test
         *///?} else {
        Vec3 bedPos = spawnInfo.spawnPos().getBottomCenter();
        //?}
        double speedX = bedPos.x() - botPos.x();
        double speedZ = bedPos.z() - botPos.z();
        Vec3 speed = new Vec3(speedX, 0,speedZ).normalize().scale(0.25);
        bot.setDeltaMovement(speed);
        boolean atPos = botPos.distanceTo(bedPos) <= 0.1;
        if (atPos || sameStateForTicks >= 200) {
            if (!atPos) {
                LevelUtils.teleport(bot, level, spawnInfo.bedPos());
            }
            currentState = BotState.APPROACHED;
            sameStateForTicks = 0;
            startTrivia(boundPlayer);
        }
    }

    public void approachedTick(ServerLevel level) {
        sameStateForTicks++;
        bot.setDeltaMovement(0, 0, 0);

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
    }

    public void answeredIncorrect() {
        super.answeredIncorrect();
        //TaskScheduler.scheduleTask(210, this::cursePlayer);
        TaskScheduler.scheduleTask(72, () -> {
            PlayerUtils.playSoundToPlayer(
                    bot.serverData.getBoundPlayer(),
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_incorrect")), 1f, 1);//TODO sound
        });
    }

    public static void initializeItemSpawner() {
        //TODO loot table
        //TODO finish items
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
    }
}
