package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.events.ClientEvents;
import net.mat0u5.lifeseries.utils.interfaces.IEntity;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 2)
public class LivingEntityMixin {

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        if (Main.modDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        ClientEvents.onClientJump(entity);
    }

    @ModifyArg(
            //? if <= 1.21 {
            method = "travel",
            //?} else {
            /*method = "travelInAir",
            *///?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"),
            index = 1
    )
    private float applyMovementInput(float slipperiness) {
        if ((System.currentTimeMillis() - MainClient.CURSE_SLIDING) > 2000 || Main.modFullyDisabled()) return slipperiness;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player playerr && MainClient.isClientPlayer(playerr.getUUID()) && playerr.onGround() && ClientEvents.onGroundFor >= 5) {
            return 1.198f;
        }
        return slipperiness;
    }

    @ModifyArg(
            method = "handleRelativeFrictionAndCalculateMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"),
            index = 0
    )
    private Vec3 applyMovementInput(Vec3 velocity) {
        if ((System.currentTimeMillis() - MainClient.CURSE_SLIDING) > 2000 || Main.modFullyDisabled()) return velocity;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player playerr && entity instanceof IEntity entityAccessor && MainClient.isClientPlayer(playerr.getUUID()) && playerr.onGround() && ClientEvents.onGroundFor >= 5) {
            BlockPos blockPos = entityAccessor.ls$getBlockPosBelowThatAffectsMyMovement();
            float originalSlipperiness = playerr.level().getBlockState(blockPos).getBlock().getFriction();
            return new Vec3((velocity.x/originalSlipperiness)*0.995f, velocity.y, (velocity.z/originalSlipperiness)*0.995f);
        }
        return velocity;
    }
}
