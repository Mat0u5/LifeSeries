package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;

public abstract class NumberConfigEntry<T extends Number> extends TextFieldConfigEntry implements IEntryGroupHeader {
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
        setText(getValueAsString());
        if (textField.getWidth()-6 < textRenderer.width(getValueAsString())) {
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
        try {
            T newValue = parseValue(text);
            if (isValueInRange(newValue)) {
                value = newValue;
                clearError();
            } else {
                setError(TextUtils.formatString("Value must be between {} and {}", minValue, maxValue));
            }
        } catch (NumberFormatException e) {
            setError("Invalid number format");
        }
        markChanged();
    }

    @Override
    protected void renderAdditionalContent(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (minValue != null && maxValue != null) {
            String rangeText = TextUtils.formatString("({}-{})", minValue, maxValue);
            int rangeWidth = textRenderer.width(rangeText);
            int entryWidth = getEntryContentWidth(width);

            context.drawString(textRenderer, rangeText,
                    x + entryWidth - rangeWidth - textField.getWidth() + RANGE_LABEL_OFFSET_X,
                    getTextFieldPosY(y, height)  + RANGE_LABEL_OFFSET_Y, TextColors.LIGHT_GRAY);
        }
    }

    @Override
    public void setValue(Object value) {
        if (isValidType(value)) {
            this.value = castValue(value);
            setText(getValueAsString());
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
        if (value == null) return "";
        return value.toString();
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDefaultValueAsString() {
        if (defaultValue == null) return "";
        return defaultValue.toString();
    }

    @Override
    public T getStartingValue() {
        return startingValue;
    }

    @Override
    public String getStartingValueAsString() {
        if (startingValue == null) return "";
        return startingValue.toString();
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    @Override
    public void expand() {
    }

    @Override
    public boolean isExpanded() {
        return clicked;
    }

    @Override
    public int expandTextX(int x, int width) {
        return textField.getX() - 10;
    }
}