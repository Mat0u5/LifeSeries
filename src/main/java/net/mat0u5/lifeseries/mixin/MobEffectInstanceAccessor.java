package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MobEffectInstance.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface MobEffectInstanceAccessor {
    @Mutable
    @Accessor("amplifier")
    void ls$setAmplifier(int amplifier);
}
