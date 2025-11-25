package net.mat0u5.lifeseries.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.sounds.SoundEngine;

//? if >= 1.20.3 {
/*import net.mat0u5.lifeseries.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
*///?}

@Mixin(value = SoundEngine.class, priority = 1)
public class SoundEngineMixin {
    //? if >= 1.20.3 {
    /*@Unique
    private static final List<String> ls$nonAdjustedSounds = List.of(
            "block.beacon.deactivate",
            "wildlife_time_slow_down",
            "wildlife_time_speed_up"
    );
    @Inject(method = "calculatePitch", at = @At("HEAD"), cancellable = true)
    private void getAdjustedPitch(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        //? if <= 1.21.9 {
        String name = sound.getLocation().getPath();
        //?} else {
        /^String name = sound.getIdentifier().getPath();
         ^///?}
        if (ls$nonAdjustedSounds.contains(name) || Main.modFullyDisabled()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.level != null) {
            TickRateManager tickManager = client.level.tickRateManager();
            if (tickManager.tickrate() != 20) {
                cir.setReturnValue(Mth.clamp(sound.getPitch(), 0.5F, 2.0F) * (tickManager.tickrate() / 20.0f));
            }
        }
    }
    *///?}
}
