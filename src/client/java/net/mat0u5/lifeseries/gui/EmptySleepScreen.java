package net.mat0u5.lifeseries.gui;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EmptySleepScreen extends Screen {
    public boolean closable = true;
    private boolean adminControlsOpen = false;
    private Button toggleButton;
    private Button skipNightButton;
    private Button wakeUpButton;
    private Button wakeUpEveryoneButton;

    public EmptySleepScreen(boolean closable) {
        super(Component.empty());
        this.closable = closable;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return closable;
    }

    @Override
    public void init() {
        super.init();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int padding = 10;

        toggleButton = Button.builder(
                Component.literal(adminControlsOpen ? "Close Admin Control" : "Open Admin Control"),
                button -> {
                    adminControlsOpen = !adminControlsOpen;
                    button.setMessage(Component.literal(adminControlsOpen ? "Close Admin Control" : "Open Admin Control"));
                    updateCommandButtons();
                }
        ).bounds(this.width - buttonWidth - padding, padding, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(toggleButton);
        int yOffset = padding + buttonHeight + 5;

        skipNightButton = Button.builder(
                Component.literal("Skip Night"),
                button -> ClientUtils.runCommand("nicelife skipNight")
        ).bounds(this.width - buttonWidth - padding, yOffset, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(skipNightButton);
        yOffset += buttonHeight + 5;

        wakeUpButton = Button.builder(
                Component.literal("Wake Up"),
                button -> ClientUtils.runCommand("nicelife wakeUp")
        ).bounds(this.width - buttonWidth - padding, yOffset, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(wakeUpButton);
        yOffset += buttonHeight + 5;

        wakeUpEveryoneButton = Button.builder(
                Component.literal("Wake Up Everyone"),
                button -> ClientUtils.runCommand("nicelife wakeUp @a")
        ).bounds(this.width - buttonWidth - padding, yOffset, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(wakeUpEveryoneButton);

        updateCommandButtons();
    }

    private void updateCommandButtons() {
        skipNightButton.visible = adminControlsOpen;
        wakeUpButton.visible = adminControlsOpen;
        wakeUpEveryoneButton.visible = adminControlsOpen;
        toggleButton.visible = MainClient.isAdmin;
    }

    @Override
    //? if <= 1.20 {
    /*public void renderBackground(GuiGraphics context) {}
     *///?} else {
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {}
    //?}

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        updateCommandButtons();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}