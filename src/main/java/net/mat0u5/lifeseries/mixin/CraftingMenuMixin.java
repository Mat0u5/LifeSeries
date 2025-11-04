package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <= 1.21 {
import net.minecraft.world.level.Level;
//?} else {
/*import net.minecraft.server.level.ServerLevel;
*///?}

@Mixin(value = CraftingMenu.class, priority = 1)
public class CraftingMenuMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    private static void blockPreviewIfNoCraftingItemPresent(AbstractContainerMenu handler, Level level, Player player,
                                                            CraftingContainer craftingInventory, ResultContainer resultInventory, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
    //?} else {
    /*private static void blockPreviewIfNoCraftingItemPresent(AbstractContainerMenu handler, ServerLevel level, Player player,
                                                            CraftingContainer craftingInventory, ResultContainer resultInventory, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;

        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack stack = craftingInventory.getItem(i);
            if (ItemStackUtils.hasCustomComponentEntry(stack, "NoCrafting") ||
                    ItemStackUtils.hasCustomComponentEntry(stack, "NoModifications")) {
                resultInventory.setItem(0, ItemStack.EMPTY);
                ci.cancel();
                return;
            }
        }
    }
}
