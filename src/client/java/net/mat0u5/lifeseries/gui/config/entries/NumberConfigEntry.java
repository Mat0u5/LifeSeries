package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.DrawContext;

public abstract class NumberConfigEntry<T extends Number> extends TextFieldConfigEntry {
    protected static final int RANGE_LABEL_OFFSET_X = -12;
    protected static final int RANGE_LABEL_OFFSET_Y = 6;
    private static final int TEXT_FIELD_WIDTH = 60;

    protected final T defaultValue;
    protected final T minValue;
    protected final T maxValue;
    protected T value;
    protected T startingValue;

    public NumberConfigEntry(String fieldName, String displayName, String description, T value, T defaultValue) {
        this(fieldName, displayName, description, value, defaultValue, null, null);
    }

    public NumberConfigEntry(String fieldName, String displayName, String description, T value, T defaultValue, T minValue, T maxValue) {
        super(fieldName, displayName, description, TEXT_FIELD_WIDTH);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        this.startingValue = value;
        initializeTextField();
    }

    @Override
    protected void initializeTextField() {
        setText(value.toString());
        if (textField.getWidth()-6 < textRenderer.getWidth(value.toString())) {
            textField.setCursorToStart(false);
        }
    }

    @Override
    protected void onTextChanged(String text) {
        super.onTextChanged(text);
        try {
            T newValue = parseValue(text);
            if (isValueInRange(newValue)) {
                value = newValue;
                clearError();
            } else {
                setError("Value must be between " + minValue + " and " + maxValue);
            }
        } catch (NumberFormatException e) {
            setError("Invalid number format");
        }
        markChanged();
    }

    @Override
    protected void renderAdditionalContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (minValue != null && maxValue != null) {
            String rangeText = "(" + minValue + "-" + maxValue + ")";
            int rangeWidth = textRenderer.getWidth(rangeText);
            int entryWidth = getEntryContentWidth(width);

            context.drawTextWithShadow(textRenderer, rangeText,
                    x + entryWidth - rangeWidth - textField.getWidth() + RANGE_LABEL_OFFSET_X,
                    getTextFieldPosY(y, height)  + RANGE_LABEL_OFFSET_Y, TextColors.LIGHT_GRAY);
        }
    }

    @Override
    public void setValue(Object value) {
        if (isValidType(value)) {
            this.value = castValue(value);
            setText(value.toString());
        }
    }

    protected abstract T parseValue(String text) throws NumberFormatException;

    protected abstract boolean isValueInRange(T value);

    protected abstract boolean isValidType(Object value);

    protected abstract T castValue(Object value);

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value.toString();
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDefaultValueAsString() {
        return defaultValue.toString();
    }

    @Override
    public T getStartingValue() {
        return startingValue;
    }

    @Override
    public String getStartingValueAsString() {
        return String.valueOf(startingValue);
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }
}