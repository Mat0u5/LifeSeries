package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import net.minecraft.resources.Identifier;

public class RenderUtils {

    public static void debugX(GuiGraphicsExtractor context, int x) {
        context.fill(x, 0, x+1, context.guiHeight(), TextColors.DEBUG);
    }

    public static void debugY(GuiGraphicsExtractor context, int y) {
        context.fill(0, y, context.guiWidth(), y+1, TextColors.DEBUG);
    }

    public static void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public static CustomTextureRenderer texture(Identifier texture, float x, float y, int width, int height) {
        return new CustomTextureRenderer(texture, x, y, width, height);
    }

    public static CustomTextRenderer text(Component text, int x, int y) {
        return new CustomTextRenderer(text, x, y);
    }

    public static CustomTextRenderer text(FormattedCharSequence text, int x, int y) {
        return new CustomTextRenderer(text, x, y);
    }

    public static CustomTextRenderer text(String text, int x, int y) {
        return new CustomTextRenderer(Component.nullToEmpty(text), x, y);
    }
}
