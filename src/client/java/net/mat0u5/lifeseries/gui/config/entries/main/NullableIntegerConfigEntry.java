package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.NumberConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

public class NullableIntegerConfigEntry extends NumberConfigEntry<Integer> {

    public NullableIntegerConfigEntry(String fieldName, String displayName, String description, Integer value, Integer defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
    }

    public NullableIntegerConfigEntry(String fieldName, String displayName, String description, Integer value, Integer defaultValue, Integer minValue, Integer maxValue) {
        super(fieldName, displayName, description, value, defaultValue, minValue, maxValue);
    }

    @Override
    protected Integer parseValue(String text) throws NumberFormatException {
        if (text.isEmpty()) return null;
        return Integer.parseInt(text);
    }

    @Override
    protected boolean isValueInRange(Integer value) {
        if (minValue == null || maxValue == null) return true;
        if (value == null) return false;
        return value >= minValue && value <= maxValue;
    }

    @Override
    protected boolean isValidType(Object value) {
        return value == null || value instanceof Integer;
    }

    @Override
    protected Integer castValue(Object value) {
        if (value == null) return null;
        return (Integer) value;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.NULLABLE_INTEGER;
    }
}