package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
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
        if (mob.level().isClientSide()) return false;
        if (mob.isPaused()) return false;

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
        return mob.level().getBlockState(blockPos).getValue(TrapDoorBlock.OPEN);
    }

    private void openTrapdoor(BlockPos blockPos) {
        if (!isTrapdoor(blockPos)) return;
        if (!isTrapdoorOpen(blockPos)) return;
        mob.level().setBlockAndUpdate(blockPos, mob.level().getBlockState(blockPos).setValue(TrapDoorBlock.OPEN, false));
    }

    private BlockState getBlockState(BlockPos blockPos) {
        Level level = mob.level();
        return level.getBlockState(blockPos);
    }
}
