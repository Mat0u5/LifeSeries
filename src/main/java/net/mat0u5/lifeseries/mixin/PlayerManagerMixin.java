package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.boogeyman.FinalLifeBoogeymanManager;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//? if > 1.20 {
import net.minecraft.server.network.CommonListenerCookie;
//?}
//? if <= 1.21.6 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
*///?}

@Mixin(value = PlayerList.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public abstract class PlayerManagerMixin implements IPlayerManager {

    @Final
    @Shadow
    private PlayerDataStorage playerIo;

    @Override
    public PlayerDataStorage ls$getSaveHandler() {
        return playerIo;
    }


    @Invoker("save")
    abstract void invokeSavePlayerData(ServerPlayer player);

    @Override
    public void ls$savePlayerData(ServerPlayer player) {
        invokeSavePlayerData(player);
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    //? if <= 1.20 {
    //public void skipLoginMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player) {
    //?} else {
    public void skipLoginMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
    //?}
        if (LifeSeries.isClientOrDisabled() || player == null) {
            instance.broadcastSystemMessage(message, overlay);
        }
        else if (!FinalLifeBoogeymanManager.overwriteConnectionMessage(player, true)) {
            PlayerUtils.broadcastToVisiblePlayers(player, message);
        }
    }

    //? if <= 1.21.6 {
    /*// Every dimension gets the overworld border here, but the In-Between has its own
    @ModifyVariable(method = "sendLevelInfo", at = @At("STORE"))
    private WorldBorder sendInBetweenBorder(WorldBorder border, ServerPlayer player, ServerLevel level) {
        return level.dimension() == InBetweenManager.DIMENSION ? level.getWorldBorder() : border;
    }

    @WrapOperation(method = "broadcastAll(Lnet/minecraft/network/protocol/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void skipBorderInInBetween(ServerGamePacketListenerImpl connection, Packet<?> packet, Operation<Void> original) {
        if (ls$isBorderPacket(packet) && InBetweenManager.isInInBetween(connection.player)) return;
        original.call(connection, packet);
    }

    @Unique
    private static boolean ls$isBorderPacket(Packet<?> packet) {
        return packet instanceof ClientboundSetBorderSizePacket || packet instanceof ClientboundSetBorderLerpSizePacket || packet instanceof ClientboundSetBorderCenterPacket
                || packet instanceof ClientboundSetBorderWarningDelayPacket || packet instanceof ClientboundSetBorderWarningDistancePacket;
    }
    *///?}
}
