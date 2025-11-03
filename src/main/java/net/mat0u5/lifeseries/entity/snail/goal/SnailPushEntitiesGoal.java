package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("resource")
public final class SnailPushEntitiesGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int lastPushTime = 20;
    private List<Entity> pushAway = new ArrayList<>();

    public SnailPushEntitiesGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.getSnailWorld().isClientSide()) return false;
        Level world = mob.ls$getEntityWorld();
        if (world == null) {
            return false;
        }

        lastPushTime++;
        int pushDelay = 20;
        if (lastPushTime < pushDelay) {
            return false;
        }
        lastPushTime = 0;

        pushAway = new ArrayList<>();
        pushAway.addAll(world.getEntitiesOfClass(PrimedTnt.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));
        pushAway.addAll(world.getEntitiesOfClass(MinecartTNT.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));
        pushAway.addAll(world.getEntitiesOfClass(ThrownPotion.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));

        return !pushAway.isEmpty();
    }

    @Override
    public void start() {
        if (pushAway != null) {
            mob.sounds.playThrowSound();
            for (Entity entity : pushAway) {
                pushAway(entity);
            }
        }
    }

    @Override
    public void stop() {
        pushAway = new ArrayList<>();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    private void pushAway(Entity entity) {
        Vec3 direction = entity.ls$getEntityPos()
                .add(0.0, 0.5, 0.0)
                .subtract(mob.ls$getEntityPos())
                .normalize()
                .scale(0.4);

        entity.setDeltaMovement(entity.getDeltaMovement().add(direction));
    }
}