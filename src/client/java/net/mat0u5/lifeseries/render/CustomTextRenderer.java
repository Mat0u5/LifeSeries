package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class CustomTextRenderer {
    private final Component text;
    private FormattedCharSequence orderedText;
    private final double x;
    private final double y;
    private float scaleX = 1;
    private float scaleY = 1;
    private boolean shadow = false;
    private int textColor = TextColors.DEFAULT;
    private Anchor anchor = Anchor.LEFT;
    private boolean wrapLines = false;
    private int wrapMaxWidth = 100;
    private int wrapGapY = 7;

    public CustomTextRenderer(Component text, double x, double y) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.orderedText = null;
    }

    public CustomTextRenderer(FormattedCharSequence orderedText, double x, double y) {
        this.x = x;
        this.y = y;
        this.orderedText = orderedText;
        this.text = null;
    }

    public CustomTextRenderer anchorCenter() {
        anchor = Anchor.CENTER;
        return this;
    }

    public CustomTextRenderer anchorRight() {
        anchor = Anchor.RIGHT;
        return this;
    }

    public CustomTextRenderer withShadow() {
        shadow = true;
        return this;
    }

    public CustomTextRenderer wrapLines(int maxWidth, int gapY) {
        wrapLines = true;
        wrapMaxWidth = maxWidth;
        wrapGapY = gapY;
        if (orderedText == null && text != null) {
            orderedText = text.getVisualOrderText();
        }
        return this;
    }

    public CustomTextRenderer scaled(float newScaleX, float newScaleY) {
        scaleX = newScaleX;
        scaleY = newScaleY;
        return this;
    }

    public CustomTextRenderer colored(int color) {
        textColor = color;
        return this;
    }

    private boolean isScaled() {
        return scaleX != 1 || scaleY != 1;
    }

    public int render(GuiGraphics context, Font textRenderer) {
        if (isScaled()) {
            //? if <= 1.21.5 {
            context.pose().pushPose();
            context.pose().scale(scaleX, scaleY, 1.0f);
            //?} else {
            /*context.pose().pushMatrix();
            context.pose().scale(scaleX, scaleY);
            *///?}
        }

        int renderedTextHeight = textRenderer.lineHeight;
        double offsetX = 0;
        double textWidth = 0;
        if (this.text != null) textWidth = textRenderer.width(text);
        if (this.orderedText != null) textWidth = textRenderer.width(orderedText);

        if (anchor == Anchor.CENTER) offsetX -= textWidth/2.0;
        if (anchor == Anchor.RIGHT) offsetX -= textWidth;

        if (this.orderedText != null) {
            if (wrapLines) {
                List<FormattedCharSequence> wrappedText = textRenderer.split(text, wrapMaxWidth);
                int offsetY = 0;
                for (FormattedCharSequence line : wrappedText) {
                    context.drawString(textRenderer, line, (int) (x / scaleX + offsetX), (int) (y / scaleY + offsetY), textColor, shadow);
                    offsetY += textRenderer.lineHeight + wrapGapY;
                }
                renderedTextHeight = offsetY;
            }
            else {
                context.drawString(textRenderer, orderedText, (int) (x / scaleX + offsetX), (int) (y / scaleY), textColor, shadow);
            }
        }
        else if (this.text != null) {
            context.drawString(textRenderer, text, (int)(x / scaleX + offsetX), (int)(y / scaleY), textColor, shadow);
        }

        if (isScaled()) {
            //? if <= 1.21.5 {
            context.pose().popPose();
            //?} else {
            /*context.pose().popMatrix();
            *///?}
        }
        return renderedTextHeight;
    }

    public enum Anchor {
        LEFT,
        CENTER,
        RIGHT;
    }
}
