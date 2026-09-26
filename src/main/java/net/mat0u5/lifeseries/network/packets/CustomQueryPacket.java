package net.mat0u5.lifeseries.network.packets;

//? if <= 1.20 {
/*public record CustomQueryPacket() {
}
*///?} else {
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;

public record CustomQueryPacket(FriendlyByteBuf data) implements CustomQueryAnswerPayload {
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBytes(data.copy());
    }
}
//?}
