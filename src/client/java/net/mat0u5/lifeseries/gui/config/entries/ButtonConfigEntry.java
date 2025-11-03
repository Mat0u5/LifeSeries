package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

//? if >= 1.21.9 {
/*import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
*///?}

public abstract class ButtonConfigEntry extends ConfigEntry {
    protected final Button button;
    protected boolean clicked = false;

    public ButtonConfigEntry(String fieldName, String displayName, String description, int buttonWidth, int buttonHeight) {
        super(fieldName, displayName, description);
        button = createButton(buttonWidth, buttonHeight);
    }

    protected Button createButton(int width, int height) {
        return Button.builder(getButtonText(), this::onButtonClick)
                .bounds(0, 0, width, height)
                .build();
    }

    protected abstract Component getButtonText();
    protected abstract void onButtonClick(Button button);

    protected void updateButtonText() {
        button.setMessage(getButtonText());
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = getEntryContentWidth(width);
        button.setX(getButtonPosX(x, entryWidth));
        button.setY(getButtonPosY(y, height));
        button.render(context, mouseX, mouseY, tickDelta);
    }

    protected int getButtonPosX(int x, int entryWidth) {
        return x + entryWidth - button.getWidth() - 5;
    }

    protected int getButtonPosY(int y, int height) {
        //return y + (height - button.getHeight()) / 2; CENTER
        return y;
    }

    //? if <= 1.21.6 {
    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (!this.button.mouseClicked(mouseX, mouseY, button)) {
            clicked = !clicked;
        }
        return true;
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return false;
    }
    //?} else {
    /*@Override
    protected boolean mouseClickedEntry(Click click, boolean doubled) {
        if (!this.button.mouseClicked(click, doubled)) {
            clicked = !clicked;
        }
        return true;
    }

    @Override
    protected boolean keyPressedEntry(KeyInput input) {
        return false;
    }

    @Override
    protected boolean charTypedEntry(CharInput input) {
        return false;
    }
    *///?}
}