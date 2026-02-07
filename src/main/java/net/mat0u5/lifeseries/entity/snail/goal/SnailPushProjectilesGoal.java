package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.5
import java.util.Optional;
//? if >= 1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
//?}

//? if <= 1.21.4
//import net.minecraft.world.entity.projectile.ThrownPotion;
//? if >= 1.21.5 && <= 1.21.9
//import net.minecraft.world.entity.projectile.AbstractThrownPotion;
//? if >= 1.21.11
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;

//? if <= 1.21.9 {
/*import net.minecraft.world.entity.projectile.ThrownTrident;
*///?} else {
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
//?}

@SuppressWarnings("resource")
public final class SnailPushProjectilesGoal extends Goal {

    @NotNull
    private final Snail mob;
    @NotNull
    private List<Projectile> projectiles  = new ArrayList<>();

    public SnailPushProjectilesGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.level().isClientSide()) return false;

        Level level = mob.level();
        this.projectiles = level.getEntitiesOfClass(
                Projectile.class,
                mob.getBoundingBox().inflate(5.0, 5.0, 5.0),
                projectile -> projectile.distanceToSqr(mob) < 16
        );

        return !this.projectiles.isEmpty();
    }

    @Override
    public void start() {
        boolean playSound = false;
        for (Projectile projectile : projectiles) {
            //? if <= 1.21.5 {
            /*CompoundTag empty = new CompoundTag();
            CompoundTag nbt = projectile.saveWithoutId(empty);
            *///?} else {
            TagValueOutput writeView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            projectile.saveWithoutId(writeView);
            CompoundTag nbt = writeView.buildResult();
            //?}
            //? if <= 1.21.4 {
            /*if (nbt.contains("inGround") && nbt.getBoolean("inGround")) {
                continue;
            }
            *///?} else {
            Optional<Boolean> bool = nbt.getBoolean("inGround");
            if (nbt.contains("inGround") && bool.isPresent()) {
                if (bool.get()) continue;
            }
            //?}

            //? if <= 1.21.4 {
            /*if (projectile instanceof ThrownPotion && Snail.ALLOW_POTION_EFFECTS) {
                continue;
            }
            *///?} else {
            if (projectile instanceof AbstractThrownPotion && Snail.ALLOW_POTION_EFFECTS) {
                continue;
            }
            //?}

            Entity sender = projectile.getOwner();
            if (sender instanceof LivingEntity target) {
                if (target instanceof Snail) {
                    continue;
                }

                double dx = target.getX() - projectile.getX();
                double dz = target.getZ() - projectile.getZ();
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

                double dy = target.getEyeY() - projectile.getY();

                float speed = 1.6F;

                double time = horizontalDistance / speed;
                double velocityY = dy / time + 0.5 * 0.05 * time;

                double velocityX = (dx / horizontalDistance) * speed;
                double velocityZ = (dz / horizontalDistance) * speed;

                projectile.shoot(velocityX, velocityY, velocityZ, 1.6F, 0.0F);
                if (!(projectile instanceof ThrownTrident)) projectile.setOwner(mob);

                playSound = true;
            }
        }
        if (playSound) {
            mob.sounds.playThrowSound();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void stop() {
        this.projectiles.clear();
    }
}
