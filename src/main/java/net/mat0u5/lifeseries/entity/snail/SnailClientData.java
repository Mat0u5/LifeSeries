package net.mat0u5.lifeseries.entity.snail;

import net.mat0u5.lifeseries.utils.other.OtherUtils;
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

    private boolean lastFlying = false;
    private boolean lastGliding = false;
    private boolean lastLanding = false;
    private int stopAllAnimations = 0;
    public void updateAnimations() {
        if (stopAllAnimations <= 0) {
            if ((!lastFlying && snail.isFlying())) {
                pauseAllAnimations("startFly");
                startFlyAnimationState.startIfNotRunning(snail.age);
                stopAllAnimations = 15;
            }
            else if ((!snail.isGliding() && lastGliding) || (!snail.isLanding() && lastLanding)) {
                pauseAllAnimations("stopFly");
                stopFlyAnimationState.startIfNotRunning(snail.age);
                stopAllAnimations = 15;
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
        else {
            stopAllAnimations--;
        }
        lastFlying = snail.isFlying();
        lastGliding = snail.isGliding();
        lastLanding = snail.isLanding();
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
