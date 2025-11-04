package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.MobSwap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = NaturalSpawner.class, priority = 1)
public class NaturalSpawnerMixin {
    @Inject(method = "isRightDistanceToPlayerAndSpawnPoint", at = @At("HEAD"), cancellable = true)
    private static void isAcceptableSpawnPosition(ServerLevel world, ChunkAccess chunk, BlockPos.MutableBlockPos pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife) {
            if (WildcardManager.isActiveWildcard(Wildcards.MOB_SWAP)) {
                MobSwap.isAcceptableSpawnPosition(world, chunk, pos, squaredDistance, cir);
            }
        }
    }
}
