package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;
import net.minecraft.network.protocol.game.*;

//? if <= 1.20.3 {
//import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
//?} else {
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
//?}

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(PlayerChatMessage message, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        if (ls$mute(handler.player, false, message, ci)) {
            return;
        }
    }

    @Inject(method = "handleUseItem", at = @At("HEAD"))
    private void onPlayerInteractItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.player;
        if (currentSeason instanceof WildLife) {
            WildcardManager.onUseItem(player);
        }
    }


    @Unique
    private static final List<String> mutedCommands = List.of("msg", "tell", "whisper", "w", "me");

    //? if > 1.20.3 {
    @Inject(method = "performUnsignedChatCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String command, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        for (String mutedCmd : mutedCommands) {
            if (command.startsWith(mutedCmd + " ")) {
                boolean stoppedCommand = ls$mute(handler.player, true, null, ci);
                if (stoppedCommand) return;
            }
        }
    }
    //?}

    //? if <= 1.20.2 {
    /*@Inject(method = "performChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)I"), cancellable = true)
    private void handleCommandExecution(ServerboundChatCommandPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
    *///?} else if <= 1.20.3 {
    /*@Inject(method = "performChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"), cancellable = true)
    private void handleCommandExecution(ServerboundChatCommandPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "performSignedChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"), cancellable = true)
    private void handleCommandExecution(ServerboundChatCommandSignedPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
    //?}
        if (LifeSeries.isClientOrDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        for (String command : mutedCommands) {
            if (packet.command().startsWith(command + " ")) {
                boolean stoppedCommand = ls$mute(handler.player, true, null, ci);
                if (stoppedCommand) return;
            }
        }
    }

    @Unique
    private boolean ls$mute(ServerPlayer player, boolean command, PlayerChatMessage message, CallbackInfo ci) {
        if (player == null || PermissionManager.isAdmin(player) || LifeSeries.isClientOrDisabled()) {
            return false;
        }
        if (TriviaWildcard.bots.containsKey(player.getUUID())) {
            TriviaBot bot = TriviaWildcard.bots.get(player.getUUID());
            if (bot.interactedWith() && !bot.submittedAnswer()) {
                ((IPlayer) player).ls$message(ModifiableText.MUTED_TRIVIABOT.get());
                ci.cancel();
                return true;
            }
        }

        if (currentSeason.MUTE_WATCHERS.get() && ((IPlayer) player).ls$isWatcher()) {
            if (message == null || !currentSeason.MUTED_CHAT.get()) {
                ((IPlayer) player).ls$message(ModifiableText.MUTED_WATCHER.get());
            }
            else {
                currentSeason.mutedPlayersChat(player, message);
            }
            ci.cancel();
            return true;
        }
        if (currentSeason.MUTE_DEAD_PLAYERS.get() && ((IPlayer) player).ls$isDead() && !((IPlayer) player).ls$isWatcher()) {
            if (message == null || !currentSeason.MUTED_CHAT.get()) {
                ((IPlayer) player).ls$message(ModifiableText.MUTED_DEADPLAYER.get());
            }
            else {
                currentSeason.mutedPlayersChat(player, message);
            }
            ci.cancel();
            return true;
        }
        return false;
    }

    //? if <= 1.20 {
    //@Redirect(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    //?} else {
    @Redirect(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    //?}
    public void noLogoffMessage(PlayerList instance, Component message, boolean overlay) {
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        if (LifeSeries.isClientOrDisabled()) {
            instance.broadcastSystemMessage(message, overlay);
        }
        else {
            PlayerUtils.broadcastToVisiblePlayers(handler.player, message);
        }
    }
    @Inject(method = "handlePlayerCommand", at = @At("HEAD"), cancellable = true)
    private void cancelStopSleeping(ServerboundPlayerCommandPacket serverboundPlayerCommandPacket, CallbackInfo ci) {
        if (serverboundPlayerCommandPacket.getAction() == ServerboundPlayerCommandPacket.Action.STOP_SLEEPING) {
            if (LifeSeries.isLogicalNonDisabled() && currentSeason instanceof NiceLife niceLife && (niceLife.isMidnight() && NiceLifeTriviaManager.triviaInProgress)) {
                ci.cancel();
            }
        }
    }

}
