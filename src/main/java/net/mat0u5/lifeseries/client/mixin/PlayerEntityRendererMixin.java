package net.mat0u5.lifeseries.client.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.client.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.client.gui.trivia.QuizScreen;
import net.mat0u5.lifeseries.client.gui.trivia.VotingScreen;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.minecraft.client.model.HumanoidModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.LifeSeries;
//? if <= 1.21.11 {
/*import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.injection.Redirect;
*///?}
//? if > 1.20 <= 1.21.11
//import net.minecraft.world.scores.DisplaySlot;
//? if <= 1.20.2 {
//import net.minecraft.world.scores.PlayerScoreEntry;
//?} else if <= 1.21.11 {
//import net.minecraft.world.scores.ReadOnlyScoreInfo;
//?}
//? if > 1.20.2 && <= 1.21
//import net.minecraft.network.chat.numbers.NumberFormat;
//? if <= 1.21 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?} else if <= 1.21.11 {
/*import net.mat0u5.matlib.client.utils.interfaces.IEntityRenderState;
import net.minecraft.world.entity.player.Player;
*///?}
//? if >= 1.21.2 && <= 1.21.6
//import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//? if >= 1.21.9 <= 1.21.11
//import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//?if <= 1.21.11 {
/*import net.mat0u5.matlib.utils.other.Time;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
import org.spongepowered.asm.mixin.injection.ModifyArg;
*///?}

import net.mat0u5.lifeseries.client.features.abilities.AbilitiesClient;
//? if <= 1.21 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
*///?} else {
import net.minecraft.world.entity.EntityAttachment;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}
//? if >= 1.21.2 && <= 1.21.6
//import net.minecraft.client.player.AbstractClientPlayer;
//? if >= 1.21.9
import net.minecraft.world.entity.Avatar;
//? if >= 26.1 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
//?}

//? if <= 1.21.6 {
/*import net.minecraft.client.renderer.entity.player.PlayerRenderer;
@Mixin(value = PlayerRenderer.class, priority = 1)
*///?} else {
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

@Mixin(value = AvatarRenderer.class, priority = 1)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public abstract class PlayerEntityRendererMixin {

    //? if <= 1.20.2 {
    /*@Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    public MutableComponent customBelowName(String string, AbstractClientPlayer abstractClientPlayer) {
        MutableComponent original = Component.literal(string);
        Scoreboard scoreboard = abstractClientPlayer.getScoreboard();
        //? if <= 1.20 {
        //Objective objective = scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_BELOW_NAME);
        //?} else {
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
        //?}
        if (objective != null) {
            Score score = scoreboard.getOrCreatePlayerScore(abstractClientPlayer.getScoreboardName(), objective);
            if (objective.getName().equalsIgnoreCase(LivesManager.SCOREBOARD_NAME)) {
                if (LifeSeries.isSeason(Seasons.LIMITED_LIFE)) {
                    Time timeLeft = Time.seconds(Math.max(0, score.getScore()));
                    return Component.literal(timeLeft.formatLong() + ";").setStyle(abstractClientPlayer.getDisplayName().getStyle());
                }
            }
        }
        return original;
    }
    @ModifyArg(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 0), index = 1)
    public Component removeLives(Component par2) {
        String belowName = par2.getString();
        if (belowName.contains(";") ) {
            return Component.literal(belowName.split(";")[0]).withStyle(par2.getStyle());
        }
        return par2;
    }
    *///?} else if <= 1.21 {
    /*//? if <= 1.20.3 {
    //@Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    //?} else {
    @Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    //?}
    public MutableComponent customBelowName(ReadOnlyScoreInfo readOnlyScoreInfo, NumberFormat numberFormat, AbstractClientPlayer abstractClientPlayer) {
        MutableComponent original = ReadOnlyScoreInfo.safeFormatValue(readOnlyScoreInfo, numberFormat);
        Scoreboard scoreboard = abstractClientPlayer.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
        if (objective != null && readOnlyScoreInfo != null) {
            if (objective.getName().equalsIgnoreCase(LivesManager.SCOREBOARD_NAME)) {
                if (LifeSeries.isSeason(Seasons.LIMITED_LIFE)) {
                    Time timeLeft = Time.seconds(Math.max(0, readOnlyScoreInfo.value()));
                    return Component.literal(timeLeft.formatLong() + ";").setStyle(abstractClientPlayer.getDisplayName().getStyle());
                }
            }
        }
        return original;
    }
    //? if <= 1.20.3 {
    //@ModifyArg(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 0), index = 1)
    //?} else {
    @ModifyArg(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 0), index = 1)
    //?}
    public Component removeLives(Component par2) {
        String belowName = par2.getString();
        if (belowName.contains(";") && !par2.getSiblings().isEmpty()) {
            return Component.literal(belowName.split(";")[0]).withStyle(par2.getSiblings().get(0).getStyle());
        }
        return par2;
    }
    *///?} else if <= 1.21.11 {
    /*//? if <= 1.21.6 {
    /^@Redirect(method = "renderNameTag(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;scoreText:Lnet/minecraft/network/chat/Component;"))
    public Component customBelowName(PlayerRenderState instance) {
    ^///?} else {
    @Redirect(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;scoreText:Lnet/minecraft/network/chat/Component;"))
    public Component customBelowName(AvatarRenderState instance) {
    //?}
        Component original = instance.scoreText;
        if (instance instanceof IEntityRenderState accessor && accessor.ml$getEntity() instanceof Player player) {
            //? if <= 1.21.6 {
            //Scoreboard scoreboard = player.getScoreboard();
            //?} else {
            Scoreboard scoreboard = player.level().getScoreboard();
            //?}
            Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
            if (objective != null) {
                ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(player, objective);
                if (scoreInfo != null && objective.getName().equalsIgnoreCase(LivesManager.SCOREBOARD_NAME)) {
                    if (LifeSeries.isSeason(Seasons.LIMITED_LIFE)) {
                        return Component.literal(Time.seconds(scoreInfo.value()).formatLong()).setStyle(player.getDisplayName().getStyle());
                    }
                }
            }
        }
        return original;
    }
    *///?}
    //^ In EntityRender from 26.1+

    //? if <= 1.20.3 {
    /*@WrapOperation(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 1))
    private void healthVision(PlayerRenderer instance, Entity entity, Component name, PoseStack poseStack, MultiBufferSource buffer, int light, Operation<Void> original) {
        Component health = AbilitiesClient.healthVisionText(entity, Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity));
        if (health != null) {
            original.call(instance, entity, health, poseStack, buffer, light);
            if (AbilitiesClient.healthOnlyEntity == entity) return;
            poseStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
        }
        original.call(instance, entity, name, poseStack, buffer, light);
    }
    *///?} else if <= 1.21 {
    /*@WrapOperation(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 1))
    private void healthVision(PlayerRenderer instance, Entity entity, Component name, PoseStack poseStack, MultiBufferSource buffer, int light, float partialTick, Operation<Void> original) {
        Component health = AbilitiesClient.healthVisionText(entity, Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity));
        if (health != null) {
            original.call(instance, entity, health, poseStack, buffer, light, partialTick);
            if (AbilitiesClient.healthOnlyEntity == entity) return;
            poseStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
        }
        original.call(instance, entity, name, poseStack, buffer, light, partialTick);
    }
    *///?} else if <= 1.21.6 {
    /*@Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    private void healthVision(AbstractClientPlayer entity, PlayerRenderState state, float partialTick, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void healthVision(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
    //?}
    //? if >= 1.21.2 {
        Component health = AbilitiesClient.healthVisionText(entity, state.distanceToCameraSq);
        if (health == null) return;
        if (state.nameTag != null) {
            state.scoreText = health;
            return;
        }
        state.nameTag = health;
        state.scoreText = null;
        state.nameTagAttachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTick));
    }
    //?}


    @ModifyReturnValue(method = "getArmPose*", at = @At("RETURN"))
    private static HumanoidModel.ArmPose noHands(HumanoidModel.ArmPose original) {
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE) && (RenderUtils.getScreen() instanceof EmptySleepScreen || (RenderUtils.getScreen() instanceof NewQuizScreen quizScreen && !quizScreen.shouldCloseOnEsc()) || (RenderUtils.getScreen() instanceof QuizScreen quizScreenOld && !quizScreenOld.shouldCloseOnEsc()) || RenderUtils.getScreen() instanceof VotingScreen)) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        return original;
    }
}