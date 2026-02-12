package net.mat0u5.lifeseries.gui.config.entries.interfaces;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface ITextPopup extends IPopup {
    Font getTextRenderer();
    Component getPopupText();
    default int getPopupWidth() {
        return getTextRenderer().width(getPopupText())+1;
    }

    default int getPopupHeight() {
        return getTextRenderer().lineHeight;
    }

    default void renderContent(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        Font textRenderer = getTextRenderer();
        Component popupText = getPopupText();
        if (popupText == null) return;
        context.drawString(textRenderer, popupText, x+1, y+1, getTextColor(), false);
    }

    default int getTextColor() {
        return TextColors.LIGHT_GRAY;
    }
}
