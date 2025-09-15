package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClientUtils {

    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }

    @Nullable
    public static PlayerEntity getPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.world == null) return null;
        return client.world.getPlayerByUuid(uuid);
    }

    @Nullable
    public static Team getPlayerTeam() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.player == null) return null;
        return client.player.getScoreboardTeam();
    }

    public static void runCommand(String command) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;

        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        handler.sendChatCommand(command);
    }
}
