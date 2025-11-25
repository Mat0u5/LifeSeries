package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;
import java.util.Locale;
//? if >= 1.21.2 && <= 1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
import java.util.function.Function;
*///?}
//? if >= 1.21.6
/*import com.mojang.blaze3d.pipeline.RenderPipeline;*/

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

@Mixin(value = Gui.class, priority = 1)
public class GuiMixin {
    @Unique
    private static final List<String> ls$allowedColors = List.of(
            "aqua","black","blue","dark_aqua","dark_blue","dark_gray","dark_green",
            "dark_purple","dark_red","gold","gray","green","light_purple","white","yellow", "red"
    );
    @Unique
    private static final List<String> ls$allowedHearts = List.of(
            "hud/heart/full", "hud/heart/full_blinking", "hud/heart/half", "hud/heart/half_blinking",
            "hud/heart/hardcore_full", "hud/heart/hardcore_full_blinking", "hud/heart/hardcore_half", "hud/heart/hardcore_half_blinking"
    );

    //? if <= 1.21 {
    @Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void customHearts(GuiGraphics instance, ResourceLocation identifier, int x, int y, int u, int v) {
    //?} else if <= 1.21.5 {
    /*@Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void customHearts(GuiGraphics instance, Function<ResourceLocation, RenderType> renderLayers, ResourceLocation identifier, int x, int y, int u, int v) {
    *///?} else if <= 1.21.9 {
    /*@Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void customHearts(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation identifier, int x, int y, int u, int v) {
    *///?} else {
    /*@Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void customHearts(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    *///?}

        String texturePath = identifier.getPath();
        Team playerTeam = ClientUtils.getPlayerTeam();
        if (!MainClient.COLORED_HEARTS || playerTeam == null || playerTeam.getColor() == null ||
                !ls$allowedColors.contains(playerTeam.getColor().getName().toLowerCase(Locale.ROOT)) ||
                !ls$allowedHearts.contains(texturePath) || Main.modFullyDisabled()) {
            if (MainClient.clientCurrentSeason == Seasons.SECRET_LIFE && texturePath.startsWith("hud/heart/container")) {
                return;
            }
            //? if <= 1.21 {
            instance.blitSprite(identifier, x, y, u, v);
            ls$afterHeartDraw(instance, identifier, x, y, u, v);
             //?} else if <= 1.21.5 {
            /*instance.blitSprite(renderLayers, identifier, x, y, u, v);
            ls$afterHeartDraw(instance, renderLayers, identifier, x, y, u, v);
            *///?} else {
            /*instance.blitSprite(renderPipeline, identifier, x, y, u, v);
            ls$afterHeartDraw(instance, renderPipeline, identifier, x, y, u, v);
            *///?}
            return;
        }

        String color = playerTeam.getColor().getName().toLowerCase(Locale.ROOT);

        String heartType = texturePath.replaceFirst("hud/heart/", "");

        if (!heartType.startsWith("hardcore_")) {
            if (MainClient.COLORED_HEARTS_HARDCORE_ALL_LIVES || (playerTeam.getName().equals("lives_1") & MainClient.COLORED_HEARTS_HARDCORE_LAST_LIFE)) {
                heartType = "hardcore_"+heartType;
            }
        }
        var customHeart = IdentifierHelper.mod("textures/gui/hearts/"+color+"_"+heartType+".png");
        //? if <= 1.21 {
        instance.blit(customHeart, x, y, 100, u, v, u, v, u, v);
        ls$afterHeartDraw(instance, identifier, x, y, u, v);
        //?} else if <= 1.21.5 {
        /*instance.blit(renderLayers, customHeart, x, y, u, v, u, v, u, v);
        ls$afterHeartDraw(instance, renderLayers, identifier, x, y, u, v);
        *///?} else {
        /*instance.blit(renderPipeline, customHeart, x, y, u, v, u, v, u, v);
        ls$afterHeartDraw(instance, renderPipeline, identifier, x, y, u, v);
        *///?}
    }

    @Unique
    //? if <= 1.21 {
    private void ls$afterHeartDraw(GuiGraphics instance, ResourceLocation identifier, int x, int y, int u, int v) {
    //?} else if <= 1.21.5 {
    /*private void ls$afterHeartDraw(GuiGraphics instance, Function<ResourceLocation, RenderType> renderLayers, ResourceLocation identifier, int x, int y, int u, int v) {
    *///?} else if <= 1.21.9 {
    /*private void ls$afterHeartDraw(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation identifier, int x, int y, int u, int v) {
    *///?} else {
    /*private void ls$afterHeartDraw(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    *///?}
        if (MainClient.clientCurrentSeason != Seasons.SECRET_LIFE || Main.modFullyDisabled()) {
            return;
        }
        String name = identifier.getPath();
        boolean blinking = name.contains("blinking");
        boolean half = name.contains("half");
        String heartName = "container";
        if (blinking) heartName += "_blinking";
        if (half) heartName += "_half";

        var customHeart = IdentifierHelper.mod("textures/gui/hearts/secretlife/"+heartName+".png");
        //? if <= 1.21 {
        instance.blit(customHeart, x, y, 100, u, v, u, v, u, v);
        //?} else if <= 1.21.5 {
        /*instance.blit(renderLayers, customHeart, x, y, u, v, u, v, u, v);
        *///?} else {
        /*instance.blit(renderPipeline, customHeart, x, y, u, v, u, v, u, v);
        *///?}
    }
}
