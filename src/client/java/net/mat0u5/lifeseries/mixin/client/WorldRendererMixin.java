package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = WorldRenderer.class, priority = 1)
public class WorldRendererMixin {
    //? if <= 1.21 {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    //?} else if <= 1.21.6 {
    /*@Redirect(method = "getEntitiesToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    *///?} else {
    /*@Redirect(method = "fillEntityRenderStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
     *///?}
    private Iterable<Entity> addMorphedEntities(ClientWorld instance) {
        List<Entity> entities = new ArrayList<>();
        instance.getEntities().forEach(entities::add);
        for (MorphComponent morphComponent : MorphManager.morphComponents.values()) {
            if (ls$shouldMorphRender(ClientUtils.getPlayer(morphComponent.playerUUID))) {
                Entity dummy = morphComponent.getDummy();
                if (morphComponent.isMorphed() && dummy != null) {
                    entities.add(dummy);
                }
            }
        }
        return entities;
    }

    @Unique
    private boolean ls$shouldMorphRender(PlayerEntity player) {
        if (player == null) return true;
        if (player.isSpectator()) return false;
        if (player.isInvisible()) return false;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (player instanceof ClientPlayerEntity && camera.getFocusedEntity() != player) {
            return false;
        }
        if (player == camera.getFocusedEntity() && !camera.isThirdPerson() &&
                !(camera.getFocusedEntity() instanceof LivingEntity livingEntityCamera && livingEntityCamera.isSleeping())) {
            return false;
        }
        return true;
    }
}
