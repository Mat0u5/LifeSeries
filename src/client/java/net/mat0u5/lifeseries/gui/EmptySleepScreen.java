package net.mat0u5.lifeseries.gui;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.TextColors;
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

    private float buttonSlideOffset = 0f;
    private static final float SLIDE_SPEED = 5f;
    private static final int SLIVER_WIDTH = 15;
    private int buttonWidth = 150;
    private int buttonHeight = 20;
    private int padding = 10;

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

        int yOffset = padding;

        toggleButton = Button.builder(
                Component.literal(adminControlsOpen ? "Close Admin Control" : "Open Admin Control"),
                button -> {
                    adminControlsOpen = !adminControlsOpen;
                    button.setMessage(Component.literal(adminControlsOpen ? "Close Admin Control" : "Open Admin Control"));
                    updateCommandButtons();
                }
        ).bounds(getToggleButtonX(), yOffset, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(toggleButton);
        yOffset += buttonHeight + 5;

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

    private int getToggleButtonX() {
        int fullyVisibleX = this.width - buttonWidth - padding;
        int hiddenX = this.width - SLIVER_WIDTH;
        return (int) (hiddenX + (fullyVisibleX - hiddenX) * buttonSlideOffset);
    }

    private boolean isMouseNearButton(int mouseX, int mouseY) {
        int buttonX = getToggleButtonX();
        int buttonY = padding;
        int hoverZoneExtension = 30;

        int leftEdge = buttonX - hoverZoneExtension;
        int rightEdge = this.width;

        return mouseX >= leftEdge &&
                mouseX <= rightEdge &&
                mouseY >= buttonY &&
                mouseY <= buttonY + buttonHeight;
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
        if (MainClient.isAdmin) {
            boolean shouldShow = adminControlsOpen || isMouseNearButton(mouseX, mouseY);

            if (shouldShow) {
                buttonSlideOffset = Math.min(1f, buttonSlideOffset + SLIDE_SPEED * delta * 0.04f);
            } else {
                buttonSlideOffset = Math.max(0f, buttonSlideOffset - SLIDE_SPEED * delta * 0.04f);
            }

            toggleButton.setX(getToggleButtonX());
        }

        super.render(context, mouseX, mouseY, delta);
        updateCommandButtons();

        if (MainClient.isAdmin && buttonSlideOffset < 0.3f && !adminControlsOpen) {
            int sliverX = this.width - SLIVER_WIDTH + 3;
            int sliverY = padding + buttonHeight / 2 - 4;
            context.drawString(this.font, "<", sliverX, sliverY, TextColors.WHITE);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}