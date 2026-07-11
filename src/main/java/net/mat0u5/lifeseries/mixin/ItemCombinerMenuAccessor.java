package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemCombinerMenu.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface ItemCombinerMenuAccessor {
    @Accessor("resultSlots")
    ResultContainer getOutput();
}

