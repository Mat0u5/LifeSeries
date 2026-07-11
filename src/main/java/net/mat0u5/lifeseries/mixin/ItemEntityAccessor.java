package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(value = ItemEntity.class, priority = 2)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface ItemEntityAccessor {
    @Accessor
    UUID getTarget();
}
