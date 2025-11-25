package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.seasons.season.Season;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
//? if <= 1.20 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
//?}

@Mixin(value = DedicatedServer.class, priority = 1)
public class DedicatedServerMixin {

    //? if <= 1.20 {
    @Inject(method = "getServerResourcePack", at = @At("HEAD"), cancellable = true)
    public void getServerResourcePack(CallbackInfoReturnable<Optional<MinecraftServer.ServerResourcePackInfo>> cir) {
        //TODO test
        String url = Season.RESOURCEPACK_MAIN_URL;
        String hash = Season.RESOURCEPACK_MAIN_SHA;
        boolean isRequired = false;
        Component prompt = Component.nullToEmpty("Life Series Resourcepack.");
        cir.setReturnValue(Optional.of(new MinecraftServer.ServerResourcePackInfo(url, hash, isRequired, prompt)));
    }
    //?}
}
