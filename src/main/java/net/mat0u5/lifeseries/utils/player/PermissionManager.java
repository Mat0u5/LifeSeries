package net.mat0u5.lifeseries.utils.player;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.server;
//? if >= 1.21.9
import net.minecraft.server.players.NameAndId;

public class PermissionManager {
    public static boolean isAdmin(ServerPlayer player) {
        if (player == null) return false;
        GameProfile realProfile = ProfileManager.getRealProfile(player);
        UUID realUUID = OtherUtils.profileId(realProfile);
        if (LifeSeries.isClientPlayer(realUUID)) return true;
        if (server == null) return false;
        //? if < 1.21.9 {
        /*return server.getPlayerList().isOp(realProfile);
        *///?} else {
        return server.getPlayerList().isOp(new NameAndId(realProfile));
        //?}
    }

    public static boolean isAdmin(CommandSourceStack source) {
        if (source.getEntity() == null) return true;
        return isAdmin(source.getPlayer());
    }
}
