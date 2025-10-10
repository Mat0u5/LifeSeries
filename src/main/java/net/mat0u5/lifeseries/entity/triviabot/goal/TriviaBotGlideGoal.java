package net.mat0u5.lifeseries.entity.triviabot.goal;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

public final class TriviaBotGlideGoal extends Goal {

    @NotNull
    private final TriviaBot mob;
    private final int ticksToWait;
    private int ticksWaited;

    public TriviaBotGlideGoal(@NotNull TriviaBot mob) {
        this.mob = mob;
        this.ticksToWait = 2;
    }

    @Override
    public boolean canStart() {
        if (mob.isGliding()) {
            return true;
        }

        if (mob.getVelocity().y >= 0 || mob.isOnGround()) {
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
        mob.setGliding(true);
    }

    @Override
    public boolean shouldContinue() {
        return mob.pathfinding.getDistanceToGroundBlock() >= 1;
    }

    @Override
    public void tick() {
        mob.setVelocity(0, -0.1, 0);
    }

    @Override
    public void stop() {
        mob.setGliding(false);
        mob.pathfinding.updateNavigation();
    }
}