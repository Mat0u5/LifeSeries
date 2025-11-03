package net.mat0u5.lifeseries.mixin;

import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemCombinerMenu.class, priority = 1)
public interface ForgingScreenHandlerAccessor {
    @Accessor("resultSlots")
    ResultContainer getOutput();
}

