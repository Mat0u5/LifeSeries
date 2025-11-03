package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mixin(value = ItemStack.class, priority = 1)
public class ItemStackMixin {
    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void areItemsAndComponentsEqual(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(otherStack.getItem()) || Main.modDisabled()) return;

        if (stack.isEmpty() && otherStack.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }
        if (stack.equals(otherStack)) {
            cir.setReturnValue(true);
            return;
        }
        //? if <= 1.21 {
        PatchedDataComponentMap comp1 = new PatchedDataComponentMap(stack.getComponents());
        PatchedDataComponentMap comp2 = new PatchedDataComponentMap(otherStack.getComponents());
        //?} else {
            /*MergedComponentMap comp1 = new MergedComponentMap(stack.getComponents());
            MergedComponentMap comp2 = new MergedComponentMap(otherStack.getComponents());
            *///?}

        comp1.set(DataComponents.FOOD, stack.getPrototype().get(DataComponents.FOOD));
        comp2.set(DataComponents.FOOD, stack.getPrototype().get(DataComponents.FOOD));
        //? if >= 1.21.2 {
            /*comp1.set(DataComponentTypes.CONSUMABLE, stack.getDefaultComponents().get(DataComponentTypes.CONSUMABLE));
            comp2.set(DataComponentTypes.CONSUMABLE, stack.getDefaultComponents().get(DataComponentTypes.CONSUMABLE));
             *///?}
        if (Objects.equals(comp1, comp2)) {
            cir.setReturnValue(true);
            return;
        }

        boolean componentsEqual = true;

        Set<DataComponentType<?>> allTypes = new HashSet<>();
        allTypes.addAll(comp1.keySet());
        allTypes.addAll(comp2.keySet());

        for (DataComponentType<?> type : allTypes) {
            if (type.equals(DataComponents.FOOD)) continue;
            //? if >= 1.21.2
            /*if (type.equals(DataComponentTypes.CONSUMABLE)) continue;*/

            Object value1 = comp1.get(type);
            Object value2 = comp2.get(type);

            if (!Objects.equals(value1, value2)) {
                componentsEqual = false;
                break;
            }
        }

        cir.setReturnValue(componentsEqual);
    }
}
