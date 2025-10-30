package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.mixin.PlayerListS2CPacketAccessor;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class PlayerUtils {
    private static HashMap<Text, Integer> broadcastCooldown = new HashMap<>();

    public static void sendTitleWithSubtitle(ServerPlayerEntity player, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        if (!player.isAlive()) {
            TaskScheduler.scheduleTask(5, () -> sendTitleWithSubtitle(getPlayer(player.getUuid()), title, subtitle, fadeIn, stay, fadeOut));
            return;
        }
        TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
        player.networkHandler.sendPacket(fadePacket);
        TitleS2CPacket titlePacket = new TitleS2CPacket(title);
        player.networkHandler.sendPacket(titlePacket);
        SubtitleS2CPacket subtitlePacket = new SubtitleS2CPacket(subtitle);
        player.networkHandler.sendPacket(subtitlePacket);
    }

    public static void sendTitle(ServerPlayerEntity player, Text title, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        if (!player.isAlive()) {
            TaskScheduler.scheduleTask(5, () -> sendTitle(getPlayer(player.getUuid()), title, fadeIn, stay, fadeOut));
            return;
        }
        TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
        player.networkHandler.sendPacket(fadePacket);
        TitleS2CPacket titlePacket = new TitleS2CPacket(title);
        player.networkHandler.sendPacket(titlePacket);
    }

    public static void sendTitleToPlayers(Collection<ServerPlayerEntity> players, Text title, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayerEntity player : players) {
            sendTitle(player, title, fadeIn, stay, fadeOut);
        }
    }

    public static void sendTitleWithSubtitleToPlayers(Collection<ServerPlayerEntity> players, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayerEntity player : players) {
            sendTitleWithSubtitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound) {
        playSoundToPlayers(players,sound,SoundCategory.MASTER,1,1);
    }
    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound, float volume, float pitch) {
        playSoundToPlayers(players,sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        for (ServerPlayerEntity player : players) {
            if (player == null) continue;
            player.playSoundToPlayer(sound, soundCategory, volume, pitch);
        }
    }

    public static void playSoundToPlayer(ServerPlayerEntity player, SoundEvent sound) {
        playSoundToPlayer(player, sound, 1, 1);
    }

    public static void playSoundToPlayer(ServerPlayerEntity player, SoundEvent sound, float volume, float pitch) {
        player.playSoundToPlayer(sound, SoundCategory.MASTER, volume, pitch);
    }

    private static final Random rnd = new Random();
    public static void playSoundWithSourceToPlayers(Entity source, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        playSoundWithSourceToPlayers(getAllPlayers(), source, sound, soundCategory, volume, pitch);
    }
    public static void playSoundWithSourceToPlayers(Collection<ServerPlayerEntity> players, Entity source, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(Registries.SOUND_EVENT.getEntry(sound), soundCategory, source, volume, pitch, rnd.nextLong());
        for (ServerPlayerEntity player : players) {
            player.networkHandler.sendPacket(packet);
        }
    }

    public static List<ServerPlayerEntity> getAllPlayers() {
        List<ServerPlayerEntity> result = new ArrayList<>();
        if (server == null) return result;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (isFakePlayer(player)) continue;
            result.add(player);
        }
        return result;
    }

    public static List<ServerPlayerEntity> getAllFunctioningPlayers() {
        List<ServerPlayerEntity> result = getAllPlayers();
        result.removeIf(WatcherManager::isWatcher);
        return result;
    }

    public static List<ServerPlayerEntity> getAdminPlayers() {
        List<ServerPlayerEntity> result = getAllPlayers();
        result.removeIf(player -> !PermissionManager.isAdmin(player));
        return result;
    }

    public static ServerPlayerEntity getPlayer(String name) {
        if (server == null || name == null) return null;
        return server.getPlayerManager().getPlayer(name);
    }

    public static ServerPlayerEntity getPlayer(UUID uuid) {
        if (server == null || uuid == null) return null;
        return server.getPlayerManager().getPlayer(uuid);
    }

    public static void applyResourcepack(UUID uuid) {
        if (NetworkHandlerServer.wasHandshakeSuccessful(uuid)) return;
        applyServerResourcepack(uuid);
    }

    public static void applyServerResourcepack(UUID uuid) {
        if (server == null) return;
        ServerPlayerEntity player = getPlayer(uuid);
        if (player == null) return;
        applySingleResourcepack(player, Season.RESOURCEPACK_MAIN_URL, Season.RESOURCEPACK_MAIN_SHA, "Life Series Resourcepack.");
        applySingleResourcepack(player, Season.RESOURCEPACK_MINIMAL_ARMOR_URL, Season.RESOURCEPACK_MINIMAL_ARMOR_SHA, "Life Series Resourcepack.");
        if (currentSeason instanceof SecretLife) {
            applySingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL, Season.RESOURCEPACK_SECRETLIFE_SHA, "Life Series Resourcepack.");
        }
        else {
            removeSingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL);
        }
    }

    private static void applySingleResourcepack(ServerPlayerEntity player, String link, String sha1, String message) {
        UUID id = UUID.nameUUIDFromBytes(link.getBytes(StandardCharsets.UTF_8));
        ResourcePackSendS2CPacket resourcepackPacket = new ResourcePackSendS2CPacket(
                id,
                link,
                sha1,
                false,
                Optional.of(Text.translatable(message))
        );
        player.networkHandler.sendPacket(resourcepackPacket);
    }

    private static void removeSingleResourcepack(ServerPlayerEntity player, String link) {
        UUID id = UUID.nameUUIDFromBytes(link.getBytes(StandardCharsets.UTF_8));
        ResourcePackRemoveS2CPacket removePackPacket = new ResourcePackRemoveS2CPacket(Optional.of(id));
        player.networkHandler.sendPacket(removePackPacket);
    }

    public static List<ItemStack> getPlayerInventory(ServerPlayerEntity player) {
        List<ItemStack> list = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack);
            }
        }
        return list;
    }

    public static void clearItemStack(ServerPlayerEntity player, ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.equals(itemStack)) {
                inventory.removeStack(i);
            }
        }
    }

    public static Entity getEntityLookingAt(ServerPlayerEntity player, double maxDistance) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d direction = player.getRotationVec(1.0F).normalize().multiply(maxDistance);
        Vec3d end = start.add(direction);

        HitResult entityHit = ProjectileUtil.raycast(player, start, end,
                player.getBoundingBox().stretch(direction).expand(1.0),
                entity -> !entity.isSpectator() && entity.isAlive(), maxDistance*maxDistance);

        if (entityHit instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity();
        }

        return null;
    }
    public static Vec3d getPosLookingAt(ServerPlayerEntity player, double maxDistance) {
        HitResult blockHit = player.raycast(maxDistance, 1, false);
        if (Math.sqrt(blockHit.squaredDistanceTo(player)) >= (maxDistance*0.99)) {
            return null;
        }
        if (blockHit instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getPos();
        }
        return null;
    }

    public static boolean isFakePlayer(Entity player) {
        return player instanceof FakePlayer;
    }
    public static void displayMessageToPlayer(ServerPlayerEntity player, Text text, int timeFor) {
        Session.skipTimer.put(player.getUuid(), timeFor/5);
        player.sendMessage(text, true);
    }

    public static List<UUID> updateInventoryQueue = new ArrayList<>();
    public static void updatePlayerInventory(ServerPlayerEntity player) {
        if (updateInventoryQueue.contains(player.getUuid())) return;
        updateInventoryQueue.add(player.getUuid());
    }

    public static void resendCommandTree(ServerPlayerEntity player) {
        if (player == null) return;
        if (server == null) return;
        server.getCommandManager().sendCommandTree(player);
    }

    public static void resendCommandTrees() {
        for (ServerPlayerEntity player : getAllPlayers()) {
            resendCommandTree(player);
        }
    }

    public static ItemStack getEquipmentSlot(PlayerEntity player, int slot) {
        //? if <= 1.21.4 {
        return player.getInventory().getArmorStack(slot);
        //?} else {
        /*return player.getInventory().getStack(slot + 36);
        *///?}
    }

    //? if <= 1.21.4 {
    public static Iterable<ItemStack> getArmorItems(ServerPlayerEntity player) {
        return player.getArmorItems();
    }
    //?} else {
    /*public static List<ItemStack> getArmorItems(ServerPlayerEntity player) {
        List<ItemStack> result = new ArrayList<>();
        result.add(getEquipmentSlot(player, 0));
        result.add(getEquipmentSlot(player, 1));
        result.add(getEquipmentSlot(player, 2));
        result.add(getEquipmentSlot(player, 3));
        return result;
    }
    *///?}

    public static void updatePlayerLists() {
        if (server == null) return;
        if (currentSeason == null) return;

        List<ServerPlayerEntity> allPlayers = server.getPlayerManager().getPlayerList();

        for (ServerPlayerEntity receivingPlayer : allPlayers) {

            PlayerListS2CPacket packet = PlayerListS2CPacket.entryFromPlayer(List.of(receivingPlayer));
            List<PlayerListS2CPacket.Entry> newEntries = new ArrayList<>();
            for (ServerPlayerEntity player : allPlayers) {
                if (player == receivingPlayer) continue;

                boolean hidePlayer = hidePlayerFrom(receivingPlayer, player);

                PlayerListS2CPacket.Entry entry = getPlayerListEntry(player, !hidePlayer);
                newEntries.add(entry);
            }

            if (packet instanceof PlayerListS2CPacketAccessor accessor) {
                accessor.setEntries(newEntries);
            }
            receivingPlayer.networkHandler.sendPacket(packet);
        }
    }

    public static PlayerListS2CPacket.Entry getPlayerListEntry(ServerPlayerEntity player, boolean listed) {
        //? if <= 1.21 {
        return new PlayerListS2CPacket.Entry(player.getUuid(), player.getGameProfile(), listed, player.networkHandler.getLatency(), player.interactionManager.getGameMode(), player.getPlayerListName(), (PublicPlayerSession.Serialized) Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
        //?} else if <= 1.21.2 {
        /*return new PlayerListS2CPacket.Entry(player.getUuid(), player.getGameProfile(), listed, player.networkHandler.getLatency(), player.interactionManager.getGameMode(), player.getPlayerListName(), player.getPlayerListOrder(), (PublicPlayerSession.Serialized)Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
        *///?} else if <= 1.21.6 {
        /*return new PlayerListS2CPacket.Entry(player.getUuid(), player.getGameProfile(), listed, player.networkHandler.getLatency(), player.interactionManager.getGameMode(), player.getPlayerListName(), player.isPartVisible(PlayerModelPart.HAT), player.getPlayerListOrder(), (PublicPlayerSession.Serialized)Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
        *///?} else {
        /*return new PlayerListS2CPacket.Entry(player.getUuid(), player.getGameProfile(), listed, player.networkHandler.getLatency(), player.getGameMode(), player.getPlayerListName(), player.isModelPartVisible(PlayerModelPart.HAT), player.getPlayerListOrder(), (PublicPlayerSession.Serialized)Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
        *///?}
    }

    public static boolean hidePlayerFrom(ServerPlayerEntity receivingPlayer, ServerPlayerEntity player) {
        if (receivingPlayer == null || player == null) return false;
        if (PlayerUtils.isFakePlayer(player)) return true;
        if (hideDeadPlayerFrom(receivingPlayer, player)) return true;
        if (hideWatcherPlayerFrom(receivingPlayer, player)) return true;
        return false;
    }

    private static boolean hideDeadPlayerFrom(ServerPlayerEntity receivingPlayer, ServerPlayerEntity player) {
        if (receivingPlayer.isSpectator()) return false;
        if (!player.isSpectator()) return false;

        if (currentSeason.TAB_LIST_SHOW_DEAD_PLAYERS) return false;
        if (receivingPlayer.ls$isDead()) return false;
        if (player.ls$isAlive()) return false;
        if (player.ls$isWatcher()) return false;
        if (Necromancy.preIsRessurectedPlayer(player)) return false;
        return true;
    }

    private static boolean hideWatcherPlayerFrom(ServerPlayerEntity receivingPlayer, ServerPlayerEntity player) {
        if (receivingPlayer.isSpectator()) return false;
        if (!player.isSpectator()) return false;

        if (currentSeason.WATCHERS_IN_TAB) return false;
        if (receivingPlayer.ls$isWatcher()) return false;
        if (!player.ls$isWatcher()) return false;
        return true;
    }

    public static ServerWorld getServerWorld(ServerPlayerEntity player) {
        //? if <= 1.21.5 {
        return player.getServerWorld();
         //?} else if <= 1.21.6 {
        /*return player.getWorld();
         *///?} else {
        /*return player.getEntityWorld();
        *///?}
    }

    public static World getWorld(PlayerEntity player) {
        //? if <= 1.21.6 {
        return player.getWorld();
         //?} else {
        /*return player.getEntityWorld();
        *///?}
    }

    public static void onTick() {
        if (!broadcastCooldown.isEmpty()) {
            HashMap<Text, Integer> newCooldowns = new HashMap<>();
            for (Map.Entry<Text, Integer> entry : broadcastCooldown.entrySet()) {
                Text key = entry.getKey();
                Integer value = entry.getValue();
                value--;
                if (value > 0) {
                    newCooldowns.put(key, value);
                }
            }
            broadcastCooldown = newCooldowns;
        }

        if (!updateInventoryQueue.isEmpty()) {
            for (UUID uuid : updateInventoryQueue) {
                ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
                if (player == null) continue;

                player.getInventory().updateItems();
                player.currentScreenHandler.sendContentUpdates();
                if (!player.isCreative()) {
                    player.currentScreenHandler.syncState();
                    player.playerScreenHandler.syncState();
                }
            }
            updateInventoryQueue.clear();
        }
    }

    public static void broadcastMessage(Text message) {
        broadcastMessage(message, 1);
    }

    public static void broadcastMessageToAdmins(Text message) {
        broadcastMessageToAdmins(message, 1);
    }

    public static void broadcastMessageExcept(Text message, ServerPlayerEntity exceptPlayer) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (player == exceptPlayer) continue;
            player.sendMessage(message, false);
        }
    }

    public static void broadcastMessage(Text message, int cooldownTicks) {
        if (broadcastCooldown.containsKey(message)) return;
        broadcastCooldown.put(message, cooldownTicks);

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.sendMessage(message, false);
        }
    }

    public static void broadcastMessageToAdmins(Text message, int cooldownTicks) {
        if (broadcastCooldown.containsKey(message)) return;
        broadcastCooldown.put(message, cooldownTicks);

        for (ServerPlayerEntity player : PlayerUtils.getAdminPlayers()) {
            player.sendMessage(message, false);
        }
        Main.LOGGER.info(message.getString());
    }

    public static void teleport(ServerPlayerEntity player, BlockPos pos) {
        teleport(player, getServerWorld(player), pos.toBottomCenterPos());
    }

    public static void teleport(ServerPlayerEntity player, Vec3d pos) {
        teleport(player, getServerWorld(player), pos);
    }

    public static void teleport(ServerPlayerEntity player, double destX, double destY, double destZ) {
        teleport(player, getServerWorld(player), destX, destY, destZ);
    }

    public static void teleport(ServerPlayerEntity player, ServerWorld world, double destX, double destY, double destZ) {
        teleport(player, world, destX, destY, destZ, player.getYaw(), player.getPitch());
    }

    public static void teleport(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        teleport(player, world, pos.toBottomCenterPos());
    }

    public static void teleport(ServerPlayerEntity player, ServerWorld world, Vec3d pos) {
        teleport(player, world, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());
    }

    public static void teleport(ServerPlayerEntity player, ServerWorld world, Vec3d pos, float yaw, float pitch) {
        teleport(player, world, pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
    }

    public static void teleport(ServerPlayerEntity player, ServerWorld world, double destX, double destY, double destZ, float yaw, float pitch) {
        //? if <= 1.21 {
        player.teleport(world, destX, destY, destZ, EnumSet.noneOf(PositionFlag.class), yaw, pitch);
        //?} else {
        /*player.teleport(world, destX, destY, destZ, EnumSet.noneOf(PositionFlag.class), yaw, pitch, false);
         *///?}
    }

    public static void safelyPutIntoSurvival(ServerPlayerEntity player) {
        if (player.interactionManager.getGameMode() == GameMode.SURVIVAL) return;

        //Teleport to the highest block in the terrain
        BlockPos.Mutable playerBlockPos = player.getBlockPos().mutableCopy();
        int safeY = WorldUtils.findTopSafeY(getServerWorld(player), playerBlockPos.toCenterPos());
        playerBlockPos.setY(safeY);
        teleport(player, playerBlockPos);

        player.changeGameMode(GameMode.SURVIVAL);
    }

    public static ServerPlayerEntity getPlayerOrProjection(ServerPlayerEntity player) {
        if (player == null) return null;
        if (!PlayerUtils.isFakePlayer(player)) return player;

        //? if <= 1.21.6 {
        if (player instanceof FakePlayer fakePlayer) {
            return PlayerUtils.getPlayer(fakePlayer.shadow);
        }
        //?}
        return player;
    }

    public static void damage(ServerPlayerEntity player, ServerWorld world, DamageSource source, float amount) {
        //? if <= 1.21 {
        player.damage(source, amount);
        //?} else {
        /*player.damage(world, source, amount);
        *///?}
    }

    public static void damage(ServerPlayerEntity player, DamageSource source, float amount) {
        damage(player, getServerWorld(player), source, amount);
    }

    public static void killFromSource(ServerPlayerEntity player, DamageSource source) {
        player.setHealth(0.0001f);
        damage(player, source, 10);
        if (player.isAlive()) {
            //? if <= 1.21 {
            player.kill();
            //?} else {
            /*player.kill(getServerWorld(player));
            *///?}
        }
    }

    public static void broadcastToVisiblePlayers(ServerPlayerEntity broadcaster, Text message) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (hidePlayerFrom(player, broadcaster)) continue;
            player.sendMessage(message);
        }
    }
}
