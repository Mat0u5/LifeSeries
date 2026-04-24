package net.mat0u5.lifeseries.mixin;

//? if <= 1.20 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public interface ServerboundCustomQueryAnswerPacketMixin {
    //Empty class to avoid mixin errors
}
*///?} else {

import io.netty.buffer.Unpooled;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.CustomQueryPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerboundCustomQueryAnswerPacket.class, priority = 1)
public class ServerboundCustomQueryAnswerPacketMixin {
    @Shadow
    @Final
    private static int MAX_PAYLOAD_SIZE;

    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void readResponse(int queryId, FriendlyByteBuf buf, CallbackInfoReturnable<CustomQueryAnswerPayload> cir) {
        if (queryId == NetworkHandlerServer.PRELOGIN_TRANSACTION_ID) {
            boolean hasPayload = buf.readBoolean();

            if (!hasPayload) {
                cir.setReturnValue(null);
                return;
            }

            cir.setReturnValue(new CustomQueryPacket(ls$read(buf, MAX_PAYLOAD_SIZE)));
        }
    }

    @Unique
    private static FriendlyByteBuf ls$read(FriendlyByteBuf byteBuf, int maxSize) {
        int size = byteBuf.readableBytes();

        if (size < 0 || size > maxSize) {
            throw new IllegalArgumentException("Payload may not be larger than " + maxSize + " bytes");
        }

        FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
        newBuf.writeBytes(byteBuf.copy());
        byteBuf.skipBytes(byteBuf.readableBytes());
        return newBuf;
    }
}

//?}