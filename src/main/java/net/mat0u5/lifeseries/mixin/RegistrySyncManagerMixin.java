package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.mat0u5.lifeseries.Main;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static net.mat0u5.lifeseries.Main.currentSeason;

//? if <= 1.20 {
/*import net.minecraft.server.level.ServerPlayer;
*///?} else {
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
//?}

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
//?}

//? if > 1.21.2 {
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}

//Don't do this at home kids

@Mixin(value = RegistrySyncManager.class, priority = 1, remap = false)
public class RegistrySyncManagerMixin {

    //? if <= 1.20 {
    /*@WrapOperation(method = "sendPacket(Lnet/minecraft/server/level/ServerPlayer;Lnet/fabricmc/fabric/impl/registry/sync/packet/RegistryPacketHandler;)V", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;createAndPopulateRegistryMap(ZLjava/util/Map;)Ljava/util/Map;"))
    private static @Nullable Map<ResourceLocation, Object2IntMap<ResourceLocation>> checkRemoteRemap(boolean b, Map map, Operation<Map<ResourceLocation, Object2IntMap<ResourceLocation>>> original, ServerPlayer player) {
        Map<ResourceLocation, Object2IntMap<ResourceLocation>> originalValue = original.call(b, map);
        UUID profileUUID = player.getUUID();
    *///?} else if <= 1.21.9 {
    /*@WrapOperation(method = "configureClient", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;createAndPopulateRegistryMap()Ljava/util/Map;"))
    private static @Nullable Map<ResourceLocation, Object2IntMap<ResourceLocation>> checkRemoteRemap(Operation<Map<ResourceLocation, Object2IntMap<ResourceLocation>>> original, ServerConfigurationPacketListenerImpl handler) {
        Map<ResourceLocation, Object2IntMap<ResourceLocation>> originalValue = original.call();
        UUID profileUUID = OtherUtils.profileId(handler.getOwner());
    *///?} else {
    @WrapOperation(method = "configureClient", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;createAndPopulateRegistryMap()Ljava/util/Map;"))
    private static @Nullable Map<Identifier, Object2IntMap<Identifier>> checkRemoteRemap(Operation<Map<Identifier, Object2IntMap<Identifier>>> original, ServerConfigurationPacketListenerImpl handler) {
        Map<Identifier, Object2IntMap<Identifier>> originalValue = original.call();
        UUID profileUUID = OtherUtils.profileId(handler.getOwner());
    //?}
        if (NetworkHandlerServer.REGISTRY_OVERRIDE_BEHAVIOR == NetworkHandlerServer.RegistryOverrideBahaviours.NEVER ||
                (NetworkHandlerServer.REGISTRY_OVERRIDE_BEHAVIOR == NetworkHandlerServer.RegistryOverrideBahaviours.LOGIN && NetworkHandlerServer.preLoginHandshake.contains(profileUUID)) ||
                (NetworkHandlerServer.REGISTRY_OVERRIDE_BEHAVIOR == NetworkHandlerServer.RegistryOverrideBahaviours.SEASON && currentSeason.getSeason().requiresClient())) {
            Main.LOGGER.info("Sending unmodified registry entries to client");
            return originalValue;
        }

        List<String> removedEntries = new ArrayList<>();
        if (originalValue != null) {
            for (var location : originalValue.keySet()) {
                //? if <= 1.21.9 {
                /*Object2IntMap<ResourceLocation> registry = originalValue.get(location);
                *///?} else {
                Object2IntMap<Identifier> registry = originalValue.get(location);
                //?}

                registry.keySet().removeIf(value -> {
                    if (value.getNamespace().equalsIgnoreCase(Main.MOD_ID)) {
                        removedEntries.add(location.getPath()+":"+value);
                        return true;
                    }
                    return false;
                });
                originalValue.put(location, registry);
            }
        }

        Main.LOGGER.info("Sending modified registry entries to client. Removed: "+String.join(", ", removedEntries));
        return originalValue;
    }

    //? if > 1.21.2 {
    @Inject(method = "areAllRegistriesOptional", at = @At(value = "HEAD"), cancellable = true)
    //? if <= 1.21.9 {
    /*private static void checkRemoteRemap(Map<ResourceLocation, Object2IntMap<ResourceLocation>> map, CallbackInfoReturnable<Boolean> cir) {
    *///?} else {
    private static void checkRemoteRemap(Map<Identifier, Object2IntMap<Identifier>> map, CallbackInfoReturnable<Boolean> cir) {
    //?}
        cir.setReturnValue(true);
    }
    //?}
}