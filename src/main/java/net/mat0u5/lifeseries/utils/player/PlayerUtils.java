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
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.server;

//? if <= 1.21
import net.minecraft.world.entity.RelativeMovement;
//? if >= 1.21.2
/*import net.minecraft.world.entity.Relative;*/
//? if >= 1.21.4
/*import net.minecraft.world.entity.player.PlayerModelPart;*/

//?if > 1.20 {
/*import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
*///?}

public class PlayerUtils {
    private static HashMap<Component, Integer> broadcastCooldown = new HashMap<>();

    public static void sendTitleWithSubtitle(ServerPlayer player, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        if (!player.isAlive()) {
            TaskScheduler.scheduleTask(5, () -> sendTitleWithSubtitle(getPlayer(player.getUUID()), title, subtitle, fadeIn, stay, fadeOut));
            return;
        }
        ClientboundSetTitlesAnimationPacket fadePacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
        player.connection.send(fadePacket);
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
        player.connection.send(titlePacket);
        ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
        player.connection.send(subtitlePacket);
    }

    public static void sendTitle(ServerPlayer player, Component title, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        if (!player.isAlive()) {
            TaskScheduler.scheduleTask(5, () -> sendTitle(getPlayer(player.getUUID()), title, fadeIn, stay, fadeOut));
            return;
        }
        ClientboundSetTitlesAnimationPacket fadePacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
        player.connection.send(fadePacket);
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
        player.connection.send(titlePacket);
    }

    public static void sendTitleToPlayers(Collection<ServerPlayer> players, Component title, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayer player : players) {
            sendTitle(player, title, fadeIn, stay, fadeOut);
        }
    }

    public static void sendTitleWithSubtitleToPlayers(Collection<ServerPlayer> players, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayer player : players) {
            sendTitleWithSubtitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void playSoundToPlayers(Collection<ServerPlayer> players, SoundEvent sound) {
        playSoundToPlayers(players,sound,SoundSource.MASTER,1,1);
    }
    public static void playSoundToPlayers(Collection<ServerPlayer> players, SoundEvent sound, float volume, float pitch) {
        playSoundToPlayers(players,sound, SoundSource.MASTER, volume, pitch);
    }

    public static void playSoundToPlayers(Collection<ServerPlayer> players, SoundEvent sound, SoundSource soundCategory, float volume, float pitch) {
        for (ServerPlayer player : players) {
            if (player == null) continue;
            player.ls$playNotifySound(sound, soundCategory, volume, pitch);
        }
    }

    public static void playSoundToPlayer(ServerPlayer player, SoundEvent sound) {
        playSoundToPlayer(player, sound, 1, 1);
    }

    public static void playSoundToPlayer(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        player.ls$playNotifySound(sound, SoundSource.MASTER, volume, pitch);
    }

    private static final Random rnd = new Random();
    public static void playSoundWithSourceToPlayers(Entity source, SoundEvent sound, SoundSource soundCategory, float volume, float pitch) {
        playSoundWithSourceToPlayers(getAllPlayers(), source, sound, soundCategory, volume, pitch);
    }
    public static void playSoundWithSourceToPlayers(Collection<ServerPlayer> players, Entity source, SoundEvent sound, SoundSource soundCategory, float volume, float pitch) {
        ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundCategory, source, volume, pitch, rnd.nextLong());
        for (ServerPlayer player : players) {
            player.connection.send(packet);
        }
    }

    public static List<ServerPlayer> getAllPlayers() {
        List<ServerPlayer> result = new ArrayList<>();
        if (server == null) return result;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isFakePlayer(player)) continue;
            result.add(player);
        }
        return result;
    }

    public static List<ServerPlayer> getAllFunctioningPlayers() {
        List<ServerPlayer> result = getAllPlayers();
        result.removeIf(WatcherManager::isWatcher);
        return result;
    }

    public static List<ServerPlayer> getAdminPlayers() {
        List<ServerPlayer> result = getAllPlayers();
        result.removeIf(player -> !PermissionManager.isAdmin(player));
        return result;
    }

    public static ServerPlayer getPlayer(String name) {
        if (server == null || name == null) return null;
        return server.getPlayerList().getPlayerByName(name);
    }

    public static ServerPlayer getPlayer(UUID uuid) {
        if (server == null || uuid == null) return null;
        return server.getPlayerList().getPlayer(uuid);
    }

    public static void applyResourcepack(UUID uuid) {
        if (NetworkHandlerServer.wasHandshakeSuccessful(uuid)) return;
        applyServerResourcepack(uuid);
    }

    public static void applyServerResourcepack(UUID uuid) {
        if (server == null) return;
        ServerPlayer player = getPlayer(uuid);
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

    private static void applySingleResourcepack(ServerPlayer player, String link, String sha1, String message) {
        UUID id = UUID.nameUUIDFromBytes(link.getBytes(StandardCharsets.UTF_8));
        ClientboundResourcePackPushPacket resourcepackPacket = new ClientboundResourcePackPushPacket(
                id,
                link,
                sha1,
                false,
                Optional.of(Component.translatable(message))
        );
        player.connection.send(resourcepackPacket);
    }

    private static void removeSingleResourcepack(ServerPlayer player, String link) {
        UUID id = UUID.nameUUIDFromBytes(link.getBytes(StandardCharsets.UTF_8));
        ClientboundResourcePackPopPacket removePackPacket = new ClientboundResourcePackPopPacket(Optional.of(id));
        player.connection.send(removePackPacket);
    }

    public static List<ItemStack> getPlayerInventory(ServerPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack);
            }
        }
        return list;
    }

    public static void clearItemStack(ServerPlayer player, ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return;
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.equals(itemStack)) {
                inventory.removeItemNoUpdate(i);
            }
        }
    }

    public static Entity getEntityLookingAt(ServerPlayer player, double maxDistance) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 direction = player.getViewVector(1.0F).normalize().scale(maxDistance);
        Vec3 end = start.add(direction);

        HitResult entityHit = ProjectileUtil.getEntityHitResult(player, start, end,
                player.getBoundingBox().expandTowards(direction).inflate(1.0),
                entity -> !entity.isSpectator() && entity.isAlive(), maxDistance*maxDistance);

        if (entityHit instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity();
        }

        return null;
    }
    public static Vec3 getPosLookingAt(ServerPlayer player, double maxDistance) {
        HitResult blockHit = player.pick(maxDistance, 1, false);
        if (Math.sqrt(blockHit.distanceTo(player)) >= (maxDistance*0.99)) {
            return null;
        }
        if (blockHit instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getLocation();
        }
        return null;
    }

    public static boolean isFakePlayer(Entity player) {
        return player instanceof FakePlayer;
    }
    public static void displayMessageToPlayer(ServerPlayer player, Component text, int timeFor) {
        Session.skipTimer.put(player.getUUID(), timeFor/5);
        player.displayClientMessage(text, true);
    }

    public static List<UUID> updateInventoryQueue = new ArrayList<>();
    public static void updatePlayerInventory(ServerPlayer player) {
        if (updateInventoryQueue.contains(player.getUUID())) return;
        updateInventoryQueue.add(player.getUUID());
    }

    public static void resendCommandTree(ServerPlayer player) {
        if (player == null) return;
        if (server == null) return;
        server.getCommands().sendCommands(player);
    }

    public static void resendCommandTrees() {
        for (ServerPlayer player : getAllPlayers()) {
            resendCommandTree(player);
        }
    }

    public static ItemStack getEquipmentSlot(Player player, int slot) {
        //? if <= 1.21.4 {
        return player.getInventory().getArmor(slot);
        //?} else {
        /*return player.getInventory().getItem(slot + 36);
        *///?}
    }

    //? if <= 1.21.4 {
    public static Iterable<ItemStack> getArmorItems(ServerPlayer player) {
        return player.getArmorSlots();
    }
    //?} else {
    /*public static List<ItemStack> getArmorItems(ServerPlayer player) {
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
        //? if <= 1.21 {
        return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), (RemoteChatSession.Data) Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        //?} else if <= 1.21.2 {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?} else if <= 1.21.6 {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode.getGameModeForPlayer(), player.getTabListDisplayName(), player.isModelPartShown(PlayerModelPart.HAT), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?} else {
        /*return new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(), listed, player.connection.latency(), player.gameMode(), player.getTabListDisplayName(), player.isModelPartShown(PlayerModelPart.HAT), player.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(player.getChatSession(), RemoteChatSession::asData));
        *///?}
    }

    public static boolean hidePlayerFrom(ServerPlayer receivingPlayer, ServerPlayer player) {
        if (receivingPlayer == null || player == null) return false;
        if (PlayerUtils.isFakePlayer(player)) return true;
        if (hideDeadPlayerFrom(receivingPlayer, player)) return true;
        if (hideWatcherPlayerFrom(receivingPlayer, player)) return true;
        return false;
    }

    private static boolean hideDeadPlayerFrom(ServerPlayer receivingPlayer, ServerPlayer player) {
        if (receivingPlayer.isSpectator()) return false;
        if (!player.isSpectator()) return false;

        if (currentSeason.TAB_LIST_SHOW_DEAD_PLAYERS) return false;
        if (receivingPlayer.ls$isDead()) return false;
        if (player.ls$isAlive()) return false;
        if (player.ls$isWatcher()) return false;
        if (Necromancy.preIsRessurectedPlayer(player)) return false;
        return true;
    }

    private static boolean hideWatcherPlayerFrom(ServerPlayer receivingPlayer, ServerPlayer player) {
        if (receivingPlayer.isSpectator()) return false;
        if (!player.isSpectator()) return false;

        if (currentSeason.WATCHERS_IN_TAB) return false;
        if (receivingPlayer.ls$isWatcher()) return false;
        if (!player.ls$isWatcher()) return false;
        return true;
    }

    public static void onTick() {
        if (!broadcastCooldown.isEmpty()) {
            HashMap<Component, Integer> newCooldowns = new HashMap<>();
            for (Map.Entry<Component, Integer> entry : broadcastCooldown.entrySet()) {
                Component key = entry.getKey();
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
                ServerPlayer player = PlayerUtils.getPlayer(uuid);
                if (player == null) continue;

                player.getInventory().tick();
                player.containerMenu.broadcastChanges();
                if (!player.isCreative()) {
                    player.containerMenu.sendAllDataToRemote();
                    player.inventoryMenu.sendAllDataToRemote();
                }
            }
            updateInventoryQueue.clear();
        }
    }

    public static void broadcastMessage(Component message) {
        broadcastMessage(message, 1);
    }

    public static void broadcastMessageToAdmins(Component message) {
        broadcastMessageToAdmins(message, 1);
    }

    public static void broadcastMessageExcept(Component message, ServerPlayer exceptPlayer) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (player == exceptPlayer) continue;
            player.displayClientMessage(message, false);
        }
    }

    public static void broadcastMessage(Component message, int cooldownTicks) {
        if (broadcastCooldown.containsKey(message)) return;
        broadcastCooldown.put(message, cooldownTicks);

        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.displayClientMessage(message, false);
        }
    }

    public static void broadcastMessageToAdmins(Component message, int cooldownTicks) {
        if (broadcastCooldown.containsKey(message)) return;
        broadcastCooldown.put(message, cooldownTicks);

        for (ServerPlayer player : PlayerUtils.getAdminPlayers()) {
            player.displayClientMessage(message, false);
        }
        Main.LOGGER.info(message.getString());
    }

    public static void teleport(ServerPlayer player, BlockPos pos) {
        teleport(player, player.ls$getServerLevel(), pos.getBottomCenter());
    }

    public static void teleport(ServerPlayer player, Vec3 pos) {
        teleport(player, player.ls$getServerLevel(), pos);
    }

    public static void teleport(ServerPlayer player, double destX, double destY, double destZ) {
        teleport(player, player.ls$getServerLevel(), destX, destY, destZ);
    }

    public static void teleport(ServerPlayer player, ServerLevel level, double destX, double destY, double destZ) {
        teleport(player, level, destX, destY, destZ, player.getYRot(), player.getXRot());
    }

    public static void teleport(ServerPlayer player, ServerLevel level, BlockPos pos) {
        teleport(player, level, pos.getBottomCenter());
    }

    public static void teleport(ServerPlayer player, ServerLevel level, Vec3 pos) {
        teleport(player, level, pos.x(), pos.y(), pos.z(), player.getYRot(), player.getXRot());
    }

    public static void teleport(ServerPlayer player, ServerLevel level, Vec3 pos, float yaw, float pitch) {
        teleport(player, level, pos.x(), pos.y(), pos.z(), yaw, pitch);
    }

    public static void teleport(ServerPlayer player, ServerLevel level, double destX, double destY, double destZ, float yaw, float pitch) {
        //? if <= 1.21 {
        player.teleportTo(level, destX, destY, destZ, EnumSet.noneOf(RelativeMovement.class), yaw, pitch);
        //?} else {
        /*player.teleportTo(level, destX, destY, destZ, EnumSet.noneOf(Relative.class), yaw, pitch, false);
         *///?}
    }

    public static void safelyPutIntoSurvival(ServerPlayer player) {
        if (player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) return;

        //Teleport to the highest block in the terrain
        BlockPos.MutableBlockPos playerBlockPos = player.blockPosition().mutable();
        int safeY = LevelUtils.findTopSafeY(player.ls$getServerLevel(), playerBlockPos.getCenter());
        playerBlockPos.setY(safeY);
        teleport(player, playerBlockPos);

        player.setGameMode(GameType.SURVIVAL);
    }

    public static ServerPlayer getPlayerOrProjection(ServerPlayer player) {
        if (player == null) return null;
        if (!PlayerUtils.isFakePlayer(player)) return player;

        //? if <= 1.21.6 {
        if (player instanceof FakePlayer fakePlayer) {
            return PlayerUtils.getPlayer(fakePlayer.shadow);
        }
        //?}
        return player;
    }

    public static void killFromSource(ServerPlayer player, DamageSource source) {
        player.setHealth(0.0001f);
        player.ls$hurt(source, 10);
        if (player.isAlive()) {
            //? if <= 1.21 {
            player.kill();
            //?} else {
            /*player.kill(player.ls$getServerLevel());
            *///?}
        }
    }

    public static void broadcastToVisiblePlayers(ServerPlayer broadcaster, Component message) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (hidePlayerFrom(player, broadcaster)) continue;
            player.sendSystemMessage(message);
        }
    }
}
