package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameRenderer.class, priority = 1)
public class GameRendererMixin {
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    //? if <= 1.21 {
    public double clampFov(double original) {
    //?} else {
    /*public float clampFov(float original) {
    *///?}
        return Math.clamp(original, 0f, 150f);
    }
}
