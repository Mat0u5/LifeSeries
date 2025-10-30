package net.mat0u5.lifeseries.entity.snail.goal;


import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("resource")
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
        if (mob.getSnailWorld().isClient()) return false;
        if (mob.isPaused()) return false;
        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }
        if (!mob.serverData.shouldPathfind()) {
            return false;
        }
        if (!mob.getBlockPos().equals(this.lastPosition)) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = mob.getBlockPos();
        }

        this.ticksSinceLastPositionChange++;


        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) return false;
        float distFromPlayer = mob.distanceTo(boundEntity);
        boolean dimensionsAreSame = mob.ls$getEntityWorld().getRegistryKey().equals(boundEntity.ls$getEntityWorld().getRegistryKey());
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