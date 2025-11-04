package net.mat0u5.lifeseries.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.21.6 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
@Mixin(value = PlayerRenderer.class, priority = 1)
//?} else {
/*import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.ModifyArg;
@Mixin(value = AvatarRenderer.class, priority = 1)
*///?}
public abstract class PlayerEntityRendererMixin {

    //? if <= 1.21 {
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    public void replaceRendering(AbstractClientPlayer abstractClientPlayerEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        if (Main.modFullyDisabled()) return;
        if (MainClient.invisiblePlayers.containsKey(abstractClientPlayerEntity.getUUID())) {
            long time = MainClient.invisiblePlayers.get(abstractClientPlayerEntity.getUUID());
            if (time > System.currentTimeMillis() || time == -1) {
                ci.cancel();
                return;
            }
        }

        MorphComponent morphComponent = MorphManager.getOrCreateComponent(abstractClientPlayerEntity.getUUID());
        LivingEntity dummy = morphComponent.getDummy();
        if(morphComponent.isMorphed() && dummy != null) {
            ci.cancel();
        }
    }
    //?}

    //? if > 1.21.6 {
    /*@ModifyArg(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V"),
            index = 3
    )
    //TODO test
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    *///?}
}