package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = LevelRenderer.class, priority = 1)
public class WorldRendererMixin {
    //? if <= 1.21 {
    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    //?} else if <= 1.21.6 {
    /*@Redirect(method = "collectVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    *///?} else {
    /*@Redirect(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
     *///?}
    private Iterable<Entity> addMorphedEntities(ClientLevel instance) {
        List<Entity> entities = new ArrayList<>();
        instance.entitiesForRendering().forEach(entities::add);
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
    private boolean ls$shouldMorphRender(Player player) {
        if (player == null) return true;
        if (player.isSpectator()) return false;
        if (player.isInvisible()) return false;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        //? if <= 1.21.9 {
        if (player instanceof LocalPlayer && camera.getEntity() != player) {
            return false;
        }
        if (player == camera.getEntity() && !camera.isDetached() &&
                !(camera.getEntity() instanceof LivingEntity livingEntityCamera && livingEntityCamera.isSleeping())) {
            return false;
        }
        //?} else {
        /*if (player instanceof LocalPlayer && camera.entity() != player) {
            return false;
        }
        if (player == camera.entity() && !camera.isDetached() &&
                !(camera.entity() instanceof LivingEntity livingEntityCamera && livingEntityCamera.isSleeping())) {
            return false;
        }
        *///?}
        return true;
    }
}
