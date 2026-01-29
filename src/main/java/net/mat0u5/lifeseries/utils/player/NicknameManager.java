package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.mat0u5.lifeseries.Main.server;

public class NicknameManager {

    private static final Map<UUID, String> nicknameStorage = new ConcurrentHashMap<>();
    private static final Map<UUID, Component> textCache = new ConcurrentHashMap<>();

    public static void setNickname(ServerPlayer player, String nickname) {
        UUID uuid = player.getUUID();
        if (server == null) return;
        if (nickname == null || nickname.trim().isEmpty()) {
            removeNickname(player);
            return;
        }

        nicknameStorage.put(uuid, nickname);

        Component parsedComponent = Component.literal(nickname);
        textCache.put(uuid, parsedComponent);

        updatePlayerDisplay(player, server);
    }

    public static void removeNickname(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (server == null) return;
        nicknameStorage.remove(uuid);
        textCache.remove(uuid);

        updatePlayerDisplay(player, server);
    }

    @Nullable
    public static String getNickname(UUID uuid) {
        return nicknameStorage.get(uuid);
    }

    @Nullable
    public static Component getNicknameText(UUID uuid) {
        return textCache.get(uuid);
    }

    public static boolean hasNickname(UUID uuid) {
        return nicknameStorage.containsKey(uuid);
    }

    public static Component getDisplayName(ServerPlayer player) {
        Component nickname = getNicknameText(player.getUUID());
        return nickname != null ? nickname : player.getName();
    }

    private static void updatePlayerDisplay(ServerPlayer player, MinecraftServer server) {
        if (player instanceof IPlayer holder) {
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
        UUID uuid = player.getUUID();
        if (hasNickname(uuid)) {
            Main.LOGGER.info("Loaded nickname for {}: {}", player.getName().getString(), getNickname(uuid));
        }
    }
}
