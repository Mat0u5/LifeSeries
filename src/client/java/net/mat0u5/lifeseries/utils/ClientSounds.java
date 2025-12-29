package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.mixin.client.AbstractSoundInstanceAccessor;
import net.mat0u5.lifeseries.mixin.client.EntityBoundSoundInstanceAccessor;
import net.mat0u5.lifeseries.mixin.client.SoundManagerAccessor;
import net.mat0u5.lifeseries.mixin.client.SoundEngineAccessor;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ClientSounds {
    public static final Map<UUID, SoundInstance> onlyPlayLatestEntities = new HashMap<>();
    public static final List<SoundInstance> onlyPlayLatest = new ArrayList<>();
    private static final List<String> onlyPlayLatestSounds = List.of(
            "wildlife_trivia_intro",
            "wildlife_trivia_suspense",
            "wildlife_trivia_suspense_end",
            "wildlife_trivia_analyzing",

            "nicelife_santabot_intro",
            "nicelife_santabot_suspense",
            "nicelife_santabot_suspense_end",
            "nicelife_santabot_analyzing",
            "nicelife_santabot_incorrect1",
            "nicelife_santabot_incorrect2",
            "nicelife_santabot_incorrect3",
            "nicelife_santabot_incorrect4",
            "nicelife_santabot_incorrect5",
            "nicelife_santabot_incorrect6",
            "nicelife_santabot_vote",
            "nicelife_santabot_turn"
    );

    public static void onSoundPlay(SoundInstance sound) {

        //? if <= 1.21.9 {
        if (!onlyPlayLatestSounds.contains(sound.getLocation().getPath())) return;
        //?} else {
        /*if (!onlyPlayLatestSounds.contains(sound.getIdentifier().getPath())) return;
        *///?}

        if (sound instanceof EntityBoundSoundInstance entityTrackingSound) {
            if ((entityTrackingSound instanceof EntityBoundSoundInstanceAccessor entityTrackingSoundAccessor)) {
                Entity entity = entityTrackingSoundAccessor.getEntity();
                if (entity == null) return;
                UUID uuid = entity.getUUID();
                if (uuid == null) return;

                if (onlyPlayLatestEntities.containsKey(uuid)) {
                    SoundInstance stopSound = onlyPlayLatestEntities.get(uuid);
                    if (stopSound != null) {
                        Minecraft.getInstance().getSoundManager().stop(stopSound);
                    }
                }
                onlyPlayLatestEntities.put(uuid, sound);
                return;
            }
        }

        for (SoundInstance stopSound : onlyPlayLatest) {
            if (stopSound != null) {
                ClientTaskScheduler.schedulePriorityTask(5, () -> {
                    Minecraft.getInstance().getSoundManager().stop(stopSound);
                });
            }
        }
        onlyPlayLatest.clear();
        onlyPlayLatest.add(sound);
    }

    private static final List<String> onlyOneOf = List.of(
            "wildlife_trivia_intro",
            "wildlife_trivia_suspense",
            "wildlife_trivia_suspense_end"
    );
    private static long ticks = 0;
    public static void updateSingleSoundVolumes() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;
        ticks++;
        if (ticks % 15 != 0) return;
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        if (!(soundManager instanceof SoundManagerAccessor managerAccessor)) return;
        SoundEngine soundSystem = managerAccessor.getSoundSystem();
        if (!(soundSystem instanceof SoundEngineAccessor accessor)) return;
        Map<String, Map<Double, SoundInstance>> soundMap = new HashMap<>();
        for (Collection<SoundInstance> soundCategory : accessor.getSounds().asMap().values()) {
            for (SoundInstance sound : soundCategory) {
                //? if <= 1.21.9 {
                String name = sound.getLocation().getPath();
                //?} else {
                /*String name = sound.getIdentifier().getPath();
                *///?}
                if (!onlyOneOf.contains(name)) continue;
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
}
