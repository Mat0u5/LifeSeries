package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Options.class)
public interface OptionsAccessor {
    @Accessor("keyMappings")
    KeyMapping[] ls$getKeyMappings();
    @Accessor("keyMappings")
    void ls$setKeyMappings(KeyMapping[] keys);
}