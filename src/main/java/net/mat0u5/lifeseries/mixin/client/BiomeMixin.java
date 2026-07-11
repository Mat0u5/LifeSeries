package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Biome.class, priority = 2)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class BiomeMixin {
    //? if <= 1.21 {
    /*@Inject(method = "getTemperature", at = @At(value = "HEAD"), cancellable = true)
    public void render(BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
    *///?} else {
    @Inject(method = "getTemperature", at = @At(value = "HEAD"), cancellable = true)
    public void render(BlockPos blockPos, int i, CallbackInfoReturnable<Float> cir) {
    //?}
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE)) {
            cir.setReturnValue(0.0f);
        }
    }
}
