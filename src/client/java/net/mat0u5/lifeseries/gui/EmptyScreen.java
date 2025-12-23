package net.mat0u5.lifeseries.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EmptyScreen extends Screen {
    public boolean closable = true;
    public EmptyScreen(boolean closable) {
        super(Component.empty());
        this.closable = closable;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return closable;
    }

    @Override
    //? if <= 1.20 {
    /*public void renderBackground(GuiGraphics context) {}
     *///?} else {
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {}
    //?}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
