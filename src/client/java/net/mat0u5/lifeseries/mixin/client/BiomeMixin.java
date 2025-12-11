package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Biome.class, priority = 2)
public class BiomeMixin {
    //? if <= 1.21 {
    @Inject(method = "getTemperature", at = @At(value = "HEAD"), cancellable = true)
    public void render(BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
    //?} else {
    /*@Inject(method = "getTemperature", at = @At(value = "HEAD"), cancellable = true)
    public void render(BlockPos blockPos, int i, CallbackInfoReturnable<Float> cir) {
    *///?}
        if (!Main.modDisabled() && MainClient.clientCurrentSeason == Seasons.NICE_LIFE) {
            cir.setReturnValue(0.0f);
        }
    }
}
