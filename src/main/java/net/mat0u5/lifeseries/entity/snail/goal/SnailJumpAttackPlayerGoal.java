package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class SnailJumpAttackPlayerGoal extends Goal {

    @NotNull
    private final Snail mob;
    @NotNull
    private Vec3 previousTargetPosition = Vec3.ZERO;
    private int attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
    private int attackCooldown2 = 0;

    public SnailJumpAttackPlayerGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.getSnailWorld().isClientSide()) return false;
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

        double distanceToTarget = mob.distanceToSqr(boundEntity);
        if (distanceToTarget > mob.serverData.getJumpRangeSquared()) {
            return false;
        }

        return mob.hasLineOfSight(boundEntity);
    }

    @Override
    public boolean canContinueToUse() {
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

        if (mob.distanceToSqr(boundEntity) > mob.serverData.getJumpRangeSquared()) {
            return false;
        }

        return mob.hasLineOfSight(boundEntity);
    }

    @Override
    public void start() {
        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity != null) {
            this.previousTargetPosition = boundEntity.position();
        }
        this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
        mob.setSnailAttacking(true);
    }

    @Override
    public void stop() {
        this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
        this.previousTargetPosition = Vec3.ZERO;
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
            this.previousTargetPosition = boundEntity.position();
            mob.lookAt(boundEntity, 15, 15);
        }
    }

    private void jumpAttackPlayer() {
        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) {
            return;
        }
        this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
        this.attackCooldown2 = Snail.JUMP_COOLDOWN_LONG;

        Vec3 mobVelocity = mob.getDeltaMovement();
        Vec3 relativeTargetPos = new Vec3(
                previousTargetPosition.x() - mob.getX(),
                previousTargetPosition.y() - mob.getY(),
                previousTargetPosition.z() - mob.getZ()
        );

        if (boundEntity.getRandom().nextInt(3) == 0) {
            //Harder attack variant
            relativeTargetPos = new Vec3(
                    boundEntity.getX() - mob.getX(),
                    boundEntity.getY() - mob.getY(),
                    boundEntity.getZ() - mob.getZ()
            );
        }

        if (boundEntity.getRandom().nextInt(6) == 0) {
            //EVEN harder attack variant
            Vec3 targetVelocity = boundEntity.position().subtract(previousTargetPosition);
            relativeTargetPos = relativeTargetPos.add(targetVelocity.scale(3));
        }

        Vec3 attackVector = mobVelocity;
        if (relativeTargetPos.lengthSqr() > 0.0001) {
            attackVector = relativeTargetPos.normalize().scale(mob.serverData.isNerfed() ? 0.8 : 1);
        }
        if (mob.isSnailFlying()) attackVector = attackVector.scale(0.5);
        double addY = 0.5 + mob.distanceToSqr(boundEntity) / mob.serverData.getJumpRangeSquared();
        mob.setDeltaMovement(attackVector.x, attackVector.y + addY, attackVector.z);
    }
}