package net.mat0u5.lifeseries.entity.snail.goal;


import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

public class SnailFlyGoal extends Goal {

    @NotNull
    private final Snail mob;

    public SnailFlyGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @NotNull
    protected Snail getMob() {
        return this.mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getSnailWorld().isClient()) return false;
        if (mob.isPaused()) return false;
        if (!mob.isSnailFlying() || mob.isSnailGliding()) {
            return false;
        }

        return getMob().pathfinding.canPathToPlayer(true);
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.isSnailFlying()) return false;
        return mob.serverData.shouldPathfind();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        mob.setSnailFlying(false);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }
}