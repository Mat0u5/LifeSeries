package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientboundCustomPayloadPacket.class)
public class ClientboundCustomPayloadPacketMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;codec(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$FallbackProvider;Ljava/util/List;)Lnet/minecraft/network/codec/StreamCodec;",  ordinal = 0), index = 1)
    private static List<CustomPacketPayload.TypeAndCodec<?, ? extends CustomPacketPayload>> injectPayloads(List<CustomPacketPayload.TypeAndCodec<?, ? extends CustomPacketPayload>> original) {
        List<CustomPacketPayload.TypeAndCodec<?, ? extends CustomPacketPayload>> list = new ArrayList<>(original);

        for (var packet : NetworkHandlerServer.PAYLOADS) {
            boolean exists = list.stream().anyMatch(existing -> existing.type().id().equals(packet.type().id()));

            if (!exists) {
                list.add((CustomPacketPayload.TypeAndCodec<?, ? extends CustomPacketPayload>)(Object) packet);
            }
        }
        return list;
    }
}