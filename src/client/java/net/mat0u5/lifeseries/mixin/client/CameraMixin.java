package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
//? if >= 1.21.6
/*import net.minecraft.entity.attribute.EntityAttributes;*/

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
    private Entity entity;

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"
            ),
            index = 0
    )
    private float modifyEntityScale(float originalDistance) {
        if (!(entity instanceof Player player) || Main.modFullyDisabled()) return originalDistance;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if (dummy != null) {
                float playerHeight = player.getDimensions(Pose.STANDING).height();
                float morphedHeight = dummy.getDimensions(Pose.STANDING).height();
                float heightScale = morphedHeight / playerHeight;
                float cameraDistance = 4.0F;
                //? if >= 1.21.6 {
                /*cameraDistance = (float)player.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
                 *///?}
                return heightScale * cameraDistance;
            }
        }
        return originalDistance;
    }
}