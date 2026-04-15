package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.20.3 {
/*import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.resources.Identifier;
*///?} else {
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.ArrayList;
import java.util.List;
//?}

@Mixin(ClientboundCustomPayloadPacket.class)
public class ClientboundCustomPayloadPacketMixin {
    //? if <= 1.20.3 {
    /*@Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void injectPayloads(Identifier id, FriendlyByteBuf buf, CallbackInfoReturnable<CustomPacketPayload> cir) {
        if (NetworkHandlerServer.PAYLOAD_READERS.containsKey(id)) {
            cir.setReturnValue(NetworkHandlerServer.PAYLOAD_READERS.get(id).apply(buf));
        }
    }
    *///?} else {
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
    //?}
}