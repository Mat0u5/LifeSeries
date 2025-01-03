package net.mat0u5.lifeseries.utils;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.server;

public class PlayerUtils {

    public static void sendTitleWithSubtitle(ServerPlayerEntity player, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        if (player.isDead()) {
            TaskScheduler.scheduleTask(5, () -> sendTitleWithSubtitle(server.getPlayerManager().getPlayer(player.getUuid()), title, subtitle, fadeIn, stay, fadeOut));
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
        if (player == null) return;
        if (player.isDead()) {
            TaskScheduler.scheduleTask(5, () -> sendTitle(server.getPlayerManager().getPlayer(player.getUuid()), title, fadeIn, stay, fadeOut));
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

    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound, SoundCategory soundCategory, int volume, int pitch) {
        for (ServerPlayerEntity player : players) {
            player.playSoundToPlayer(sound, soundCategory, volume, pitch);
        }
    }

    public static List<ServerPlayerEntity> getAllPlayers() {
        return server.getPlayerManager().getPlayerList();
    }

    public static void applyResorucepack(ServerPlayerEntity player) {
        String RESOURCEPACK_LINK = currentSeries.getResourcepackURL();
        String RESOURCEPACK_SHA1 = currentSeries.getResourcepackSHA1();
        UUID id = UUID.nameUUIDFromBytes(RESOURCEPACK_LINK.getBytes(StandardCharsets.UTF_8));

        if (player.getServer().isDedicated()) {
            ResourcePackSendS2CPacket resourcepackPacket = new ResourcePackSendS2CPacket(
                    id,
                    RESOURCEPACK_LINK,
                    RESOURCEPACK_SHA1,
                    false,
                    Optional.of(Text.translatable("Life Series resourcepack."))
            );
            player.networkHandler.sendPacket(resourcepackPacket);
        }
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
}
