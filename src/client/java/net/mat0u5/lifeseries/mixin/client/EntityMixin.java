package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
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
        if (Main.isLogicalSide() || Main.modDisabled()) return;
        if (System.currentTimeMillis() - MainClient.snailAirTimestamp > 5000) return;
        if (MainClient.snailAir >= 300) return;

        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player && !player.hasEffect(MobEffects.WATER_BREATHING)) {
            int initialAir = cir.getReturnValue();
            if (MainClient.snailAir < initialAir) {
                cir.setReturnValue(MainClient.snailAir);
            }
        }
    }

    @Override
    public EntityDimensions ls$getEntityDimensions() {
        return dimensions;
    }
}
