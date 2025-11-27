package net.mat0u5.lifeseries.gui.config.entries.interfaces;

import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphics;

public interface IPopup {
    boolean shouldShowPopup();
    int getPopupWidth();
    int getPopupHeight();
    void renderContent(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta);

    default int getActualPopupWidth() {
        int width = getPopupWidth() + getPadding();
        if (width % 2 != 0) width++;
        return width;
    }

    default int getActualPopupHeight() {
        return getPopupHeight() + getPadding();
    }

    default int getPadding() {
        return 4;
    }

    default void renderPopup(GuiGraphics context, int x, int y, int mouseX, int mouseY, float tickDelta) {
        if (!shouldShowPopup()) return;

        //?if <= 1.20 {
        int offsetX = 0;
        int offsetY = -1;
        //?} else {
        /*int offsetX = 0;
        int offsetY = 0;
        *///?}

        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().translate(0, 0, 100);
        //?} else {
        /*context.pose().pushMatrix();
        *///?}
        int width = getActualPopupWidth();
        int height = getActualPopupHeight();
        renderBackground(context, x, y+offsetY, width, height, mouseX, mouseY, tickDelta);
        renderContent(context, x+getPadding()/2, y+getPadding()/2+offsetY, width, height, mouseX, mouseY, tickDelta);
        //? if <= 1.21.5 {
        context.pose().popPose();
        //?} else {
        /*context.pose().popMatrix();
        *///?}
    }

    default void renderBackground(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        context.fill(x, y, x + width, y + height, TextColors.LIGHT_BLACK);
        RenderUtils.drawBorder(context, x, y, width, height, TextColors.LIGHT_GRAY);
    }
}