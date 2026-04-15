package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Set;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

//? if <= 1.20.3 {
/*import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
*///?} else {
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
//?}
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;

//? if >= 1.21
import net.minecraft.network.DisconnectionDetails;
//? if >= 1.20.5

//? if <= 1.21
//import net.minecraft.world.entity.RelativeMovement;
//? if >= 1.21.2 <= 1.21.6 {
/*import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.PositionMoveRotation;
*///?}
//? if >= 26.1 {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
//?}

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(PlayerChatMessage message, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.player;

        if (ls$mute(handler.player, ci)) {
            return;
        }

        Component originalText = message.decoratedContent();
        String originalContent = originalText.getString();
        if (!originalContent.contains(":")) return;

        String formattedContent = TextUtils.replaceEmotes(originalContent);

        if (!originalContent.equals(formattedContent)) {
            Component formattedContentText = Component.literal(formattedContent).setStyle(originalText.getStyle());
            Component finalMessage = TextUtils.format("<{}> {}",player, formattedContentText);

            PlayerUtils.broadcastMessage(finalMessage);
            ci.cancel();
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

    //? if <= 1.21.6 {
    /*//? if <= 1.21 {
    /^@Inject(method = "teleport(DDDFFLjava/util/Set;)V", at = @At("TAIL"))
    public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<RelativeMovement> flags, CallbackInfo ci) {
    ^///?} else {
    @Inject(method = "teleport(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At("TAIL"))
    public void requestTeleport(PositionMoveRotation pos, Set<Relative> flags, CallbackInfo ci) {
        //?}
        if (LifeSeries.modFullyDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.getPlayer();
        if (player instanceof FakePlayer) {
            ServerLevel level = player.ls$getServerLevel();
            if (level.getPlayerByUUID(player.getUUID()) != null) {
                handler.resetPosition();
                level.getChunkSource().move(player);
            }
        }
    }
    *///?}

    @Unique
    private static final List<String> mutedCommands = List.of("msg", "tell", "whisper", "w", "me");

    //? if > 1.20.3 {
    @Inject(method = "performUnsignedChatCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String command, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        for (String mutedCmd : mutedCommands) {
            if (command.startsWith(mutedCmd + " ")) {
                boolean stoppedCommand = ls$mute(handler.player, ci);
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
                boolean stoppedCommand = ls$mute(handler.player, ci);
                if (stoppedCommand) return;
            }
        }
    }

    @Unique
    private boolean ls$mute(ServerPlayer player, CallbackInfo ci) {
        if (player == null || PermissionManager.isAdmin(player) || LifeSeries.isClientOrDisabled()) {
            return false;
        }
        if (TriviaWildcard.bots.containsKey(player.getUUID())) {
            TriviaBot bot = TriviaWildcard.bots.get(player.getUUID());
            if (bot.interactedWith() && !bot.submittedAnswer()) {
                player.ls$message(ModifiableText.MUTED_TRIVIABOT.get());
                ci.cancel();
                return true;
            }
        }

        if (currentSeason.WATCHERS_MUTED && player.ls$isWatcher()) {
            player.ls$message(ModifiableText.MUTED_WATCHER.get());
            ci.cancel();
            return true;
        }
        if (currentSeason.MUTE_DEAD_PLAYERS && player.ls$isDead() && !player.ls$isWatcher()) {
            player.ls$message(ModifiableText.MUTED_DEADPLAYER.get());
            ci.cancel();
            return true;
        }
        return false;
    }

    @Inject(method = "handlePlayerAction", at = @At("RETURN"))
    public void onPlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        if (LifeSeries.isClientOrDisabled()) return;
        if (packet.getAction() == ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            currentSeason.onUpdatedInventory(handler.player);
        }
    }

    //? if <= 1.20 {
    /*@Redirect(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    *///?} else {
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

    //? if >= 26.1 {
    @Inject(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    public void handleInteract(ServerboundInteractPacket packet, CallbackInfo info, @Local(name = "target") Entity target) {
        Level level = player.level();

        //? if <= 1.21.11 {
        /*EntityHitResult hitResult = new EntityHitResult(target, packet.location().add(target.getX(), target.getY(), target.getZ()));
        InteractionResult result = Events.onRightClickEntity(player, level, packet.hand(), target, hitResult);
        *///?} else {
        EntityHitResult hitResult = new EntityHitResult(target, packet.location().add(target.getX(), target.getY(), target.getZ()));
        InteractionResult result = Events.onRightClickEntity(player, level, packet.hand(), target, hitResult);
        //?}

        if (result != InteractionResult.PASS) {
            info.cancel();
        }
    }
    //?}

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    //? if <= 1.20.5 {
    /*private void onDisconnect(Component component, CallbackInfo ci) {
    *///?} else {
    private void onDisconnect(DisconnectionDetails details, CallbackInfo ci) {
    //?}
        Events.onPlayerDisconnect(this.player);
    }

    //? if >= 1.20.5 {
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onHandlePayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        NetworkHandlerServer.onCustomPayload(packet.payload(), this.player);
    }
    //?}
}
