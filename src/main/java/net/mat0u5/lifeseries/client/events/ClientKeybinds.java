package net.mat0u5.lifeseries.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.mat0u5.lifeseries.client.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientKeybinds {
    public static KeyMapping superpower;
    public static KeyMapping openConfig;
    public static KeyMapping runCommand;

    //? if <= 1.21.6 {
    /*public static final String KEYBIND_ID = "key.category.lifeseries.general";
     *///?} else {
    public static KeyMapping.Category KEYBIND_ID = KeyMapping.Category.register(IdentifierHelper.mod("general"));
    //?}

    public static void tick() {
        while (superpower != null && superpower.consumeClick()) {
            NetworkHandlerClient.pressSuperpowerKey();
        }
        while (runCommand != null && runCommand.consumeClick() && VersionControl.isDevVersion()) {
            NetworkHandlerClient.pressRunCommandKey();
        }
        while (openConfig != null && openConfig.consumeClick()) {
            NetworkHandlerClient.pressOpenConfigKey();
        }
    }

    public static void init() {
        if (superpower != null) return;

        //? if <= 26.2 {
        var type = InputConstants.Type.KEYSYM;
        //?} else {
        /*var type = InputConstants.Type.KEYBOARD;
        *///?}

        superpower = new KeyMapping(
                "key.lifeseries.superpower",
                type,
                //? if <= 1.21.5 {
                /*InputConstants.KEY_G,
                 *///?} else {
                InputConstants.KEY_R,
                //?}
                KEYBIND_ID
        );
        openConfig = new KeyMapping(
                "key.lifeseries.openconfig",
                type,
                InputConstants.UNKNOWN.getValue(),
                KEYBIND_ID
        );
        if (VersionControl.isDevVersion()) {
            runCommand = new KeyMapping(
                    "key.lifeseries.runcommand",
                    type,
                    InputConstants.KEY_RALT,
                    KEYBIND_ID
            );
        }
    }
    public static KeyMapping[] appendCustomKeybinds(KeyMapping[] vanillaKeys) {
        init();
        List<KeyMapping> allKeys = new ArrayList<>(Arrays.asList(vanillaKeys));

        if (!allKeys.contains(superpower)) {
            allKeys.add(superpower);
            allKeys.add(openConfig);
            if (VersionControl.isDevVersion()) {
                allKeys.add(runCommand);
            }
        }

        return allKeys.toArray(new KeyMapping[0]);
    }
}