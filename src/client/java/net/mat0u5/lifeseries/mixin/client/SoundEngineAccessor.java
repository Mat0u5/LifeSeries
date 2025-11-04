package net.mat0u5.lifeseries.mixin.client;

import com.google.common.collect.Multimap;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SoundEngine.class, priority = 2)
public interface SoundEngineAccessor {

    @Accessor("instanceBySource")
    Multimap<SoundSource, SoundInstance> getSounds();
}
