package net.mat0u5.lifeseries.entity.snail.goal;


import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
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
        this.lastPosition = BlockPos.ZERO;
    }

    @Override
    public boolean canUse() {
        if (mob.getSnailWorld().isClientSide()) return false;
        if (mob.isPaused()) return false;
        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }
        if (!mob.serverData.shouldPathfind()) {
            return false;
        }
        if (!mob.blockPosition().equals(this.lastPosition)) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = mob.blockPosition();
        }

        this.ticksSinceLastPositionChange++;


        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) return false;
        float distFromPlayer = mob.distanceTo(boundEntity);
        boolean dimensionsAreSame = mob.ls$getEntityWorld().dimension().equals(boundEntity.ls$getEntityWorld().dimension());
        return !dimensionsAreSame || distFromPlayer > Snail.MAX_DISTANCE || this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange;
    }

    @Override
    public void start() {
        teleportCooldown = 20;
        mob.pathfinding.fakeTeleportNearPlayer(Snail.TP_MIN_RANGE);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}