package net.mat0u5.lifeseries.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HopperBlockEntity.class, priority = 1)
public class HopperBlockEntityMixin {

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void dontSuckOwnerItems(Container container, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        if (itemEntity instanceof ItemEntityAccessor accessor && accessor.getTarget() != null) {
            cir.setReturnValue(false);
        }
    }
}
