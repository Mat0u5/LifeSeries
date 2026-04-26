package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {
    @Shadow
    @Final
    @Mutable
    public KeyMapping[] keyMappings;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void lifeseries$initKeysInConstructor(CallbackInfo ci) {
        //? if <= 1.21.6 {
        /*KeyMappingAccessor.ls$getCategorySortOrder().putIfAbsent(ClientKeybinds.KEYBIND_ID, 20);
        *///?}

        this.keyMappings = ClientKeybinds.appendCustomKeybinds(this.keyMappings);
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void lifeseries$ensureKeysBeforeLoad(CallbackInfo ci) {
        this.keyMappings = ClientKeybinds.appendCustomKeybinds(this.keyMappings);
    }
}