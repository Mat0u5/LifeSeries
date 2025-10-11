package net.mat0u5.lifeseries.entity.snail.goal;


import net.mat0u5.lifeseries.entity.snail.Snail;
import org.jetbrains.annotations.NotNull;

public final class SnailMineTowardsPlayerGoal extends SnailFlyGoal {

    public SnailMineTowardsPlayerGoal(@NotNull Snail mob) {
        super(mob);
    }

    @Override
    public boolean canStart() {
        if (getMob().isPaused()) return false;

        if (getMob().serverData.getBoundPlayer() == null) {
            return false;
        }

        if (getMob().getNavigation().getCurrentPath() == null) {
            return false;
        }

        if (!getMob().getNavigation().getCurrentPath().isFinished()) {
            return false;
        }

        boolean canWalk = getMob().pathfinding.canPathToPlayer(false);
        boolean canFly = getMob().pathfinding.canPathToPlayer(true);

        return !canWalk && !canFly;
    }

    @Override
    public boolean shouldContinue() {
        if (getMob().serverData.getBoundPlayer() == null) {
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