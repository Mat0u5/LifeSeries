package net.mat0u5.lifeseries.entity.snail;

import net.minecraft.entity.AnimationState;

public class SnailClientData {
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState glideAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState stopFlyAnimationState = new AnimationState();
    public final AnimationState startFlyAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();

    public final Snail snail;

    public SnailClientData(Snail snail) {
        this.snail = snail;
    }

    public void tick() {
        updateAnimations();
    }

    private int flyAnimation = 0;
    private int glideAnimationTimeout = 0;
    private int flyAnimationTimeout = 0;
    private int walkAnimationTimeout = 0;
    private int idleAnimationTimeout = 0;
    public void updateAnimations() {
        if (glideAnimationTimeout > 0) glideAnimationTimeout--;
        if (flyAnimationTimeout > 0) flyAnimationTimeout--;
        if (walkAnimationTimeout > 0) walkAnimationTimeout--;
        if (idleAnimationTimeout > 0) idleAnimationTimeout--;

        if (!snail.lastFlying && snail.isFlying()) {
            playStartFlyAnimation();
        }
        if (snail.lastFlying && !snail.isFlying()) {
            playStopFlyAnimation();
        }

        if (flyAnimation < 0) {
            flyAnimation++;
            pauseAllAnimations("stopFly");
        }
        else if (flyAnimation > 0) {
            flyAnimation--;
            pauseAllAnimations("startFly");
        }
        else if (snail.isFlying()) {
            pauseAllAnimations("fly");
            flyAnimationState.startIfNotRunning(snail.age);
        }
        else if (snail.isGliding() || snail.isLanding()) {
            pauseAllAnimations("glide");
            glideAnimationState.startIfNotRunning(snail.age);
        }
        else if (snail.limbAnimator.isLimbMoving() && snail.limbAnimator.getSpeed() > 0.02) {
            pauseAllAnimations("walk");
            walkAnimationState.startIfNotRunning(snail.age);
        }
        else {
            pauseAllAnimations("idle");
            idleAnimationState.startIfNotRunning(snail.age);
        }

    }
    public void pauseAllAnimations(String except) {
        if (!except.equalsIgnoreCase("glide")) glideAnimationState.stop();
        if (!except.equalsIgnoreCase("fly")) flyAnimationState.stop();
        if (!except.equalsIgnoreCase("walk")) walkAnimationState.stop();
        if (!except.equalsIgnoreCase("idle")) idleAnimationState.stop();
        if (!except.equalsIgnoreCase("startFly")) startFlyAnimationState.stop();
        if (!except.equalsIgnoreCase("stopFly")) stopFlyAnimationState.stop();
    }

    public void playStartFlyAnimation() {
        flyAnimation = 7;
        pauseAllAnimations("startFly");
        startFlyAnimationState.start(snail.age);
    }

    public void playStopFlyAnimation() {
        flyAnimation = -7;
        pauseAllAnimations("stopFly");
        stopFlyAnimationState.start(snail.age);
    }
}
