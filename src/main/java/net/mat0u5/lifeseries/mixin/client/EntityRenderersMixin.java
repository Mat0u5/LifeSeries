package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.entity.angrysnowman.AngrySnowmanRenderer;
import net.mat0u5.lifeseries.client.entity.snail.SnailRenderer;
import net.mat0u5.lifeseries.client.entity.triviabot.TriviaBotRenderer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if <= 26.3 {
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?} else {
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}

@Mixin(EntityRenderers.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class EntityRenderersMixin {
    //? if <= 26.3 {
    /*@Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerRenderers(CallbackInfo ci) {
        EntityRenderers.register(MobRegistry.SNAIL, SnailRenderer::new);
        EntityRenderers.register(MobRegistry.TRIVIA_BOT, TriviaBotRenderer::new);
        EntityRenderers.register(MobRegistry.ANGRY_SNOWMAN, AngrySnowmanRenderer::new);
    }
    *///?} else {
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void registerRenderers(Registry<EntityRendererProvider<?>> registry, CallbackInfoReturnable<EntityRendererProvider<?>> cir) {
        EntityRenderers.register(registry, MobRegistry.SNAIL, SnailRenderer::new);
        EntityRenderers.register(registry, MobRegistry.TRIVIA_BOT, TriviaBotRenderer::new);
        EntityRenderers.register(registry, MobRegistry.ANGRY_SNOWMAN, AngrySnowmanRenderer::new);
    }
    //?}
}