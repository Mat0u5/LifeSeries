package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DamageSource.class, priority = 1)
public class DamageSourceMixin {
    @WrapOperation(method = "getLocalizedDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent modifyDeathMessage(String string, Object[] objects, Operation<MutableComponent> original) {
        DamageSource source = (DamageSource) (Object) this;
        if (!Main.modDisabled()) {
            String deathMessageType = source.type().msgId();
            if (deathMessageType.equals(DoubleLife.SOULMATE_DAMAGE_IDENTIFIER_NAME)) {
                if (objects.length <= 1) {
                    return ModifiableText.DOUBLELIFE_SOULMATE_DEATH_MSG_SOLO.get(objects).copy();
                }
                else {
                    return ModifiableText.DOUBLELIFE_SOULMATE_DEATH_MSG.get(objects).copy();
                }
            }
        }
        return original.call(string, objects);
    }
}
