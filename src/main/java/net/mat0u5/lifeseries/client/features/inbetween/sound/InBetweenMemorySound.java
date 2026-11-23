package net.mat0u5.lifeseries.client.features.inbetween.sound;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

// Plays a memory sound straight from the memory cache, it isn't a registered sound event. The audio comes from SoundBufferLibraryMixin.
public class InBetweenMemorySound extends InBetweenFileSound {
	private static final String PATH_PREFIX = "in_between_memory/";
	private static final String FILE_PREFIX = "sounds/" + PATH_PREFIX;
	private static final int ATTENUATION_DISTANCE = 32;

	public InBetweenMemorySound(String hash, DoubleSupplier volume) {
		super(IdentifierHelper.lifeseries(PATH_PREFIX + hash), SoundSource.MASTER, true, ATTENUATION_DISTANCE, volume);
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
