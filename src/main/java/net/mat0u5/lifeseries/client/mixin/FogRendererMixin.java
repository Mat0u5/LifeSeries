package net.mat0u5.lifeseries.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenEffectsClient;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if <= 1.21 {
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else if <= 1.21.5 {
//import net.minecraft.client.renderer.FogParameters;
//?}
//? if <= 1.21
//import org.spongepowered.asm.mixin.injection.Redirect;
//? if > 1.21 <= 1.21.11
//import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//? if <= 1.21
//import com.mojang.blaze3d.systems.RenderSystem;
//? if <= 1.21.5 {
//import net.minecraft.client.renderer.FogRenderer;
//?} else {
import net.minecraft.client.renderer.fog.FogRenderer;
//?}
//? if >= 1.21.6
import net.minecraft.client.DeltaTracker;
//? if >= 1.21.2
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 26.1
import net.minecraft.client.renderer.fog.FogData;
//? if <= 1.21.5 {
/*import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
*///?} else if <= 1.21.9 {
/*import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.DimensionOrBossFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
*///?}

@Mixin(value = FogRenderer.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class FogRendererMixin {
    @Unique
    private static Vec3 ls$fogColor(Vec3 original) {
        Vec3 inBetweenOverride = InBetweenEffectsClient.getFogColorIfActive();
        if (inBetweenOverride != null) {
            return inBetweenOverride;
        }
        return ClientRenderer.modifyColor(original, LifeSeriesClient.fogColor, LifeSeriesClient.fogColorSetMode, null);
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
//? if !forge {
    //? if <= 1.21 {
    //private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
    //?} else if <= 1.21.5 {
    //private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float f, boolean bl, float g, CallbackInfoReturnable<FogParameters> cir) {
    //?} else if <= 1.21.9 {
    //private static void stopFog(Camera camera, int i, boolean bl, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    //?} else if <= 1.21.11 {
    //private static void stopFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    //?} else {
    private static void stopFog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
    //?}
//?} else {
    /*//? if <= 1.21 {
    //private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
    //?} else if <= 1.21.5 {
    //private static void stopFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float f, boolean bl, float g, CallbackInfoReturnable<FogParameters> cir) {
    //?} else if <= 1.21.9 {
    //private void stopFog(Camera camera, int i, boolean bl, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    //?} else if <= 1.21.11 {
    //private void stopFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
    //?} else {
    private void stopFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<FogData> cir) {
    //?}
*///?}
        ClientLevel nether = Minecraft.getInstance().level;
        if (LifeSeriesClient.fogColor == null && camera.getFluidInCamera() == FogType.NONE && nether != null && nether.dimension() == Level.NETHER &&
                LifeSeriesClient.NICELIFE_SNOWY_NETHER && !LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE)) {
            //? if <= 1.21 {
            //ci.cancel();
            //?} else if <= 1.21.5 {
            //cir.setReturnValue(FogParameters.NO_FOG);
            //?} else if <= 1.21.11 {
            //cir.setReturnValue(new Vector4f(0, 0, 0, 0));
            //?} else {
            FogData fog = new FogData();
            fog.color = new Vector4f(0, 0, 0, 0);
            cir.setReturnValue(fog);
            //?}
        }
    }

    //? if <= 1.21 {
    /*@Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"))
    private static void customFogColor(float r, float g, float b, float a) {
        Vec3 result = ls$fogColor(new Vec3(r, g, b));
        LifeSeriesClient.cachedFogRenderColor = result;
        RenderSystem.clearColor((float) result.x, (float) result.y, (float) result.z, a);
    }

    @Redirect(method = "levelFogColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogColor(FFF)V"))
    private static void customFogColor(float r, float g, float b) {
        Vec3 result = ls$fogColor(new Vec3(r, g, b));
        RenderSystem.setShaderFogColor((float) result.x, (float) result.y, (float) result.z);
    }
    *///?} else if <= 1.21.11 {
    /*@ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
    private static Vector4f customFogColor(Vector4f original) {
        Vec3 color = ls$fogColor(new Vec3(original.x, original.y, original.z));
        Vector4f result = new Vector4f((float) color.x, (float) color.y, (float) color.z, original.w);
        LifeSeriesClient.cachedFogRenderColor = new Vec3(result.x, result.y, result.z);
        return result;
    }
*///?} else {
    //? if fabric || forge {
    @WrapOperation(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"))
    private static Vector4f customFogColor(Vector4f instance, float x, float y, float z, float w, Operation<Vector4f> original) {
        Vec3 color = ls$fogColor(new Vec3(x, y, z));
        Vector4f result = new Vector4f((float) color.x, (float) color.y, (float) color.z, w);
        LifeSeriesClient.cachedFogRenderColor = new Vec3(result.x, result.y, result.z);
        return original.call(instance, result.x, result.y, result.z, result.w);
    }
    //?} else {
    /*@WrapOperation(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;getFogColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IFFFFLorg/joml/Vector4f;)V"))
    private static void customFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue, Vector4f dest, Operation<Void> original) {
        Vec3 result = ls$fogColor(new Vec3(fogRed, fogGreen, fogBlue));
        LifeSeriesClient.cachedFogRenderColor = new Vec3(result.x, result.y, result.z);
        original.call(camera, partialTick, level, renderDistance, darkenWorldAmount, (float)result.x, (float)result.y, (float)result.z, dest);
    }
    *///?}
//?}

    //? if <= 1.21 {
    /*@Inject(method = "setupFog", at = @At("TAIL"))
    private static void inBetweenFog(CallbackInfo ci) {
        if (!ls$hasInBetweenFog()) return;
        RenderSystem.setShaderFogStart(InBetweenEffectsClient.FOG_START);
        RenderSystem.setShaderFogEnd(InBetweenEffectsClient.FOG_END);
    }
    *///?} else if <= 1.21.5 {
    /*@ModifyReturnValue(method = "setupFog", at = @At("RETURN"))
    private static FogParameters inBetweenFog(FogParameters fog) {
        if (fog == FogParameters.NO_FOG || !ls$hasInBetweenFog()) return fog;
        return new FogParameters(InBetweenEffectsClient.FOG_START, InBetweenEffectsClient.FOG_END, fog.shape(), fog.red(), fog.green(), fog.blue(), fog.alpha());
    }
    *///?} else if <= 1.21.9 {
    /*@WrapOperation(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;setupFog(Lnet/minecraft/client/renderer/fog/FogData;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/DeltaTracker;)V"))
    private static void inBetweenFog(FogEnvironment environment, FogData fog, Entity entity, BlockPos pos, ClientLevel level, float renderDistance, DeltaTracker deltaTracker, Operation<Void> original) {
        original.call(environment, fog, entity, pos, level, renderDistance, deltaTracker);
        if (!(environment instanceof DimensionOrBossFogEnvironment) || !InBetweenEffectsClient.isActive()) return;
        fog.environmentalStart = InBetweenEffectsClient.FOG_START;
        fog.environmentalEnd = InBetweenEffectsClient.FOG_END;
        fog.skyEnd = InBetweenEffectsClient.FOG_END;
        fog.cloudEnd = InBetweenEffectsClient.FOG_END;
    }
    *///?}

    //? if <= 1.21.5 {
    /*@Unique
    private static boolean ls$hasInBetweenFog() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!InBetweenEffectsClient.isActive() || camera.getFluidInCamera() != FogType.NONE) return false;
        return !(camera.getEntity() instanceof LivingEntity living) || (!living.hasEffect(MobEffects.BLINDNESS) && !living.hasEffect(MobEffects.DARKNESS));
    }
    *///?}
}
