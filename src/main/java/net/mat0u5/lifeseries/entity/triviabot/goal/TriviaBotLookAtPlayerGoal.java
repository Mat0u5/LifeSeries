package net.mat0u5.lifeseries.entity.triviabot.goal;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TriviaBotLookAtPlayerGoal extends Goal {
    protected final TriviaBot mob;
    protected static final double RANGE_SQUARED = 400;
    private int lookTime;

    public TriviaBotLookAtPlayerGoal(TriviaBot mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (mob.getBotWorld().isClientSide()) return false;
        if (!mob.interactedWith()) return false;

        Vec3 targetPos = mob.serverData.getPlayerPos();
        if (targetPos == null) return false;

        return this.mob.distanceToSqr(targetPos) <= RANGE_SQUARED;
    }

    @Override
    public boolean canContinueToUse() {
        Vec3 targetPos = mob.serverData.getPlayerPos();
        if (targetPos == null) return false;

        if (this.mob.distanceToSqr(targetPos) > RANGE_SQUARED) return false;
        return this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
    }

    @Override
    public void tick() {
        Vec3 targetPos = mob.serverData.getPlayerPos();
        if (targetPos != null) {
            double d = this.mob.getEyeY();
            this.mob.getLookControl().setLookAt(targetPos.x(), d, targetPos.z());
            --this.lookTime;
        }
    }
}
