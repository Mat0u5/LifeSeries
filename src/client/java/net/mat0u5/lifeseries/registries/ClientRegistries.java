package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mat0u5.lifeseries.command.ClientCommands;
import net.mat0u5.lifeseries.entity.newsnail.NewSnailModel;
import net.mat0u5.lifeseries.entity.newsnail.NewSnailRenderer;
import net.mat0u5.lifeseries.events.ClientEvents;
import net.mat0u5.lifeseries.events.ClientKeybinds;

public class ClientRegistries {
    public static void registerModStuff() {
        registerCommands();
        ClientEvents.registerClientEvents();
        ClientKeybinds.registerKeybinds();
        registerEntities();
    }
    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);
    }

    private static void registerEntities() {
        EntityModelLayerRegistry.registerModelLayer(NewSnailModel.NEWSNAIL, NewSnailModel::getTexturedModelData);
        EntityRendererRegistry.register(MobRegistry.NEW_SNAIL, NewSnailRenderer::new);
    }
}
