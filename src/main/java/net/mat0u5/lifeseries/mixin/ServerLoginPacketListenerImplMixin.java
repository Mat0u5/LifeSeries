package net.mat0u5.lifeseries.mixin;

import io.netty.buffer.Unpooled;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    private static final int PRELOGIN_TRANSACTION_ID = 104242;

    @Shadow public abstract void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket packet);

    @Unique private boolean ls$querySent = false;
    @Unique private boolean ls$queryAnswered = false;
    @Unique private GameProfile ls$pendingProfile = null;

    @Inject(method = "finishLoginAndWaitForClient", at = @At("HEAD"), cancellable = true)
    private void lifeseries$interceptFinish(GameProfile profile, CallbackInfo ci) {
        if (ls$queryAnswered) return;

        if (!ls$querySent) {
            ls$querySent = true;
            ls$pendingProfile = profile;

            ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl)(Object)this;
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            DiscardedQueryPayload payload = new DiscardedQueryPayload(IdentifierHelper.mod(NetworkHandlerServer.preLoginPacketID));
            payload.write(buf);
            self.connection.send(new ClientboundCustomQueryPacket(
                    PRELOGIN_TRANSACTION_ID, payload
            ));
        }
        ci.cancel();
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void lifeseries$onHandleAnswer(ServerboundCustomQueryAnswerPacket packet, CallbackInfo ci) {
        if (packet.transactionId() != PRELOGIN_TRANSACTION_ID) return;

        boolean understood = packet.payload() != null;
        ls$queryAnswered = true;

        ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl)(Object)this;
        NetworkHandlerServer.handlePreLogin(understood, self);
        lifeseries$finishLogin(ls$pendingProfile);
        ci.cancel();
    }

    @Shadow
    private void finishLoginAndWaitForClient(GameProfile profile) {}

    @Unique
    private void lifeseries$finishLogin(GameProfile profile) {
        finishLoginAndWaitForClient(profile);
    }
}