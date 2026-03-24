package net.mat0u5.lifeseries.utils.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.mixin.ChunkMapAccessor;
import net.mat0u5.lifeseries.mixin.PlayerAccessor;
import net.mat0u5.lifeseries.mixin.TrackedEntityAccessor;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.minecraft.network.protocol.game.*;
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
    public static final Map<UUID, String> manualSkins = new HashMap<>();

    public static class ProfileChange {
        final ProfileChangeType type;
        final String info;
        public ProfileChange(ProfileChangeType type) {
            this(type, "");
        }
        public ProfileChange(ProfileChangeType type, String info) {
            this.type = type;
            this.info = info;
        }
        public static ProfileChange none() {
            return new ProfileChange(ProfileChangeType.NONE);
        }
        public static ProfileChange empty() {
            return new ProfileChange(ProfileChangeType.EMPTY);
        }
        public static ProfileChange original() {
            return new ProfileChange(ProfileChangeType.ORIGINAL);
        }
        public static ProfileChange set(String info) {
            return new ProfileChange(ProfileChangeType.SET, info);
        }
    }
    public enum ProfileChangeType {
        NONE,
        EMPTY,
        ORIGINAL,
        SET;
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
                if (skinChange.type == ProfileChangeType.ORIGINAL) {
                    targetSkin = originalSkins.get(player.getUUID());
                }
                if (skinChange.type == ProfileChangeType.SET) {
                    targetSkin = fetchSkinFromUsername(skinChange.info);
                }

                Tuple<Boolean, Boolean> changed = setProfile(player, skinChange, usernameChange, targetSkin);
                boolean changedSkin = changed.x;
                boolean changedName = changed.y;

                if (changedSkin || changedName) {
                    refreshPlayerProfile(player);
                    if (usernameChange.type != ProfileChangeType.NONE && changedName) {
                        currentSeason.onPlayerJoin(player);
                        currentSeason.usernameChanged(player);
                    }
                    LifeSkinsManager.refreshLifeSkin(player);
                }
                return changedSkin || changedName;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private static Tuple<Boolean, Boolean> setProfile(ServerPlayer player, ProfileChange skinChange, ProfileChange usernameChange, Property targetSkin) {
        GameProfile currentProfile = player.getGameProfile();

        String name = OtherUtils.profileName(currentProfile);
        if (usernameChange.type == ProfileChangeType.ORIGINAL) {
            name = originalNames.get(player.getUUID());
        }
        if (usernameChange.type == ProfileChangeType.SET) {
            name = usernameChange.info;
        }


        //? if > 1.21.6 {
        Multimap<String, Property> properties;
        if (skinChange.type != ProfileChangeType.NONE) {
            properties = ArrayListMultimap.create();
            OtherUtils.profileProperties(currentProfile).forEach((key, property) -> {
                if (!key.equals("textures") && property != null) {
                    properties.put(key, property);
                }
            });
            if (skinChange.type != ProfileChangeType.EMPTY && targetSkin != null) {
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
        /*if (skinChange.type != ProfileChangeType.NONE) {
            OtherUtils.profileProperties(currentProfile).forEach((key, property) -> {
                if (!key.equals("textures") && property != null) {
                    newProfile.getProperties().put(key, property);
                }
            });
            if (skinChange.type != ProfileChangeType.EMPTY && targetSkin != null) {
                newProfile.getProperties().put("textures", targetSkin);
            }
        }
        *///?}

        boolean changedName = usernameChange.type != ProfileChangeType.NONE && !Objects.equals(OtherUtils.profileName(currentProfile), OtherUtils.profileName(newProfile));
        Property originalSkin = getSkinProperty(player.getGameProfile());
        boolean changedSkin = skinChange.type != ProfileChangeType.NONE && !areEqualSkins(originalSkin, targetSkin);
        if (changedName  || changedSkin) {
            ((PlayerAccessor) player).ls$setGameProfile(newProfile);
        }
        return new Tuple<>(changedSkin, changedName);
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
                                /^level.dimensionTypeId(),
                                ^///?} else {
                                level.dimensionTypeRegistration(),
                                //?}
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
            TrackedEntityAccessor trackedEntity = ((ChunkMapAccessor) chunkSource.chunkMap).getEntityTrackers().get(player.getId());
            if (trackedEntity == null) return;

            trackedEntity.getSeenBy().forEach(connection ->
                    trackedEntity.getServerEntity().addPairing(connection.getPlayer())
            );
        } catch (Exception e) {
            Main.LOGGER.error("Entity tracker refresh failed: " + e.getMessage());
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
            return modifyProfile(player, ProfileChange.original(), ProfileChange.original());
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
            return !areEqualSkins(currentSkin, originalSkin);
        }
        return false;
    }

    public static boolean areEqualSkins(Property skin1, Property skin2) {
        //~ if > 1.20 '.getValue()' -> '.value()' {
        if (skin1 != null && skin2 != null && !skin1.value().equalsIgnoreCase(skin2.value())) {
        //~}
            return false;
        }
        if ((skin1 == null) != (skin2 == null)) {
            return false;
        }
        return true;
    }

    public static void resetAll() {
        PlayerUtils.getAllPlayers().forEach(ProfileManager::resetPlayer);
    }
}