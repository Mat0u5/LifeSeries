package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class TriviaHandler {
    protected TriviaBot bot;
    public int difficulty = 0;
    public int interactedAtAge = 0;
    public int timeToComplete = 0;
    public TriviaQuestion question;

    public TriviaHandler(TriviaBot bot) {
        this.bot = bot;
    }

    public void tick() {
        if (bot.interactedWith()) {
            bot.triviaHandler.sendTimeUpdatePacket();
        }
    }

    public void startTrivia(ServerPlayer boundPlayer) {
        DatapackIntegration.EVENT_TRIVIA_BOT_OPEN.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Player", boundPlayer.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
        ));

        if (!bot.interactedWith() || question == null) {
            interactedAtAge = bot.tickCount;
            Tuple<Integer, TriviaQuestion> triviaQuestion = generateTrivia(boundPlayer);
            difficulty = triviaQuestion.x;
            question = triviaQuestion.y;
            setTimeBasedOnDifficulty(difficulty);
        }
        sendTimeUpdatePacket();
        NetworkHandlerServer.sendTriviaPacket(boundPlayer, question.getQuestion(), difficulty, System.currentTimeMillis(), timeToComplete, question.getAnswers());
        bot.setInteractedWith(true);
    }

    abstract Tuple<Integer, TriviaQuestion> generateTrivia(ServerPlayer boundPlayer);
    abstract void setTimeBasedOnDifficulty(int difficulty);

    public InteractionResult interactMob(Player player, InteractionHand hand) {
        return InteractionResult.SUCCESS;
    }

    public void sendTimeUpdatePacket() {
        ServerPlayer player = bot.serverData.getBoundPlayer();
        if (player != null) {
            int ticksSinceStart = bot.tickCount - interactedAtAge;
            SimplePackets.TRIVIA_TIMER.target(player).sendToClient(ticksSinceStart);
        }
    }

    public int getRemainingTicks() {
        int ticksSinceStart = bot.tickCount - interactedAtAge;
        return (timeToComplete*20) - ticksSinceStart;
    }

    public boolean handleAnswer(int answer) {
        if (bot.level().isClientSide()) return false;
        if (bot.submittedAnswer()) return false;
        bot.setSubmittedAnswer(true);
        if (answer == question.getCorrectAnswerIndex()) {
            answeredCorrect();
        }
        else {
            answeredIncorrect();
        }
        return true;
    }

    public void answeredCorrect() {
        ServerPlayer player = bot.serverData.getBoundPlayer();
        if (player != null) {
            DatapackIntegration.EVENT_TRIVIA_SUCCEED.trigger(List.of(
                    new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                    new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
            ));
        }
        bot.setAnsweredRight(true);
    }

    public void answeredIncorrect() {
        ServerPlayer player = bot.serverData.getBoundPlayer();
        if (player != null) {
            DatapackIntegration.EVENT_TRIVIA_FAIL.trigger(List.of(
                    new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                    new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
            ));
        }
        bot.setAnsweredRight(false);
    }
}
