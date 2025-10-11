package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SnailPushEntitiesGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int lastPushTime = 20;
    private List<Entity> pushAway = new ArrayList<>();

    public SnailPushEntitiesGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getSnailWorld().isClient()) return false;
        World world = WorldUtils.getEntityWorld(mob);
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
        pushAway.addAll(world.getEntitiesByClass(TntEntity.class, mob.getBoundingBox().expand(8.0), entity -> mob.squaredDistanceTo(entity) < 64.0));
        pushAway.addAll(world.getEntitiesByClass(TntMinecartEntity.class, mob.getBoundingBox().expand(8.0), entity -> mob.squaredDistanceTo(entity) < 64.0));
        pushAway.addAll(world.getEntitiesByClass(PotionEntity.class, mob.getBoundingBox().expand(8.0), entity -> mob.squaredDistanceTo(entity) < 64.0));

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
    public boolean shouldContinue() {
        return false;
    }

    private void pushAway(Entity entity) {
        Vec3d direction = WorldUtils.getEntityPos(entity)
                .add(0.0, 0.5, 0.0)
                .subtract(WorldUtils.getEntityPos(mob))
                .normalize()
                .multiply(0.4);

        entity.setVelocity(entity.getVelocity().add(direction));
    }
}