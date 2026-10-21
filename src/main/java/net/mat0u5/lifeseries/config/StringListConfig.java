package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.config.SimpleConfig;

import java.util.ArrayList;
import java.util.List;

public class StringListConfig extends SimpleConfig {

    public StringListConfig(String folder, String name) {
        super(LifeSeries.MOD_ID+":"+name, folder, name);
    }

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
