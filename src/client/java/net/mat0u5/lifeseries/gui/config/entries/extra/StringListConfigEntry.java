package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringListConfigEntry extends StringListPopupConfigEntry<String> {

    private List<String> allowedValues;

    public StringListConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue, List<String> allowedValues) {
        super(fieldName, displayName, description, value, defaultValue);
        this.allowedValues = allowedValues;
    }

    @Override
    protected void reloadEntries(List<String> items) {
        if (entries != null) {
            entries.clear();
        }

        List<String> newList = new ArrayList<>();
        boolean errors = false;

        for (String entry : items) {
            if (entry.isEmpty()) continue;
            if (allowedValues != null && !allowedValues.contains(entry.toLowerCase(Locale.ROOT))) {
                setError(TextUtils.formatString("Invalid entry: '{}'", entry));
                errors = true;
                continue;
            }
            newList.add(entry.toLowerCase(Locale.ROOT));
        }

        entries = newList;
        if (!errors) {
            clearError();
        }
    }

    @Override
    protected void renderListEntry(GuiGraphics context, String entry, int x, int y, int mouseX, int mouseY, float tickDelta) {

    }

    @Override
    public boolean shouldShowPopup() {
        return false;
    }

    @Override
    public boolean hasCustomErrors() {
        return true;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.STRING_LIST;
    }
}
