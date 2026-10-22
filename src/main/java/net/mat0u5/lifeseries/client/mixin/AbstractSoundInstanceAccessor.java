package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractSoundInstance.class, priority = 2)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface AbstractSoundInstanceAccessor {
    @Mutable
    @Accessor
    void setVolume(float volume);
}
