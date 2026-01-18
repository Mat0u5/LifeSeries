package net.mat0u5.lifeseries.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
//? if <= 1.21.11 {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?} else {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*///?}

public class ClientKeybinds {
    public static KeyMapping superpower;
    public static KeyMapping openConfig;

    public static KeyMapping runCommand;

    //? if <= 1.21.6 {
    public static final String KEYBIND_ID = "key.category.lifeseries.general";
     //?} else {
    /*public static final KeyMapping.Category KEYBIND_ID = new KeyMapping.Category(IdentifierHelper.mod("general"));
    *///?}

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
    public static void registerKeybinds() {
        //? if <= 1.21.11 {
        superpower = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        //?} else {
        /*superpower = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        *///?}
                "key.lifeseries.superpower",
                InputConstants.Type.KEYSYM,
                //? if <= 1.21.5 {
                GLFW.GLFW_KEY_G,
                //?} else {
                /*GLFW.GLFW_KEY_R,
                 *///?}

                KEYBIND_ID
        ));
        //? if <= 1.21.11 {
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        //?} else {
        /*openConfig = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        *///?}
                "key.lifeseries.openconfig",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEYBIND_ID
        ));
        if (VersionControl.isDevVersion()) {
            //? if <= 1.21.11 {
            runCommand = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            //?} else {
            /*runCommand = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            *///?}
                    "key.lifeseries.runcommand",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_ALT,
                    KEYBIND_ID
            ));
        }
    }
}
