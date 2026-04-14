package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if <= 1.20.3 {
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.gen.Accessor;
*///?} else {
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}

@Mixin(value = GrindstoneMenu.class, priority = 1)
public abstract class GrindstoneMenuMixin {

    //? if <= 1.20.3 {

    /*@Accessor("resultSlots")
    abstract Container ls$resultSlots();
    @Accessor("repairSlots")
    abstract Container ls$repairSlots();

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void modifyResult(CallbackInfo ci) {
        ItemStack input = this.ls$resultSlots().getItem(0);
        ItemStack additional = this.ls$resultSlots().getItem(1);
        if (ItemStackUtils.hasCustomComponentEntry(input, "NoStonecutter") ||
                ItemStackUtils.hasCustomComponentEntry(input, "NoModifications") ||
                ItemStackUtils.hasCustomComponentEntry(additional, "NoStonecutter") ||
                ItemStackUtils.hasCustomComponentEntry(additional, "NoModifications")
        ) {
            this.ls$repairSlots().setItem(0, ItemStack.EMPTY);
            ci.cancel();
        }
    }
    *///?} else {
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
    //?}
}
