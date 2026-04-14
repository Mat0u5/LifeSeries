package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.events.Events;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.20.2
import net.minecraft.server.network.CommonListenerCookie;

@Mixin(value = PlayerList.class, priority = 1)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundPlayerAbilitiesPacket;<init>(Lnet/minecraft/world/entity/player/Abilities;)V"))
    //? if <= 1.20 {
    /*private void handlePlayerConnection(Connection connection, ServerPlayer player, CallbackInfo ci) {
    *///?} else {
    private void handlePlayerConnection(Connection connection, ServerPlayer player, CommonListenerCookie arg, CallbackInfo ci) {
    //?}
        Events.onPlayerJoin(player);
    }
}
