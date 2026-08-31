package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.client.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

public class ModifiableSoundConfigEntry extends StringConfigEntry {
    public ModifiableSoundConfigEntry(String fieldName, String displayName, String value, String defaultValue) {
        super(fieldName, displayName, "", value, defaultValue, 300, 14);
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.MODIFIABLE_SOUND;
    }
}
