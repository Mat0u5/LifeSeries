package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.LifeSeriesClient;
import net.mat0u5.lifeseries.utils.interfaces.IClientEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 2)
public class EntityMixin implements IClientEntity {
    @Shadow
    private EntityDimensions dimensions;

    @Inject(method = "getAirSupply", at = @At("RETURN"), cancellable = true)
    public void getAir(CallbackInfoReturnable<Integer> cir) {
        if (LifeSeries.isLogicalSide() || LifeSeries.modDisabled()) return;
        if (System.currentTimeMillis() - LifeSeriesClient.snailAirTimestamp > 5000) return;
        if (LifeSeriesClient.snailAir >= 300) return;

        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player && !player.hasEffect(MobEffects.WATER_BREATHING)) {
            int initialAir = cir.getReturnValue();
            if (LifeSeriesClient.snailAir < initialAir) {
                cir.setReturnValue(LifeSeriesClient.snailAir);
            }
        }
    }

    @Override
    public EntityDimensions ls$getEntityDimensions() {
        return dimensions;
    }
}
