package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.mixin.client.AbstractSoundInstanceAccessor;
import net.mat0u5.lifeseries.mixin.client.EntityTrackingSoundInstanceAccessor;
import net.mat0u5.lifeseries.mixin.client.SoundManagerAccessor;
import net.mat0u5.lifeseries.mixin.client.SoundSystemAccessor;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class ClientSounds {
    public static final Map<UUID, SoundInstance> trackedEntitySounds = new HashMap<>();
    private static final List<String> trackedSounds = List.of(
            "wildlife_trivia_intro",
            "wildlife_trivia_suspense",
            "wildlife_trivia_suspense_end",
            "wildlife_trivia_analyzing"
    );

    public static void onSoundPlay(SoundInstance sound) {
        if (!(sound instanceof EntityTrackingSoundInstance entityTrackingSound)) return;

        if (!trackedSounds.contains(entityTrackingSound.getId().getPath())) return;

        if (!(entityTrackingSound instanceof EntityTrackingSoundInstanceAccessor entityTrackingSoundAccessor)) return;
        Entity entity = entityTrackingSoundAccessor.getEntity();
        if (entity == null) return;
        UUID uuid = entity.getUuid();
        if (uuid == null) return;

        if (trackedEntitySounds.containsKey(uuid)) {
            SoundInstance stopSound = trackedEntitySounds.get(uuid);
            if (stopSound != null) {
                MinecraftClient.getInstance().getSoundManager().stop(stopSound);
            }
        }
        trackedEntitySounds.put(uuid, sound);
    }

    private static final List<String> onlyOneOf = List.of(
            "wildlife_trivia_intro",
            "wildlife_trivia_suspense",
            "wildlife_trivia_suspense_end"
    );
    private static long ticks = 0;
    public static void updateSingleSoundVolumes() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ticks++;
        if (ticks % 15 != 0) return;
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        if (!(soundManager instanceof SoundManagerAccessor managerAccessor)) return;
        SoundSystem soundSystem = managerAccessor.getSoundSystem();
        if (!(soundSystem instanceof SoundSystemAccessor accessor)) return;
        Map<String, Map<Double, SoundInstance>> soundMap = new HashMap<>();
        for (Collection<SoundInstance> soundCategory : accessor.getSounds().asMap().values()) {
            for (SoundInstance sound : soundCategory) {
                String name = sound.getId().getPath();
                if (!onlyOneOf.contains(name)) continue;
                Vec3d soundPosition = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
                double distance = WorldUtils.getEntityPos(player).distanceTo(soundPosition);
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
}
