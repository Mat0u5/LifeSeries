package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.TextFieldConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;

public class StringConfigEntry extends TextFieldConfigEntry {
    private static final int FIELD_WIDTH = 150;
    private static final float ANIMATION_SPEED = 0.15f;
    private static final int PADDING = 4;

    protected final String defaultValue;
    protected String value;
    protected String startingValue;

    private float currentWidth;
    private float targetWidth;
    private int x = -1;

    public StringConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, FIELD_WIDTH);
        this.defaultValue = defaultValue;
        this.value = value;
        this.startingValue = value;
        this.currentWidth = FIELD_WIDTH;
        this.targetWidth = FIELD_WIDTH;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        setText(value);
        if (textField.getWidth()-6 < textRenderer.width(value)) {
            textField.moveCursorToStart(false);
        }
    }

    @Override
    protected void onTextChanged(String text) {
        super.onTextChanged(text);
        this.value = text;
        if (!hasCustomErrors()) {
            clearError();
        }
    }

    @Override
    protected void postTextChanged() {
        markChanged();
        updateFieldDimensions();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        updateFieldDimensions();
    }

    private void updateFieldDimensions() {
        if (textField == null) return;
        if (x < 0) return;

        String text = textField.getValue();
        if (text == null) return;
        if (text.isEmpty()) {
            targetWidth = FIELD_WIDTH;
            return;
        }

        int textWidth = textRenderer.width(text) + 20;

        int labelEndX = x + LABEL_OFFSET_X + textRenderer.width(getDisplayName());
        int fieldEndX = textField.getX() + textField.getWidth();
        int maxFieldWidth = fieldEndX - labelEndX - 15;
        if (maxFieldWidth <= FIELD_WIDTH) maxFieldWidth = FIELD_WIDTH;

        int requiredWidth = Math.clamp(textWidth + PADDING * 2, FIELD_WIDTH, maxFieldWidth);

        if (isFocused()) {
            targetWidth = requiredWidth;
        }
        else {
            targetWidth = Math.min(FIELD_WIDTH, requiredWidth);
        }
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.x = x;
        updateAnimations(tickDelta);

        textField.setWidth((int) currentWidth);

        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    private void updateAnimations(float tickDelta) {
        if (Math.abs(currentWidth - targetWidth) > 2f) {
            currentWidth += (targetWidth - currentWidth) * ANIMATION_SPEED * tickDelta;
        }
        else {
            currentWidth = targetWidth;
        }
    }

    @Override
    protected int getTextFieldPosX(int x, int entryWidth) {
        return x + entryWidth - (int)currentWidth - 5;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String stringValue) {
            this.value = stringValue;
            setText(stringValue);
            updateFieldDimensions();
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public String getDefaultValueAsString() {
        return getDefaultValue();
    }

    @Override
    public String getStartingValue() {
        return startingValue;
    }

    @Override
    public String getStartingValueAsString() {
        return startingValue;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.STRING;
    }
}