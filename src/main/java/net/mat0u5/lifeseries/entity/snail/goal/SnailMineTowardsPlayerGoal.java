package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import org.jetbrains.annotations.NotNull;

public final class SnailMineTowardsPlayerGoal extends SnailFlyGoal {

    public SnailMineTowardsPlayerGoal(@NotNull Snail mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        if (getMob().getSnailWorld().isClientSide()) return false;
        if (getMob().isPaused()) return false;

        if (!getMob().serverData.shouldPathfind()) {
            return false;
        }

        if (getMob().getNavigation().getPath() == null) {
            return false;
        }

        if (!getMob().getNavigation().getPath().isDone()) {
            return false;
        }

        boolean canWalk = getMob().pathfinding.canPathToPlayer(false);
        boolean canFly = getMob().pathfinding.canPathToPlayer(true);

        return !canWalk && !canFly;
    }

    @Override
    public boolean canContinueToUse() {
        if (!getMob().serverData.shouldPathfind()) {
            return false;
        }

        boolean canWalk = getMob().pathfinding.canPathToPlayer(false);
        boolean canFly = getMob().pathfinding.canPathToPlayer(true);

        return !canWalk && !canFly;
    }

    @Override
    public void start() {
        getMob().setSnailMining(true);
        getMob().pathfinding.updateNavigation();
    }

    @Override
    public void stop() {
        getMob().setSnailMining(false);
        getMob().setSnailFlying(true);
        getMob().pathfinding.updateNavigation();
    }
}