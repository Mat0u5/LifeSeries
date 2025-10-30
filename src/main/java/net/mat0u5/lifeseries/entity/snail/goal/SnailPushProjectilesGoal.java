package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
//? if >= 1.21.4
/*import java.util.Optional;*/
//? if >= 1.21.6 {
/*import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
*///?}

@SuppressWarnings("resource")
public final class SnailPushProjectilesGoal extends Goal {

    @NotNull
    private final Snail mob;
    @NotNull
    private List<ProjectileEntity> projectiles  = new ArrayList<>();

    public SnailPushProjectilesGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getSnailWorld().isClient()) return false;
        if (mob.ls$getEntityWorld() == null) {
            return false;
        }

        World world = mob.ls$getEntityWorld();
        this.projectiles = world.getEntitiesByClass(
                ProjectileEntity.class,
                mob.getBoundingBox().expand(5.0, 5.0, 5.0),
                projectile -> projectile.squaredDistanceTo(mob) < 16
        );

        return !this.projectiles.isEmpty();
    }

    @Override
    public void start() {
        boolean playSound = false;
        for (ProjectileEntity projectile : projectiles) {
            //? if <= 1.21.5 {
            NbtCompound empty = new NbtCompound();
            NbtCompound nbt = projectile.writeNbt(empty);
            //?} else {
            /*NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY);
            projectile.writeData(writeView);
            NbtCompound nbt = writeView.getNbt();
            *///?}
            //? if <= 1.21.4 {
            if (nbt.contains("inGround") && nbt.getBoolean("inGround")) {
                continue;
            }
            //?} else {
            /*Optional<Boolean> bool = nbt.getBoolean("inGround");
            if (nbt.contains("inGround") && bool.isPresent()) {
                if (bool.get()) continue;
            }
            *///?}

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

                projectile.setVelocity(velocityX, velocityY, velocityZ, 1.6F, 0.0F);
                if (!(projectile instanceof TridentEntity)) projectile.setOwner(mob);

                playSound = true;
            }
        }
        if (playSound) {
            mob.sounds.playThrowSound();
        }
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void stop() {
        this.projectiles.clear();
    }
}
