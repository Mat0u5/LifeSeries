package net.mat0u5.lifeseries.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.blacklist;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerPickup(PlayerEntity player, CallbackInfo ci) {
        if (blacklist == null) return;
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.cannotPickup()) return;
        if (itemEntity.getWorld().isClient) return;
        ItemStack stack = itemEntity.getStack();
        blacklist.onCollision(player,stack,ci);
    }
}
