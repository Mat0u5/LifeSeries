package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.mixin.client.EntityTrackingSoundInstanceAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
}
