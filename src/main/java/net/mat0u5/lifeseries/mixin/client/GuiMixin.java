package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.client.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if > 1.20
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.20.3 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
*///?}
//? if >= 1.21.2 && <= 1.21.5 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
import java.util.function.Function;
*///?}
//? if >= 1.21.6
import com.mojang.blaze3d.pipeline.RenderPipeline;
//? if >= 1.21
import net.minecraft.client.DeltaTracker;

import net.minecraft.resources.Identifier;

//? if <= 26.1 {
/*import net.minecraft.client.gui.Gui;
@Mixin(value = Gui.class)
*///?} else {
import net.minecraft.client.gui.Hud;
@Mixin(value = Hud.class, priority = 1)
//?}
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class GuiMixin {
    //? if <= 1.20.5 {
    /*@Inject(method = "render", at = @At(value = "HEAD"))
    public void render(GuiGraphicsExtractor guiGraphics, float f, CallbackInfo ci) {
    *///?} else if <= 1.21.11 {
    /*@Inject(method = "render", at = @At(value = "HEAD"))
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "extractRenderState", at = @At(value = "HEAD"))
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    //?}
        //? if !(neoforge && <= 1.20.3) && !(forge && <= 1.20) {
        ClientRenderer.render(guiGraphics);
        //?}
    }

//? if forge {
    /*//? if <= 1.20.5 {
    /^@Inject(method = "render", at = @At(value = "TAIL"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, float f, CallbackInfo ci) {
    ^///?} else if <= 1.21.6 {
    /^@Inject(method = "render", at = @At(value = "TAIL"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    ^///?} else if <= 1.21.11 {
    /^@Inject(method = "render", at = @At(value = "RETURN"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    ^///?} else {
    @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    //?}
*///?} else {
    //? if <= 1.20.5 {
    /*@Inject(method = "render", at = @At(value = "TAIL"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, float f, CallbackInfo ci) {
    *///?} else if <= 1.21.11 {
    /*@Inject(method = "render", at = @At(value = "TAIL"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "extractRenderState", at = @At(value = "TAIL"))
    public void renderPost(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    //?}
//?}
        //? if !(neoforge && <= 1.20.3) && !(forge && <= 1.20) {
        ClientRenderer.postRender(guiGraphics);
        //?}
    }

    //? if <= 1.20 {
    /*@Inject(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lnet/minecraft/resources/Identifier;IIIIII)V"), cancellable = true)
    private void customHearts(GuiGraphicsExtractor guiGraphics, Gui.HeartType heartType, int x, int y, int k, boolean isBlinking, boolean isHalf, CallbackInfo ci) {
    *///?} else if <= 1.21 {
    /*@Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lnet/minecraft/resources/Identifier;IIII)V"))
    private void customHearts(GuiGraphicsExtractor instance, Identifier identifier, int x, int y, int u, int v) {
    *///?} else if <= 1.21.5 {
    /*@Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void customHearts(GuiGraphicsExtractor instance, Function<Identifier, RenderType> renderLayers, Identifier identifier, int x, int y, int u, int v) {
    *///?} else if <= 1.21.11 {
    /*@Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void customHearts(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    *///?} else if <= 26.2 {
    @Redirect(method = "extractHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void customHearts(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    //?} else {
    /*@Redirect(method = "extractHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void customHearts(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    *///?}

        //? if <= 1.20 {
        /*String texturePath = "hud/heart/";
        boolean isHardcore = k > 0;
        if (isHardcore) texturePath += "hardcore_";
        if (isHalf) texturePath += "half";
        else texturePath += "full";
        if (isBlinking) texturePath += "_blinking";
        if (heartType != Gui.HeartType.NORMAL) {
            return;
        }
        *///?} else {
        String texturePath = identifier.getPath();
        //?}
        String playerTeamColor = ClientUtils.getPlayerTeamColor();
        String playerTeamName = ClientUtils.getPlayerTeamName();
        if (!LifeSeriesClient.COLORED_HEARTS || playerTeamColor == null || playerTeamName == null ||
                !RenderUtils.lifeSkinsAllowedColors.contains(playerTeamColor.toLowerCase(Locale.ROOT)) ||
                !RenderUtils.lifeSkinsAllowedHearts.contains(texturePath) || LifeSeries.modFullyDisabled()) {
            if (LifeSeries.isSeason(Seasons.SECRET_LIFE) && texturePath.startsWith("hud/heart/container")) {
                return;
            }
            //? if <= 1.20 {
            //?} else if <= 1.21 {
            /*instance.blitSprite(identifier, x, y, u, v);
            ls$afterHeartDraw(instance, identifier, x, y, u, v);
            *///?} else if <= 1.21.5 {
            /*instance.blitSprite(renderLayers, identifier, x, y, u, v);
            ls$afterHeartDraw(instance, renderLayers, identifier, x, y, u, v);
            *///?} else {
            instance.blitSprite(renderPipeline, identifier, x, y, u, v);
            ls$afterHeartDraw(instance, renderPipeline, identifier, x, y, u, v);
            //?}
            return;
        }

        String color = playerTeamColor.toLowerCase(Locale.ROOT);

        String heartTypeStr = texturePath.replaceFirst("hud/heart/", "");

        if (!heartTypeStr.startsWith("hardcore_")) {
            if (LifeSeriesClient.COLORED_HEARTS_HARDCORE_ALL_LIVES || (playerTeamName.equals("lives_1") & LifeSeriesClient.COLORED_HEARTS_HARDCORE_LAST_LIFE)) {
                heartTypeStr = "hardcore_"+heartTypeStr;
            }
        }

        //? if <= 1.20 {
        /*var customHeart = IdentifierHelper.mod("textures/gui/sprites/"+color+"_"+heartTypeStr+".png");
         *///?} else {
        var customHeart = IdentifierHelper.mod(color+"_"+heartTypeStr);
        //?}

        //? if <= 1.20 {
        /*guiGraphics.blit(customHeart, x, y, 0, 0, 9, 9, 9, 9);
        ls$afterHeartDraw(guiGraphics, customHeart, x, y, 0, 0);
        ci.cancel();
        *///? } else if <= 1.21 {
        /*instance.blitSprite(customHeart, x, y, u, v);
        ls$afterHeartDraw(instance, identifier, x, y, u, v);
        *///?} else if <= 1.21.5 {
        /*instance.blitSprite(renderLayers, customHeart, x, y, u, v);
        ls$afterHeartDraw(instance, renderLayers, identifier, x, y, u, v);
        *///?} else {
        instance.blitSprite(renderPipeline, customHeart, x, y, u, v);
        ls$afterHeartDraw(instance, renderPipeline, identifier, x, y, u, v);
        //?}
    }

    @Unique
    //? if <= 1.21 {
    /*private void ls$afterHeartDraw(GuiGraphicsExtractor instance, Identifier identifier, int x, int y, int u, int v) {
    *///?} else if <= 1.21.5 {
    /*private void ls$afterHeartDraw(GuiGraphicsExtractor instance, Function<Identifier, RenderType> renderLayers, Identifier identifier, int x, int y, int u, int v) {
    *///?} else {
    private void ls$afterHeartDraw(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    //?}
        if (!LifeSeries.isSeason(Seasons.SECRET_LIFE) || LifeSeries.modFullyDisabled()) {
            return;
        }
        String name = identifier.getPath();
        boolean blinking = name.contains("blinking");
        boolean half = name.contains("half");
        String heartName = "container";
        if (blinking) heartName += "_blinking";
        if (half) heartName += "_half";

        var customHeart = IdentifierHelper.mod("secretlife_"+heartName);
        //? if <= 1.20 {
        /*instance.blit(customHeart, x, y, u, v, u, v);
        *///? } else if <= 1.21 {
        /*instance.blitSprite(customHeart, x, y, u, v);
        *///?} else if <= 1.21.5 {
        /*instance.blitSprite(renderLayers, customHeart, x, y, u, v);
        *///?} else {
        instance.blitSprite(renderPipeline, customHeart, x, y, u, v);
        //?}
    }


    //? if <= 1.20.3 {
    /*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getSleepTimer()I"))
    private int stopSleepDarkness(LocalPlayer instance, Operation<Integer> original) {
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE) && !(RenderUtils.getScreen() instanceof InBedChatScreen) && LifeSeriesClient.hideSleepDarkness) {
            return 0;
        }
        return original.call(instance);
    }
    *///?} else if <= 1.20.5 {
    /*@Inject(method = "renderSleepOverlay", at = @At("HEAD"), cancellable = true)
    private void stopSleepDarkness(GuiGraphicsExtractor guiGraphics, float f, CallbackInfo ci) {
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE) && !(RenderUtils.getScreen() instanceof InBedChatScreen) && LifeSeriesClient.hideSleepDarkness) {
            ci.cancel();
        }
    }
    *///?} else if <= 1.21.11 {
    /*@Inject(method = "renderSleepOverlay", at = @At("HEAD"), cancellable = true)
    private void stopSleepDarkness(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE) && !(RenderUtils.getScreen() instanceof InBedChatScreen) && LifeSeriesClient.hideSleepDarkness) {
            ci.cancel();
        }
    }
    *///?} else {
    @Inject(method = "extractSleepOverlay", at = @At("HEAD"), cancellable = true)
    private void stopSleepDarkness(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE) && !(RenderUtils.getScreen() instanceof InBedChatScreen) && LifeSeriesClient.hideSleepDarkness) {
            ci.cancel();
        }
    }
    //?}
}
