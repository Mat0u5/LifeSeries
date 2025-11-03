package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.mat0u5.lifeseries.Main.server;

public class PermissionManager {
    public static boolean isAdmin(ServerPlayer player) {
        if (player == null) return false;
        if (Main.isClientPlayer(player.getUUID())) return true;
        if (server == null) return false;
        //? if < 1.21.9 {
        return server.getPlayerList().isOp(player.getGameProfile());
        //?} else {
        /*return server.getPlayerManager().isOperator(player.getPlayerConfigEntry());
        *///?}
    }

    public static boolean isAdmin(CommandSourceStack source) {
        if (source.getEntity() == null) return true;
        return isAdmin(source.getPlayer());
    }
}
