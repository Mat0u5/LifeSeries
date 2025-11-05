package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import java.util.List;

//? if >= 1.21.2 && <= 1.21.5
/*import net.minecraft.client.renderer.RenderType;*/
//? if >= 1.21.6
/*import net.minecraft.client.renderer.RenderPipelines;*/

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

public class RenderUtils {

    public static void debugX(GuiGraphics context, int x) {
        context.fill(x, 0, x+1, context.guiHeight(), TextColors.DEBUG);
    }

    public static void debugY(GuiGraphics context, int y) {
        context.fill(0, y, context.guiWidth(), y+1, TextColors.DEBUG);
    }

    //? if <= 1.21.9 {
    public static void drawTextureScaled(GuiGraphics context, ResourceLocation texture, float x, float y, int u, int v, int width, int height, float scaleX, float scaleY) {
    //?} else {
    /*public static void drawTextureScaled(GuiGraphics context, Identifier texture, float x, float y, int u, int v, int width, int height, float scaleX, float scaleY) {
    *///?}
        //? if <= 1.21 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.blit(texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height);
        context.pose().popPose();
        //?} else {
        /*drawTextureScaled(context, texture, x, y, u, v, width, height, width, height, scaleX, scaleY);
        *///?}
    }
    //? if <= 1.21.9 {
    public static void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
    //?} else {
    /*public static void drawTexture(GuiGraphics context, Identifier texture, int x, int y, int u, int v, int width, int height) {
    *///?}
        //? if <= 1.21 {
        context.blit(texture, x, y, u, v, width, height);
        //?} else {
        /*drawTexture(context, texture, x, y, u, v, width, height, width, height);
        *///?}
    }
    //? if >= 1.21.2 && <= 1.21.5 {
    /*public static void drawTextureScaled(GuiGraphics context, ResourceLocation texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.blit(RenderType::guiTextured, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.pose().popPose();
    }
    public static void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    *///?} else if >= 1.21.6 && <= 1.21.9 {
    /*public static void drawTextureScaled(GuiGraphics context, ResourceLocation texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.blit(RenderPipelines.GUI_TEXTURED, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.pose().popMatrix();
    }
    public static void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    *///?} else if > 1.21.9 {
    /*public static void drawTextureScaled(GuiGraphics context, Identifier texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.blit(RenderPipelines.GUI_TEXTURED, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.pose().popMatrix();
    }
    public static void drawTexture(GuiGraphics context, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    *///?}

    //Center Fixed Text
    public static void drawTextCenter(GuiGraphics context, Font textRenderer, Component text, int x, int y) {
        drawTextCenter(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextCenterScaled(GuiGraphics context, Font textRenderer, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextCenterScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextCenter(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y) {
        context.drawString(textRenderer, text, x - textRenderer.width(text)/2, y, textColor, false);
    }

    public static void drawTextCenterScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY) {
        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.drawString(textRenderer, text, (int)(x / scaleX - textRenderer.width(text)/2.0), (int)(y / scaleY), textColor, false);
        context.pose().popPose();
        //?} else {
        /*context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.drawString(textRenderer, text, (int)(x / scaleX - textRenderer.width(text)/2.0), (int)(y / scaleY), textColor, false);
        context.pose().popMatrix();
        *///?}
    }


    //Left Fixed Text
    public static void drawTextLeft(GuiGraphics context, Font textRenderer, Component text, int x, int y) {
        drawTextLeft(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextLeftScaled(GuiGraphics context, Font textRenderer, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextLeftScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextLeft(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y) {
        context.drawString(textRenderer, text, x, y, textColor, false);
    }

    public static void drawOrderedTextLeft(GuiGraphics context, Font textRenderer, int textColor, FormattedCharSequence text, int x, int y) {
        context.drawString(textRenderer, text, x, y, textColor, false);
    }

    public static void drawTextLeftScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY) {
        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.drawString(textRenderer, text, (int)(x / scaleX), (int)(y / scaleY), textColor, false);
        context.pose().popPose();
        //?} else {
        /*context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.drawString(textRenderer, text, (int)(x / scaleX), (int)(y / scaleY), textColor, false);
        context.pose().popMatrix();
        *///?}
    }

    public static int drawTextLeftWrapLines(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y, int maxWidth, int gapY) {
        List<FormattedCharSequence> wrappedText = textRenderer.split(text, maxWidth);
        int offsetY = 0;
        for (FormattedCharSequence line : wrappedText) {
            context.drawString(textRenderer, line, x, y + offsetY, textColor, false);
            offsetY += textRenderer.lineHeight + gapY;
        }
        return offsetY;
    }

    //Right Fixed Text
    public static void drawTextRight(GuiGraphics context, Font textRenderer, Component text, int x, int y) {
        drawTextRight(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextRightScaled(GuiGraphics context, Font textRenderer, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextRightScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextRight(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y) {
        drawTextRight(context, textRenderer, textColor, text, x, y, false);
    }

    public static void drawTextRight(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y, boolean shadow) {
        context.drawString(textRenderer, text, x - textRenderer.width(text), y, textColor, shadow);
    }

    public static void drawTextRightScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextRightScaled(context, textRenderer, textColor, text, x, y, scaleX, scaleY, false);
    }

    public static void drawTextRightScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY, boolean shadow) {
        int width = textRenderer.width(text);
        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.drawString(textRenderer, text, (int)(x / scaleX - width), (int)(y / scaleY), textColor, shadow);
        context.pose().popPose();
        //?} else {
        /*context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.drawString(textRenderer, text, (int)(x / scaleX - width), (int)(y / scaleY), textColor, shadow);
        context.pose().popMatrix();
        *///?}
    }


    public static void drawBorder(GuiGraphics context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    //TODO i hate all of this
}
