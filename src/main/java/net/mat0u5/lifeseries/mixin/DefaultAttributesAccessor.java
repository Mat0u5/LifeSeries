package net.mat0u5.lifeseries.mixin;

import java.util.Map;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

@Mixin(DefaultAttributes.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface DefaultAttributesAccessor {
    @Accessor("SUPPLIERS")
    static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getRegistry() {
        throw new AssertionError("mixin dummy");
    }
}