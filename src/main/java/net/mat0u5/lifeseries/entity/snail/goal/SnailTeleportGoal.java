package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
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

    private int lowSpeedTicks = 0;
    private static final double MIN_SPEED_THRESHOLD = 0.01;

    public SnailTeleportGoal(@NotNull Snail mob) {
        this.mob = mob;
        this.maxTicksSinceLastPositionChange = Snail.STATIONARY_TP_COOLDOWN;
        this.lastPosition = mob.blockPosition();
    }

    @Override
    public boolean canUse() {
        if (mob.level().isClientSide()) return false;
        if (mob.isPaused()) return false;

        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }

        if (!mob.serverData.shouldPathfind()) {
            return false;
        }

        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) return false;

        boolean dimensionsAreSame = mob.level().dimension().equals(boundEntity.level().dimension());
        if (!dimensionsAreSame) {
            return true;
        }

        float distFromPlayer = mob.distanceTo(boundEntity);
        if (distFromPlayer > Snail.MAX_DISTANCE) {
            return true;
        }

        BlockPos currentPos = mob.blockPosition();
        if (!currentPos.equals(this.lastPosition)) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = currentPos;
            this.lowSpeedTicks = 0;
        } else {
            this.ticksSinceLastPositionChange++;
        }

        double currentSpeed = mob.getDeltaMovement().horizontalDistance();
        if (currentSpeed < (MIN_SPEED_THRESHOLD*Snail.GLOBAL_SPEED_MULTIPLIER) && mob.getNavigation().getPath() != null) {
            lowSpeedTicks++;
        } else {
            lowSpeedTicks = 0;
        }

        boolean stuckByPosition = this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange;
        boolean stuckBySpeed = this.lowSpeedTicks > (this.maxTicksSinceLastPositionChange / 2);

        return stuckByPosition || stuckBySpeed;
    }

    @Override
    public void start() {
        teleportCooldown = 20;

        mob.pathfinding.fakeTeleportNearPlayer(Snail.TP_MIN_RANGE);

        this.ticksSinceLastPositionChange = 0;
        this.lowSpeedTicks = 0;
        this.lastPosition = mob.blockPosition();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}