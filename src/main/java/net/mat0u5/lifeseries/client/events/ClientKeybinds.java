package net.mat0u5.lifeseries.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.mat0u5.lifeseries.client.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.matlib.client.events.ClientRegistryEvents;
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
    public static KeyMapping.Category KEYBIND_ID = KeyMapping.Category.register(IdentifierHelper.lifeseries("general"));
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

    public static void register() {

        //? if <= 26.2 {
        /*var type = InputConstants.Type.KEYSYM;
        *///?} else {
        var type = InputConstants.Type.KEYBOARD;
        //?}

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
        ClientRegistryEvents.KEYBIND.register(list -> {
            if (!list.contains(superpower)) {
                list.add(superpower);
                list.add(openConfig);
                if (VersionControl.isDevVersion()) {
                    list.add(runCommand);
                }
            }
        });
    }
}