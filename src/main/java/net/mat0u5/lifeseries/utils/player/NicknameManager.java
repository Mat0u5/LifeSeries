package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerUsername;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.mat0u5.lifeseries.LifeSeries.server;

public class NicknameManager {

    private static final Map<RealUUID, String> nicknameStorage = new ConcurrentHashMap<>();
    private static final Map<RealUUID, Component> textCache = new ConcurrentHashMap<>();

    public static void setNickname(ServerPlayer player, String nickname) {
        RealUUID realUUID = ProfileManager.getRealUUID(player);
        if (server == null) return;
        if (nickname == null || nickname.trim().isEmpty()) {
            removeNickname(player);
            return;
        }

        nicknameStorage.put(realUUID, nickname);

        Component parsedComponent = Component.literal(nickname);
        textCache.put(realUUID, parsedComponent);

        updatePlayerDisplay(player, server);
    }

    public static void removeNickname(ServerPlayer player) {
        RealUUID realUUID = ProfileManager.getRealUUID(player);
        if (server == null) return;
        nicknameStorage.remove(realUUID);
        textCache.remove(realUUID);

        updatePlayerDisplay(player, server);
    }

    @Nullable
    public static String getNickname(RealUUID realUUID) {
        return nicknameStorage.get(realUUID);
    }

    @Nullable
    public static Component getNicknameText(Player player) {
        RealUUID realUUID = ProfileManager.getRealUUID(player);
        return textCache.get(realUUID);
    }

    public static boolean hasNickname(RealUUID realUUID) {
        return nicknameStorage.containsKey(realUUID);
    }

    public static Component getDisplayName(ServerPlayer player) {
        Component nickname = getNicknameText(player);
        return nickname != null ? nickname : player.getName();
    }

    private static void updatePlayerDisplay(ServerPlayer player, MinecraftServer server) {
        if (player instanceof IPlayerUsername holder) {
            holder.ls$resetUsernameCache();
        }

        server.getPlayerList().broadcastAll(
                new ClientboundPlayerInfoUpdatePacket(
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                        player
                )
        );
    }

    public static void loadNicknameData(ServerPlayer player) {
        RealUUID realUUID = ProfileManager.getRealUUID(player);
        if (hasNickname(realUUID)) {
            LifeSeries.LOGGER.info("Loaded nickname for {}: {}", player.getName().getString(), getNickname(realUUID));
        }
    }
}
