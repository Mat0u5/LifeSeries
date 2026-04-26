package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowmanRenderer;
import net.mat0u5.lifeseries.entity.snail.SnailRenderer;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotRenderer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerRenderers(CallbackInfo ci) {
        EntityRenderers.register(MobRegistry.SNAIL, SnailRenderer::new);
        EntityRenderers.register(MobRegistry.TRIVIA_BOT, TriviaBotRenderer::new);
        EntityRenderers.register(MobRegistry.ANGRY_SNOWMAN, AngrySnowmanRenderer::new);
    }
}