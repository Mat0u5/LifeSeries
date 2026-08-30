package net.mat0u5.lifeseries.config.modifiable;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Locale;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public enum ModifiableSound {
	TEST(SoundEvents.AMBIENT_WARPED_FOREST_MOOD);

	final String name;
	final Seasons requiredSeason;
	final String defaultSoundId;
	String cachedSoundId;
	SoundEvent cachedSound;

	ModifiableSound(Holder.Reference<SoundEvent> soundRef) {
		this(null, soundRef.value());
	}

	ModifiableSound(SoundEvent sound) {
		this(null, sound);
	}

	ModifiableSound(Seasons season, Holder.Reference<SoundEvent> soundRef) {
		this(season, soundRef.value());
	}

	ModifiableSound(Seasons season, SoundEvent sound) {
		this.requiredSeason = season;
		this.name = this.name().toLowerCase(Locale.ROOT).replace("_",".");
		//~ if >= 1.21.9 '.getLocation()' -> '.location()' {
		String soundId = sound.location().toString();
		//~}
		this.defaultSoundId = soundId;
		this.cachedSoundId = soundId;
		this.cachedSound = sound;
	}

	public SoundEvent get() {
		return ModifiableSoundManager.get(this);
	}

	public static void registerAllSounds() {
		for (ModifiableSound modifiableSound : ModifiableSound.values()) {
			if (modifiableSound.requiredSeason != null && currentSeason != null && currentSeason.getSeason() != modifiableSound.requiredSeason) continue;
			ModifiableSoundManager.register(modifiableSound.name, modifiableSound.cachedSoundId);
		}
	}

	public static ModifiableSound fromName(String name) {
		if (name == null) return null;
		if (name.startsWith("sound.")) name = name.substring(6);
		for (ModifiableSound modifiableSound : ModifiableSound.values()) {
			if (modifiableSound.name.equals(name)) {
				return modifiableSound;
			}
		}
		return null;
	}
}
