package net.mat0u5.lifeseries.config.modifiable;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Locale;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public enum ModifiableSound {
	TEST(SoundEvents.AMBIENT_WARPED_FOREST_MOOD),
	TEST2(IdentifierHelper.mod("lastlife_boogeyman_cure"));

	final String name;
	final Seasons requiredSeason;
	final String defaultSoundId;
	String cachedSoundId;
	SoundEvent cachedSound;

	ModifiableSound(Identifier sound) {
		this(null, SoundEvent.createVariableRangeEvent(sound));
	}

	ModifiableSound(Holder.Reference<SoundEvent> soundRef) {
		this(null, soundRef.value());
	}

	ModifiableSound(SoundEvent sound) {
		this(null, sound);
	}

	ModifiableSound(Seasons season, Identifier sound) {
		this(season, SoundEvent.createVariableRangeEvent(sound));
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

	public void play(ServerPlayer player) {
		PlayerUtils.playSoundToPlayer(player, this.get());
	}

	public void play(ServerPlayer player, float volume, float pitch) {
		PlayerUtils.playSoundToPlayer(player, this.get(), volume, pitch);
	}

	public void play(List<ServerPlayer> players) {
		PlayerUtils.playSoundToPlayers(players, this.get());
	}

	public void play(List<ServerPlayer> players, float volume, float pitch) {
		PlayerUtils.playSoundToPlayers(players, this.get(), volume, pitch);
	}

	public static void registerAllSounds() {
		for (ModifiableSound modifiableSound : ModifiableSound.values()) {
			if (modifiableSound.requiredSeason != null && currentSeason != null && currentSeason.getSeason() != modifiableSound.requiredSeason) continue;
			ModifiableSoundManager.register(modifiableSound.name, modifiableSound.defaultSoundId, modifiableSound.cachedSoundId);
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
