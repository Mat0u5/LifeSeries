package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.events.Events;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
//? if <= 1.20.2 {
/*import net.mat0u5.lifeseries.seasons.season.Season;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
*///?}

@Mixin(value = MinecraftServer.class, priority = 1)
public abstract class MinecraftServerMixin {
    @Shadow
    private MinecraftServer.ReloadableResources resources;

    @Inject(method = "runServer", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        LifeSeries.onInitialize_();
    }

    //? if <= 1.20.2 {
    /*@Inject(method = "getServerResourcePack", at = @At("HEAD"), cancellable = true)
    public void getServerResourcePack(CallbackInfoReturnable<Optional<MinecraftServer.ServerResourcePackInfo>> cir) {
        String url = Season.RESOURCEPACK_COMBINED_URL;
        String hash = Season.RESOURCEPACK_COMBINED_SHA;
        boolean isRequired = false;
        Component prompt = Component.nullToEmpty("Life Series Resourcepack.");
        cir.setReturnValue(Optional.of(new MinecraftServer.ServerResourcePackInfo(url, hash, isRequired, prompt)));
    }
    *///?}

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"), method = "runServer")
    private void beforeSetupServer(CallbackInfo info) {
        Events.onServerStarting((MinecraftServer) (Object) this);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;", ordinal = 0), method = "runServer")
    private void afterSetupServer(CallbackInfo info) {
        Events.onServerStart((MinecraftServer) (Object) this);
    }

    @Inject(at = @At("HEAD"), method = "stopServer")
    private void beforeShutdownServer(CallbackInfo info) {
        Events.onServerStopping((MinecraftServer) (Object) this);
    }

    @Inject(at = @At("TAIL"), method = "tickServer")
    private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        Events.onServerTickEnd((MinecraftServer) (Object) this);
    }

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private void startResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Events.onReloadStart((MinecraftServer) (Object) this, this.resources.resourceManager());
    }

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void endResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().handleAsync((value, throwable) -> {
            // Hook into fail
            Events.onReloadEnd((MinecraftServer) (Object) this, this.resources.resourceManager(), throwable == null);
            return value;
        }, (MinecraftServer) (Object) this);
    }
}
