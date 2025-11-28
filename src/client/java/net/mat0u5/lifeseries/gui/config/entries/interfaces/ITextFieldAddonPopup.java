package net.mat0u5.lifeseries.gui.config.entries.interfaces;

import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;

public interface ITextFieldAddonPopup extends ITextPopup {
    EditBox getTextField();

    @Override
    default void renderBackground(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        EditBox textField = getTextField();
        context.fill(x, y, x + width, y + height, TextColors.LIGHT_BLACK);

        RenderUtils.drawBorder(context, x, y, width, height, TextColors.DARK_GRAY);
        RenderUtils.drawBorder(context, x-1, y-1, width+2, height+1, textField.isFocused()?TextColors.WHITE_BORDER:TextColors.LIGHT_GRAY);

        int textFieldX = textField.getX();
        int textFieldY = textField.getY();
        if (textField.getWidth() <= width) {
            //? if <= 1.20 {
            context.fill(textFieldX, textFieldY-1, textFieldX+textField.getWidth(), textFieldY, TextColors.DARK_GRAY);
            //?} else {
            /*context.fill(textFieldX+1, textFieldY, textFieldX+textField.getWidth()-1, textFieldY+1, TextColors.DARK_GRAY);
            *///?}
        }
        else {
            //? if <= 1.20 {
            context.fill(x, textFieldY-1, x+width, textFieldY, TextColors.DARK_GRAY);
            //?} else {
            /*context.fill(x, textFieldY, x+width, textFieldY+1, TextColors.DARK_GRAY);
            *///?}
        }
    }

    default void renderPopup(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        if (!shouldShowPopup()) return;
        EditBox textField = getTextField();
        int popupWidth = getActualPopupWidth();
        int popupX = textField.getX()+textField.getWidth()/2-popupWidth/2;
        int popupY = Math.max(0, textField.getY() - getActualPopupHeight()) + 1;
        renderPopup(context, popupX, popupY, mouseX, mouseY, tickDelta);
    }
}
