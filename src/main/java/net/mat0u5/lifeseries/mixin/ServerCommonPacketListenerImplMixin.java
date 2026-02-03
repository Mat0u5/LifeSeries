package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.fakeplayer.FakeClientConnection;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21.5
//import net.minecraft.network.PacketSendListener;
//? if >= 1.21.6
import io.netty.channel.ChannelFutureListener;

//? if <= 1.20 {
/*import net.minecraft.server.network.ServerGamePacketListenerImpl;
@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1)
*///?} else {
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
@Mixin(value = ServerCommonPacketListenerImpl.class, priority = 1)
//?}
public class ServerCommonPacketListenerImplMixin {

    @Final
    @Shadow
    protected Connection connection;

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (connection instanceof FakeClientConnection) {
            ci.cancel();
        }
    }


    //? if <= 1.20 {
    /*@WrapOperation(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V")
    )
    public void send(Connection instance, Packet<?> packet, PacketSendListener packetSendListener, Operation<Void> original) {
        if (connection instanceof FakeClientConnection) return;
        original.call(instance, packet, packetSendListener);
    }
    *///?} else if <= 1.21.5 {
    /*@WrapOperation(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V")
    )
    public void send(Connection instance, Packet<?> packet, PacketSendListener callbacks, boolean flush, Operation<Void> original) {
        if (connection instanceof FakeClientConnection) return;
        original.call(instance, packet, callbacks, flush);
    }
    *///?} else {
    @WrapOperation(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V")
    )
    public void send(Connection instance, Packet packet, ChannelFutureListener channelFutureListener, boolean b, Operation<Void> original) {
        if (connection instanceof FakeClientConnection) return;
        original.call(instance, packet, channelFutureListener, b);
    }
    //?}

    @Inject(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    public void disconnect(Component reason, CallbackInfo ci) {
        if (reason.getString().contains("lifeseries") && reason.getString().contains("registr")) {
            ci.cancel();
            return;
        }
        if (Main.modFullyDisabled()) return;
        if (connection instanceof FakeClientConnection) {
            ci.cancel();
        }
    }
}
