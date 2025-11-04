package net.mat0u5.lifeseries.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

//? if <= 1.21.2 {
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.Main;
import org.jetbrains.annotations.Nullable;
//?} else {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

//Don't do this at home kids

@Mixin(value = RegistrySyncManager.class, priority = 1, remap = false)
public class RegistrySyncManagerMixin {
    //? if <= 1.21.2 {
    @ModifyReturnValue(method = "createAndPopulateRegistryMap", at = @At(value = "RETURN"))
    private static @Nullable Map<ResourceLocation, Object2IntMap<ResourceLocation>> checkRemoteRemap(@Nullable Map<ResourceLocation, Object2IntMap<ResourceLocation>> original) {
        if (original != null) {
            ResourceLocation entityType = IdentifierHelper.vanilla("entity_type");
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
    //? if <= 1.21.9 {
    private static void checkRemoteRemap(Map<ResourceLocation, Object2IntMap<ResourceLocation>> map, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /^private static void checkRemoteRemap(Map<Identifier, Object2IntMap<Identifier>> map, CallbackInfoReturnable<Boolean> cir) {
    ^///?}
        cir.setReturnValue(true);
    }
    *///?}
}