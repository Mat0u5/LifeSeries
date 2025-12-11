package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = IceBlock.class, priority = 1)
public class IceBlockMixin {

    @Inject(method = "melt", at = @At("HEAD"), cancellable = true)
    private void cancelMelt(BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (!Main.modDisabled() && currentSeason.getSeason() == Seasons.NICE_LIFE && !NiceLife.LIGHT_MELTS_SNOW) {
            ci.cancel();
        }
    }
}
