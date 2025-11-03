package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.MobSwap;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = MobCategory.class, priority = 1)
public class SpawnGroupMixin {

    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void getCapacity(CallbackInfoReturnable<Integer> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        MobCategory group = (MobCategory)(Object)this;
        if (!group.getName().equalsIgnoreCase("monster") && !group.getName().equalsIgnoreCase("creature")) {
            return;
        }
        if (currentSeason instanceof WildLife) {
            if (WildcardManager.isActiveWildcard(Wildcards.MOB_SWAP)) {
                MobSwap.getSpawnCapacity(group, cir);
            }
        }
    }


    @Inject(method = "isPersistent", at = @At("HEAD"), cancellable = true)
    private void isRare(CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        MobCategory group = (MobCategory)(Object)this;
        if (!group.getName().equalsIgnoreCase("creature")) return;

        if (currentSeason instanceof WildLife) {
            if (WildcardManager.isActiveWildcard(Wildcards.MOB_SWAP)) {
                MobSwap.isRare(group, cir);
            }
        }
    }
}