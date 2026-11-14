package net.mat0u5.lifeseries.config.modifiable;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static net.mat0u5.lifeseries.LifeSeries.seasonConfig;

public class ModifiableSoundManager {
	public record SoundEntry(String key, String defaultValue) {
		public String configKey() {
			return "sound." + key;
		}
	}

	private static Map<String, SoundEntry> registeredEntries = new TreeMap<>();
	private static boolean initialized = false;

	public static void initialize() {
		initialized = true;
		registeredEntries.clear();
		LifeSeries.LOGGER.info("Loading modifiable sounds...");
		ModifiableSound.registerAllSounds();
		LifeSeries.LOGGER.info("Loaded "+registeredEntries.size()+" modifiable sounds");
	}

	public static Map<String, SoundEntry> getRegisteredEntries() {
		return registeredEntries;
	}

	public static boolean isRegistered(ModifiableSound modifiableSound) {
		if (!initialized) {
			initialize();
		}
		return registeredEntries.containsKey(modifiableSound.name);
	}

	public static List<String> getSoundBehaviorEntries() {
		List<String> result = new ArrayList<>();
		for (SoundBehavior behavior : SoundBehavior.values()) {
			if (!behavior.getFollowUps().isEmpty()) {
				// FOLLOWED_BY;sound_id;follow_up_sound_id;volume;pitch
				for (SoundBehavior.FollowUpSound followUp : behavior.getFollowUps()) {
					result.add(behavior.name() + ";" + followUp.sound().getCurrentSoundId() + ";" + followUp.followUp().getCurrentSoundId() + ";" + followUp.volume() + ";" + followUp.pitch());
				}
				continue;
			}
			for (ModifiableSound sound : behavior.getSounds()) {
				// BEHAVIOR;sound_id
				result.add(behavior.name() + ";" + sound.getCurrentSoundId());
			}
		}
		return result;
	}

	public static void register(String key, String defaultSoundId, String soundId) {
		if (registeredEntries.containsKey(key)) {
			LifeSeries.LOGGER.error("Tried to register duplicate key for modifiable sound: "+key);
			return;
		}
		SoundEntry entry = new SoundEntry(key, defaultSoundId);
		if (seasonConfig != null) {
			seasonConfig.getOrCreateProperty(entry.configKey(), defaultSoundId); // To initialize it
		}
		registeredEntries.put(key, entry);
	}

	public static SoundEvent get(ModifiableSound modifiableSound) {
		if (!initialized) {
			initialize();
		}
		String key = modifiableSound.name;
		SoundEntry configEntry = registeredEntries.get(key);
		if (configEntry == null) {
			LifeSeries.LOGGER.warn("Could not find modifiable sound " + key);
			return SoundEvents.EMPTY;
		}
		String configSoundId = seasonConfig.getOrCreateProperty(configEntry.configKey(), configEntry.defaultValue());
		if (!configSoundId.contains(":")) {
			configSoundId = "minecraft:"+configSoundId;
		}
		if (configSoundId.equalsIgnoreCase(modifiableSound.cachedSoundId) && modifiableSound.cachedSound != null) {
			return modifiableSound.cachedSound;
		}

		modifiableSound.cachedSoundId = configSoundId;
		modifiableSound.cachedSound = null;
		try {
			var identifier = IdentifierHelper.parse(configSoundId);
			if (identifier.getPath().isEmpty()) {
				modifiableSound.cachedSound = SoundEvents.EMPTY;
				return SoundEvents.EMPTY;
			}
			ResourceKey<SoundEvent> soundResource = ResourceKey.create(BuiltInRegistries.SOUND_EVENT.key(), identifier);
			//? if <= 1.21 {
			//SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundResource);
			 //?} else {
			SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundResource);
			//?}
			if (sound != null) {
				modifiableSound.cachedSound = sound;
				return sound;
			}
			else {
				sound = SoundEvent.createVariableRangeEvent(identifier);
				modifiableSound.cachedSound = sound;
				return sound;
			}
		}catch(Exception e) {}
		return SoundEvents.EMPTY;
	}
}
