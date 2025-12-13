package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.joml.Vector4f;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if <= 1.21 {
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else if <= 1.21.5 {
/*import net.minecraft.client.renderer.FogParameters;
*///?}

//? if <= 1.21.5 {
import net.minecraft.client.renderer.FogRenderer;
//?} else {
/*import net.minecraft.client.renderer.fog.FogRenderer;
*///?}
//? if >= 1.21.6
/*import net.minecraft.client.DeltaTracker;*/
//? if >= 1.21.2
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;*/


@Mixin(value = FogRenderer.class, priority = 1)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
    //?} else if <= 1.21.5 {
    /*private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float f, boolean bl, float g, CallbackInfoReturnable<FogParameters> cir) {
    *///?} else if <= 1.21.9 {
    /*private static void stopFog(Camera camera, int i, boolean bl, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    *///?} else {
    /*private static void stopFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    *///?}
        ClientLevel nether = Minecraft.getInstance().level;
        if (camera.getFluidInCamera() == FogType.NONE && nether != null && nether.dimension() == Level.NETHER &&
                NiceLife.SNOWY_NETHER && !Main.modDisabled() && currentSeason.getSeason() == Seasons.NICE_LIFE) {
            //? if <= 1.21 {
            ci.cancel();
            //?} else if <= 1.21.5 {
            /*cir.setReturnValue(FogParameters.NO_FOG);
            *///?} else {
            /*cir.setReturnValue(new Vector4f(0, 0, 0, 0));
            *///?}
        }
    }
}
