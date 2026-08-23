package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretKeeper;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

@Mixin(value = ExplosionDamageCalculator.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ExplosionDamageCalculatorMixin {

    @Inject(method = "shouldBlockExplode", at = @At("HEAD"), cancellable = true)
    private void protectSecretKeeperButtons(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power, CallbackInfoReturnable<Boolean> cir) {
        if (LifeSeries.isClientOrDisabled()) return;
        if (!(currentSeason instanceof SecretLife)) return;
        if (SecretKeeper.isProtectedPos(pos)) {
            cir.setReturnValue(false);
        }
    }
}
