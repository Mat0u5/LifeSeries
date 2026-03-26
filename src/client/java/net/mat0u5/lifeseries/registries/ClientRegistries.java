package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.mat0u5.lifeseries.command.ClientCommands;
import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowmanRenderer;
import net.mat0u5.lifeseries.entity.snail.SnailModel;
import net.mat0u5.lifeseries.entity.snail.SnailRenderer;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotModel;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotRenderer;
import net.mat0u5.lifeseries.events.ClientEvents;
import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.mat0u5.lifeseries.particle.TriviaSpiritParticle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

public class ClientRegistries {
    public static void registerModStuff() {
        registerCommands();
        ClientEvents.registerClientEvents();
        ClientKeybinds.registerKeybinds();
        registerEntities();
        registerParticles();
    }
    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);
    }

    private static void registerEntities() {
        ModelLayerRegistry.registerModelLayer(SnailModel.SNAIL, SnailModel::getTexturedModelData);
        EntityRendererRegistry.register(MobRegistry.SNAIL, SnailRenderer::new);

        ModelLayerRegistry.registerModelLayer(TriviaBotModel.TRIVIA_BOT, TriviaBotModel::getTexturedModelData);
        EntityRendererRegistry.register(MobRegistry.TRIVIA_BOT, TriviaBotRenderer::new);

        EntityRendererRegistry.register(MobRegistry.ANGRY_SNOWMAN, AngrySnowmanRenderer::new);
    }

    private static void registerParticles() {
        ParticleProviderRegistry.getInstance().register(
                ParticleRegistry.TRIVIA_SPIRIT,
                new TriviaSpiritParticle.Provider()
        );
    }
}
