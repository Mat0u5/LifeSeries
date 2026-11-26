package net.mat0u5.lifeseries.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.21.6 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?} else {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.matlib.client.utils.interfaces.IEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
//?}
//? if >= 1.21.11 <= 26.2 {
/*import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
*///?}
//? if >= 26.3 {
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(value = LevelRenderer.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class LevelRendererMixin {
    /**
     * For >= 26.3, located in:
     * {@link CloudRendererMixin}
     */
    //? if >= 1.21.11 <= 26.2 {
    /*@ModifyVariable(method = "addCloudsPass", at = @At("HEAD"), index = 7, argsOnly = true)
    private int setCloudColor(int value) {
        return ClientRenderer.modifyColor(value, LifeSeriesClient.cloudColor, LifeSeriesClient.cloudColorSetMode, LifeSeriesClient.cachedFogRenderColor);
    }
    *///?}

    //? if >= 26.3 {
    @Inject(method = "executeClassicTransparency", at = @At("HEAD"))
    private void onDepth(CallbackInfo ci) {
        InBetweenShaders.copySceneDepth();
    }
    //?}

    //? if <= 1.21.6 {
    /*@Inject(method = "renderEntity", at = @At("TAIL"))
    private void submitGhoseEntities(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo ci) {
        if (!DashClient.hasTrail(entity)) return;
        double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        //? if <= 1.21 {
        //float yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        //?}
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        int light = dispatcher.getPackedLightCoords(entity, partialTick);
        Matrix4f pose = new Matrix4f(poseStack.last().pose());
        Matrix3f normal = new Matrix3f(poseStack.last().normal());
        Runnable render = () -> {
            PoseStack ghostPoseStack = new PoseStack();
            ghostPoseStack.last().pose().set(pose);
            ghostPoseStack.last().normal().set(normal);
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            bufferSource.endBatch();
            DashClient.forEachGhost(entity, x, y, z, (dx, dy, dz) -> {
                //? if <= 1.21 {
                //dispatcher.render(entity, x - camX + dx, y - camY + dy, z - camZ + dz, yRot, partialTick, ghostPoseStack, bufferSource, light);
                //?} else {
                dispatcher.render(entity, x - camX + dx, y - camY + dy, z - camZ + dz, partialTick, ghostPoseStack, bufferSource, light);
                //?}
            }, bufferSource::endBatch);
        };
        if (Minecraft.useShaderTransparency()) {
            render.run();
        }
        else {
            DashClient.deferGhosts(render);
        }
    }
    *///?} else {
    //? if <= 1.21.11 {
    //@WrapOperation(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    //?} else {
    @WrapOperation(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    //?}
    private void submitGhoseEntities(EntityRenderDispatcher dispatcher, EntityRenderState state, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector collector, Operation<Void> original) {
        original.call(dispatcher, state, camera, x, y, z, poseStack, collector);
        if (!(state instanceof IEntityRenderState accessor) || accessor.ml$getEntity() == null || !DashClient.hasTrail(accessor.ml$getEntity())) return;
        Entity entity = accessor.ml$getEntity();
        Runnable render = () -> DashClient.forEachGhost(entity, state.x, state.y, state.z, (dx, dy, dz) -> dispatcher.submit(state, camera, x + dx, y + dy, z + dz, new PoseStack(), collector), () -> {});
        //? if <= 26.1 {
        //DashClient.deferGhosts(render);
        //?} else {
        render.run();
        //?}
    }
    //?}
}
