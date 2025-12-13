package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;

import static net.mat0u5.lifeseries.MainClient.clientConfig;

public class ClientResourcePacks {
    private static final String MINIMAL_ARMOR_RESOURCEPACK = "lifeseries:minimal_armor";
    private static final String NICELIFE_ARMOR_RESOURCEPACK = "lifeseries:nicelife";

    public static void checkClientPacks() {
        handleClientResourcepack(MINIMAL_ARMOR_RESOURCEPACK, ClientConfig.MINIMAL_ARMOR.get(clientConfig));
        handleClientResourcepack(NICELIFE_ARMOR_RESOURCEPACK, (MainClient.clientCurrentSeason == Seasons.NICE_LIFE && MainClient.NICELIFE_SNOWY_NETHER));
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
        Minecraft client = Minecraft.getInstance();
        if (client.getResourcePackRepository() != null && !client.getResourcePackRepository().getSelectedIds().contains(id)) {
            for (Pack profile : client.getResourcePackRepository().getAvailablePacks()) {
                if (profile.getId().equals(id)) {
                    client.getResourcePackRepository().addPack(id);
                    Main.LOGGER.info("Enabling resourcepack " + id);
                    client.reloadResourcePacks();
                    return;
                }
            }
        }
        if (forceReload) {
            Main.LOGGER.info("Force enabling resourcepack " + id);
            client.reloadResourcePacks();
        }
    }

    public static void disableClientResourcePack(String id) {
        Minecraft client = Minecraft.getInstance();
        if (client.getResourcePackRepository() == null) return;
        if (!client.getResourcePackRepository().getSelectedIds().contains(id)) return;

        for (Pack profile : client.getResourcePackRepository().getAvailablePacks()) {
            if (profile.getId().equals(id)) {
                client.getResourcePackRepository().removePack(id);
                Main.LOGGER.info("Disabling resourcepack " + id);
                client.reloadResourcePacks();
                return;
            }
        }
    }
}
