package net.mat0u5.lifeseries.mixin.client;

import io.netty.channel.ChannelHandlerContext;
import net.mat0u5.lifeseries.events.ClientEvents;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "channelInactive", at = @At("HEAD"))
    private void disconnectAddon(ChannelHandlerContext channelHandlerContext, CallbackInfo ci) {
        ClientEvents.onClientDisconnect();
    }

    //? if <= 1.20.5 {
    /*@Inject(method = "handleDisconnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketListener;onDisconnect(Lnet/minecraft/network/chat/Component;)V"))
    *///?} else {
    @Inject(method = "handleDisconnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketListener;onDisconnect(Lnet/minecraft/network/DisconnectionDetails;)V"))
    //?}
    private void disconnectAddon(CallbackInfo ci) {
        ClientEvents.onClientDisconnect();
    }
}
