package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
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
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public abstract class MinecraftServerMixin {

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

    @Inject(at = @At("TAIL"), method = "tickServer")
    private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        Events.onServerTickEnd((MinecraftServer) (Object) this);
    }
}
