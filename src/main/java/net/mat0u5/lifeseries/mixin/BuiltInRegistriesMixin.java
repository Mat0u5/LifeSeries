package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.registries.ModRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class BuiltInRegistriesMixin {
    @Inject(method = "freeze", at = @At("HEAD"))
    private static void registerPreFreeze(CallbackInfo ci) {
		//? fabric && <= 1.20.5 {
		/*if (CompatibilityManager.fabricApiLoaded()) {
			return;
		}
		*///?}
        ModRegistries.registerModStuff();
    }
}