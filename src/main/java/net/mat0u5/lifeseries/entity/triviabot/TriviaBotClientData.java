package net.mat0u5.lifeseries.entity.triviabot;

import net.minecraft.entity.AnimationState;

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
        if (!bot.getBotWorld().isClient()) return;
        updateAnimations();
    }


    private boolean lastSubmittedAnswer = false;
    private boolean lastRanOutOfTime = false;
    public void updateAnimations() {
        if (bot.submittedAnswer() && !lastSubmittedAnswer) {
            pauseAllAnimations("analyzing");
            analyzingAnimationState.startIfNotRunning(bot.age);
        }

        if (bot.ranOutOfTime()) {
            pauseAllAnimations("snail_transform");
            if (!lastRanOutOfTime) {
                snailTransformAnimationState.startIfNotRunning(bot.age);
            }
        }
        else if (bot.getAnalyzingTime() > 0) {
            pauseAllAnimations("analyzing");
        }
        else if (bot.submittedAnswer()) {
            if (bot.getAnalyzingTime() == 0) {
                if (bot.answeredRight()) {
                    pauseAllAnimations("answer_correct");
                    answerCorrectAnimationState.startIfNotRunning(bot.age);
                }
                else {
                    pauseAllAnimations("answer_incorrect");
                    answerIncorrectAnimationState.startIfNotRunning(bot.age);
                }
            }
        }
        else if (bot.interactedWith()) {
            pauseAllAnimations("countdown");
            countdownAnimationState.startIfNotRunning(bot.age);
        }
        else if (bot.isBotGliding()) {
            pauseAllAnimations("glide");
            glideAnimationState.startIfNotRunning(bot.age);
        }
        else if (bot.limbAnimator.isLimbMoving() && bot.limbAnimator.getSpeed() > 0.02) {
            pauseAllAnimations("walk");
            walkAnimationState.startIfNotRunning(bot.age);
        }
        else {
            pauseAllAnimations("idle");
            idleAnimationState.startIfNotRunning(bot.age);
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
