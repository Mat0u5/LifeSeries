package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.client.ClientHandler;
import net.mat0u5.lifeseries.client.render.VignetteRenderer;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkHandlerClient {
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleNumberPacket(payload.name(),payload.number()));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleStringPacket(payload.name(),payload.value()));
        });
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(NetworkHandlerClient::respondHandshake);
        });
        ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> Trivia.receiveTrivia(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleLongPacket(payload.name(),payload.number()));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handlePlayerDisguise(payload.name(),payload.hiddenUUID(), payload.hiddenName(), payload.shownUUID(), payload.shownName()));
        });
    }
    
    public static void handleStringPacket(String name, String value) {
        if (name.equalsIgnoreCase("currentSeries")) {
            Main.LOGGER.info("[PACKET_CLIENT] Updated current series to {}", value);
            MainClient.clientCurrentSeries = SeriesList.getSeriesFromStringName(value);
            if (Main.isClient()) {
                ClientHandler.checkSecretLifeClient();
            }
        }
        if (name.equalsIgnoreCase("activeWildcards")) {
            List<Wildcards> newList = new ArrayList<>();
            for (String wildcardStr : value.split("__")) {
                newList.add(Wildcards.getFromString(wildcardStr));
            }
            Main.LOGGER.info("[PACKET_CLIENT] Updated current wildcards to {}", newList);

            if (!MainClient.clientActiveWildcards.contains(Wildcards.TIME_DILATION) && newList.contains(Wildcards.TIME_DILATION)) {
                if (!newList.contains(Wildcards.CALLBACK)) MainClient.TIME_DILATION_TIMESTAMP = System.currentTimeMillis();
            }
            MainClient.clientActiveWildcards = newList;
        }
        if (name.equalsIgnoreCase("curse_sliding")) {
            if (value.equalsIgnoreCase("true")) {
                MainClient.CURSE_SLIDING = true;
            }
            else {
                MainClient.CURSE_SLIDING = false;
            }
        }
        if (name.equalsIgnoreCase("jump") && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.jump();
        }
    }

    public static void handleNumberPacket(String name, double number) {
        int intNumber = (int) number;
        if (name.equalsIgnoreCase("hunger_version")) {
            Main.LOGGER.info("[PACKET_CLIENT] Updated hunger shuffle version to {}", intNumber);
            Hunger.shuffleVersion = intNumber;
        }
        if (name.equalsIgnoreCase("player_min_mspt")) {
            Main.LOGGER.info("[PACKET_CLIENT] Updated min. player MSPT to {}", number);
            TimeDilation.MIN_PLAYER_MSPT = (float) number;
        }
    }

    public static void handleLongPacket(String name, long number) {
        if (name.equalsIgnoreCase("superpower_cooldown")) {
            MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP = number;
        }
        if (name.equalsIgnoreCase("show_vignette")) {
            Main.LOGGER.info("[PACKET_CLIENT] Showing vignette for {}", number);
            VignetteRenderer.showVignetteFor(0.35f, number);
        }
        if (name.equalsIgnoreCase("mimicry_cooldown")) {
            MainClient.MIMICRY_COOLDOWN_TIMESTAMP = number;
        }
        if (name.startsWith("player_invisible__")) {
            try {
                UUID uuid = UUID.fromString(name.replaceFirst("player_invisible__",""));
                if (number == 0) {
                    MainClient.invisiblePlayers.remove(uuid);
                }
                else {
                    MainClient.invisiblePlayers.put(uuid, number);
                }
            }catch(Exception ignored) {}
        }
    }

    public static void handlePlayerDisguise(String name, String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        if (name.equalsIgnoreCase("player_disguise")) {
            if (shownName.isEmpty()) {
                MainClient.playerDisguiseNames.remove(hiddenName);
                try {
                    UUID hideUUID = UUID.fromString(hiddenUUID);
                    MainClient.playerDisguiseUUIDs.remove(hideUUID);
                }catch(Exception ignored) {}
            }
            else {
                MainClient.playerDisguiseNames.put(hiddenName, shownName);
                try {
                    UUID hideUUID = UUID.fromString(hiddenUUID);
                    UUID showUUID = UUID.fromString(shownUUID);
                    MainClient.playerDisguiseUUIDs.put(hideUUID, showUUID);
                }catch(Exception ignored) {}
            }

        }
    }

    /*
        Sending
     */

    public static void sendTriviaAnswer(int answer) {
        Main.LOGGER.info("[PACKET_CLIENT] Sending trivia answer: {}", answer);
        ClientPlayNetworking.send(new NumberPayload("trivia_answer", answer));
    }

    public static void respondHandshake() {
        String modVersionStr = Main.MOD_VERSION;
        int modVersion = OtherUtils.getModVersionInt(modVersionStr);
        HandshakePayload payload = new HandshakePayload(modVersionStr, modVersion);
        ClientPlayNetworking.send(payload);
        Main.LOGGER.info("[PACKET_CLIENT] Sent handshake: {"+modVersionStr+", "+modVersion+"}");
    }

    public static void sendHoldingJumpPacket() {
        ClientPlayNetworking.send(new StringPayload("holding_jump", "true"));
    }

    public static void pressSuperpowerKey() {
        ClientPlayNetworking.send(new StringPayload("superpower_key", "true"));
    }
}
