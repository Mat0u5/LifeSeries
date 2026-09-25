package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
import net.mat0u5.lifeseries.mixin.PlayerListS2CPacketAccessor;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.util.WatcherManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.minecraft.Optionull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.*;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;
import static net.mat0u5.lifeseries.LifeSeries.seasonConfig;
import static net.mat0u5.matlib.MatLib.server;

//? if >= 1.21.4
import net.minecraft.world.entity.player.PlayerModelPart;

public class PlayerUtils extends net.mat0u5.matlib.utils.player.PlayerUtils {

    public static void resetAttributesOnPlayerJoin(ServerPlayer player) {
        if (player == null) return;
        resetMaxPlayerHealthIfNecessary(player);
        if (!WildLifeTriviaHandler.cursedMoonJumpPlayers.contains(player.getUUID())) {
            AttributeUtils.JUMP_STRENGTH.of(player).reset();
        }
        //? if > 1.20.3 {
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
            AttributeUtils.SAFE_FALL_DISTANCE.of(player).reset();
        }
        AttributeUtils.STEP_HEIGHT.of(player).reset();
        //?}
        AttributeUtils.MOVEMENT_SPEED.of(player).reset();
    }

    public static void resetMaxPlayerHealthIfNecessary(ServerPlayer player) {
        if (LifeSeries.modDisabled()) {
            resetMaxPlayerHealth(player);
            return;
        }
        if (LifeSeries.isSeason(Seasons.SECRET_LIFE)) return;
        double currentMaxHealth = AttributeUtils.MAX_HEALTH.of(player).get();
        if (currentMaxHealth == 13 && WildLifeTriviaHandler.cursedHeartPlayers.contains(player.getUUID())) return;
        if (currentMaxHealth == SuperpowersWildcard.ZOMBIES_HEALTH && Necromancy.isRessurectedPlayer(player)) return;
        resetMaxPlayerHealth(player);
    }

    public static void resetMaxPlayerHealth(ServerPlayer player) {
        double health = seasonConfig.MAX_PLAYER_HEALTH.get();
        AttributeUtils.MAX_HEALTH.of(player).set(health);
    }

    public static List<ServerPlayer> getAllFunctioningPlayers() {
        List<ServerPlayer> result = getAllPlayers();
        result.removeIf(WatcherManager::isWatcher);
        return result;
    }

    public static void applyResourcepacks(UUID uuid) {
        if (NetworkHandlerServer.wasHandshakeSuccessful(uuid)) return;
        applyServerResourcepacks(uuid);
    }

    public static void applyServerResourcepacks(UUID uuid) {
        if (server == null) return;
        ServerPlayer player = getPlayer(uuid);
        if (player == null) return;
        //? if > 1.20.2 {
        applySingleResourcepack(player, Season.RESOURCEPACK_MAIN_URL, Season.RESOURCEPACK_MAIN_SHA, "Life Series Resourcepack.");
        applySingleResourcepack(player, Season.RESOURCEPACK_MINIMAL_ARMOR_URL, Season.RESOURCEPACK_MINIMAL_ARMOR_SHA, "Life Series Resourcepack.");
        if (currentSeason instanceof SecretLife) {
            applySingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL, Season.RESOURCEPACK_SECRETLIFE_SHA, "Life Series Resourcepack.");
        }
        else {
            removeSingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL);
        }
        //?}
    }

    public static void displayMessageToPlayer(ServerPlayer player, Component text, int timeFor) {
        Session.skipTimer.put(player.getUUID(), timeFor/5);
        ((IPlayer) player).ls$message(text, true);
    }

    public static void updatePlayerLists() {
        if (server == null) return;
        if (currentSeason == null) return;

        List<ServerPlayer> allPlayers = server.getPlayerList().getPlayers();

        for (ServerPlayer receivingPlayer : allPlayers) {

            ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(receivingPlayer));
            List<ClientboundPlayerInfoUpdatePacket.Entry> newEntries = new ArrayList<>();
            for (ServerPlayer player : allPlayers) {
                if (player == receivingPlayer) continue;

                boolean hidePlayer = hidePlayerFrom(receivingPlayer, player);

                ClientboundPlayerInfoUpdatePacket.Entry entry = getPlayerListEntry(player, !hidePlayer);
                newEntries.add(entry);
            }

            if (packet instanceof PlayerListS2CPacketAccessor accessor) {
                accessor.setEntries(newEntries);
            }
            receivingPlayer.connection.send(packet);
        }
    }

    public static ClientboundPlayerInfoUpdatePacket.Entry getPlayerListEntry(ServerPlayer player, boolean listed) {
        //? if <= 1.20 {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.latency, player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?} else if <= 1.21 {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), (RemoteChatSession.Data) Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?} else if <= 1.21.2 {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?} else if <= 1.21.6 {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), player.isModelPartShown(PlayerModelPart.HAT), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?} else {
        return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode(), player.getTabListDisplayName(), player.isModelPartShown(PlayerModelPart.HAT), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        //?}
    }

    public static boolean hidePlayerFrom(ServerPlayer receivingPlayer, ServerPlayer player) {
        if (receivingPlayer == null || player == null) return false;
        if (isFakePlayer(player)) return true;
        if (hideDeadPlayerFrom(receivingPlayer, player)) return true;
        if (hideWatcherPlayerFrom(receivingPlayer, player)) return true;
        return false;
    }

    private static boolean hideDeadPlayerFrom(ServerPlayer receivingPlayer, ServerPlayer player) {
        if (receivingPlayer.isSpectator()) return false;
        if (!player.isSpectator()) return false;

        if (currentSeason.TAB_LIST_SHOW_DEAD_PLAYERS) return false;
        if (((IPlayer) receivingPlayer).ls$isDead()) return false;
        if (((IPlayer) player).ls$isAlive()) return false;
        if (((IPlayer) player).ls$isWatcher()) return false;
        if (Necromancy.preIsRessurectedPlayer(player)) return false;
        return true;
    }

    private static boolean hideWatcherPlayerFrom(ServerPlayer receivingPlayer, ServerPlayer player) {
        if (receivingPlayer.isSpectator()) return false;
        if (!player.isSpectator()) return false;

        if (currentSeason.WATCHERS_IN_TAB) return false;
        if (((IPlayer) receivingPlayer).ls$isWatcher()) return false;
        if (!((IPlayer) player).ls$isWatcher()) return false;
        return true;
    }

    public static ServerPlayer getPlayerOrProjection(ServerPlayer player) {
        if (player == null) return null;
        if (!isFakePlayer(player)) return player;

        //? if <= 1.21.6 {
        /*if (player instanceof FakePlayer fakePlayer) {
            return PlayerUtils.getPlayer(fakePlayer.shadow);
        }
        *///?}
        return player;
    }

    public static void broadcastToVisiblePlayers(ServerPlayer broadcaster, Component message) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (hidePlayerFrom(player, broadcaster)) continue;
            ((IPlayer) player).ls$message(message);
        }
    }
}
