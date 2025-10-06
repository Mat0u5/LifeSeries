package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
//? if >= 1.21.2 && <= 1.21.5
/*import net.minecraft.client.render.RenderLayer;*/
//? if >= 1.21.6
/*import net.minecraft.client.gl.RenderPipelines;*/

public class RenderUtils {

    public static void debugX(DrawContext context, int x) {
        context.fill(x, 0, x+1, context.getScaledWindowHeight(), TextColors.DEBUG);
    }

    public static void debugY(DrawContext context, int y) {
        context.fill(0, y, context.getScaledWindowWidth(), y+1, TextColors.DEBUG);
    }

    public static void drawTextureScaled(DrawContext context, Identifier texture, float x, float y, int u, int v, int width, int height, float scaleX, float scaleY) {
        //? if <= 1.21 {
        context.getMatrices().push();
        context.getMatrices().scale(scaleX, scaleY, 1.0f);
        context.drawTexture(texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height);
        context.getMatrices().pop();
        //?} else {
        /*drawTextureScaled(context, texture, x, y, u, v, width, height, width, height, scaleX, scaleY);
        *///?}
    }
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height) {
        //? if <= 1.21 {
        context.drawTexture(texture, x, y, u, v, width, height);
        //?} else {
        /*drawTexture(context, texture, x, y, u, v, width, height, width, height);
        *///?}
    }
    //? if >= 1.21.2 && <= 1.21.5 {
    /*public static void drawTextureScaled(DrawContext context, Identifier texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.getMatrices().push();
        context.getMatrices().scale(scaleX, scaleY, 1.0f);
        context.drawTexture(RenderLayer::getGuiTextured, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.getMatrices().pop();
    }
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    *///?} else if >= 1.21.6 {
    /*public static void drawTextureScaled(DrawContext context, Identifier texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scaleX, scaleY);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.getMatrices().popMatrix();
    }
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    *///?}

    //Center Fixed Text
    public static void drawTextCenter(DrawContext context, TextRenderer textRenderer, Text text, int x, int y) {
        drawTextCenter(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextCenterScaled(DrawContext context, TextRenderer textRenderer, Text text, double x, double y, float scaleX, float scaleY) {
        drawTextCenterScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextCenter(DrawContext context, TextRenderer textRenderer, int textColor, Text text, int x, int y) {
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text)/2, y, textColor, false);
    }

    public static void drawTextCenterScaled(DrawContext context, TextRenderer textRenderer, int textColor, Text text, double x, double y, float scaleX, float scaleY) {
        //? if <= 1.21.5 {
        context.getMatrices().push();
        context.getMatrices().scale(scaleX, scaleY, 1.0f);
        context.drawText(textRenderer, text, (int)(x / scaleX - textRenderer.getWidth(text)/2.0), (int)(y / scaleY), textColor, false);
        context.getMatrices().pop();
        //?} else {
        /*context.getMatrices().pushMatrix();
        context.getMatrices().scale(scaleX, scaleY);
        context.drawText(textRenderer, text, (int)(x / scaleX - textRenderer.getWidth(text)/2.0), (int)(y / scaleY), textColor, false);
        context.getMatrices().popMatrix();
        *///?}
    }


    //Left Fixed Text
    public static void drawTextLeft(DrawContext context, TextRenderer textRenderer, Text text, int x, int y) {
        drawTextLeft(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextLeftScaled(DrawContext context, TextRenderer textRenderer, Text text, double x, double y, float scaleX, float scaleY) {
        drawTextLeftScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextLeft(DrawContext context, TextRenderer textRenderer, int textColor, Text text, int x, int y) {
        context.drawText(textRenderer, text, x, y, textColor, false);
    }

    public static void drawOrderedTextLeft(DrawContext context, TextRenderer textRenderer, int textColor, OrderedText text, int x, int y) {
        context.drawText(textRenderer, text, x, y, textColor, false);
    }

    public static void drawTextLeftScaled(DrawContext context, TextRenderer textRenderer, int textColor, Text text, double x, double y, float scaleX, float scaleY) {
        //? if <= 1.21.5 {
        context.getMatrices().push();
        context.getMatrices().scale(scaleX, scaleY, 1.0f);
        context.drawText(textRenderer, text, (int)(x / scaleX), (int)(y / scaleY), textColor, false);
        context.getMatrices().pop();
        //?} else {
        /*context.getMatrices().pushMatrix();
        context.getMatrices().scale(scaleX, scaleY);
        context.drawText(textRenderer, text, (int)(x / scaleX), (int)(y / scaleY), textColor, false);
        context.getMatrices().popMatrix();
        *///?}
    }

    public static int drawTextLeftWrapLines(DrawContext context, TextRenderer textRenderer, int textColor, Text text, int x, int y, int maxWidth, int gapY) {
        List<OrderedText> wrappedText = textRenderer.wrapLines(text, maxWidth);
        int offsetY = 0;
        for (OrderedText line : wrappedText) {
            context.drawText(textRenderer, line, x, y + offsetY, textColor, false);
            offsetY += textRenderer.fontHeight + gapY;
        }
        return offsetY;
    }

    //Right Fixed Text
    public static void drawTextRight(DrawContext context, TextRenderer textRenderer, Text text, int x, int y) {
        drawTextRight(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextRightScaled(DrawContext context, TextRenderer textRenderer, Text text, double x, double y, float scaleX, float scaleY) {
        drawTextRightScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextRight(DrawContext context, TextRenderer textRenderer, int textColor, Text text, int x, int y) {
        drawTextRight(context, textRenderer, textColor, text, x, y, false);
    }

    public static void drawTextRight(DrawContext context, TextRenderer textRenderer, int textColor, Text text, int x, int y, boolean shadow) {
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text), y, textColor, shadow);
    }

    public static void drawTextRightScaled(DrawContext context, TextRenderer textRenderer, int textColor, Text text, double x, double y, float scaleX, float scaleY) {
        drawTextRightScaled(context, textRenderer, textColor, text, x, y, scaleX, scaleY, false);
    }

    public static void drawTextRightScaled(DrawContext context, TextRenderer textRenderer, int textColor, Text text, double x, double y, float scaleX, float scaleY, boolean shadow) {
        int width = textRenderer.getWidth(text);
        //? if <= 1.21.5 {
        context.getMatrices().push();
        context.getMatrices().scale(scaleX, scaleY, 1.0f);
        context.drawText(textRenderer, text, (int)(x / scaleX - width), (int)(y / scaleY), textColor, shadow);
        context.getMatrices().pop();
        //?} else {
        /*context.getMatrices().pushMatrix();
        context.getMatrices().scale(scaleX, scaleY);
        context.drawText(textRenderer, text, (int)(x / scaleX - width), (int)(y / scaleY), textColor, false);
        context.getMatrices().popMatrix();
        *///?}
    }


    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    //TODO i hate all of this
}
