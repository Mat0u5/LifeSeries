package net.mat0u5.lifeseries.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RegistrySyncManager.class, priority = 1)
public class RegistrySyncManagerMixin {
/*
    @Inject(method = "checkRemoteRemap", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"))
    private static void checkRemoteRemap(Map<Identifier, Object2IntMap<Identifier>> map, CallbackInfo ci) {
        if (map.size() == 1) {
            ci.cancel();
        }
    }
    */
}
