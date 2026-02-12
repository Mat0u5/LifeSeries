package net.mat0u5.lifeseries.seasons.boogeyman;

import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class Boogeyman {
    public UUID uuid;
    public String name;
    public boolean cured = false;
    public boolean failed = false;
    public boolean died = false;
    public Time timeBoogeyman = Time.zero();
    public int killsNeeded;

    public Boogeyman(ServerPlayer player) {
        uuid = player.getUUID();
        name = player.getScoreboardName();
        resetKills();
    }

    public ServerPlayer getPlayer() {
        return PlayerUtils.getPlayer(uuid);
    }

    public void tick() {
        timeBoogeyman.tick();
    }

    public void onKill() {
        killsNeeded--;
    }
    public void resetKills() {
        killsNeeded = Math.abs(seasonConfig.BOOGEYMAN_KILLS_NEEDED.get());
    }
    public boolean shouldCure() {
        return killsNeeded <= 0;
    }
}
