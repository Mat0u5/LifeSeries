package net.mat0u5.lifeseries.mixin.client;

import io.netty.buffer.Unpooled;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryAnswerPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHandshakePacketListenerImpl.class, priority = 1)
public class ClientHandshakePacketListenerImplMixin {
    @Inject(method = "handleCustomQuery", at = @At("HEAD"), cancellable = true)
    private void handleCustomQuery(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        if (packet.payload().id().equals(IdentifierHelper.mod(NetworkHandlerServer.preLoginPacketID))) {
            ClientHandshakePacketListenerImpl handler = (ClientHandshakePacketListenerImpl) (Object) this;

            FriendlyByteBuf responseBuf = new FriendlyByteBuf(Unpooled.buffer());
            responseBuf.writeBoolean(true);

            DiscardedQueryAnswerPayload answerPayload = new DiscardedQueryAnswerPayload();
            answerPayload.write(responseBuf);

            handler.connection.send(new ServerboundCustomQueryAnswerPacket(packet.transactionId(), answerPayload));
            ci.cancel();
        }
    }
}
