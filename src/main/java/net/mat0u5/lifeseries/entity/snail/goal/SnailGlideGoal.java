package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public final class SnailGlideGoal extends Goal {

    @NotNull
    private final Snail mob;
    private final int ticksToWait;
    private int ticksWaited;

    public SnailGlideGoal(@NotNull Snail mob) {
        this.mob = mob;
        this.ticksToWait = 2;
    }

    @Override
    public boolean canStart() {
        if (mob.getSnailWorld().isClient()) return false;
        if (mob.isSnailGliding()) {
            return true;
        }

        if (mob.isSnailLanding()) {
            return false;
        }

        if (mob.getVelocity().y >= 0 || mob.isOnGround() || mob.isSnailFlying()) {
            return false;
        }

        if (mob.pathfinding.getDistanceToGroundBlock() <= 1.5) {
            return false;
        }

        if (ticksWaited < ticksToWait) {
            ticksWaited++;
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        ticksWaited = 0;
        mob.setSnailGliding(true);
    }

    @Override
    public boolean shouldContinue() {
        boolean canWalk = mob.pathfinding.canPathToPlayer(false);
        if (!canWalk) {
            mob.setSnailFlying(true);
            return false;
        }

        return mob.serverData.shouldPathfind() && mob.pathfinding.getDistanceToGroundBlock() >= 1;
    }

    @Override
    public void tick() {
        glideToPlayer();
    }

    @Override
    public void stop() {
        mob.setSnailGliding(false);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }

    private void glideToPlayer() {
        Vec3d targetPos = mob.serverData.getPlayerPos();
        if (targetPos == null) {
            return;
        }

        Vec3d directionToTarget = targetPos.subtract(WorldUtils.getEntityPos(mob)).normalize();
        float speedMultiplier = mob.getMovementSpeed() / 2;
        mob.setVelocity(directionToTarget.x * speedMultiplier, -0.1, directionToTarget.z * speedMultiplier);
    }
}