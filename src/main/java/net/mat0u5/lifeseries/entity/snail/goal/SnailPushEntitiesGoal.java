package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//? if <= 1.21.4 {
/*import net.minecraft.world.entity.projectile.ThrownPotion;
*///?} else if <= 1.21.9 {
/*import net.minecraft.world.entity.projectile.AbstractThrownPotion;
*///?} else {
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
//?}

//? if <= 1.21.9 {
/*import net.minecraft.world.entity.vehicle.MinecartTNT;
*///?} else {
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
//?}

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
        if (mob.level().isClientSide()) return false;
        Level level = mob.level();

        lastPushTime++;
        int pushDelay = 20;
        if (lastPushTime < pushDelay) {
            return false;
        }
        lastPushTime = 0;

        pushAway = new ArrayList<>();
        pushAway.addAll(level.getEntitiesOfClass(PrimedTnt.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));
        pushAway.addAll(level.getEntitiesOfClass(MinecartTNT.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));
        //? if <= 1.21.4 {
        /*pushAway.addAll(level.getEntitiesOfClass(ThrownPotion.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));
        *///?} else {
        pushAway.addAll(level.getEntitiesOfClass(AbstractThrownPotion.class, mob.getBoundingBox().inflate(8.0), entity -> mob.distanceToSqr(entity) < 64.0));

        //?}

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
        Vec3 direction = entity.position()
                .add(0.0, 0.5, 0.0)
                .subtract(mob.position())
                .normalize()
                .scale(0.4);

        entity.setDeltaMovement(entity.getDeltaMovement().add(direction));
    }
}