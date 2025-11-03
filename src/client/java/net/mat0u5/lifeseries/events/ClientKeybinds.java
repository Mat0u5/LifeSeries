package net.mat0u5.lifeseries.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {
    public static KeyMapping superpower;
    public static KeyMapping openConfig;

    public static KeyMapping runCommand;

    //? if <= 1.21.6 {
    public static final String KEYBIND_ID = "key.category.lifeseries.general";
     //?} else {
    /*public static final KeyBinding.Category KEYBIND_ID = new KeyBinding.Category(Identifier.of("lifeseries","general"));
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
        superpower = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lifeseries.superpower",
                InputConstants.Type.KEYSYM,
                //? if <= 1.21.5 {
                GLFW.GLFW_KEY_G,
                //?} else {
                /*GLFW.GLFW_KEY_R,
                 *///?}

                KEYBIND_ID
        ));
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lifeseries.openconfig",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEYBIND_ID
        ));
        if (VersionControl.isDevVersion()) {
            runCommand = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    "key.lifeseries.runcommand",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_ALT,
                    KEYBIND_ID
            ));
        }
    }
}
