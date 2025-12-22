package net.mat0u5.lifeseries.gui.trivia;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NewQuizScreen extends Screen {
    protected NewQuizScreen(Component component) {
        super(component);
    }
    @Override
    //? if <= 1.20 {
    /*public void renderBackground(GuiGraphics context) {}
     *///?} else {
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {}
    //?}

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {

    }
}
