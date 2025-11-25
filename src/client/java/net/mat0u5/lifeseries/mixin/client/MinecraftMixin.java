package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
//? if >= 1.20.3 {
/*import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

@Mixin(value = Minecraft.class, priority = 1)
public abstract class MinecraftMixin {

    //? if >= 1.20.3 {
    /*@Inject(method = "getTickTargetMillis", at = @At("HEAD"), cancellable = true)
    private void getTargetMillisPerTick(float millis, CallbackInfoReturnable<Float> cir) {
        if (Main.modFullyDisabled()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.level != null) {
            TickRateManager tickManager = client.level.tickRateManager();
            if (MainClient.TIME_DILATION_TIMESTAMP != 0) {
                long timeSinceDilationActivate = System.currentTimeMillis() - MainClient.TIME_DILATION_TIMESTAMP;
                if (timeSinceDilationActivate < 14000) {
                    if (timeSinceDilationActivate < 1000) {
                        float tickRate = 1000/(1-(timeSinceDilationActivate / 4050.0f));
                        cir.setReturnValue(tickRate);
                        ClientRenderer.isGameFullyFrozen = false;
                        return;
                    }
                    if (timeSinceDilationActivate <= 10000) {
                        cir.setReturnValue(500000f);
                        ClientRenderer.isGameFullyFrozen = true;
                        return;
                    }
                    float tickRate = 1000/((timeSinceDilationActivate-10000) / 4050.0f);
                    cir.setReturnValue(tickRate);
                    ClientRenderer.isGameFullyFrozen = false;
                    return;
                }
            }
            if (tickManager.runsNormally()) {
                float mspt = Math.max(TimeDilation.MIN_PLAYER_MSPT, tickManager.millisecondsPerTick());
                cir.setReturnValue(mspt);
                ClientRenderer.isGameFullyFrozen = false;
                return;
            }
        }
        cir.setReturnValue(millis);
        ClientRenderer.isGameFullyFrozen = false;
    }
    *///?}
}
