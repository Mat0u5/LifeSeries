package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.mat0u5.lifeseries.Main;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

//Don't do this at home kids

@Mixin(value = RegistrySyncManager.class, priority = 1, remap = false)
public class RegistrySyncManagerMixin {
    //? if <= 1.21.2 {
    @ModifyReturnValue(method = "createAndPopulateRegistryMap", at = @At(value = "RETURN"))
    private static @Nullable Map<ResourceLocation, Object2IntMap<ResourceLocation>> checkRemoteRemap(@Nullable Map<ResourceLocation, Object2IntMap<ResourceLocation>> original) {
        if (original != null) {
            ResourceLocation entityType = ResourceLocation.withDefaultNamespace("entity_type");
            if (original.containsKey(entityType)) {
                Object2IntMap<ResourceLocation> entityTypes = original.get(entityType);
                entityTypes.keySet().removeIf(value ->
                        value.getNamespace().equalsIgnoreCase(Main.MOD_ID)
                );
                original.put(entityType, entityTypes);
            }
        }
        return original;
    }
    //?} else {
    /*@Inject(method = "areAllRegistriesOptional", at = @At(value = "HEAD"), cancellable = true)
    private static void checkRemoteRemap(Map<Identifier, Object2IntMap<Identifier>> map, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
    *///?}
}