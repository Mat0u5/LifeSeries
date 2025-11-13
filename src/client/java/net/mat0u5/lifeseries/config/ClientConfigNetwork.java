package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.config.entries.*;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.network.packets.ConfigPayload;
import net.mat0u5.lifeseries.utils.ClientResourcePacks;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static net.mat0u5.lifeseries.MainClient.clientConfig;

public class ClientConfigNetwork {

    public static Map<Integer, ConfigObject> configObjects = new TreeMap<>();
    public static Map<Integer, ConfigObject> clientConfigObjects = new TreeMap<>();

    public static void load() {
        configObjects.clear();
        clientConfigObjects.clear();

        int index = 0;
        for (ConfigFileEntry<?> entry : clientConfig.getAllConfigEntries()) {
            handleConfigPacket(clientConfig.getConfigPayload(entry, index), true);
            index++;
        }
    }

    public static void handleConfigPacket(ConfigPayload payload, boolean client) {
        int index = payload.index();
        ConfigObject configObject = getConfigEntry(payload);
        if (configObject == null) return;
        if (!client) {
            configObjects.put(index, configObject);
        }
        else {
            clientConfigObjects.put(index, configObject);
        }
    }

    public static ConfigObject getConfigEntry(ConfigPayload payload) {
        ConfigTypes configType = ConfigTypes.getFromString(payload.configType());
        String id = payload.id();
        String name = payload.name();
        String description = payload.description();
        List<String> args = payload.args();
        if (args.size() < 3) return null;
        String argValue = args.get(0);
        String argDefaultValue = args.get(1);
        String argGroupInfo = args.get(2);

        if (configType.parentText()) {
            boolean clickable = !argDefaultValue.equalsIgnoreCase("false");
            return new TextObject(payload, name, clickable);
        }
        if (configType.parentString()) {
            return new StringObject(payload, argValue, argDefaultValue);
        }
        if (configType.parentBoolean()) {
            boolean value = argValue.equalsIgnoreCase("true");
            boolean defaultValue = argDefaultValue.equalsIgnoreCase("true");
            return new BooleanObject(payload, value, defaultValue);
        }
        if (configType.parentDouble()) {
            try {
                double value = Double.parseDouble(argValue);
                double defaultValue = Double.parseDouble(argDefaultValue);
                return new DoubleObject(payload, value, defaultValue);
            }catch(Exception e){}
        }
        if (configType.parentInteger()) {
            try {
                int value = Integer.parseInt(argValue);
                int defaultValue = Integer.parseInt(argDefaultValue);
                return new IntegerObject(payload, value, defaultValue);
            }catch(Exception e){}
        }
        if (configType.parentNullableInteger()) {
            Integer value = null;
            Integer defaultValue = null;
            try {
                value = Integer.parseInt(argValue);
            }catch(Exception e){}
            try {
                defaultValue = Integer.parseInt(argDefaultValue);
            }catch(Exception e){}
            try {
                return new NullableIntegerObject(payload, value, defaultValue);
            }catch(Exception e){}
        }

        return null;
    }

    public static void onConfigSave(ConfigEntry entry) {
        String id = entry.getFieldName();
        String valueStr = entry.getValueAsString();
        clientConfig.setProperty(id, valueStr);

        // Actions
        if (id.equals(ClientConfig.MINIMAL_ARMOR.key)) {
            ClientResourcePacks.checkClientPacks();
        }
    }
}
