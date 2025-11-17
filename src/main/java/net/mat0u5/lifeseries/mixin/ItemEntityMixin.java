package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.blacklist;

@Mixin(value = ItemEntity.class, priority = 1)
public abstract class ItemEntityMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void onPlayerPickup(Player player, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            if (blacklist == null) return;
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            if (itemEntity.hasPickUpDelay()) return;
            if (itemEntity.level().isClientSide()) return;
            ItemStack stack = itemEntity.getItem();
            blacklist.onCollision(serverPlayer,stack,ci);
        }
    }
    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void preventBurning(CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity instanceof ItemEntityAccessor accessor && accessor.getTarget() != null) {
            cir.setReturnValue(true);
        }
    }
}
