package net.mat0u5.lifeseries.client.registries;

import net.mat0u5.lifeseries.client.entity.angrysnowman.AngrySnowmanRenderer;
import net.mat0u5.lifeseries.client.entity.snail.SnailModel;
import net.mat0u5.lifeseries.client.entity.snail.SnailRenderer;
import net.mat0u5.lifeseries.client.entity.triviabot.TriviaBotModel;
import net.mat0u5.lifeseries.client.entity.triviabot.TriviaBotRenderer;
import net.mat0u5.lifeseries.client.events.ClientEvents;
import net.mat0u5.lifeseries.client.events.ClientKeybinds;
import net.mat0u5.lifeseries.client.features.EntityRenderModifier;
import net.mat0u5.lifeseries.client.particle.TriviaSpiritParticle;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import net.mat0u5.matlib.client.events.*;
import net.mat0u5.matlib.client.registries.util.LayerDefinitionModel;
import net.mat0u5.matlib.client.registries.util.ProvidedParticle;
import net.mat0u5.matlib.client.registries.util.RenderableEntity;
import net.mat0u5.matlib.events.EventResult;

import java.util.List;

public class ClientRegistries {
	public static void register() {
		registerEvents();
		ClientKeybinds.register();
		registerRenderers();
	}

	public static void registerEvents() {
		EntityRenderModifier.registerRenderEntityEvents();
		ClientPackSourceEvents.SERVER_PACK_DOWNLOAD.register(url -> url.contains("github.com/Mat0u5/LifeSeries-Resources") ? EventResult.DENY : EventResult.PASS);
		ClientPlayerEvents.JOIN.register(packet -> ClientEvents.onClientJoin());
		ClientPlayerEvents.LEAVE.register(ClientEvents::onClientDisconnect);
		ClientRenderEvents.RENDER_GUI.register((guiGraphics, deltaTracker) -> ClientRenderer.render(guiGraphics));
		ClientRenderEvents.RENDER_GUI_POST.register((guiGraphics, deltaTracker) -> ClientRenderer.postRender(guiGraphics));
		ClientLifecycleEvents.CLIENT_STARTED.register(ClientEvents::onClientStart);
		ClientLifecycleEvents.CLIENT_STOPPING.register(ClientEvents::onClientStopping);
		ClientPlayerEvents.START_TICK.register(ClientEvents::onClientTickStart);
		ClientPlayerEvents.END_TICK.register(ClientEvents::onClientTickEnd);
	}
	public static void registerRenderers() {
		ClientRegistryEvents.ENTITY_RENDERER.register(() -> List.of(
				new RenderableEntity<>(MobRegistry.SNAIL, SnailRenderer::new),
				new RenderableEntity<>(MobRegistry.TRIVIA_BOT, TriviaBotRenderer::new),
				new RenderableEntity<>(MobRegistry.ANGRY_SNOWMAN, AngrySnowmanRenderer::new)
		));
		ClientRegistryEvents.PARTICLE_PROVIDER.register(() -> List.of(
				new ProvidedParticle<>(ParticleRegistry.TRIVIA_SPIRIT, new TriviaSpiritParticle.Provider())
		));
		ClientRegistryEvents.ENTITY_MODEL_LAYER_DEFINITION.register(() -> List.of(
				new LayerDefinitionModel(SnailModel.SNAIL, SnailModel.getTexturedModelData()),
				new LayerDefinitionModel(TriviaBotModel.TRIVIA_BOT, TriviaBotModel.getTexturedModelData())
		));
	}
}