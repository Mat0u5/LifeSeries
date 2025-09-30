package net.mat0u5.lifeseries.mixin;

//? if < 1.21.9 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class PlayerLikeEntityMixin {
    //Empty class to avoid mixin errors
}
//?} else {
/*import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerLikeEntity.class, priority = 1)
public class PlayerLikeEntityMixin {
    @Inject(method = "getBaseDimensions", at = @At("HEAD"), cancellable = true)
    public void getBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (Main.modFullyDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
            if (!morphComponent.isMorphed()) return;

            float scaleRatio = 1 / player.getScale();
            LivingEntity dummy = morphComponent.getDummy();
            if (morphComponent.isMorphed() && dummy != null) {
                cir.setReturnValue(dummy.getDimensions(pose).scaled(scaleRatio, scaleRatio));
            }
        }
    }
}
*///?}