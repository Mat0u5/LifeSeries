package net.mat0u5.lifeseries.client.config;

import com.google.auto.service.AutoService;
import net.mat0u5.lifeseries.client.gui.config.entries.extra.*;
import net.mat0u5.lifeseries.config.LifeSeriesConfigTypes;
import net.mat0u5.matlib.client.config.ConfigGuiClient;
import net.mat0u5.matlib.client.gui.config.entries.main.BooleanConfigEntry;
import net.mat0u5.matlib.client.services.RegistrableClient;
import net.mat0u5.matlib.config.entry.ConfigGuiEntry;
import net.mat0u5.matlib.config.type.ConfigTypes;

import java.util.ArrayList;
import java.util.List;

@AutoService(RegistrableClient.class)
public class LifeSeriesConfigGuiClient implements RegistrableClient {
    @Override
    public void onRegister() {
        LifeSeriesConfigTypes.init();
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.BOOGEYMAN, e -> new BoogeymanConfigEntry(e, ConfigTypes.BOOLEAN.parse(e.value()), ConfigTypes.BOOLEAN.parse(e.defaultValue)));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.BOOLEAN_REVERSE_HEADER, e -> new BooleanConfigEntry(e, ConfigTypes.BOOLEAN.parse(e.value()), ConfigTypes.BOOLEAN.parse(e.defaultValue), true));

        // Dynamic Entries
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.LIVES_ENTRY, e -> new LivesConfigEntry(e, ConfigTypes.NULLABLE_INTEGER.parse(e.value()), ConfigTypes.NULLABLE_INTEGER.parse(e.value())));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.EVENT_ENTRY, e -> new EventConfigEntry(e, e.value(), e.value(), optionOrDefault(e, 0, "")));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.MODIFIABLE_TEXT, e -> new ModifiableTextConfigEntry(e, e.value(), optionOrDefault(e, 0, e.value())));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.MODIFIABLE_SOUND, e -> new ModifiableSoundConfigEntry(e, e.value(), optionOrDefault(e, 0, e.value())));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.TEAM_ENTRY, e -> new TeamConfigEntry(e, e.args));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.SOULMATE_ENTRY, e -> new SoulmateConfigEntry(e, e.args));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.SECRET_TASK, e -> new SecretLifeTaskConfigEntry(e, e.args));
        ConfigGuiClient.registerEntryFactory(LifeSeriesConfigTypes.TRIVIA_QUESTION, TriviaQuestionConfigEntry::fromGuiEntry);
    }

    private static String optionOrDefault(ConfigGuiEntry entry, int index, String defaultValue) {
        List<String> options = entry.options();
        return index < options.size() ? options.get(index) : defaultValue;
    }

    public static ConfigGuiEntry copyWithKey(ConfigGuiEntry base, String key) {
        return new ConfigGuiEntry(base.configId, key, base.type, "", "", base.group, false, false, true, false, false, "", new ArrayList<>());
    }
}
