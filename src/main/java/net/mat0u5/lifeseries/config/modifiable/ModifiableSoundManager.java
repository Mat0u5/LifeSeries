package net.mat0u5.lifeseries.config.modifiable;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Map;
import java.util.TreeMap;

public class ModifiableSoundManager {
	private static Map<String, ConfigFileEntry<String>> registeredEntries = new TreeMap<>();
	private static boolean initialized = false;

	public static void initialize() {
		initialized = true;
		registeredEntries.clear();
		LifeSeries.LOGGER.info("Loading modifiable sounds...");
		ModifiableSound.registerAllSounds();
		LifeSeries.LOGGER.info("Loaded "+registeredEntries.size()+" modifiable sounds");
	}

	public static void register(String key, String soundId) {
		if (registeredEntries.containsKey(key)) {
			LifeSeries.LOGGER.error("Tried to register duplicate key for modifiable sound: "+key);
			return;
		}
		ConfigFileEntry<String> configEntry = new ConfigFileEntry<>("sound."+key, soundId, ConfigTypes.MODIFIABLE_SOUND, "sound", key, "");
		configEntry.get(); // To initialize it
		registeredEntries.put(key, configEntry);
	}

	public static SoundEvent get(ModifiableSound modifiableSound) {
		if (!initialized) {
			initialize();
		}
		String key = modifiableSound.name;
		ConfigFileEntry<String> configEntry = registeredEntries.get(key);
		if (configEntry == null) {
			LifeSeries.LOGGER.warn("Could not find modifiable sound " + key);
			return SoundEvents.EMPTY;
		}
		String configSoundId = configEntry.get();
		if (configSoundId.equalsIgnoreCase(modifiableSound.cachedSoundId)) {
			return modifiableSound.cachedSound;
		}

		modifiableSound.cachedSoundId = configSoundId;

		try {
			ResourceKey<SoundEvent> soundResource = ResourceKey.create(BuiltInRegistries.SOUND_EVENT.key(), IdentifierHelper.parse(configSoundId));
			//? if <= 1.21 {
			/*SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundResource);
			 *///?} else {
			SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundResource);
			//?}
			if (sound != null) {
				return sound;
			}
		}catch(Exception e) {}
		return SoundEvents.EMPTY;
	}
}
