package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.entity.snail.SnailModel;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {
    @Inject(method = "createRoots", at = @At("RETURN"), cancellable = true)
    private static void injectLayerDefinitions(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        Map<ModelLayerLocation, LayerDefinition> map = new HashMap<>(cir.getReturnValue());

        map.put(SnailModel.SNAIL, SnailModel.getTexturedModelData());
        map.put(TriviaBotModel.TRIVIA_BOT, TriviaBotModel.getTexturedModelData());

        cir.setReturnValue(map);
    }
}