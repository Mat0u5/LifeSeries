package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.livesManager;

//? if >= 1.21.2 && <= 1.21.6
/*import net.minecraft.entity.player.PlayerPosition;*/
//? if <= 1.21.6 {
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import java.util.Set;
//?}
//? if >= 1.21.9
/*import net.minecraft.entity.EntityPosition;*/

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(PlayerChatMessage message, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
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
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.player;
        if (currentSeason instanceof WildLife) {
            WildcardManager.onUseItem(player);
        }
    }

    //? if <= 1.21.6 {
    //? if <= 1.21 {
    @Inject(method = "teleport(DDDFFLjava/util/Set;)V", at = @At("TAIL"))
    public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<RelativeMovement> flags, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "requestTeleport(Lnet/minecraft/entity/player/PlayerPosition;Ljava/util/Set;)V", at = @At("TAIL"))
    public void requestTeleport(PlayerPosition pos, Set<PositionFlag> flags, CallbackInfo ci) {
        *///?}
        if (Main.modFullyDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.getPlayer();
        if (player instanceof FakePlayer) {
            ServerLevel world = PlayerUtils.getServerWorld(player);
            if (world.getPlayerByUUID(player.getUUID()) != null) {
                handler.resetPosition();
                world.getChunkSource().move(player);
            }
        }
    }
    //?}

    @Unique
    private static final List<String> mutedCommands = List.of("msg", "tell", "whisper", "w", "me");

    @Inject(method = "performUnsignedChatCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String command, CallbackInfo ci) {
        if (Main.modDisabled()) return;
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        for (String mutedCmd : mutedCommands) {
            if (command.startsWith(mutedCmd + " ")) {
                boolean stoppedCommand = ls$mute(handler.player, ci);
                if (stoppedCommand) return;
            }
        }
    }

    @Inject(method = "performSignedChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"), cancellable = true)
    private void handleCommandExecution(ServerboundChatCommandSignedPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
        if (Main.modDisabled()) return;
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
        if (player == null || PermissionManager.isAdmin(player) || Main.modDisabled()) {
            return false;
        }
        if (TriviaWildcard.bots.containsKey(player.getUUID())) {
            TriviaBot bot = TriviaWildcard.bots.get(player.getUUID());
            if (bot.interactedWith() && !bot.submittedAnswer()) {
                player.sendSystemMessage(Component.nullToEmpty("<Trivia Bot> No phoning a friend allowed!"));
                ci.cancel();
                return true;
            }
        }

        if (currentSeason.WATCHERS_MUTED && player.ls$isWatcher()) {
            player.sendSystemMessage(Component.nullToEmpty("Watchers aren't allowed to talk in chat! Admins can change this behavior in the config."));
            ci.cancel();
            return true;
        }
        if (currentSeason.MUTE_DEAD_PLAYERS && player.ls$isDead() && !player.ls$isWatcher()) {
            player.sendSystemMessage(Component.nullToEmpty("Dead players aren't allowed to talk in chat! Admins can change this behavior in the config."));
            ci.cancel();
            return true;
        }
        return false;
    }

    @Inject(method = "handlePlayerAction", at = @At("RETURN"))
    public void onPlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (packet.getAction() == ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            currentSeason.onUpdatedInventory(handler.player);
        }
    }

    @Redirect(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public void noLogoffMessage(PlayerList instance, Component message, boolean overlay) {
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        if (!Main.isLogicalSide() || Main.modDisabled()) {
            instance.broadcastSystemMessage(message, overlay);
        }
        PlayerUtils.broadcastToVisiblePlayers(handler.player, message);
    }
}
