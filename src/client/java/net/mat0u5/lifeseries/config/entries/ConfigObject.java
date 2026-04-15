package net.mat0u5.lifeseries.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

import java.util.List;

public class ConfigObject {
    public ConfigTypes configType;
    private int index;
    public String id;
    public String name;
    public String description;
    public List<String> args;
    public boolean modified = false;
    public ConfigObject(ConfigPayload payload) {
        configType = ConfigTypes.getFromString(payload.configType());
        index = payload.index();
        id = payload.configId();
        name = payload.name();
        description = payload.description();
        args = payload.args();
    }

    public String getGroupInfo() {
        if (args.size() < 3) {
            return "";
        }
        return args.get(2);
    }
}
