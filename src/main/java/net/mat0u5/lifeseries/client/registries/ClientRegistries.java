package net.mat0u5.lifeseries.client.registries;

import net.mat0u5.lifeseries.client.events.ClientEvents;
import net.mat0u5.lifeseries.client.features.EntityRenderModifier;
import net.mat0u5.matlib.client.events.ClientPlayerEvents;
import net.mat0u5.matlib.events.EventResult;
import net.mat0u5.matlib.client.events.ClientPackSourceEvents;

public class ClientRegistries {
	/**
	 * Client-side registries in:
	 * {@link net.mat0u5.lifeseries.mixin.client.EntityRenderersMixin}
	 * {@link net.mat0u5.lifeseries.mixin.client.ParticleResourcesMixin}
	 * {@link net.mat0u5.lifeseries.mixin.client.OptionsMixin}
	 */
	public static void register() {
		registerEvents();
	}

	public static void registerEvents() {
		EntityRenderModifier.registerRenderEntityEvents();
		ClientPackSourceEvents.SERVER_PACK_DOWNLOAD.register(url -> url.contains("github.com/Mat0u5/LifeSeries-Resources") ? EventResult.DENY : EventResult.PASS);
		ClientPlayerEvents.JOIN.register(packet -> ClientEvents.onClientJoin());
		ClientPlayerEvents.LEAVE.register(ClientEvents::onClientDisconnect);
	}
}