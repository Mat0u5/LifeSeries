package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
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
        if (mob.gliding) {
            return true;
        }

        if (mob.landing) {
            return false;
        }

        if (mob.getVelocity().y >= 0 || mob.isOnGround() || mob.flying) {
            return false;
        }

        if (mob.getDistanceToGroundBlock() <= 1.5) {
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
        mob.gliding = true;
    }

    @Override
    public boolean shouldContinue() {
        boolean canWalk = mob.canPathToPlayer(false);
        if (!canWalk) {
            mob.flying = true;
            return false;
        }

        return mob.getBoundPlayer() != null && mob.getDistanceToGroundBlock() >= 1;
    }

    @Override
    public void tick() {
        glideToPlayer();
    }

    @Override
    public void stop() {
        mob.gliding = false;
        mob.updateNavigation();
        mob.updateMoveControl();
        mob.playStopFlyAnimation();
    }

    private void glideToPlayer() {
        ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
        if (boundPlayer == null) {
            return;
        }

        Vec3d directionToTarget = WorldUtils.getEntityPos(boundPlayer).subtract(WorldUtils.getEntityPos(mob)).normalize();
        float speedMultiplier = mob.getMovementSpeed() / 2;
        mob.setVelocity(directionToTarget.x * speedMultiplier, -0.1, directionToTarget.z * speedMultiplier);
    }
}