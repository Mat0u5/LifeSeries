package net.mat0u5.lifeseries.seasons.season.wildlife.morph;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MorphManager {
    public static final Map<UUID, MorphComponent> morphComponents = new HashMap<>();

    public static MorphComponent getOrCreateComponent(Player player) {
        return getOrCreateComponent(player.getUUID());
    }

    public static MorphComponent getOrCreateComponent(UUID playerUUID) {
        return morphComponents.computeIfAbsent(playerUUID, k -> new MorphComponent(playerUUID));
    }

    public static void removeComponent(ServerPlayer player) {
        morphComponents.remove(player.getUUID());
        syncFromPlayer(player);
    }

    public static void resetMorphs() {
        morphComponents.clear();
    }

    @Nullable
    public static MorphComponent getComponent(Player player) {
        return morphComponents.get(player.getUUID());
    }

    public static boolean hasComponent(Player player) {
        return morphComponents.containsKey(player.getUUID());
    }

    public static void setMorph(Player player, EntityType<?> morph) {
        MorphComponent component = getOrCreateComponent(player);
        component.setMorph(morph);
        syncFromPlayer(player);
    }

    public static void resetMorph(Player player) {
        setMorph(player, null);
    }

    public static void onPlayerJoin(ServerPlayer player) {
        getOrCreateComponent(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        removeComponent(player);
    }

    public static void syncFromPlayer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        MorphComponent component = getComponent(serverPlayer);
        String typeStr = "null";
        if (component != null) typeStr = component.getTypeAsString();
        SimplePackets.MORPH.sendToClient(List.of(serverPlayer.getStringUUID(), typeStr));
    }

    public static void syncToPlayer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        for (ServerPlayer otherPlayer : PlayerUtils.getAllPlayers()) {
            MorphComponent component = getOrCreateComponent(otherPlayer);
            String typeStr = component.getTypeAsString();
            SimplePackets.MORPH.target(serverPlayer).sendToClient(List.of(otherPlayer.getStringUUID(), typeStr));
        }
    }

    public static MorphComponent setFromPacket(UUID uuid, EntityType<?> morph) {
        MorphComponent component = getOrCreateComponent(uuid);
        component.setMorph(morph);
        return component;
    }

    /*
    private void loadFromPlayer() {
        if (player == null) return;
        NbtCompound playerData = ((IEntityDataSaver) player).getPersistentData();
        if (playerData.contains(MORPH_NBT_KEY)) {
            NbtCompound morphData = playerData.getCompound(MORPH_NBT_KEY);
            if (morphData.contains(TYPE_KEY)) {
                morph = Registries.ENTITY_TYPE.get(Identifier.of(morphData.getString(TYPE_KEY)));
            }
        }
    }

    private void saveToPlayer() {
        if (player == null) return;
        NbtCompound playerData = ((IEntityDataSaver) player).getPersistentData();
        NbtCompound morphData = new NbtCompound();

        if (morph != null) {
            morphData.putString(TYPE_KEY, Registries.ENTITY_TYPE.getId(morph).toString());
        }

        playerData.put(MORPH_NBT_KEY, morphData);
    }
    */
}