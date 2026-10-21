package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if > 1.20.3 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Shadow;
//?}

@Mixin(value = ClientPacketListener.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class ClientPacketListenerMixin {
    //? if > 1.20.3 {
    @Shadow
    private ClientLevel level;

    @WrapOperation(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V"))
    private void wrapSetBaseValue(AttributeInstance instance, double baseValue, Operation<Void> original, @Local ClientboundUpdateAttributesPacket packet) {
        if (!ClientUtils.handleUpdatedAttribute(level, instance, baseValue, packet)) {
            original.call(instance, baseValue);
        }
    }
    //?}
}
