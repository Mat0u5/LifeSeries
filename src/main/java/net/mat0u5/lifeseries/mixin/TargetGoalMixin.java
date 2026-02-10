package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TargetGoal.class, priority = 1)
public class TargetGoalMixin {
    @WrapOperation(method = "canContinueToUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getTeam()Lnet/minecraft/world/scores/PlayerTeam;"))
    private PlayerTeam petsNoTeam(Mob instance, Operation<PlayerTeam> original) {
        if (instance instanceof TamableAnimal) {
            return null;
        }
        return original.call(instance);
    }
}
