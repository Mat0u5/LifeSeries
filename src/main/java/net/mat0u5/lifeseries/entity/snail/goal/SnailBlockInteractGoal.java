package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("resource")
public final class SnailBlockInteractGoal extends Goal {

    @NotNull
    private final Snail mob;

    public SnailBlockInteractGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (mob.getSnailWorld().isClientSide()) return false;
        if (mob.isPaused()) return false;
        if (mob.ls$getEntityWorld() == null) {
            return false;
        }

        BlockPos blockPos = mob.blockPosition();

        BlockPos blockBelow = blockPos.below();
        return isTrapdoor(blockBelow) && isTrapdoorOpen(blockBelow);
    }

    @Override
    public void start() {
        BlockPos blockPos = mob.blockPosition();
        //openTrapdoor(blockPos, true);

        BlockPos blockBelow = blockPos.below();
        openTrapdoor(blockBelow);
    }

    @Override
    public void tick() {
        start();
    }

    private boolean isTrapdoor(BlockPos blockPos) {
        BlockState blockState = getBlockState(blockPos);
        return blockState.is(BlockTags.TRAPDOORS);
    }

    private boolean isTrapdoorOpen(BlockPos blockPos) {
        return mob.ls$getEntityWorld().getBlockState(blockPos).getValue(TrapDoorBlock.OPEN);
    }

    private void openTrapdoor(BlockPos blockPos) {
        if (!isTrapdoor(blockPos)) return;
        Level world = mob.ls$getEntityWorld();
        if (world == null) return;
        if (!isTrapdoorOpen(blockPos)) return;
        mob.ls$getEntityWorld().setBlockAndUpdate(blockPos, mob.ls$getEntityWorld().getBlockState(blockPos).setValue(TrapDoorBlock.OPEN, false));
    }

    private BlockState getBlockState(BlockPos blockPos) {
        Level world = mob.ls$getEntityWorld();
        if (world != null) {
            return world.getBlockState(blockPos);
        }
        return Blocks.AIR.defaultBlockState();
    }
}
