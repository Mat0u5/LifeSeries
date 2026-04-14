package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void lifeseries$registerKeybinds(CallbackInfo ci) {
        ClientKeybinds.registerKeybinds();

        OptionsAccessor self = (OptionsAccessor)(Object)this;
        List<KeyMapping> list = new ArrayList<>(Arrays.asList(self.ls$getKeyMappings()));
        list.add(ClientKeybinds.superpower);
        list.add(ClientKeybinds.openConfig);
        if (ClientKeybinds.runCommand != null) {
            list.add(ClientKeybinds.runCommand);
        }
        self.ls$setKeyMappings(list.toArray(new KeyMapping[0]));
    }
}