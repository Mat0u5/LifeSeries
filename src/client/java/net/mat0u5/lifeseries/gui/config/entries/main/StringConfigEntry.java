package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.TextFieldConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.gui.GuiGraphics;

public class StringConfigEntry extends TextFieldConfigEntry {
    private static final float ANIMATION_SPEED = 0.15f;
    private static final int PADDING = 4;

    protected final String defaultValue;
    protected String value;
    protected String startingValue;

    private int minFieldWidth;
    private float currentWidth;
    private float targetWidth;
    private int x = -1;

    public StringConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        this(fieldName, displayName, description, value, defaultValue, 150, DEFAULT_TEXT_FIELD_HEIGHT);
    }
    public StringConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue, int textFieldWidth, int textFieldHeight) {
        super(fieldName, displayName, description, textFieldWidth, textFieldHeight);
        this.minFieldWidth = textFieldWidth;
        this.defaultValue = defaultValue;
        this.value = value;
        this.startingValue = value;
        this.currentWidth = textFieldWidth;
        this.targetWidth = textFieldWidth;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        setText(value);
        if (textField.getWidth()-6 < textRenderer.width(value)) {
            //? if <= 1.20 {
            /*textField.moveCursorToStart();
            *///?} else {
            textField.moveCursorToStart(false);
            //?}
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
    }

    private void updateFieldDimensions() {
        if (textField == null) return;
        if (x < 0) return;

        String text = textField.getValue();
        if (text == null) return;

        int textWidth = textRenderer.width(text) + 20;

        int labelEndX = labelEndX();
        int fieldEndX = textField.getX() + textField.getWidth();
        int maxFieldWidth = fieldEndX - labelEndX - 15;
        int newMinFieldWidth = minFieldWidth;
        if (maxFieldWidth <= newMinFieldWidth) newMinFieldWidth = maxFieldWidth;

        if (text.isEmpty()) {
            targetWidth = newMinFieldWidth;
            return;
        }

        int requiredWidth = OtherUtils.clamp(textWidth + PADDING * 2, newMinFieldWidth, maxFieldWidth);

        if (isFocused()) {
            targetWidth = requiredWidth;
        }
        else {
            targetWidth = Math.min(newMinFieldWidth, requiredWidth);
        }
    }

    public int labelEndX() {
        return x + LABEL_OFFSET_X + textRenderer.width(getDisplayName());
    }


    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.x = x;
        updateFieldDimensions();
        updateAnimations(tickDelta);

        textField.setWidth((int) currentWidth);

        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        if (renderTicks < 10) {
            currentWidth = targetWidth;
        }
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