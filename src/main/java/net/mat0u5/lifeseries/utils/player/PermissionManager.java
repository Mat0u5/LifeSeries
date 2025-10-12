package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.mat0u5.lifeseries.Main.server;

public class PermissionManager {
    public static boolean isAdmin(ServerPlayerEntity player) {
        if (player == null) return false;
        if (Main.isClientPlayer(player.getUuid())) return true;
        if (server == null) return false;
        //? if < 1.21.9 {
        return server.getPlayerManager().isOperator(player.getGameProfile());
        //?} else {
        /*return server.getPlayerManager().isOperator(player.getPlayerConfigEntry());
        *///?}
    }

    public static boolean isAdmin(ServerCommandSource source) {
        if (source.getEntity() == null) return true;
        return isAdmin(source.getPlayer());
    }
}
