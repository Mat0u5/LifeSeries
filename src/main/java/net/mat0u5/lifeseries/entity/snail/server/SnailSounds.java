package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.sound.SoundEvent;

import java.util.Random;

public class SnailSounds {
    private static final Random random = new Random();
    private final Snail snail;
    public SnailSounds(Snail snail) {
        this.snail = snail;
    }

    private int propellerSoundCooldown = 0;
    private int walkSoundCooldown = 0;
    private boolean lastGlidingOrLanding = false;
    public boolean lastFlying = false;
    public void playSounds() {
        if (soundCooldown > 0) {
            soundCooldown--;
        }

        if (snail.isInLavaLocal && random.nextInt(100) == 0) {
            playLavaSound();
        }

        if (snail.isOnFire() && random.nextInt(100) == 0) {
            playBurnSound();
        }

        if (snail.getAir() == 0 && random.nextInt(100) == 0) {
            playDrownSound();
        }

        if (snail.isSnailGliding() || snail.isSnailLanding()) {
            if (!lastGlidingOrLanding) {
                playFallSound();
            }
        }

        if (snail.isSnailFlying()) {
            if (!lastFlying) {
                playFlySound();
            }
            if (propellerSoundCooldown > 0) {
                propellerSoundCooldown--;
            }
            if (propellerSoundCooldown == 0) {
                propellerSoundCooldown=40;
                playPropellerSound();
            }
        }
        if (!snail.isSnailFlying() && !snail.isSnailGliding() && !snail.isSnailLanding() && snail.forwardSpeed > 0.001) {
            if (walkSoundCooldown > 0) {
                walkSoundCooldown--;
            }
            if (walkSoundCooldown == 0) {
                walkSoundCooldown = 22;
                playWalkSound();
            }
        }

        lastFlying = snail.isSnailFlying();
        lastGlidingOrLanding = snail.isSnailGliding() || snail.isSnailLanding();
    }

    public void playAttackSound() {
        forcePlayRandomSound("attack", 0.25f, 1, 9);
    }

    public void playBurnSound() {
        playRandomSound("burn", 0.25f, 1, 9);
    }

    public void playDrownSound() {
        playRandomSound("drown", 0.25f, 1, 9);
    }

    public void playFallSound() {
        playRandomSound("fall", 0.25f, 1, 5);
    }

    public void playFlySound() {
        playRandomSound("fly", 0.25f, 1, 8);
    }

    public void playPropellerSound() {
        forcePlayRandomSound("propeller", 0.2f, 0, 0);
    }

    public void playWalkSound() {
        forcePlayRandomSound("walk", 0.1f, 0, 0);
    }

    public void playLavaSound() {
        playRandomSound("lava", 0.25f, 1, 2);
    }

    public void playThrowSound() {
        playRandomSound("throw", 0.25f, 1, 7);
    }

    private int soundCooldown = 0;
    public void forcePlayRandomSound(String name, float volume, int from, int to) {
        int cooldownBefore = soundCooldown;
        soundCooldown = 0;
        playRandomSound(name, volume, from, to);
        soundCooldown = cooldownBefore;
    }
    public void playRandomSound(String name, float volume, int from, int to) {
        if (soundCooldown > 0) {
            return;
        }
        soundCooldown = 20;
        SoundEvent sound = OtherUtils.getRandomSound("wildlife_snail_"+name, from, to);
        snail.playSound(sound, volume, 1);
    }
}
