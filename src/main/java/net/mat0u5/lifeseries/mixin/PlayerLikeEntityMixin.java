package net.mat0u5.lifeseries.mixin;

//? if < 1.21.9 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class PlayerLikeEntityMixin {
    //Empty class to avoid mixin errors
}
*///?} else {
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Avatar.class, priority = 1)
public class PlayerLikeEntityMixin {
    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    public void getBaseDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (Main.modFullyDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
            if (!morphComponent.isMorphed()) return;

            float scaleRatio = 1 / player.getScale();
            LivingEntity dummy = morphComponent.getDummy();
            if (morphComponent.isMorphed() && dummy != null) {
                cir.setReturnValue(dummy.getDimensions(pose).scale(scaleRatio, scaleRatio));
            }
        }
    }
}
//?}