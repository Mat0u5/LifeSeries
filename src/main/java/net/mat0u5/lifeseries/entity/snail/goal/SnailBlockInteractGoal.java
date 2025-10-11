package net.mat0u5.lifeseries.entity.snail.goal;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class SnailBlockInteractGoal extends Goal {

    @NotNull
    private final Snail mob;

    public SnailBlockInteractGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getSnailWorld().isClient()) return false;
        if (mob.isPaused()) return false;
        if (WorldUtils.getEntityWorld(mob) == null) {
            return false;
        }

        BlockPos blockPos = mob.getBlockPos();

        BlockPos blockBelow = blockPos.down();
        return isTrapdoor(blockBelow) && isTrapdoorOpen(blockBelow);
    }

    @Override
    public void start() {
        BlockPos blockPos = mob.getBlockPos();
        //openTrapdoor(blockPos, true);

        BlockPos blockBelow = blockPos.down();
        openTrapdoor(blockBelow);
    }

    @Override
    public void tick() {
        start();
    }

    private boolean isTrapdoor(BlockPos blockPos) {
        BlockState blockState = getBlockState(blockPos);
        return blockState.isIn(BlockTags.TRAPDOORS);
    }

    private boolean isTrapdoorOpen(BlockPos blockPos) {
        return WorldUtils.getEntityWorld(mob).getBlockState(blockPos).get(TrapdoorBlock.OPEN);
    }

    private void openTrapdoor(BlockPos blockPos) {
        if (!isTrapdoor(blockPos)) return;
        World world = WorldUtils.getEntityWorld(mob);
        if (world == null) return;
        if (!isTrapdoorOpen(blockPos)) return;
        WorldUtils.getEntityWorld(mob).setBlockState(blockPos, WorldUtils.getEntityWorld(mob).getBlockState(blockPos).with(TrapdoorBlock.OPEN, false));
    }

    private BlockState getBlockState(BlockPos blockPos) {
        World world = WorldUtils.getEntityWorld(mob);
        if (world != null) {
            return world.getBlockState(blockPos);
        }
        return Blocks.AIR.getDefaultState();
    }
}
