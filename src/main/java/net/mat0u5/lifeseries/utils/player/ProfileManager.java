package net.mat0u5.lifeseries.utils.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.mixin.PlayerAccessor;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.server;

//? if > 1.21 {
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.world.entity.PositionMoveRotation;
//?}

public class ProfileManager {

    private static final Map<UUID, Property> originalSkins = new HashMap<>();
    private static final Map<UUID, String> originalNames = new HashMap<>();

    public enum ProfileChange {
        NONE,
        EMPTY,
        ORIGINAL,
        SET;
        String info = "";
        public ProfileChange withInfo(String info) {
            this.info = info;
            return this;
        }
    }

    public static CompletableFuture<Boolean> modifyProfile(ServerPlayer player, ProfileChange skinChange, ProfileChange usernameChange) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!originalSkins.containsKey(player.getUUID())) {
                    Property originalSkin = getSkinProperty(player.getGameProfile());
                    originalSkins.put(player.getUUID(), originalSkin);
                }
                if (!originalNames.containsKey(player.getUUID())) {
                    originalNames.put(player.getUUID(), player.getScoreboardName());
                }

                Property targetSkin = null;
                if (skinChange == ProfileChange.ORIGINAL) {
                    targetSkin = originalSkins.get(player.getUUID());
                }
                if (skinChange == ProfileChange.SET) {
                    targetSkin = fetchSkinFromUsername(skinChange.info);
                }

                setProfile(player, skinChange, usernameChange, targetSkin);

                refreshPlayerProfile(player);
                if (usernameChange != ProfileChange.NONE) {
                    currentSeason.onPlayerJoin(player);
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private static void setProfile(ServerPlayer player, ProfileChange skinChange, ProfileChange usernameChange, Property targetSkin) {
        GameProfile currentProfile = player.getGameProfile();

        String name = OtherUtils.profileName(currentProfile);
        if (usernameChange == ProfileChange.ORIGINAL) {
            name = originalNames.get(player.getUUID());
        }
        if (usernameChange == ProfileChange.SET) {
            name = usernameChange.info;
        }


        //? if > 1.21.6 {
        Multimap<String, Property> properties;
        if (skinChange != ProfileChange.NONE) {
            properties = ArrayListMultimap.create();
            OtherUtils.profileProperties(currentProfile).forEach((key, property) -> {
                if (!key.equals("textures") && property != null) {
                    properties.put(key, property);
                }
            });
            if (skinChange != ProfileChange.EMPTY && targetSkin != null) {
                properties.put("textures", targetSkin);
            }
        }
        else {
            properties = currentProfile.properties();
        }
        //?}


        GameProfile newProfile = new GameProfile(
                OtherUtils.profileId(currentProfile),
                name
                //? if > 1.21.6 {
                ,new PropertyMap(properties)
                //?}
        );
        //? if <= 1.21.6 {
        /*if (skinChange != ProfileChange.NONE) {
            OtherUtils.profileProperties(currentProfile).forEach((key, property) -> {
                if (!key.equals("textures") && property != null) {
                    newProfile.getProperties().put(key, property);
                }
            });
            if (skinChange != ProfileChange.EMPTY && targetSkin != null) {
                newProfile.getProperties().put("textures", targetSkin);
            }
        }
        *///?}

        ((PlayerAccessor) player).ls$setGameProfile(newProfile);
    }

    private static Property getSkinProperty(GameProfile profile) {
        Collection<Property> textures = OtherUtils.profileProperties(profile).get("textures");
        if (textures.isEmpty()) {
            return null;
        }
        return textures.iterator().next();
    }

    private static Property fetchSkinFromUsername(String username) {
        try {
            URL uuidUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection uuidConn = (HttpURLConnection) uuidUrl.openConnection();
            uuidConn.setRequestMethod("GET");

            BufferedReader uuidReader = new BufferedReader(new InputStreamReader(uuidConn.getInputStream()));
            StringBuilder uuidResponse = new StringBuilder();
            String line;
            while ((line = uuidReader.readLine()) != null) {
                uuidResponse.append(line);
            }
            uuidReader.close();

            JsonObject uuidJson = JsonParser.parseString(uuidResponse.toString()).getAsJsonObject();
            String uuid = uuidJson.get("id").getAsString();

            URL skinUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection skinConn = (HttpURLConnection) skinUrl.openConnection();
            skinConn.setRequestMethod("GET");

            BufferedReader skinReader = new BufferedReader(new InputStreamReader(skinConn.getInputStream()));
            StringBuilder skinResponse = new StringBuilder();
            while ((line = skinReader.readLine()) != null) {
                skinResponse.append(line);
            }
            skinReader.close();

            JsonObject skinJson = JsonParser.parseString(skinResponse.toString()).getAsJsonObject();
            JsonObject texturesProperty = skinJson.getAsJsonArray("properties").get(0).getAsJsonObject();

            String value = texturesProperty.get("value").getAsString();
            String signature = texturesProperty.has("signature") ?
                    texturesProperty.get("signature").getAsString() : null;

            return new Property("textures", value, signature);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void refreshPlayerProfile(ServerPlayer player) {
        ServerLevel level = player.ls$getServerLevel();
        PlayerList playerList = server.getPlayerList();

        List<UUID> uuidList = Collections.singletonList(player.getUUID());
        playerList.broadcastAll(new ClientboundPlayerInfoRemovePacket(uuidList));
        playerList.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singleton(player)));

        refreshEntityForTrackers(player, level);

        //? if <= 1.20 {
        /*player.connection.send(new ClientboundRespawnPacket(
                                level.dimensionTypeId(),
                                level.dimension(),
                                BiomeManager.obfuscateSeed(level.getSeed()),
                                player.gameMode.getGameModeForPlayer(),
                                player.gameMode.getPreviousGameModeForPlayer(),
                                level.isDebug(),
                                level.isFlat(),
                                ClientboundRespawnPacket.KEEP_ALL_DATA,
                                player.getLastDeathLocation(),
                                player.getPortalCooldown()
                )
        );
        *///?} else if <= 1.21 {
        /*player.connection.send(new ClientboundRespawnPacket(
                        new CommonPlayerSpawnInfo(
                                //? if <= 1.20.3 {
                                level.dimensionTypeId(),
                                //?} else {
                                /^level.dimensionTypeRegistration(),
                                ^///?}
                                level.dimension(),
                                BiomeManager.obfuscateSeed(level.getSeed()),
                                player.gameMode.getGameModeForPlayer(),
                                player.gameMode.getPreviousGameModeForPlayer(),
                                level.isDebug(),
                                level.isFlat(),
                                player.getLastDeathLocation(),
                                player.getPortalCooldown()
                        ),
                        ClientboundRespawnPacket.KEEP_ALL_DATA
                )
        );
        *///?} else {
        player.connection.send(new ClientboundRespawnPacket(
                new CommonPlayerSpawnInfo(
                        level.dimensionTypeRegistration(),
                        level.dimension(),
                        BiomeManager.obfuscateSeed(level.getSeed()),
                        player.gameMode.getGameModeForPlayer(),
                        player.gameMode.getPreviousGameModeForPlayer(),
                        level.isDebug(),
                        level.isFlat(),
                        player.getLastDeathLocation(),
                        player.getPortalCooldown(),
                        level.getSeaLevel()
                ),
                ClientboundRespawnPacket.KEEP_ALL_DATA
        ));
        //?}

        restorePlayerState(player, level, playerList);
    }

    private static void refreshEntityForTrackers(ServerPlayer player, ServerLevel level) {
        try {
            ServerChunkCache chunkSource = level.getChunkSource();
            ChunkMap chunkMap = chunkSource.chunkMap;

            Field entityMapField = ChunkMap.class.getDeclaredField("entityMap");
            entityMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Integer, Object> entityMap = (Map<Integer, Object>) entityMapField.get(chunkMap);

            Object trackedEntity = entityMap.get(player.getId());
            if (trackedEntity != null) {
                Field seenByField = trackedEntity.getClass().getDeclaredField("seenBy");
                seenByField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Set<Object> seenBy = (Set<Object>) seenByField.get(trackedEntity);

                Field serverEntityField = trackedEntity.getClass().getDeclaredField("serverEntity");
                serverEntityField.setAccessible(true);
                Object serverEntity = serverEntityField.get(trackedEntity);

                for (Object connection : seenBy) {
                    Field playerField = connection.getClass().getDeclaredField("player");
                    playerField.setAccessible(true);
                    ServerPlayer trackingPlayer = (ServerPlayer) playerField.get(connection);

                    serverEntity.getClass().getMethod("addPairing", ServerPlayer.class)
                            .invoke(serverEntity, trackingPlayer);
                }
            }
        } catch (Exception e) {
            System.err.println("Entity tracker refresh failed: " + e.getMessage());
        }
    }

    private static void restorePlayerState(ServerPlayer player, ServerLevel level, PlayerList playerList) {
        //? if <= 1.21 {
        /*player.connection.send(new ClientboundPlayerPositionPacket(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), Collections.emptySet(), 0));
        *///?} else {
        player.connection.send(new ClientboundPlayerPositionPacket(0, PositionMoveRotation.of(player), Collections.emptySet()));
        //?}

        //? if <= 1.21 {
        /*player.connection.send(new ClientboundSetCarriedItemPacket(player.getInventory().selected));
        *///?} else if <= 1.21.4 {
        /*player.connection.send(new ClientboundSetCursorItemPacket(player.getInventory().getSelected()));
        *///?} else {
        player.connection.send(new ClientboundSetCursorItemPacket(player.getInventory().getSelectedItem()));
        //?}

        player.connection.send(new ClientboundChangeDifficultyPacket(level.getDifficulty(), level.getLevelData().isDifficultyLocked()));
        player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));

        playerList.sendLevelInfo(player, level);
        playerList.sendPlayerPermissionLevel(player);

        for (MobEffectInstance effect : player.getActiveEffects()) {
            //? if <= 1.20.3 {
            /*player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect));
            *///?} else {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect, false));
            //?}
        }

        List<Pair<EquipmentSlot, ItemStack>> equipment = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                equipment.add(Pair.of(slot, stack.copy()));
            }
        }
        if (!equipment.isEmpty()) {
            player.connection.send(new ClientboundSetEquipmentPacket(player.getId(), equipment));
        }

        if (!player.getPassengers().isEmpty()) {
            player.connection.send(new ClientboundSetPassengersPacket(player));
        }
        if (player.isPassenger()) {
            player.connection.send(new ClientboundSetPassengersPacket(player.getVehicle()));
        }

        player.onUpdateAbilities();
        playerList.sendAllPlayerInfo(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        resetPlayer(player).thenRun(() -> {
            originalSkins.remove(player.getUUID());
            originalNames.remove(player.getUUID());
        });
    }

    public static CompletableFuture<Boolean> resetPlayer(ServerPlayer player) {
        if (player == null) return CompletableFuture.completedFuture(false);
        if (hasChangedName(player) || hasChangedSkin(player)) {
            return modifyProfile(player, ProfileChange.ORIGINAL, ProfileChange.ORIGINAL);
        }
        return CompletableFuture.completedFuture(false);
    }

    public static boolean hasChangedName(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (originalNames.containsKey(uuid)) {
            if (!player.getScoreboardName().equals(originalNames.get(uuid))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasChangedSkin(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (originalSkins.containsKey(uuid)) {
            Property currentSkin = getSkinProperty(player.getGameProfile());
            Property originalSkin = originalSkins.get(uuid);
            //? if <= 1.20 {
            /*if (currentSkin != null && originalSkin != null && !currentSkin.getValue().equalsIgnoreCase(originalSkin.getValue())) {
             *///?} else {
            if (currentSkin != null && originalSkin != null && !currentSkin.value().equalsIgnoreCase(originalSkin.value())) {
                //?}
                return true;
            }
            if ((currentSkin == null) != (originalSkin == null)) {
                return true;
            }
        }
        return false;
    }

    public static void resetAll() {
        PlayerUtils.getAllPlayers().forEach(ProfileManager::resetPlayer);
    }
}