package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import java.util.Objects;
//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
*///?}

public abstract class TextFieldConfigEntry extends ConfigEntry {
    protected final EditBox textField;
    private static final int DEFAULT_TEXT_FIELD_WIDTH = 100;
    protected static final int DEFAULT_TEXT_FIELD_HEIGHT = 18;
    private static final int TEXT_FIELD_OFFSET_X = -5;
    private static final int TEXT_FIELD_OFFSET_Y = 1;
    private int maxTextFieldLength = 8192;
    protected boolean clicked = false;

    public TextFieldConfigEntry(String fieldName, String displayName, String description) {
        this(fieldName, displayName, description, DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT);
    }

    public TextFieldConfigEntry(String fieldName, String displayName, String description, int textFieldWidth) {
        this(fieldName, displayName, description, textFieldWidth, DEFAULT_TEXT_FIELD_HEIGHT);
    }

    public TextFieldConfigEntry(String fieldName, String displayName, String description, int textFieldWidth, int textFieldHeight) {
        super(fieldName, displayName, description);
        textField = new EditBox(textRenderer, 0, 0, textFieldWidth, textFieldHeight, Component.empty());
        textField.setResponder(this::onChanged);
        textField.setMaxLength(maxTextFieldLength);
    }

    protected abstract void initializeTextField();

    private void onChanged(String text) {
        onTextChanged(text);
        postTextChanged();
    }

    protected void onTextChanged(String text) {
        if (text.length() >= maxTextFieldLength) {
            while (text.length() >= maxTextFieldLength && maxTextFieldLength < 1_000_000_000) {
                maxTextFieldLength *= 2;
            }
            textField.setMaxLength(maxTextFieldLength);
        }
    }

    protected void postTextChanged() {
    }

    protected void renderAdditionalContent(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryWidth = getEntryContentWidth(width);

        renderAdditionalContent(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);

        textField.setX(getTextFieldPosX(x, entryWidth));
        textField.setY(getTextFieldPosY(y, height));
        textField.render(context, mouseX, mouseY, tickDelta);

        if (hasError()) {
            textField.setTextColor(TextColors.PASTEL_RED);
        }
        else {
            textField.setTextColor(TextColors.WHITE);
        }
    }

    protected int getTextFieldPosX(int x, int entryWidth) {
        return x + entryWidth - textField.getWidth() + TEXT_FIELD_OFFSET_X;
    }

    protected int getTextFieldPosY(int y, int height) {
        //return y + (height - textField.getHeight()) / 2; CENTER
        return y+TEXT_FIELD_OFFSET_Y;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        textField.setFocused(focused);
    }

    //? if <= 1.21.6 {
    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (!textField.mouseClicked(mouseX, mouseY, button)) {
            clicked = !clicked;
        }
        return true;
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return textField.charTyped(chr, modifiers);
    }
    //?} else {
    /*@Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (!textField.mouseClicked(click, doubled)) {
            clicked = !clicked;
        }
        return true;
    }

    @Override
    protected boolean keyPressedEntry(KeyEvent input) {
        return textField.keyPressed(input);
    }

    @Override
    protected boolean charTypedEntry(CharacterEvent input) {
        return textField.charTyped(input);
    }
    *///?}

    @Override
    public void resetToDefault() {
        setText(getDefaultValueAsString());
        if (!hasCustomErrors()) {
            clearError();
        }
    }

    public void setText(String text) {
        onTextChanged(text);
        postTextChanged();
        textField.setValue(text);
    }

    public boolean hasCustomErrors() {
        return false;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(textField.getValue(), getStartingValueAsString());
    }

    @Override
    public boolean canReset() {
        return !Objects.equals(textField.getValue(), getDefaultValueAsString());
    }
}