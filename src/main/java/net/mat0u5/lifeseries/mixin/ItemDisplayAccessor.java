package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.ItemDisplay.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface ItemDisplayAccessor {
    @Invoker("setItemStack")
    void ls$setItemStack(ItemStack stack);
}
