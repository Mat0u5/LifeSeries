package net.mat0u5.lifeseries.mixin.client;

import io.netty.buffer.Unpooled;
import net.mat0u5.lifeseries.client.events.ClientEvents;
import net.mat0u5.lifeseries.client.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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

//? if <= 1.20 {
/*import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
*///?}

@Mixin(value = ClientPacketListener.class, priority = 1)
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

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void handleServerPlayReady(ClientboundLoginPacket packet, CallbackInfo ci) {
        ClientEvents.onClientJoin();
    }

    //? if <= 1.20 {
    /*@Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onHandlePayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket, CallbackInfo ci) {
        Identifier id = clientboundCustomPayloadPacket.getIdentifier();
        if (NetworkHandlerServer.PAYLOAD_READERS.containsKey(id)) {
            CustomPacketPayload payload = NetworkHandlerServer.PAYLOAD_READERS.get(id).apply(clientboundCustomPayloadPacket.getData());
            if (NetworkHandlerClient.onCustomPayload(payload)) {
                ci.cancel();
            }
        }
    }
    *///?} else {
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onHandlePayload(CustomPacketPayload payload, CallbackInfo ci) {
        if (NetworkHandlerClient.onCustomPayload(payload)) {
            ci.cancel();
        }
    }
    //?}
}
