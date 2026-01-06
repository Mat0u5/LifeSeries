package net.mat0u5.lifeseries.entity.triviabot;

import net.mat0u5.lifeseries.utils.other.OtherUtils;
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

    public final AnimationState santaAnalyzingAnimationState = new AnimationState();
    public final AnimationState santaAnswerCorrectAnimationState = new AnimationState();
    public final AnimationState santaAnswerIncorrectAnimationState = new AnimationState();
    public final AnimationState santaFlyAnimationState = new AnimationState();
    public final AnimationState santaGlideAnimationState = new AnimationState();
    public final AnimationState santaIdleAnimationState = new AnimationState();
    public final AnimationState santaWaveAnimationState = new AnimationState();
    public final AnimationState santaWalkAnimationState = new AnimationState(); //TODO
    public final AnimationState santaLandAnimationState = new AnimationState(); //TODO
    public final AnimationState faceAngryAnimationState = new AnimationState();
    public final AnimationState faceHappyAnimationState = new AnimationState();
    public void tick() {
        if (!bot.level().isClientSide()) return;
        updateAnimations();
    }


    private boolean lastSubmittedAnswer = false;
    private boolean lastRanOutOfTime = false;
    public void updateAnimations() {
        if (!bot.santaBot()) {
            normalAnimations();
        } else {
            santaAnimations();
        }
    }
    public void normalAnimations() {
        if (bot.submittedAnswer() && !lastSubmittedAnswer) {
            pauseAllAnimations("analyzing");
            analyzingAnimationState.startIfStopped(bot.tickCount);
        }

        if (bot.ranOutOfTime() && !bot.santaBot()) {
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

    private int notGlidingFor = 0;
    public void santaAnimations() {

        if (bot.isBotGliding()) {
            notGlidingFor = 0;
        }
        else {
            notGlidingFor++;
        }

        if (bot.submittedAnswer() && !lastSubmittedAnswer && bot.getAnalyzingTime() > 0) {
            pauseAllAnimations("santa_analyzing");
            santaAnalyzingAnimationState.startIfStopped(bot.tickCount);
        }

        if (bot.leaving()) {
            pauseAllAnimations("santa_fly");
            santaFlyAnimationState.startIfStopped(bot.tickCount);
        }
        else if (bot.getAnalyzingTime() > 0) {
            pauseAllAnimations("santa_analyzing");
        }
        else if (bot.submittedAnswer() && ((bot.answeredRight() && bot.getAnalyzingTime() >= -70) || (!bot.answeredRight() && bot.getAnalyzingTime() >= -80))) {
            if (bot.getAnalyzingTime() == 0) {
                if (bot.answeredRight()) {
                    pauseAllAnimations("santa_answer_correct");
                    santaAnswerCorrectAnimationState.startIfStopped(bot.tickCount);
                }
                else {
                    pauseAllAnimations("santa_answer_incorrect");
                    santaAnswerIncorrectAnimationState.startIfStopped(bot.tickCount);
                }
            }
        }
        else if (bot.waving() > 0) {
            pauseAllAnimations("santa_wave");
            santaWaveAnimationState.startIfStopped(bot.tickCount);
        }
        else if (bot.interactedWith()) {
            pauseAllAnimations("santa_idle");
            santaIdleAnimationState.startIfStopped(bot.tickCount);
        }
        else if (bot.isBotGliding()) {
            pauseAllAnimations("santa_glide");
            santaGlideAnimationState.startIfStopped(bot.tickCount);
        }
        else if (notGlidingFor < 47) {
            pauseAllAnimations("santa_land");
            santaLandAnimationState.startIfStopped(bot.tickCount);
        }
        else if (bot.walkAnimation.isMoving() && bot.walkAnimation.speed() > 0.02) {
            pauseAllAnimations("santa_walk");
            santaWalkAnimationState.startIfStopped(bot.tickCount);
        }
        else {
            pauseAllAnimations("santa_idle");
            santaIdleAnimationState.startIfStopped(bot.tickCount);
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

        if (!except.equalsIgnoreCase("santa_analyzing")) santaAnalyzingAnimationState.stop();
        if (!except.equalsIgnoreCase("santa_answer_correct")) {
            santaAnswerCorrectAnimationState.stop();
            if (bot.submittedAnswer() && bot.getAnalyzingTime() < 0 && bot.answeredRight()) {
                faceHappyAnimationState.startIfStopped(bot.tickCount);
            }
        }
        if (!except.equalsIgnoreCase("santa_answer_incorrect")) {
            santaAnswerIncorrectAnimationState.stop();
            if (bot.submittedAnswer() && bot.getAnalyzingTime() < 0 && !bot.answeredRight()) {
                faceAngryAnimationState.startIfStopped(bot.tickCount);
            }
        }
        if (!except.equalsIgnoreCase("santa_fly")) santaFlyAnimationState.stop();
        if (!except.equalsIgnoreCase("santa_glide")) santaGlideAnimationState.stop();
        if (!except.equalsIgnoreCase("santa_idle")) santaIdleAnimationState.stop();
        if (!except.equalsIgnoreCase("santa_wave")) santaWaveAnimationState.stop();
        if (!except.equalsIgnoreCase("santa_walk")) santaWalkAnimationState.stop();
        if (!except.equalsIgnoreCase("santa_land")) santaLandAnimationState.stop();

        // These are permanent
        //if (!except.equalsIgnoreCase("face_angry")) faceAngryAnimationState.stop();
        //if (!except.equalsIgnoreCase("face_happy")) faceHappyAnimationState.stop();
    }
}
