package net.mat0u5.lifeseries.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void onHealHead(float amount, CallbackInfo info) {
        if (!currentSeries.NO_HEALING) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            info.cancel();
        }
    }

    @Inject(method = "heal", at = @At("TAIL"))
    private void onHeal(float amount, CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            currentSeries.onPlayerHeal((ServerPlayerEntity) player, amount);
        }
    }
}
