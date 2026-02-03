package net.mat0u5.lifeseries.mixin.client;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.gui.trivia.VotingScreen;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.mat0u5.lifeseries.Main;

//? if > 1.20
import net.minecraft.world.scores.DisplaySlot;
//? if <= 1.20.2 {
/*import net.minecraft.world.scores.Score;
*///?} else {
import net.minecraft.world.scores.ReadOnlyScoreInfo;
//?}
//? if > 1.20.2 && <= 1.21
//import net.minecraft.network.chat.numbers.NumberFormat;
//? if <= 1.21 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?} else {
import net.mat0u5.lifeseries.utils.interfaces.IEntityRenderState;
import net.minecraft.world.entity.player.Player;
//?}
//? if >= 1.21.2 && <= 1.21.6
/*import net.minecraft.client.renderer.entity.state.PlayerRenderState;*/
//? if >= 1.21.9
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

//? if <= 1.21.6 {
/*import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(value = PlayerRenderer.class, priority = 1)
*///?} else {
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.ModifyArg;
@Mixin(value = AvatarRenderer.class, priority = 1)
//?}
public abstract class PlayerEntityRendererMixin {

    //? if <= 1.21 {
    /*@Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    public void replaceRendering(AbstractClientPlayer abstractClientPlayerEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        if (Main.modFullyDisabled()) return;
        if (MainClient.invisiblePlayers.containsKey(abstractClientPlayerEntity.getUUID())) {
            long time = MainClient.invisiblePlayers.get(abstractClientPlayerEntity.getUUID());
            if (time > System.currentTimeMillis() || time == -1) {
                ci.cancel();
                return;
            }
        }

        MorphComponent morphComponent = MorphManager.getOrCreateComponent(abstractClientPlayerEntity.getUUID());
        LivingEntity dummy = morphComponent.getDummy();
        if(morphComponent.isMorphed() && dummy != null) {
            ci.cancel();
        }
    }
    *///?}

    //? if > 1.21.6 {
    @ModifyArg(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V"),
            index = 3
    )
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    //?}

    //? if <= 1.20.2 {
    /*@Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    public MutableComponent customBelowName(String string, AbstractClientPlayer abstractClientPlayer) {
        MutableComponent original = Component.literal(string);
        Scoreboard scoreboard = abstractClientPlayer.getScoreboard();
        //? if <= 1.20 {
        /^Objective objective = scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_BELOW_NAME);
        ^///?} else {
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
        //?}
        if (objective != null) {
            Score score = scoreboard.getOrCreatePlayerScore(abstractClientPlayer.getScoreboardName(), objective);
            if (objective.getName().equalsIgnoreCase(LivesManager.SCOREBOARD_NAME)) {
                if (MainClient.clientCurrentSeason == Seasons.LIMITED_LIFE) {
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
    /^@Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    ^///?} else {
    @Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/ReadOnlyScoreInfo;safeFormatValue(Lnet/minecraft/world/scores/ReadOnlyScoreInfo;Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"))
    //?}
    public MutableComponent customBelowName(ReadOnlyScoreInfo readOnlyScoreInfo, NumberFormat numberFormat, AbstractClientPlayer abstractClientPlayer) {
        MutableComponent original = ReadOnlyScoreInfo.safeFormatValue(readOnlyScoreInfo, numberFormat);
        Scoreboard scoreboard = abstractClientPlayer.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
        if (objective != null && readOnlyScoreInfo != null) {
            if (objective.getName().equalsIgnoreCase(LivesManager.SCOREBOARD_NAME)) {
                if (MainClient.clientCurrentSeason == Seasons.LIMITED_LIFE) {
                    Time timeLeft = Time.seconds(Math.max(0, readOnlyScoreInfo.value()));
                    return Component.literal(timeLeft.formatLong() + ";").setStyle(abstractClientPlayer.getDisplayName().getStyle());
                }
            }
        }
        return original;
    }
    //? if <= 1.20.3 {
    /^@ModifyArg(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 0), index = 1)
    ^///?} else {
    @ModifyArg(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 0), index = 1)
    //?}
    public Component removeLives(Component par2) {
        String belowName = par2.getString();
        if (belowName.contains(";") && !par2.getSiblings().isEmpty()) {
            return Component.literal(belowName.split(";")[0]).withStyle(par2.getSiblings().get(0).getStyle());
        }
        return par2;
    }
    *///?} else {
    //? if <= 1.21.6 {
    /*@Redirect(method = "renderNameTag(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;scoreText:Lnet/minecraft/network/chat/Component;"))
    public Component customBelowName(PlayerRenderState instance) {
    *///?} else {
    @Redirect(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;scoreText:Lnet/minecraft/network/chat/Component;"))
    public Component customBelowName(AvatarRenderState instance) {
    //?}
        Component original = instance.scoreText;
        if (instance instanceof IEntityRenderState accessor && accessor.ls$getEntity() instanceof Player player) {
            //? if <= 1.21.6 {
            /*Scoreboard scoreboard = player.getScoreboard();
            *///?} else {
            Scoreboard scoreboard = player.level().getScoreboard();
            //?}
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
    //?}


    @ModifyReturnValue(method = "getArmPose*", at = @At("RETURN"))
    private static HumanoidModel.ArmPose noHands(HumanoidModel.ArmPose original) {
        if (!Main.modDisabled() && MainClient.clientCurrentSeason == Seasons.NICE_LIFE && (Minecraft.getInstance().screen instanceof EmptySleepScreen || Minecraft.getInstance().screen instanceof NewQuizScreen || Minecraft.getInstance().screen instanceof VotingScreen)) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        return original;
    }
}