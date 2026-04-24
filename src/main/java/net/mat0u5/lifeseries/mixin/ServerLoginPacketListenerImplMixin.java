package net.mat0u5.lifeseries.mixin;

import io.netty.buffer.Unpooled;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <= 1.20 {
/*import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.server.MinecraftServer;
*///?} else {
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
import com.mojang.authlib.GameProfile;
//?}

@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 1)
public abstract class ServerLoginPacketListenerImplMixin {
    @Unique private boolean ls$querySent = false;
    @Unique private boolean ls$queryAnswered = false;

    //? if <= 1.20 {
    /*@Shadow @Final
    MinecraftServer server;
    @Shadow ServerLoginPacketListenerImpl.State state;
    @Shadow public abstract void handleAcceptedLogin();

    @Inject(method = "handleAcceptedLogin", at = @At("HEAD"), cancellable = true)
    private void onHandleAcceptedLogin(CallbackInfo ci) {
        if (ls$queryAnswered) {
            return;
        }

        if (!ls$querySent) {
            ls$querySent = true;

            ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl)(Object)this;
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            self.connection.send(new ClientboundCustomQueryPacket(
                    NetworkHandlerServer.PRELOGIN_TRANSACTION_ID,
                    IdentifierHelper.mod(NetworkHandlerServer.preLoginPacketID),
                    buf
            ));

            this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
        }

        ci.cancel();
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleAnswer(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if (packet.getTransactionId() != NetworkHandlerServer.PRELOGIN_TRANSACTION_ID) return;

        boolean understood = packet.getData() != null;
        ls$queryAnswered = true;

        ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl)(Object)this;

        this.server.execute(() -> {
            NetworkHandlerServer.handlePreLogin(understood, self);

            this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
            this.handleAcceptedLogin();
        });

        ci.cancel();
    }
    *///?} else {
    @Shadow public abstract void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket packet);

    @Unique private GameProfile ls$pendingProfile = null;

    @Inject(method = "finishLoginAndWaitForClient", at = @At("HEAD"), cancellable = true)
    private void interceptFinish(GameProfile profile, CallbackInfo ci) {
        if (ls$queryAnswered) return;

        if (!ls$querySent) {
            ls$querySent = true;
            ls$pendingProfile = profile;

            ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl)(Object)this;
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            DiscardedQueryPayload payload = new DiscardedQueryPayload(IdentifierHelper.mod(NetworkHandlerServer.preLoginPacketID));
            payload.write(buf);
            self.connection.send(new ClientboundCustomQueryPacket(
                    NetworkHandlerServer.PRELOGIN_TRANSACTION_ID, payload
            ));
        }
        ci.cancel();
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleAnswer(ServerboundCustomQueryAnswerPacket packet, CallbackInfo ci) {
        if (packet.transactionId() != NetworkHandlerServer.PRELOGIN_TRANSACTION_ID) return;

        boolean understood = false;

        if (packet.payload() != null) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.payload().write(buf);
            if (buf.isReadable()) {
                understood = buf.readBoolean();
            }
        }
        ls$queryAnswered = true;

        ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl)(Object)this;
        NetworkHandlerServer.handlePreLogin(understood, self);
        finishLogin(ls$pendingProfile);
        ci.cancel();
    }

    @Shadow
    private void finishLoginAndWaitForClient(GameProfile profile) {}

    @Unique
    private void finishLogin(GameProfile profile) {
        finishLoginAndWaitForClient(profile);
    }
    //?}
}