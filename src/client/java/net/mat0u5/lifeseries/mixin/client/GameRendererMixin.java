package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
@Mixin(value = GameRenderer.class, priority = 1)
//?} else {
/*import net.minecraft.client.Camera;
@Mixin(value = Camera.class, priority = 2)
*///?}
public class GameRendererMixin {
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    //? if <= 1.21 {
    /*public double clampFov(double original) {
    *///?} else {
    public float clampFov(float original) {
    //?}
        if (Main.modFullyDisabled()) return original;
        return OtherUtils.clamp(original, 0f, 150f);
    }
}
