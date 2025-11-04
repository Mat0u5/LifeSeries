package net.mat0u5.lifeseries.entity.snail;

import net.minecraft.world.entity.AnimationState;

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

    private boolean lastFlying = false;
    private boolean lastGliding = false;
    private boolean lastLanding = false;
    private int stopAllAnimations = 0;
    public void updateAnimations() {
        if (stopAllAnimations <= 0) {
            if ((!lastFlying && snail.isSnailFlying())) {
                pauseAllAnimations("startFly");
                startFlyAnimationState.startIfStopped(snail.tickCount);
                stopAllAnimations = 15;
            }
            else if ((!snail.isSnailGliding() && lastGliding) || (!snail.isSnailLanding() && lastLanding)) {
                pauseAllAnimations("stopFly");
                stopFlyAnimationState.startIfStopped(snail.tickCount);
                stopAllAnimations = 15;
            }
            else if (snail.isSnailFlying()) {
                pauseAllAnimations("fly");
                flyAnimationState.startIfStopped(snail.tickCount);
            }
            else if (snail.isSnailGliding() || snail.isSnailLanding()) {
                pauseAllAnimations("glide");
                glideAnimationState.startIfStopped(snail.tickCount);
            }
            else if (snail.walkAnimation.isMoving() && snail.walkAnimation.speed() > 0.02) {
                pauseAllAnimations("walk");
                walkAnimationState.startIfStopped(snail.tickCount);
            }
            else {
                pauseAllAnimations("idle");
                idleAnimationState.startIfStopped(snail.tickCount);
            }
        }
        else {
            stopAllAnimations--;
        }
        lastFlying = snail.isSnailFlying();
        lastGliding = snail.isSnailGliding();
        lastLanding = snail.isSnailLanding();
    }
    public void pauseAllAnimations(String except) {
        if (!except.equalsIgnoreCase("glide")) glideAnimationState.stop();
        if (!except.equalsIgnoreCase("fly")) flyAnimationState.stop();
        if (!except.equalsIgnoreCase("walk")) walkAnimationState.stop();
        if (!except.equalsIgnoreCase("idle")) idleAnimationState.stop();
        if (!except.equalsIgnoreCase("startFly")) startFlyAnimationState.stop();
        if (!except.equalsIgnoreCase("stopFly")) stopFlyAnimationState.stop();
    }
}
