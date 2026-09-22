package net.mat0u5.lifeseries.client.features;

import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.mat0u5.matlib.client.events.ClientEntityRenderEvents;
import net.mat0u5.matlib.events.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EntityRenderModifier {
	public static void registerRenderEntityEvents() {
		ClientEntityRenderEvents.SHOULD_RENDER.register((entity, frustum, camX, camY, camZ) -> {
			if (entity instanceof Player playerEntity) {
				if (LifeSeriesClient.invisiblePlayers.containsKey(playerEntity.getUUID())) {
					long time = LifeSeriesClient.invisiblePlayers.get(playerEntity.getUUID());
					if (time > System.currentTimeMillis() || time == -1) {
						return EventResult.DENY;
					}
				}

				MorphComponent morphComponent = MorphManager.getOrCreateComponent(playerEntity);
				LivingEntity dummy = morphComponent.getDummy();
				if(morphComponent.isMorphed() && dummy != null) {
					return EventResult.DENY;
				}
			}

			if (entity instanceof IMorph morph && morph.isFromMorph()) {
				return EventResult.ALLOW;
			}
			return EventResult.PASS;
		});
		ClientEntityRenderEvents.ENTITIES_FOR_RENDERING.register(entitiesForRendering -> {
			for (MorphComponent morphComponent : MorphManager.morphComponents.values()) {
				if (shouldMorphRender(ClientUtils.getPlayer(morphComponent.playerUUID))) {
					Entity dummy = morphComponent.getDummy();
					if (morphComponent.isMorphed() && dummy != null) {
						entitiesForRendering.add(dummy);
					}
				}
			}

		});
	}

	private static boolean shouldMorphRender(Player player) {
		if (player == null) return true;
		if (player.isSpectator()) return false;
		if (player.isInvisible()) return false;
		//~ if >= 26.2 '.getMainCamera()' -> '.mainCamera()' {
		Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
		//~}
		//~ if > 1.21.9 '.getEntity()' -> '.entity()' {
		if (player instanceof LocalPlayer && camera.entity() != player) {
			return false;
		}
		if (player == camera.entity() && !camera.isDetached() &&
				!(camera.entity() instanceof LivingEntity livingEntityCamera && livingEntityCamera.isSleeping())) {
			return false;
		}
		//~}
		return true;
	}
}
