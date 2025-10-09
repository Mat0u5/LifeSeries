package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

public final class SnailStartFlyingGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int startFlyingCounter;
    private final int startFlyingDelay = 70;
    private boolean canWalk = true;
    private boolean canFly = true;

    public SnailStartFlyingGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.isPaused()) return false;
        if (mob.serverData.getBoundPlayer() == null) {
            return false;
        }

        if (mob.isFlying()) {
            return false;
        }

        /*
        if (!mob.isTargetOnGround()) {
            return false;
        }*/

        if (mob.getNavigation().getCurrentPath() == null) {
            return false;
        }

        canWalk = mob.pathfinding.canPathToPlayer(false);
        canFly = mob.pathfinding.canPathToPlayer(true);

        if (canWalk) {
            startFlyingCounter = 0;
        }
        else if (canFly) {
            startFlyingCounter++;
        }

        return startFlyingCounter >= startFlyingDelay;
    }

    @Override
    public void start() {
        mob.setFlying(true);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }

    @Override
    public void stop() {
        startFlyingCounter = 0;
        canWalk = true;
    }
}