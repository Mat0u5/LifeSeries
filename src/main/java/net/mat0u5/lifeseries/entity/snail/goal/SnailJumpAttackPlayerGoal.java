package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public final class SnailJumpAttackPlayerGoal extends Goal {

    @NotNull
    private final Snail mob;
    @NotNull
    private Vec3d previousTargetPosition = Vec3d.ZERO;
    private int attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
    private int attackCooldown2 = 0;

    public SnailJumpAttackPlayerGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getSnailWorld().isClient()) return false;
        if (!mob.serverData.shouldPathfind()) return false;
        if (mob.isPaused()) return false;
        if (mob.serverData.dontAttackFor > 0) {
            return false;
        }

        if (mob.isSnailGliding() || mob.isSnailMining()) {
            return false;
        }

        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) {
            return false;
        }

        if (mob.isSnailAttacking()) {
            return true;
        }

        double distanceToTarget = mob.squaredDistanceTo(boundEntity);
        if (distanceToTarget > mob.serverData.getJumpRangeSquared()) {
            return false;
        }

        return mob.canSee(boundEntity);
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.serverData.shouldPathfind()) return false;

        if (attackCooldown2 > 0) {
            attackCooldown2--;
            return false;
        }

        if (attackCooldown <= 4) {
            return true;
        }

        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) {
            return false;
        }

        if (mob.squaredDistanceTo(boundEntity) > mob.serverData.getJumpRangeSquared()) {
            return false;
        }

        return mob.canSee(boundEntity);
    }

    @Override
    public void start() {
        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity != null) {
            this.previousTargetPosition = boundEntity.ls$getEntityPos();
        }
        this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
        mob.setSnailAttacking(true);
    }

    @Override
    public void stop() {
        this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
        this.previousTargetPosition = Vec3d.ZERO;
        mob.setSnailAttacking(false);
    }

    @Override
    public void tick() {
        if (attackCooldown2 > 0) {
            attackCooldown2--;
            return;
        }

        Entity boundEntity = mob.serverData.getBoundEntity();
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (attackCooldown == 4) {
            mob.sounds.playAttackSound();
        }
        if (attackCooldown <= 0) {
            jumpAttackPlayer();
        }

        if (boundEntity != null) {
            this.previousTargetPosition = boundEntity.ls$getEntityPos();
            mob.lookAtEntity(boundEntity, 15, 15);
        }
    }

    private void jumpAttackPlayer() {
        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) {
            return;
        }
        this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
        this.attackCooldown2 = Snail.JUMP_COOLDOWN_LONG;

        Vec3d mobVelocity = mob.getVelocity();
        Vec3d relativeTargetPos = new Vec3d(
                previousTargetPosition.getX() - mob.getX(),
                previousTargetPosition.getY() - mob.getY(),
                previousTargetPosition.getZ() - mob.getZ()
        );

        if (boundEntity.getRandom().nextInt(3) == 0) {
            //Harder attack variant
            relativeTargetPos = new Vec3d(
                    boundEntity.getX() - mob.getX(),
                    boundEntity.getY() - mob.getY(),
                    boundEntity.getZ() - mob.getZ()
            );
        }

        if (boundEntity.getRandom().nextInt(6) == 0) {
            //EVEN harder attack variant
            Vec3d targetVelocity = boundEntity.ls$getEntityPos().subtract(previousTargetPosition);
            relativeTargetPos = relativeTargetPos.add(targetVelocity.multiply(3));
        }

        Vec3d attackVector = mobVelocity;
        if (relativeTargetPos.lengthSquared() > 0.0001) {
            attackVector = relativeTargetPos.normalize().multiply(mob.serverData.isNerfed() ? 0.8 : 1);
        }
        if (mob.isSnailFlying()) attackVector = attackVector.multiply(0.5);
        double addY = 0.5 + mob.squaredDistanceTo(boundEntity) / mob.serverData.getJumpRangeSquared();
        mob.setVelocity(attackVector.x, attackVector.y + addY, attackVector.z);
    }
}