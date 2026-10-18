package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
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
    /*public void skipLoginMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player) {
    *///?} else {
    public void skipLoginMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
    //?}
        if (LifeSeries.isClientOrDisabled() || player == null) {
            instance.broadcastSystemMessage(message, overlay);
        }
        else {
            PlayerUtils.broadcastToVisiblePlayers(player, message);
        }
    }
}
