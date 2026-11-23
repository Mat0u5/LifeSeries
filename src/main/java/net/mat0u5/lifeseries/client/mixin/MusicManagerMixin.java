package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class MusicManagerMixin {
	@Shadow
	@Nullable
	private SoundInstance currentMusic;
	@Shadow
	private int nextSongDelay;

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void silenceMusic(CallbackInfo ci) {
		if (!InBetweenManager.isInInBetween(Minecraft.getInstance().player)) return;
		if (currentMusic != null) {
			Minecraft.getInstance().getSoundManager().stop(currentMusic);
			currentMusic = null;
		}
		nextSongDelay = Math.max(nextSongDelay, 100);
		ci.cancel();
	}
}
