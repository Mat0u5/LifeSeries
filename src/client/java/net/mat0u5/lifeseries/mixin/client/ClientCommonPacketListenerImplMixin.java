package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
//? if <= 1.20 {
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
//?} else {
/*import java.util.UUID;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
*///?}

//? if <= 1.20 {
@Mixin(ClientPacketListener.class)
//?} else {
/*@Mixin(ClientCommonPacketListenerImpl.class)
*///?}
public class ClientCommonPacketListenerImplMixin {
    @Unique
    private static final List<String> ls$bannedURLs = List.of(
            "github.com/Mat0u5/LifeSeries-Resources"
    );

    @Shadow
    @Final
    protected Connection connection;

    //? if <= 1.20 {
    @Inject(method = "handleResourcePack",at = @At(target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;parseResourcePackUrl(Ljava/lang/String;)Ljava/net/URL;", shift = At.Shift.AFTER, value = "INVOKE" ), cancellable = true)
    public void onResourcePackSend(ClientboundResourcePackPacket packet, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "handleResourcePackPush",at = @At(target = "Lnet/minecraft/client/multiplayer/ClientCommonPacketListenerImpl;parseResourcePackUrl(Ljava/lang/String;)Ljava/net/URL;", shift = At.Shift.AFTER, value = "INVOKE" ), cancellable = true)
    public void onResourcePackSend(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
    *///?}
        if (Main.modFullyDisabled()) return;
        //? if <= 1.20 {
        String url = packet.getUrl();
        //?} else {
        /*String url = packet.url();
        UUID uuid = packet.id();
        *///?}
        boolean banned = false;
        for (String bannedURL : ls$bannedURLs) {
            if (url.contains(bannedURL)) {
                banned = true;
                break;
            }
        }
        if (!banned) return;
        Main.LOGGER.info("Skipping resourcepack download ({})", url);
        //? if <= 1.20 {
        this.connection.send(new ServerboundResourcePackPacket(ServerboundResourcePackPacket.Action.ACCEPTED));//TODO test
        //?} else {
        /*this.connection.send(new ServerboundResourcePackPacket(uuid, ServerboundResourcePackPacket.Action.ACCEPTED));
        this.connection.send(new ServerboundResourcePackPacket(uuid, ServerboundResourcePackPacket.Action.DOWNLOADED));
        this.connection.send(new ServerboundResourcePackPacket(uuid, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
        *///?}
        ci.cancel();
    }
}
