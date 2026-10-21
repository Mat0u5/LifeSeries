package net.mat0u5.lifeseries.client.registries;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.entity.angrysnowman.AngrySnowmanRenderer;
import net.mat0u5.lifeseries.client.entity.snail.SnailModel;
import net.mat0u5.lifeseries.client.entity.snail.SnailRenderer;
import net.mat0u5.lifeseries.client.entity.triviabot.TriviaBotModel;
import net.mat0u5.lifeseries.client.entity.triviabot.TriviaBotRenderer;
import net.mat0u5.lifeseries.client.events.*;
import net.mat0u5.lifeseries.client.features.EntityRenderModifier;
import net.mat0u5.lifeseries.client.features.LifeSkinsClient;
import net.mat0u5.lifeseries.client.network.LifeSeriesNetworkHandlerClient;
import net.mat0u5.lifeseries.client.particle.TriviaSpiritParticle;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.client.utils.ClientSounds;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.client.events.*;
import net.mat0u5.matlib.client.registries.util.LayerDefinitionModel;
import net.mat0u5.matlib.client.registries.util.ProvidedParticle;
import net.mat0u5.matlib.client.registries.util.RenderableEntity;
import net.mat0u5.matlib.client.utils.TickRateSoundScaling;
import net.mat0u5.matlib.events.EventResult;
import net.mat0u5.matlib.events.common.CommonEntityEvents;
import net.mat0u5.matlib.events.server.ServerLifecycleEvents;

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
		ClientLocalPlayerEvents.JOIN.register(packet -> ClientEvents.onClientJoin());
		ClientLocalPlayerEvents.LEAVE.register(ClientEvents::onClientDisconnect);
		ClientRenderEvents.RENDER_GUI.register((guiGraphics, deltaTracker) -> ClientRenderer.render(guiGraphics));
		ClientRenderEvents.RENDER_GUI_POST.register((guiGraphics, deltaTracker) -> ClientRenderer.postRender(guiGraphics));
		ClientLifecycleEvents.CLIENT_STARTED.register(ClientEvents::onClientStart);
		ClientLifecycleEvents.CLIENT_STOPPING.register(ClientEvents::onClientStopping);
		ClientLocalPlayerEvents.START_TICK.register(ClientEvents::onClientTickStart);
		ClientLocalPlayerEvents.END_TICK.register(ClientEvents::onClientTickEnd);
		ServerLifecycleEvents.SERVER_STARTING.register(ClientEvents::onServerStarting);
		ServerLifecycleEvents.SERVER_STARTED.register(ClientEvents::onServerStart);
		ClientScreenEvents.OPEN_SCREEN.register(ClientEvents::onScreenOpen);
		ClientSoundEvents.PLAY_SOUND.register(ClientSounds::onSoundPlay);
		CommonEntityEvents.JUMP.register(ClientEvents::onClientJump);
		ClientPlayerEvents.GET_SKIN.register(LifeSkinsClient::onGetSkin);
		ClientEntityEvents.RENDER_ENTITY_NAMETAG.register(ClientUtils::getEntityName);
		ClientNetworkEvents.RECEIVE_HANDSHAKE.register(LifeSeriesNetworkHandlerClient::sendUpdatePackets);
		//? if >= 1.20.3 {
		ClientTickRateEvents.MODIFY_TARGET_MSPT.register(ClientRenderer::modifyTargetMspt);
		//?}
		TickRateSoundScaling.addEnableCondition(() -> (!LifeSeries.modFullyDisabled() && LifeSeries.isSeason(Seasons.WILD_LIFE)));
		TickRateSoundScaling.excludeSound("block.beacon.deactivate", "wildlife_time_slow_down", "wildlife_time_speed_up");
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