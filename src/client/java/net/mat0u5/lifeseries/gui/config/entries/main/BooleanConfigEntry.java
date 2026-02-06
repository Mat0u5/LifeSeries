package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.ButtonConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BooleanConfigEntry extends ButtonConfigEntry implements IEntryGroupHeader {
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 20;
    private static final String TEXT_TRUE = "§aYes";
    private static final String TEXT_FALSE = "§cNo";

    private final boolean defaultValue;
    protected boolean value;
    private boolean startingValue;

    public BooleanConfigEntry(String fieldName, String displayName, String description, boolean value, boolean defaultValue) {
        super(fieldName, displayName, description, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.defaultValue = defaultValue;
        this.value = value;
        this.startingValue = value;
        updateButtonText();
        clicked = false;
    }

    @Override
    protected void onButtonClick(Button button) {
        value = !value;
        updateButtonText();
        markChanged();
    }

    @Override
    public Component getButtonText() {
        return value ? Component.nullToEmpty(TEXT_TRUE) : Component.nullToEmpty(TEXT_FALSE);
    }

    @Override
    public void resetToDefault() {
        value = defaultValue;
        updateButtonText();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean booleanValue) {
            this.value = booleanValue;
            updateButtonText();
        }
    }

    @Override
    public void updateButtonText() {
        super.updateButtonText();
        clicked = this.value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(value);
    }

    @Override
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDefaultValueAsString() {
        return String.valueOf(defaultValue);
    }

    @Override
    public Boolean getStartingValue() {
        return startingValue;
    }

    @Override
    public String getStartingValueAsString() {
        return String.valueOf(startingValue);
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.BOOLEAN;
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
        return button.getX() - 10;
    }
}