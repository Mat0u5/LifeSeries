package net.mat0u5.lifeseries.entity.triviabot;

import net.minecraft.world.entity.AnimationState;

public class TriviaBotClientData {
    private TriviaBot bot;
    public TriviaBotClientData(TriviaBot bot) {
        this.bot = bot;
    }

    public final AnimationState glideAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState countdownAnimationState = new AnimationState();
    public final AnimationState analyzingAnimationState = new AnimationState();
    public final AnimationState answerCorrectAnimationState = new AnimationState();
    public final AnimationState answerIncorrectAnimationState = new AnimationState();
    public final AnimationState snailTransformAnimationState = new AnimationState();
    public void tick() {
        if (!bot.getBotWorld().isClientSide()) return;
        updateAnimations();
    }


    private boolean lastSubmittedAnswer = false;
    private boolean lastRanOutOfTime = false;
    public void updateAnimations() {
        if (bot.submittedAnswer() && !lastSubmittedAnswer) {
            pauseAllAnimations("analyzing");
            analyzingAnimationState.startIfStopped(bot.tickCount);
        }

        if (bot.ranOutOfTime()) {
            pauseAllAnimations("snail_transform");
            if (!lastRanOutOfTime) {
                snailTransformAnimationState.startIfStopped(bot.tickCount);
            }
        }
        else if (bot.getAnalyzingTime() > 0) {
            pauseAllAnimations("analyzing");
        }
        else if (bot.submittedAnswer()) {
            if (bot.getAnalyzingTime() == 0) {
                if (bot.answeredRight()) {
                    pauseAllAnimations("answer_correct");
                    answerCorrectAnimationState.startIfStopped(bot.tickCount);
                }
                else {
                    pauseAllAnimations("answer_incorrect");
                    answerIncorrectAnimationState.startIfStopped(bot.tickCount);
                }
            }
        }
        else if (bot.interactedWith()) {
            pauseAllAnimations("countdown");
            countdownAnimationState.startIfStopped(bot.tickCount);
        }
        else if (bot.isBotGliding()) {
            pauseAllAnimations("glide");
            glideAnimationState.startIfStopped(bot.tickCount);
        }
        else if (bot.walkAnimation.isMoving() && bot.walkAnimation.speed() > 0.02) {
            pauseAllAnimations("walk");
            walkAnimationState.startIfStopped(bot.tickCount);
        }
        else {
            pauseAllAnimations("idle");
            idleAnimationState.startIfStopped(bot.tickCount);
        }

        lastSubmittedAnswer = bot.submittedAnswer();
        lastRanOutOfTime = bot.ranOutOfTime();
    }

    public void pauseAllAnimations(String except) {
        if (!except.equalsIgnoreCase("glide")) glideAnimationState.stop();
        if (!except.equalsIgnoreCase("walk")) walkAnimationState.stop();
        if (!except.equalsIgnoreCase("idle")) idleAnimationState.stop();
        if (!except.equalsIgnoreCase("countdown")) countdownAnimationState.stop();
        if (!except.equalsIgnoreCase("analyzing")) analyzingAnimationState.stop();
        if (!except.equalsIgnoreCase("answer_correct")) answerCorrectAnimationState.stop();
        if (!except.equalsIgnoreCase("answer_incorrect")) answerIncorrectAnimationState.stop();
        if (!except.equalsIgnoreCase("snail_transform")) snailTransformAnimationState.stop();
    }
}
