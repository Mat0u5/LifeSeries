package net.mat0u5.lifeseries.config;

import java.util.ArrayList;
import java.util.List;

public class StringListConfig extends ConfigManager {

    public StringListConfig(String folder, String name) {
        super(folder, name);
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of();
    }
    @Override
    protected void instantiateProperties() {}

    public void save(List<String> list) {
        resetProperties("-- DO NOT MODIFY --");
        if (list == null || list.isEmpty()) return;
        for (int pos = 0; pos < list.size(); pos++) {
            String str = list.get(pos);
            if (str.isEmpty()) continue;
            setPropertyCommented("entry"+pos,str, "-- DO NOT MODIFY --");
        }
    }

    public List<String> load() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String property = getProperty("entry"+i);
            if (property == null) break;
            if (property.isEmpty()) continue;
            list.add(property);
        }
        return list;
    }
}
