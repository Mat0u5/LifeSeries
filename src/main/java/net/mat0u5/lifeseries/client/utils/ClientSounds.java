package net.mat0u5.lifeseries.client.utils;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.mixin.AbstractSoundInstanceAccessor;
import net.mat0u5.lifeseries.client.mixin.SoundEngineAccessor;
import net.mat0u5.lifeseries.client.mixin.SoundManagerAccessor;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.config.modifiable.SoundBehavior;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ClientSounds {
    public static final Map<UUID, SoundInstance> onlyPlayLatestEntities = new HashMap<>();
    public static final List<SoundInstance> onlyPlayLatest = new ArrayList<>();

    private static final Map<String, EnumSet<SoundBehavior>> soundBehaviors = new HashMap<>();
    private static final Map<String, ClientFollowUpSound> followUpSounds = new HashMap<>();
    private static final List<PendingFollowUpSound> pendingFollowUps = new ArrayList<>();
    private static boolean receivedSoundBehaviors = false;
    private static boolean loadedDefaultSoundBehaviors = false;

    private static final int FOLLOW_UP_OVERLAP_TICKS = 0;
    private static final Map<String, Integer> DEFAULT_SOUND_LENGTH_TICKS = Map.of(
            "lifeseries:nicelife_santabot_introduction_long", 615,
            "lifeseries:nicelife_santabot_introduction_short", 78
    );

    private record ClientFollowUpSound(String soundId, float volume, float pitch) {}
    private static class PendingFollowUpSound {
        final SoundInstance sound;
        final ClientFollowUpSound followUp;
        final int lengthTicks;
        boolean wasActive = false;
        int ticks = 0;
        PendingFollowUpSound(SoundInstance sound, ClientFollowUpSound followUp) {
            this.sound = sound;
            this.followUp = followUp;
            this.lengthTicks = DEFAULT_SOUND_LENGTH_TICKS.getOrDefault(getSoundId(sound), 0);
        }
    }

    public static void resetSoundBehaviors() {
        receivedSoundBehaviors = false;
        loadedDefaultSoundBehaviors = false;
        pendingFollowUps.clear();
    }

    private static void loadDefaultSoundBehaviors() {
        loadedDefaultSoundBehaviors = true;
        soundBehaviors.clear();
        followUpSounds.clear();
        for (SoundBehavior behavior : SoundBehavior.values()) {
            for (ModifiableSound sound : behavior.getSounds()) {
                addSoundBehavior(sound.getDefaultSoundId(), behavior);
            }
            for (SoundBehavior.FollowUpSound followUp : behavior.getFollowUps()) {
                addFollowUp(followUp.sound().getDefaultSoundId(), new ClientFollowUpSound(followUp.followUp().getDefaultSoundId(), followUp.volume(), followUp.pitch()));
            }
        }
    }

    public static void setSoundBehaviors(List<String> entries) {
        receivedSoundBehaviors = true;
        soundBehaviors.clear();
        followUpSounds.clear();
        for (String entry : entries) {
            String[] split = entry.split(";");
            if (split.length < 2) continue;
            SoundBehavior behavior = SoundBehavior.fromName(split[0]);
            if (behavior == null) continue;
            if (behavior == SoundBehavior.FOLLOWED_BY) {
                // FOLLOWED_BY;sound_id;follow_up_sound_id;volume;pitch
                if (split.length < 5) continue;
                try {
                    addFollowUp(split[1], new ClientFollowUpSound(split[2], Float.parseFloat(split[3]), Float.parseFloat(split[4])));
                } catch (NumberFormatException ignored) {}
                continue;
            }
            addSoundBehavior(split[1], behavior);
        }
    }

    private static void addSoundBehavior(String soundId, SoundBehavior behavior) {
        if (soundId == null || soundId.isEmpty()) return;
        soundBehaviors.computeIfAbsent(soundId.toLowerCase(Locale.ROOT), k -> EnumSet.noneOf(SoundBehavior.class)).add(behavior);
    }

    private static void addFollowUp(String soundId, ClientFollowUpSound followUp) {
        if (soundId == null || soundId.isEmpty() || followUp.soundId().isEmpty()) return;
        addSoundBehavior(soundId, SoundBehavior.FOLLOWED_BY);
        followUpSounds.put(soundId.toLowerCase(Locale.ROOT), followUp);
    }

    public static String getSoundId(SoundInstance sound) {
        //? if <= 1.21.9 {
        //return sound.getLocation().toString();
        //?} else {
        return sound.getIdentifier().toString();
        //?}
    }

    public static boolean hasBehavior(SoundInstance sound, SoundBehavior behavior) {
        return hasBehavior(getSoundId(sound), behavior);
    }

    public static boolean hasBehavior(String soundId, SoundBehavior behavior) {
        if (!receivedSoundBehaviors && !loadedDefaultSoundBehaviors) {
            loadDefaultSoundBehaviors();
        }
        EnumSet<SoundBehavior> behaviors = soundBehaviors.get(soundId);
        return behaviors != null && behaviors.contains(behavior);
    }

    public static void onSoundPlay(SoundInstance sound) {
        if (LifeSeries.modFullyDisabled()) return;
        String soundId = getSoundId(sound);
        if (hasBehavior(soundId, SoundBehavior.FOLLOWED_BY)) {
            ClientFollowUpSound followUp = followUpSounds.get(soundId);
            if (followUp != null) {
                pendingFollowUps.add(new PendingFollowUpSound(sound, followUp));
            }
        }
        if (!hasBehavior(soundId, SoundBehavior.ONLY_PLAY_LATEST)) return;

        if (sound instanceof EntityBoundSoundInstance entityTrackingSound) {
            Entity entity = entityTrackingSound.entity;
            if (entity == null) return;
            UUID uuid = entity.getUUID();
            if (uuid == null) return;

            if (onlyPlayLatestEntities.containsKey(uuid)) {
                SoundInstance stopSound = onlyPlayLatestEntities.get(uuid);
                if (stopSound != null) {
                    cancelFollowUp(stopSound);
                    Minecraft.getInstance().getSoundManager().stop(stopSound);
                }
            }
            onlyPlayLatestEntities.put(uuid, sound);
            return;
        }

        for (SoundInstance stopSound : onlyPlayLatest) {
            if (stopSound != null) {
                cancelFollowUp(stopSound);
                ClientTaskScheduler.schedulePriorityTask(5, () -> {
                    Minecraft.getInstance().getSoundManager().stop(stopSound);
                });
            }
        }
        onlyPlayLatest.clear();
        onlyPlayLatest.add(sound);
    }

    public static void updateSingleSoundVolumes() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        if (!(soundManager instanceof SoundManagerAccessor managerAccessor)) return;
        SoundEngine soundSystem = managerAccessor.getSoundSystem();
        if (!(soundSystem instanceof SoundEngineAccessor accessor)) return;
        Map<String, Map<Double, SoundInstance>> soundMap = new HashMap<>();
        for (Collection<SoundInstance> soundCategory : accessor.getSounds().asMap().values()) {
            for (SoundInstance sound : soundCategory) {
                String name = getSoundId(sound);
                if (!hasBehavior(name, SoundBehavior.ONLY_CLOSEST)) continue;
                if (sound instanceof EntityBoundSoundInstance entitySound && sound.getSource() == SoundSource.MASTER && hasBehavior(name, SoundBehavior.FOLLOW_PLAYER_ON_MASTER)) {
                    entitySound.entity = player;
                    entitySound.tick();
                }
                Vec3 soundPosition = new Vec3(sound.getX(), sound.getY(), sound.getZ());
                double distance = player.position().distanceTo(soundPosition);
                if (soundMap.containsKey(name)) {
                    soundMap.get(name).put(distance, sound);
                }
                else {
                    Map<Double, SoundInstance> distanceMap = new TreeMap<>();
                    distanceMap.put(distance, sound);
                    soundMap.put(name, distanceMap);
                }
            }
        }

        if (soundMap.isEmpty()) return;
        for (Map<Double, SoundInstance> distanceMap : soundMap.values()) {
            if (distanceMap.isEmpty()) continue;
            int index = 0;
            for (SoundInstance sound : distanceMap.values()) {
                if (sound instanceof AbstractSoundInstanceAccessor soundAccessor) {
                    if (index == 0) {
                        soundAccessor.setVolume(1);
                    }
                    else {
                        soundAccessor.setVolume(0);
                    }
                    index++;
                }
            }
        }
    }

    private static void cancelFollowUp(SoundInstance sound) {
        pendingFollowUps.removeIf(pending -> pending.sound == sound);
    }

    public static void checkFollowUpSounds() {
        if (pendingFollowUps.isEmpty()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            pendingFollowUps.clear();
            return;
        }
        SoundManager soundManager = client.getSoundManager();
        List<PendingFollowUpSound> finished = new ArrayList<>();
        Iterator<PendingFollowUpSound> iterator = pendingFollowUps.iterator();
        while (iterator.hasNext()) {
            PendingFollowUpSound pending = iterator.next();
            if (!client.isPaused()) pending.ticks++;
            if (pending.lengthTicks > 0) {
                if (pending.ticks < pending.lengthTicks - FOLLOW_UP_OVERLAP_TICKS) continue;
            }
            else if (soundManager.isActive(pending.sound)) {
                pending.wasActive = true;
                continue;
            }
            if (!pending.wasActive && pending.ticks < 10) continue;
            iterator.remove();
            finished.add(pending);
        }
        for (PendingFollowUpSound pending : finished) {
            SoundInstance followUp = createFollowUpInstance(pending);
            if (followUp != null) {
                soundManager.play(followUp);
            }
        }
    }

    private static SoundInstance createFollowUpInstance(PendingFollowUpSound pending) {
        Identifier soundId;
        try {
            soundId = IdentifierHelper.parse(pending.followUp.soundId());
        } catch (Exception e) {
            return null;
        }
        SoundInstance original = pending.sound;
        float volume = pending.followUp.volume();
        float pitch = pending.followUp.pitch();
        if (original instanceof EntityBoundSoundInstance entitySound && entitySound.entity != null && !entitySound.entity.isRemoved()) {
            return new EntityBoundSoundInstance(SoundEvent.createVariableRangeEvent(soundId), original.getSource(), volume, pitch, entitySound.entity, RandomSource.create().nextLong());
        }
        return new SimpleSoundInstance(soundId, original.getSource(), volume, pitch, RandomSource.create(), false, 0, original.getAttenuation(), original.getX(), original.getY(), original.getZ(), original.isRelative());
    }

    public static void stopTriviaSounds() {
        pendingFollowUps.clear();
        for (SoundInstance stopSound : onlyPlayLatest) {
            if (stopSound != null) {
                Minecraft.getInstance().getSoundManager().stop(stopSound);
            }
        }
        for (SoundInstance stopSound : onlyPlayLatestEntities.values()) {
            if (stopSound != null) {
                Minecraft.getInstance().getSoundManager().stop(stopSound);
            }
        }
        onlyPlayLatest.clear();
        onlyPlayLatestEntities.clear();
    }
}
