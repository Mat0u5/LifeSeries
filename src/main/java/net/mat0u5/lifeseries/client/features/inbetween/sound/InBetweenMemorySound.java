package net.mat0u5.lifeseries.client.features.inbetween.sound;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

// Plays a memory sound straight from the memory cache, it isn't a registered sound event. The audio comes from SoundBufferLibraryMixin.
public class InBetweenMemorySound extends InBetweenWindowSound {
	private static final String PATH_PREFIX = "in_between_memory/";
	private static final String FILE_PREFIX = "sounds/" + PATH_PREFIX;
	private static final int ATTENUATION_DISTANCE = 32;

	private final Sound memorySound;
	private final WeighedSoundEvents memoryEvents;

	public InBetweenMemorySound(String hash, DoubleSupplier volume) {
		super(getLocation(hash), SoundSource.MASTER, false, volume); // No statements before super(), older versions compile with Java 17/21
		Identifier location = getLocation(hash);
		//? if <= 1.20.5 {
		//this.memorySound = new Sound(location.toString(), ConstantFloat.of(1), ConstantFloat.of(1), 1, Sound.Type.FILE, true, false, ATTENUATION_DISTANCE);
		//?} else {
		this.memorySound = new Sound(location, ConstantFloat.of(1), ConstantFloat.of(1), 1, Sound.Type.FILE, true, false, ATTENUATION_DISTANCE);
		//?}
		this.memoryEvents = new WeighedSoundEvents(location, null);
	}

	//? if <= 26.2 {
	/*@Override
	public WeighedSoundEvents resolve(SoundManager soundManager) {
		this.sound = memorySound;
		return memoryEvents;
	}
	*///?} else {
	@Override
	public WeighedSoundEvents getOrResolve(SoundManager soundManager) {
		this.sound = memorySound;
		this.soundEvent = memoryEvents;
		return memoryEvents;
	}
	//?}

	private static Identifier getLocation(String hash) {
		return IdentifierHelper.lifeseries(PATH_PREFIX + hash);
	}

	@Nullable
	public static String getHash(Identifier path) {
		// Identifier lifeseries:sounds/in_between_memory/<hash>.ogg -> <hash>
		if (!path.getNamespace().equals(LifeSeries.MOD_ID)) return null;
		String file = path.getPath();
		if (!file.startsWith(FILE_PREFIX) || !file.endsWith(".ogg")) return null;
		return file.substring(FILE_PREFIX.length(), file.length() - 4);
	}
}
