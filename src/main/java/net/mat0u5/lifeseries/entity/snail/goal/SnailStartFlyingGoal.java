package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

public final class SnailStartFlyingGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int startFlyingCounter;
    private final int startFlyingDelay = 70;

    public SnailStartFlyingGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.level().isClientSide()) return false;
        if (mob.isPaused()) return false;
        if (!mob.serverData.shouldPathfind()) {
            return false;
        }

        if (mob.isSnailFlying()) {
            return false;
        }

        if (mob.getNavigation().getPath() == null) {
            startFlyingCounter = 0;
            return false;
        }

        // Use cached pathfinding results
        boolean canWalk = mob.pathfinding.canPathToPlayer(false);
        boolean canFly = mob.pathfinding.canPathToPlayer(true);

        if (canWalk) {
            startFlyingCounter = 0;
            return false;
        }
        else if (canFly) {
            startFlyingCounter++;
        }
        else {
            startFlyingCounter = 0;
            return false;
        }

        return startFlyingCounter >= startFlyingDelay;
    }

    @Override
    public void start() {
        mob.setSnailFlying(true);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }

    @Override
    public void stop() {
        startFlyingCounter = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}