package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;

import static net.mat0u5.lifeseries.MainClient.clientConfig;

public class ClientResourcePacks {
    private static final String MINIMAL_ARMOR_RESOURCEPACK = "lifeseries:minimal_armor";

    public static void checkClientPacks() {
        handleClientResourcepack(MINIMAL_ARMOR_RESOURCEPACK, ClientConfig.MINIMAL_ARMOR.get(clientConfig));
    }

    public static void handleClientResourcepack(String id, boolean action) {
        if (action) {
            enableClientResourcePack(id);
        }
        else {
            disableClientResourcePack(id);
        }
    }

    public static void enableClientResourcePack(String id) {
        enableClientResourcePack(id, false);
    }

    public static void enableClientResourcePack(String id, boolean forceReload) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourcePackManager() != null && !client.getResourcePackManager().getEnabledIds().contains(id)) {
            for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
                if (profile.getId().equals(id)) {
                    client.getResourcePackManager().enable(id);
                    Main.LOGGER.info("Enabling resourcepack " + id);
                    client.reloadResources();
                    return;
                }
            }
        }
        if (forceReload) {
            Main.LOGGER.info("Force enabling resourcepack " + id);
            client.reloadResources();
        }
    }

    public static void disableClientResourcePack(String id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourcePackManager() == null) return;
        if (!client.getResourcePackManager().getEnabledIds().contains(id)) return;

        for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
            if (profile.getId().equals(id)) {
                client.getResourcePackManager().disable(id);
                Main.LOGGER.info("Disabling resourcepack " + id);
                client.reloadResources();
                return;
            }
        }
    }
}
