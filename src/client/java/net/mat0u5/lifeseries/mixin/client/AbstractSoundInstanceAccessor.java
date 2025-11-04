package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractSoundInstance.class, priority = 2)
public interface AbstractSoundInstanceAccessor {
    @Mutable
    @Accessor
    void setVolume(float volume);
}
