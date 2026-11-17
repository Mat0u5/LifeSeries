package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.features.inbetween.sound.EchoAudioStream;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenMemoryCache;
import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenMemorySound;
import net.mat0u5.lifeseries.client.features.inbetween.sound.SilentAudioStream;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;

//? if <= 1.20.3 {
/*import com.mojang.blaze3d.audio.OggAudioStream;
*///?} else {
import net.minecraft.client.sounds.JOrbisAudioStream;
//?}

@Mixin(value = SoundBufferLibrary.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class SoundBufferLibraryMixin {
	@Inject(method = "getStream", at = @At("HEAD"), cancellable = true)
	private void memoryStream(Identifier location, boolean looping, CallbackInfoReturnable<CompletableFuture<AudioStream>> cir) {
		String hash = InBetweenMemorySound.getHash(location);
		if (hash == null) return;
		cir.setReturnValue(CompletableFuture.supplyAsync(() -> {
			byte[] data = InBetweenMemoryCache.readFile(hash);
			if (data == null) {
				LifeSeries.LOGGER.warn("[In-Between] Memory sound {} is not downloaded.", hash);
				return new SilentAudioStream();
			}
			try {
				//? if <= 1.20.3 {
				//return new EchoAudioStream(new OggAudioStream(new ByteArrayInputStream(data)));
				//?} else {
				return new EchoAudioStream(new JOrbisAudioStream(new ByteArrayInputStream(data)));
				//?}
			} catch (Exception e) {
				LifeSeries.LOGGER.error("[In-Between] Memory sound {} could not be decoded, it must be an OGG Vorbis file.", hash, e);
				return new SilentAudioStream();
			}
		}));
	}
}
