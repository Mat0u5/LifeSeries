package net.mat0u5.lifeseries.entity.snail.goal;

import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningNavigation extends BirdNavigation {

    public int cooldown = 40;

    public MiningNavigation(MobEntity mob, World world) {
        super(mob, world);
    }

    @Override
    protected boolean isAtValidPosition() {
        if (cooldown > 0) {
            cooldown--;
        }
        if (cooldown == 0 && this.currentPath != null && !this.currentPath.isFinished()) {
            cooldown = 20;
            BlockPos entityPos = this.entity.getBlockPos();
            BlockPos targetPos = this.currentPath.getCurrentNode().getBlockPos();

            breakBlocksForPath(entityPos, targetPos);
        }

        return super.isAtValidPosition();
    }

    private void breakBlocksForPath(BlockPos start, BlockPos end) {
        int dx = Integer.signum(end.getX() - start.getX());
        int dy = Integer.signum(end.getY() - start.getY());
        int dz = Integer.signum(end.getZ() - start.getZ());

        breakBlockIfNecessary(start.add(dx, dy, dz));

        if (dx != 0 && dz != 0) {
            breakBlockIfNecessary(start.add(dx, 0, 0));
            breakBlockIfNecessary(start.add(0, 0, dz));
        }

        if (dy != 0) {
            breakBlockIfNecessary(start.add(0, dy, 0));

            if (dx != 0 || dz != 0) {
                breakBlockIfNecessary(start.add(dx, dy, dz));
            }
        }
    }

    private void breakBlockIfNecessary(BlockPos pos) {
        if (!this.world.getBlockState(pos).isAir()) {
            this.world.breakBlock(pos, true, this.entity);
        }
    }
}