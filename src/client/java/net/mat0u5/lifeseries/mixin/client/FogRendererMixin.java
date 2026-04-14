package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.LifeSeriesClient;
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.joml.Vector4f;

//? if <= 1.21 {
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?} else if <= 1.21.5 {
/*import net.minecraft.client.renderer.FogParameters;
*///?}
//? if <= 1.21
//import org.spongepowered.asm.mixin.injection.Redirect;
//? if > 1.21 <= 1.21.11
//import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//? if <= 1.21
//import com.mojang.blaze3d.systems.RenderSystem;

//? if <= 1.21.5 {
/*import net.minecraft.client.renderer.FogRenderer;
*///?} else {
import net.minecraft.client.renderer.fog.FogRenderer;
//?}
//? if >= 1.21.6
import net.minecraft.client.DeltaTracker;
//? if >= 1.21.2
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = FogRenderer.class, priority = 1)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    /*private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
    *///?} else if <= 1.21.5 {
    /*private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float f, boolean bl, float g, CallbackInfoReturnable<FogParameters> cir) {
    *///?} else if <= 1.21.9 {
    /*private static void stopFog(Camera camera, int i, boolean bl, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    *///?} else {
    private static void stopFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    //?}
        ClientLevel nether = Minecraft.getInstance().level;
        if (LifeSeriesClient.fogColor == null && camera.getFluidInCamera() == FogType.NONE && nether != null && nether.dimension() == Level.NETHER &&
                LifeSeriesClient.NICELIFE_SNOWY_NETHER && !LifeSeries.modDisabled() && LifeSeriesClient.clientCurrentSeason == Seasons.NICE_LIFE) {
            //? if <= 1.21 {
            /*ci.cancel();
            *///?} else if <= 1.21.5 {
            /*cir.setReturnValue(FogParameters.NO_FOG);
            *///?} else {
            cir.setReturnValue(new Vector4f(0, 0, 0, 0));
            //?}
        }
    }

    //? if <= 1.21 {
    /*@Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"))
    private static void customFogColor(float r, float g, float b, float a) {
        Vec3 result = ClientRenderer.modifyColor(r, g, b, LifeSeriesClient.fogColor, LifeSeriesClient.fogColorSetMode, null);
        LifeSeriesClient.cachedFogRenderColor = result;
        RenderSystem.clearColor((float) result.x, (float) result.y, (float) result.z, a);
    }

    @Redirect(method = "levelFogColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogColor(FFF)V"))
    private static void customFogColor(float r, float g, float b) {
        Vec3 result = ClientRenderer.modifyColor(r, g, b, LifeSeriesClient.fogColor, LifeSeriesClient.fogColorSetMode, null);
        RenderSystem.setShaderFogColor((float) result.x, (float) result.y, (float) result.z);
    }
    *///?} else if <= 1.21.11{
    /*@ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
    private static Vector4f customFogColor(Vector4f original) {
        Vector4f result = ClientRenderer.modifyColor(original, LifeSeriesClient.fogColor, LifeSeriesClient.fogColorSetMode, null);
        LifeSeriesClient.cachedFogRenderColor = new Vec3(result.x, result.y, result.z);
        return result;
    }
    *///?} else {
    @WrapOperation(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"))
    private static Vector4f customFogColor(Vector4f instance, float x, float y, float z, float w, Operation<Vector4f> original) {
        Vector4f originalColor = new Vector4f(x, y, z, w);
        Vector4f result = ClientRenderer.modifyColor(originalColor, LifeSeriesClient.fogColor, LifeSeriesClient.fogColorSetMode, null);
        LifeSeriesClient.cachedFogRenderColor = new Vec3(result.x, result.y, result.z);
        return original.call(instance, result.x, result.y, result.z, result.w);
    }
    //?}


}
