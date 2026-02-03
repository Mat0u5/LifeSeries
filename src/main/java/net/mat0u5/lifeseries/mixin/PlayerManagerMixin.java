package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import static net.mat0u5.lifeseries.Main.currentSeason;
//? if <= 1.21.5
//import net.minecraft.nbt.CompoundTag;
//? if >= 1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.ValueInput;
//?}
//? if > 1.20 {
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.Entity;
//?}

@Mixin(value = PlayerList.class, priority = 1)
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

    @Inject(method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Z)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(Component message, Function<ServerPlayer, Component> playerMessageFactory, boolean overlay, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (message.getString().contains("`")) ci.cancel();
    }

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(PlayerChatMessage message, Predicate<ServerPlayer> shouldSendFiltered, ServerPlayer sender, ChatType.Bound params, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (message.decoratedContent().getString().contains("`")) ci.cancel();
    }

    //? if <= 1.21.6 {
    /*@Inject(method = "load", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
            //? if <= 1.21.5 {
    /^public void loadPlayerData(ServerPlayer player, CallbackInfoReturnable<Optional<CompoundTag>> cir) {
     ^///?} else {
    public void loadPlayerData(ServerPlayer player, ProblemReporter errorReporter, CallbackInfoReturnable<Optional<ValueInput>> cir) {
        //?}
        if (Main.modFullyDisabled()) return;
        if (player instanceof FakePlayer fakePlayer) {
            fakePlayer.fixStartingPosition.run();
        }
    }
    *///?}

    @Inject(method = "respawn", at = @At("RETURN"))
    //? if <= 1.20.5 {
    /*public void respawnPlayer(ServerPlayer serverPlayer, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
    *///?} else {
    public void respawnPlayer(ServerPlayer player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
    //?}
        //? if <= 1.20.5 {
        /*if (alive) return;
        *///?} else {
        if (alive || removalReason != Entity.RemovalReason.KILLED) return;
        //?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
              currentSeason.onPlayerRespawn(cir.getReturnValue());
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    //? if <= 1.20 {
    /*public void skipLoginMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player) {
    *///?} else {
    public void skipLoginMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
    //?}
        if (!Main.isLogicalSide() || Main.modDisabled() || player == null) {
            instance.broadcastSystemMessage(message, overlay);
        }
        else {
            PlayerUtils.broadcastToVisiblePlayers(player, message);
        }
    }
}
