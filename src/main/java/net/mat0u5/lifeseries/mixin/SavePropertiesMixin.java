package net.mat0u5.lifeseries.mixin;

import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
//? if = 1.21.2 {
/*import net.mat0u5.lifeseries.Main;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

@Mixin(value = WorldData.class, priority = 1)
public interface SavePropertiesMixin {
    //? if = 1.21.2 {
    /*@Inject(method = "enabledFeatures", at = @At("HEAD"), cancellable = true)
    default void getEnabledFeatures(CallbackInfoReturnable<FeatureFlagSet> cir) {
        if (Main.modDisabled()) return;
        WorldData defaultProperties = (WorldData) (Object) this;
        if (!defaultProperties.getDataConfiguration().enabledFeatures().contains(FeatureFlags.WINTER_DROP)) {
            cir.setReturnValue(defaultProperties.getDataConfiguration().expandFeatures(FeatureFlagSet.of(FeatureFlags.WINTER_DROP)).enabledFeatures());
        }
    }
    *///?}
}
