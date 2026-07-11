package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value = EntityBoundSoundInstance.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface EntityBoundSoundInstanceAccessor {
    @Accessor("entity")
    Entity getEntity();
}