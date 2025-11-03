package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.UUID;

public abstract class AdvancedDeath {
    protected UUID playerUUID;
    protected boolean started = false;
    protected boolean finished = false;
    protected long ticks = -100;
    public AdvancedDeath(ServerPlayer player) {
        this.playerUUID = player.getUUID();
    }

    protected abstract AdvancedDeaths getDeathType();
    protected abstract void tick(ServerPlayer player);
    protected abstract void begin(ServerPlayer player);
    protected abstract void end();
    protected abstract int maxTime();
    protected abstract DamageSource damageSource(ServerPlayer player);

    public ServerPlayer getPlayer() {
        return PlayerUtils.getPlayer(playerUUID);
    }

    public void onTick() {
        if (playerNotFound()) return;
        ServerPlayer player = getPlayer();

        ticks++;

        if (ticks >= maxTime()) {
            ranOutOfTime(player);
            onEnd();
            return;
        }

        if (ticks >= 0) {
            if (!started) {
                started = true;
                begin(player);
            }
            tick(player);
        }
    }

    public boolean playerNotFound() {
        ServerPlayer player = getPlayer();
        return player == null || !player.isAlive();
    }

    public void onEnd() {
        finished = true;
        end();
    }

    public void ranOutOfTime(ServerPlayer player) {
        PlayerUtils.killFromSource(player, damageSource(player));
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public String toString() {
        return getDeathType().toString();
    }
}
