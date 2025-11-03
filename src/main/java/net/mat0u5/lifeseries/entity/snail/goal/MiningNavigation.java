package net.mat0u5.lifeseries.entity.snail.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;

public class MiningNavigation extends FlyingPathNavigation {

    public int cooldown = 40;

    public MiningNavigation(Mob mob, Level world) {
        super(mob, world);
    }

    @Override
    protected boolean canUpdatePath() {
        if (cooldown > 0) {
            cooldown--;
        }
        if (cooldown == 0 && this.path != null && !this.path.isDone()) {
            cooldown = 20;
            BlockPos entityPos = this.mob.blockPosition();
            BlockPos targetPos = this.path.getNextNode().asBlockPos();

            breakBlocksForPath(entityPos, targetPos);
        }

        return super.canUpdatePath();
    }

    private void breakBlocksForPath(BlockPos start, BlockPos end) {
        int dx = Integer.signum(end.getX() - start.getX());
        int dy = Integer.signum(end.getY() - start.getY());
        int dz = Integer.signum(end.getZ() - start.getZ());

        breakBlockIfNecessary(start.offset(dx, dy, dz));

        if (dx != 0 && dz != 0) {
            breakBlockIfNecessary(start.offset(dx, 0, 0));
            breakBlockIfNecessary(start.offset(0, 0, dz));
        }

        if (dy != 0) {
            breakBlockIfNecessary(start.offset(0, dy, 0));

            if (dx != 0 || dz != 0) {
                breakBlockIfNecessary(start.offset(dx, dy, dz));
            }
        }
    }

    private void breakBlockIfNecessary(BlockPos pos) {
        if (!this.level.getBlockState(pos).isAir()) {
            this.level.destroyBlock(pos, true, this.mob);
        }
    }
}