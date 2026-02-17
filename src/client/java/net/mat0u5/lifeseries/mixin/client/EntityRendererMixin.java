package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >= 1.21.2 {
import net.mat0u5.lifeseries.utils.interfaces.IEntityRenderState;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}

//? if <= 1.21.6 {
/*import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.network.chat.Component;
*///?}

//? if >= 26.1 {
/*import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
*///?}


@Mixin(value = EntityRenderer.class, priority = 1)
//? if <= 1.21 {
/*public class EntityRendererMixin<T extends Entity> {
*///?} else {
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
//?}

    //? if <= 1.20.3 {
    /*@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), index = 1)
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    *///?} else if <= 1.21 {
    /*@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"), index = 1)
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    *///?} else if <= 1.21.6 {
    /*@ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
            index = 1
    )
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
     *///?}


    //? if >= 1.21.2 {
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public void injectEntity(T entity, S state, float tickProgress, CallbackInfo ci) {
        if (state instanceof IEntityRenderState accessor) {
            accessor.ls$update(entity, tickProgress);
        }
    }
    @Inject(method = "affectedByCulling", at = @At("HEAD"), cancellable = true)
    public void stopCulling(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof IMorph morph && morph.isFromMorph()) {
            cir.setReturnValue(false);
            return;
        }
        if (entity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getOrCreateComponent(player.getUUID());
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
    //?}

    // In PlayerEntityRendererMixin for <= 1.21.11
    //? if >= 26.1 {
    /*@ModifyArg(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V"),
            index = 3
    )
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    @Redirect(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;scoreText:Lnet/minecraft/network/chat/Component;"))
    public Component customBelowName(EntityRenderState instance) {
        Component original = instance.scoreText;
        if (instance instanceof IEntityRenderState accessor && accessor.ls$getEntity() instanceof Player player) {
            Scoreboard scoreboard = player.level().getScoreboard();
            Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
            if (objective != null) {
                ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(player, objective);
                if (scoreInfo != null && objective.getName().equalsIgnoreCase(LivesManager.SCOREBOARD_NAME)) {
                    if (MainClient.clientCurrentSeason == Seasons.LIMITED_LIFE) {
                        return Component.literal(Time.seconds(scoreInfo.value()).formatLong()).setStyle(player.getDisplayName().getStyle());
                    }
                }
            }
        }
        return original;
    }
    *///?}
}
