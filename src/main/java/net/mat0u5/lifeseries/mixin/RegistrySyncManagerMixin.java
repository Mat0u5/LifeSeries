package net.mat0u5.lifeseries.mixin;
//?if < 1.21.4 {
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.mat0u5.lifeseries.Main;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = RegistrySyncManager.class, priority = 1, remap = false)
public class RegistrySyncManagerMixin {
    @ModifyReturnValue(method = "createAndPopulateRegistryMap", at = @At(value = "RETURN"))
    private static @Nullable Map<Identifier, Object2IntMap<Identifier>> checkRemoteRemap(@Nullable Map<Identifier, Object2IntMap<Identifier>> original) {
        if (original != null) {
            Identifier entityType = Identifier.ofVanilla("entity_type");
            if (original.containsKey(entityType)) {
                Object2IntMap<Identifier> entityTypes = original.get(entityType);
                entityTypes.keySet().removeIf(value ->
                        value.getNamespace().equalsIgnoreCase(Main.MOD_ID)
                );
                original.put(entityType, entityTypes);
            }
        }
        return original;
    }
}

//?} else {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public interface RegistrySyncManagerMixin {
    //Empty class to avoid mixin errors
}
*///?}