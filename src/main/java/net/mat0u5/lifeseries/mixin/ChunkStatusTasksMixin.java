package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if < 1.20.5 {
/*import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Blender.class, priority = 1)
*///?} else {
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.world.level.chunk.ImposterProtoChunk;

@Mixin(value = ChunkStatusTasks.class, priority = 1)
//?}
public abstract class ChunkStatusTasksMixin {
    //? if < 1.20.5 {
    /*@Inject(method = "generateBorderTicks", at = @At(value = "RETURN"))
    private static void snowNether(WorldGenRegion worldGenRegion, ChunkAccess chunkAccess, CallbackInfo ci) {
        ServerLevel level = worldGenRegion.getLevel();
    *///?} else if <= 1.20.5 {
    /*@ModifyVariable(method = "generateFull", at = @At("HEAD"), index = 5, argsOnly = true)
    private static ChunkAccess snowNether(ChunkAccess chunkAccess, WorldGenContext context) {
        ServerLevel level = context.level();
        if (chunkAccess instanceof ImposterProtoChunk) {
            return chunkAccess;
        }
    *///?} else {
    @ModifyVariable(method = "full", at = @At("HEAD"), index = 3, argsOnly = true)
    private static ChunkAccess snowNether(ChunkAccess chunkAccess, WorldGenContext context) {
        ServerLevel level = context.level();
        if (chunkAccess instanceof ImposterProtoChunk) {
            return chunkAccess;
        }
    //?}
        if (chunkAccess instanceof ProtoChunk) {
            if (NiceLife.SNOWY_NETHER && level.dimension() == Level.NETHER && !Main.modDisabled() && currentSeason instanceof NiceLife niceLife) {
                //? if <= 1.21 {
                int minY = chunkAccess.getMinBuildHeight();
                int maxY = chunkAccess.getMaxBuildHeight();
                //?} else {
                /*int minY = chunkAccess.getMinY();
                int maxY = chunkAccess.getMaxY();
                *///?}
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            BlockPos blockPos = new BlockPos(x, y, z);
                            BlockState oldState = chunkAccess.getBlockState(blockPos);
                            BlockState newState = niceLife.transformBlockState(oldState);
                            if (newState != null) {
                                //? if <= 1.21.4 {
                                chunkAccess.setBlockState(blockPos, newState, false);
                                //?} else {
                                /*chunkAccess.setBlockState(blockPos, newState, 1);
                                *///?}
                            }
                        }
                    }
                }
            }
        }
        //? if >= 1.20.5 {
        return chunkAccess;
        //?}
    }

}
