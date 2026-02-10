package net.mat0u5.lifeseries.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TamableAnimal.class, priority = 1)
public class TamableAnimalMixin {
    @Inject(method = "considersEntityAsAlly", at = @At("HEAD"), cancellable = true)
    private void petsAlwaysAttackSameTeam(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
