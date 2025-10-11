package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
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
        if (mob.isPaused()) return false;
        if (mob.serverData.dontAttackFor > 0) {
            return false;
        }

        if (mob.isGliding() || mob.isMining()) {
            return false;
        }

        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();
        if (boundPlayer == null) {
            return false;
        }

        if (mob.isSnailAttacking()) {
            return true;
        }

        double distanceToTarget = mob.squaredDistanceTo(boundPlayer);
        if (distanceToTarget > mob.serverData.getJumpRangeSquared()) {
            return false;
        }

        return mob.canSee(boundPlayer);
    }

    @Override
    public boolean shouldContinue() {
        if (attackCooldown2 > 0) {
            attackCooldown2--;
            return false;
        }

        if (attackCooldown <= 4) {
            return true;
        }

        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();
        if (boundPlayer == null) {
            return false;
        }

        if (mob.squaredDistanceTo(boundPlayer) > mob.serverData.getJumpRangeSquared()) {
            return false;
        }

        return mob.canSee(boundPlayer);
    }

    @Override
    public void start() {
        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();
        if (boundPlayer != null) {
            this.previousTargetPosition = WorldUtils.getEntityPos(boundPlayer);
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

        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (attackCooldown == 4) {
            mob.sounds.playAttackSound();
        }
        if (attackCooldown <= 0) {
            jumpAttackPlayer();
        }

        if (boundPlayer != null) {
            this.previousTargetPosition = WorldUtils.getEntityPos(boundPlayer);
            mob.lookAtEntity(boundPlayer, 15, 15);
        }
    }

    private void jumpAttackPlayer() {
        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();
        if (boundPlayer == null) {
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

        if (boundPlayer.getRandom().nextInt(3) == 0) {
            //Harder attack variant
            relativeTargetPos = new Vec3d(
                    boundPlayer.getX() - mob.getX(),
                    boundPlayer.getY() - mob.getY(),
                    boundPlayer.getZ() - mob.getZ()
            );
        }

        if (boundPlayer.getRandom().nextInt(6) == 0) {
            //EVEN harder attack variant
            Vec3d targetVelocity = WorldUtils.getEntityPos(boundPlayer).subtract(previousTargetPosition);
            relativeTargetPos = relativeTargetPos.add(targetVelocity.multiply(3));
        }

        Vec3d attackVector = mobVelocity;
        if (relativeTargetPos.lengthSquared() > 0.0001) {
            attackVector = relativeTargetPos.normalize().multiply(mob.serverData.isNerfed() ? 0.8 : 1);
        }
        if (mob.isFlying()) attackVector = attackVector.multiply(0.5);
        double addY = 0.5 + mob.squaredDistanceTo(boundPlayer) / mob.serverData.getJumpRangeSquared();
        mob.setVelocity(attackVector.x, attackVector.y + addY, attackVector.z);
    }
}