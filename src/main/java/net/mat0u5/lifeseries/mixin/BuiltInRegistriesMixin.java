package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.registries.ModRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

    @Inject(method = "freeze", at = @At("HEAD"))
    private static void registerPreFreeze(CallbackInfo ci) {
        ModRegistries.registerModStuff();
    }
}