package net.mat0u5.lifeseries.entity.snail.goal;


import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class SnailTeleportGoal extends Goal {


    @NotNull
    private final Snail mob;
    private int ticksSinceLastPositionChange;
    private final int maxTicksSinceLastPositionChange;
    private int teleportCooldown = 0;
    @NotNull
    private BlockPos lastPosition;

    public SnailTeleportGoal(@NotNull Snail mob) {
        this.mob = mob;
        this.maxTicksSinceLastPositionChange = Snail.STATIONARY_TP_COOLDOWN;
        this.lastPosition = BlockPos.ORIGIN;
    }

    @Override
    public boolean canStart() {
        if (mob.isPaused()) return false;
        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }
        if (mob.serverData.getBoundPlayer() == null) {
            return false;
        }
        if (!mob.getBlockPos().equals(this.lastPosition)) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = mob.getBlockPos();
        }

        this.ticksSinceLastPositionChange++;


        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();
        float distFromPlayer = mob.distanceTo(boundPlayer);
        boolean dimensionsAreSame = WorldUtils.getEntityWorld(mob).getRegistryKey().equals(WorldUtils.getEntityWorld(boundPlayer).getRegistryKey());
        return !dimensionsAreSame || distFromPlayer > Snail.MAX_DISTANCE || this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange;
    }

    @Override
    public void start() {
        teleportCooldown = 20;
        mob.pathfinding.fakeTeleportNearPlayer(Snail.TP_MIN_RANGE);
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}