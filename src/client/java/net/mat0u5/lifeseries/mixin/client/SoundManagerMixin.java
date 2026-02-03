package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.utils.ClientSounds;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <= 1.21.5
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.6 {
import net.mat0u5.lifeseries.Main;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}

@Mixin(value = SoundManager.class, priority = 1)
public class SoundManagerMixin {
    //? if <= 1.21.5 {
    /*@Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("HEAD"))
    private void play(SoundInstance sound, CallbackInfo ci) {
        ClientSounds.onSoundPlay(sound);
    }
    *///?} else {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"))
    private void play(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (Main.modFullyDisabled()) return;
        ClientSounds.onSoundPlay(sound);
    }
    //?}
}
