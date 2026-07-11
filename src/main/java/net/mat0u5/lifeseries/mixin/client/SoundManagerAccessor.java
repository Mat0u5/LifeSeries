package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SoundManager.class, priority = 2)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface SoundManagerAccessor {

    @Accessor("soundEngine")
    SoundEngine getSoundSystem();
}
