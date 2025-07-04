package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.server;

public class PlayerUtils {

    public static void sendTitleWithSubtitle(ServerPlayerEntity player, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        if (player.isDead()) {
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
        if (player.isDead()) {
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
            player.playSoundToPlayer(sound, soundCategory, volume, pitch);
        }
    }

    private static final Random rnd = new Random();
    public static void playSoundWithSourceToPlayers(Collection<ServerPlayerEntity> players, Entity source, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(Registries.SOUND_EVENT.getEntry(sound), soundCategory, source, volume, pitch, rnd.nextLong());
        for (ServerPlayerEntity player : players) {
            player.networkHandler.sendPacket(packet);
        }
    }

    public static List<ServerPlayerEntity> getAllPlayers() {
        if (server == null) return new ArrayList<>();
        List<ServerPlayerEntity> result = new ArrayList<>();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!(player instanceof FakePlayer)) {
                result.add(player);
            }
        }
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
        applySingleResourcepack(player, Season.RESOURCEPACK_MAIN_URL, Season.RESOURCEPACK_MAIN_SHA, "Life Series Main Resourcepack.");
        applySingleResourcepack(player, Season.RESOURCEPACK_MINIMAL_ARMOR_URL, Season.RESOURCEPACK_MINIMAL_ARMOR_SHA, "Minimal Armor Resourcepack.");
        if (currentSeason instanceof SecretLife) {
            applySingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL, Season.RESOURCEPACK_SECRETLIFE_SHA, "Secret Life Resourcepack.");
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

    public static boolean isFakePlayer(PlayerEntity player) {
        return player instanceof FakePlayer;
    }
    public static void displayMessageToPlayer(ServerPlayerEntity player, Text text, int timeFor) {
        Session.skipTimer.put(player.getUuid(), timeFor/5);
        player.sendMessage(text, true);
    }

    public static void updatePlayerInventory(ServerPlayerEntity player) {
        if (!player.isCreative()) {
            player.currentScreenHandler.syncState();
            player.playerScreenHandler.syncState();
        }
        player.getInventory().updateItems();
        player.currentScreenHandler.sendContentUpdates();
    }

    public static void resendCommandTree(ServerPlayerEntity player) {
        if (player == null) return;
        if (player.getServer() == null) return;
        player.getServer().getCommandManager().sendCommandTree(player);
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
            List<ServerPlayerEntity> visiblePlayers = new ArrayList<>();
            List<UUID> hiddenPlayerUUIDs = new ArrayList<>();

            for (ServerPlayerEntity player : allPlayers) {
                if (player == receivingPlayer) continue;

                if (!currentSeason.isAlive(receivingPlayer) || currentSeason.TAB_LIST_SHOW_DEAD_PLAYERS || currentSeason.isAlive(player)) {
                    visiblePlayers.add(player);
                    continue;
                }

                hiddenPlayerUUIDs.add(player.getUuid());
            }
            if (!visiblePlayers.isEmpty()) {
                receivingPlayer.networkHandler.sendPacket(PlayerListS2CPacket.entryFromPlayer(visiblePlayers));
            }
            if (!hiddenPlayerUUIDs.isEmpty()) {
                PlayerRemoveS2CPacket hidePacket = new PlayerRemoveS2CPacket(hiddenPlayerUUIDs);
                receivingPlayer.networkHandler.sendPacket(hidePacket);
            }
        }
    }

    public static ServerWorld getServerWorld(ServerPlayerEntity player) {
        //? if <= 1.21.5 {
        return player.getServerWorld();
        //?} else {
        /*return player.getWorld();
        *///?}
    }
}
