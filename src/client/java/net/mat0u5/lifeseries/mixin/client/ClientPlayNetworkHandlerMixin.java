package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientPacketListener.class, priority = 1)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientLevel level;
    @WrapOperation(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V"))
    private void wrapSetBaseValue(AttributeInstance instance, double baseValue, Operation<Void> original, @Local ClientboundUpdateAttributesPacket packet) {
        if (!ClientUtils.handleUpdatedAttribute(level, instance, baseValue, packet)) {
            original.call(instance, baseValue);
        }
    }
}
