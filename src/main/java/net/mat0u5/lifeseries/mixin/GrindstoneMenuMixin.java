package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GrindstoneMenu.class, priority = 1)
public class GrindstoneMenuMixin {

    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    private void modifyResult(ItemStack input, ItemStack additional, CallbackInfoReturnable<ItemStack> cir) {
        if (ItemStackUtils.hasCustomComponentEntry(input, "NoStonecutter") ||
                ItemStackUtils.hasCustomComponentEntry(input, "NoModifications") ||
                ItemStackUtils.hasCustomComponentEntry(additional, "NoStonecutter") ||
                ItemStackUtils.hasCustomComponentEntry(additional, "NoModifications")
        ) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
