package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.entity.snail.SnailModel;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin {
    @Mutable
    @Shadow
    private Map<ModelLayerLocation, LayerDefinition> roots;

    @Inject(method = "<init>", at = @At("TAIL"))
    //? if <= 1.21.2 {
    /*private void lifeseries$addModelLayers(CallbackInfo ci) {
    *///?} else {
    private void lifeseries$addModelLayers(Map<ModelLayerLocation, LayerDefinition> roots, CallbackInfo ci) {
    //?}
        this.roots = new HashMap<>(this.roots);
        this.roots.put(SnailModel.SNAIL, SnailModel.getTexturedModelData());
        this.roots.put(TriviaBotModel.TRIVIA_BOT, TriviaBotModel.getTexturedModelData());
    }
}