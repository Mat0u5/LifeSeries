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
        if (mob.isPaused()) return false;
        if (!mob.isSnailFlying() || mob.isSnailGliding()) {
            return false;
        }

        LivingEntity boundPlayer = mob.serverData.getBoundPlayer();
        if (boundPlayer == null) {
            return false;
        }

        return getMob().pathfinding.canPathToPlayer(true);
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.isSnailFlying()) return false;
        return mob.serverData.getBoundPlayer() != null;
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