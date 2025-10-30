package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.blacklist;

@Mixin(value = ItemEntity.class, priority = 1)
public abstract class ItemEntityMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerPickup(PlayerEntity player, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (blacklist == null) return;
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            if (itemEntity.cannotPickup()) return;
            if (itemEntity.ls$getEntityWorld().isClient()) return;
            ItemStack stack = itemEntity.getStack();
            blacklist.onCollision(serverPlayer,stack,ci);
        }
    }
}
