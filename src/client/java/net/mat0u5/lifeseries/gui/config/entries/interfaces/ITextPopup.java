package net.mat0u5.lifeseries.gui.config.entries.interfaces;

import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public interface ITextPopup extends IPopup {
    Font getTextRenderer();
    Component getPopupText();
    default int getPopupWidth() {
        if (allowMultiline()) {
            int maxWidth = 0;
            for (FormattedCharSequence line : getTextRenderer().split(getPopupText(), multilineWrap())) {
                maxWidth = Math.max(maxWidth, getTextRenderer().width(line));
            }
            return maxWidth+1;
        }
        return getTextRenderer().width(getPopupText())+1;
    }

    default int getPopupHeight() {
        if (allowMultiline()) {
            int lineCount = getTextRenderer().split(getPopupText(), multilineWrap()).size();
            int gapY = 5;
            return lineCount * getTextRenderer().lineHeight + (lineCount-1)*gapY;
        }
        return getTextRenderer().lineHeight;
    }
    default boolean allowMultiline() {
        return false;
    }
    default int multilineWrap() {
        return 1000;
    }

    default void renderContent(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        Font textRenderer = getTextRenderer();
        Component popupText = getPopupText();
        if (popupText == null) return;
        if (allowMultiline()) {
            RenderUtils.text(popupText, x+1, y+1).wrapLines(width, 5).colored(getTextColor()).render(context, textRenderer);
        }
        else {
            context.drawString(textRenderer, popupText, x+1, y+1, getTextColor(), false);
        }
    }

    default int getTextColor() {
        return TextColors.LIGHT_GRAY;
    }
}
