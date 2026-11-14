package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.entity.snail.Snail;

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

        if (snail.getAirSupply() == 0 && random.nextInt(100) == 0) {
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
        if (!snail.isSnailFlying() && !snail.isSnailGliding() && !snail.isSnailLanding() && snail.zza > 0.001) {
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
        forcePlayRandomSound(ModifiableSound.WILDLIFE_SNAIL_ATTACK, 0.25f);
    }

    public void playBurnSound() {
        playRandomSound(ModifiableSound.WILDLIFE_SNAIL_BURN, 0.25f);
    }

    public void playDrownSound() {
        playRandomSound(ModifiableSound.WILDLIFE_SNAIL_DROWN, 0.25f);
    }

    public void playFallSound() {
        playRandomSound(ModifiableSound.WILDLIFE_SNAIL_FALL, 0.25f);
    }

    public void playFlySound() {
        playRandomSound(ModifiableSound.WILDLIFE_SNAIL_FLY, 0.25f);
    }

    public void playPropellerSound() {
        forcePlayRandomSound(ModifiableSound.WILDLIFE_SNAIL_PROPELLER, 0.2f);
    }

    public void playWalkSound() {
        forcePlayRandomSound(ModifiableSound.WILDLIFE_SNAIL_WALK, 0.1f);
    }

    public void playLavaSound() {
        playRandomSound(ModifiableSound.WILDLIFE_SNAIL_LAVA, 0.25f);
    }

    public void playThrowSound() {
        playRandomSound(ModifiableSound.WILDLIFE_SNAIL_THROW, 0.25f);
    }

    private int soundCooldown = 0;
    public void forcePlayRandomSound(ModifiableSound sound, float volume) {
        int cooldownBefore = soundCooldown;
        soundCooldown = 0;
        playRandomSound(sound, volume);
        soundCooldown = cooldownBefore;
    }
    public void playRandomSound(ModifiableSound sound, float volume) {
        if (soundCooldown > 0) {
            return;
        }
        soundCooldown = 20;
        snail.playSound(sound.get(), volume, 1);
    }
}
