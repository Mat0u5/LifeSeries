package net.mat0u5.lifeseries.mixin;
//? if <= 1.20.2 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public interface CrafterBlockMixin {
    //Empty class to avoid mixin errors
}
*///?} else {

import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
//? if <= 1.20.5 {
/*import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.Level;
*///?} else {
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
//?}

@Mixin(value = CrafterBlock.class, priority = 1)
public class CrafterBlockMixin {
    @Inject(method = "getPotentialResults", at = @At("HEAD"), cancellable = true)
    //? if <= 1.20.5 {
    /*private static void cancelResult(Level level, CraftingContainer input, CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir) {
        for (ItemStack item : input.getItems()) {
    *///?} else if <= 1.21 {
    /*private static void cancelResult(Level level, CraftingInput input, CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir) {
        for (ItemStack item : input.items()) {
    *///?} else {
    private static void cancelResult(ServerLevel level, CraftingInput input, CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir) {
        for (ItemStack item : input.items()) {
    //?}
            if (ItemStackUtils.hasCustomComponentEntry(item, "NoCrafting") ||
                    ItemStackUtils.hasCustomComponentEntry(item, "NoModifications")) {
                cir.setReturnValue(Optional.empty());
            }
        }
    }
}
//?}