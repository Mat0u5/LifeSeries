package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;

//? if <= 1.21.4 {
/*import org.spongepowered.asm.mixin.injection.ModifyArg;
*///?} else {
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(value = TamableAnimal.class, priority = 1)
public class TamableAnimalMixin {
    //? if <= 1.21 {
    /*@Inject(method = "isAlliedTo", at = @At("HEAD"), cancellable = true)
    *///?} else {
    @Inject(method = "considersEntityAsAlly", at = @At("HEAD"), cancellable = true)
    //?}
    private void petsAlwaysAttackSameTeam(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    //? if <= 1.21.4 {
    /*@ModifyArg(method = "tame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;setOwnerUUID(Ljava/util/UUID;)V"), index = 0)
    private UUID subin(UUID uuid) {
        UUID subUUID = SubInManager.getSubstitutedPlayerUUID(uuid);
        if (subUUID != null) {
            return subUUID;
        }
        return uuid;
    }
    *///?} else {
    @Inject(method = "setOwner", at = @At("HEAD"), cancellable = true)
    private void subin(LivingEntity livingEntity, CallbackInfo ci) {
        UUID subUUID = SubInManager.getSubstitutedPlayerUUID(livingEntity.getUUID());
        if (subUUID != null) {
            //? if <= 1.21.6 {
            /*((TamableAnimal)(Object) this).setOwnerReference(new EntityReference(subUUID));
            *///?} else {
            ((TamableAnimal)(Object) this).setOwnerReference(EntityReference.of(subUUID));
             //?}
            ci.cancel();
        }
    }
    //?}
}
