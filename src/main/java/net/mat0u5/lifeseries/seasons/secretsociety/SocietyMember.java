package net.mat0u5.lifeseries.seasons.secretsociety;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SocietyMember {
    public final UUID uuid;
    public boolean initiated = false;
    public SocietyMember(ServerPlayer player) {
        this.uuid = player.getUUID();
    }
    public ServerPlayer getPlayer() {
        return PlayerUtils.getPlayer(uuid);
    }
}
