package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.config.ClientConfigNetwork;
import net.mat0u5.lifeseries.features.SnailSkinsClient;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.config.ClientConfigGuiManager;
import net.mat0u5.lifeseries.gui.other.ChooseWildcardScreen;
import net.mat0u5.lifeseries.gui.other.PastLifeChooseTwistScreen;
import net.mat0u5.lifeseries.gui.other.SnailTextureInfoScreen;
import net.mat0u5.lifeseries.gui.seasons.ChooseSeasonScreen;
import net.mat0u5.lifeseries.gui.seasons.SeasonInfoScreen;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.render.VignetteRenderer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.ClientResourcePacks;
import net.mat0u5.lifeseries.utils.ClientTaskScheduler;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkHandlerClient {
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleNumberPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleStringPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleHandshake(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> Trivia.receiveTrivia(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleLongPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handlePlayerDisguise(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(ImagePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleImagePacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleConfigPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleStringListPacket(payload));
        });
    }
    public static void handleStringListPacket(StringListPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        List<String> value = payload.value();

        if (name == PacketNames.MORPH) {
            try {
                String morphUUIDStr = value.get(0);
                UUID morphUUID = UUID.fromString(morphUUIDStr);
                String morphTypeStr = value.get(1);
                EntityType<?> morphType = null;
                if (!morphTypeStr.equalsIgnoreCase("null") && !morphUUIDStr.isEmpty()) {
                    morphType = Registries.ENTITY_TYPE.get(Identifier.of(morphTypeStr));
                }
                if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Received morph packet: {} ({})", morphType, morphUUID);
                MorphManager.setFromPacket(morphUUID, morphType);
            } catch (Exception e) {}
        }
    }

    public static void handleConfigPacket(ConfigPayload payload) {
        ClientConfigNetwork.handleConfigPacket(payload, false);
    }

    public static void handleImagePacket(ImagePayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        if (name == PacketNames.SNAIL_SKIN) {
            SnailSkinsClient.handleSnailSkin(payload);
        }
    }
    
    public static void handleStringPacket(StringPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        String value = payload.value();

        if (name == PacketNames.CURRENT_SEASON) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated current season to {}", value);
            MainClient.clientCurrentSeason = Seasons.getSeasonFromStringName(value);
            ClientResourcePacks.checkClientPacks();
            MainClient.reloadConfig();
        }

        if (name == PacketNames.SESSION_STATUS) {
            MainClient.clientSessionStatus = SessionStatus.getSessionName(value);
        }

        if (name == PacketNames.ACTIVE_WILDCARDS) {
            List<Wildcards> newList = new ArrayList<>();
            for (String wildcardStr : value.split("__")) {
                newList.add(Wildcards.getFromString(wildcardStr));
            }
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated current wildcards to {}", newList);
            MainClient.clientActiveWildcards = newList;
        }

        if (name == PacketNames.JUMP && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.jump();
        }

        if (name == PacketNames.RESET_TRIVIA) {
            Trivia.resetTrivia();
        }

        if (name == PacketNames.SELECT_WILDCARDS) {
            MinecraftClient.getInstance().setScreen(new ChooseWildcardScreen());
        }

        if (name == PacketNames.OPEN_CONFIG) {
            ClientConfigNetwork.load();
            ClientTaskScheduler.scheduleTask(10, ClientConfigGuiManager::openConfig);
        }


        if (name == PacketNames.SELECT_SEASON) {
            MinecraftClient.getInstance().setScreen(new ChooseSeasonScreen(!value.isEmpty()));
        }
        if (name == PacketNames.SEASON_INFO) {
            Seasons season = Seasons.getSeasonFromStringName(value);
            if (season != Seasons.UNASSIGNED) MinecraftClient.getInstance().setScreen(new SeasonInfoScreen(season));
        }

        if (name == PacketNames.TRIVIA_BOT_PART) {
            try {
                UUID uuid = UUID.fromString(value);
                MainClient.triviaBotPartUUIDs.add(uuid);
            }catch(Exception e) {}
        }

        if (name == PacketNames.SNAIL_PART) {
            try {
                UUID uuid = UUID.fromString(value);
                MainClient.snailPartUUIDs.add(uuid);
            }catch(Exception e) {}
        }

        if (name == PacketNames.SNAIL_POS) {
            try {
                String[] split = value.split("_");
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                MainClient.snailPos = pos;
                MainClient.snailPosTime = System.currentTimeMillis();
            }catch(Exception e) {}
        }

        if (name == PacketNames.TRIVIA_SNAIL_PART) {
            try {
                UUID uuid = UUID.fromString(value);
                MainClient.triviaSnailPartUUIDs.add(uuid);
            }catch(Exception e) {}
        }

        if (name == PacketNames.TRIVIA_SNAIL_POS) {
            try {
                String[] split = value.split("_");
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                MainClient.triviaSnailPos = pos;
                MainClient.triviaSnailPosTime = System.currentTimeMillis();
            }catch(Exception e) {}
        }

        if (name == PacketNames.SNAIL_TEXTURES_INFO) {
            MinecraftClient.getInstance().setScreen(new SnailTextureInfoScreen());
        }

        if (name == PacketNames.PREVENT_GLIDING) {
            MainClient.preventGliding = value.equalsIgnoreCase("true");
        }

        if (name == PacketNames.TOGGLE_TIMER) {
            String key = ClientConfig.SESSION_TIMER.key;
            MainClient.clientConfig.setProperty(key, String.valueOf(!MainClient.SESSION_TIMER));
            MainClient.reloadConfig();
        }
        if (name == PacketNames.TABLIST_SHOW_EXACT) {
            MainClient.TAB_LIST_SHOW_EXACT_LIVES = value.equalsIgnoreCase("true");
        }
        if (name == PacketNames.SHOW_TOTEM) {
            ItemStack totemItem = Items.TOTEM_OF_UNDYING.getDefaultStack();
            if (value.equalsIgnoreCase("task") || value.equalsIgnoreCase("task_red")) {
                totemItem = AnimationUtils.getSecretLifeTotemItem(value.equalsIgnoreCase("task_red"));
            }
            MinecraftClient.getInstance().gameRenderer.showFloatingItem(totemItem);
        }
        if (name == PacketNames.PAST_LIFE_CHOOSE_TWIST) {
            MinecraftClient.getInstance().setScreen(new PastLifeChooseTwistScreen());
        }
    }

    public static void handleNumberPacket(NumberPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        double number = payload.number();

        int intNumber = (int) number;
        if (name == PacketNames.PLAYER_MIN_MSPT) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated min. player MSPT to {}", number);
            TimeDilation.MIN_PLAYER_MSPT = (float) number;
        }
        if (name == PacketNames.SNAIL_AIR) {
            MainClient.snailAir = intNumber;
            MainClient.snailAirTimestamp = System.currentTimeMillis();
        }
        if (name == PacketNames.FAKE_THUNDER && MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.setLightningTicksLeft(intNumber);
        }
    }

    public static void handleLongPacket(LongPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        long number = payload.number();

        if (name == PacketNames.SUPERPOWER_COOLDOWN) {
            MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP = number;
        }
        if (name == PacketNames.SHOW_VIGNETTE) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Showing vignette for {}", number);
            VignetteRenderer.showVignetteFor(0.35f, number);
        }
        if (name == PacketNames.MIMICRY_COOLDOWN) {
            MainClient.MIMICRY_COOLDOWN_TIMESTAMP = number;
        }
        if (nameStr.startsWith(PacketNames.PLAYER_INVISIBLE.getName())) {
            try {
                UUID uuid = UUID.fromString(nameStr.replaceFirst(PacketNames.PLAYER_INVISIBLE.getName(),""));
                if (number == 0) {
                    MainClient.invisiblePlayers.remove(uuid);
                }
                else {
                    MainClient.invisiblePlayers.put(uuid, number);
                }
            }catch(Exception ignored) {}
        }

        if (name == PacketNames.TIME_DILATION) {
            MainClient.TIME_DILATION_TIMESTAMP = number;
        }

        if (name == PacketNames.SESSION_TIMER) {
            MainClient.sessionTime = number;
            MainClient.sessionTimeLastUpdated = System.currentTimeMillis();
        }

        if (nameStr.startsWith(PacketNames.LIMITED_LIFE_TIMER.getName())) {
            MainClient.limitedLifeTimerColor = nameStr.replaceFirst(PacketNames.LIMITED_LIFE_TIMER.getName(),"");
            MainClient.limitedLifeLives = number;
            MainClient.limitedLifeTimeLastUpdated = System.currentTimeMillis();
        }

        if (name == PacketNames.CURSE_SLIDING) {
            MainClient.CURSE_SLIDING = number;
        }
    }

    public static void handlePlayerDisguise(PlayerDisguisePayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);

        String hiddenUUID = payload.hiddenUUID();
        String hiddenName = payload.hiddenName();
        String shownUUID = payload.shownUUID();
        String shownName = payload.shownName();

        if (name == PacketNames.PLAYER_DISGUISE) {
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
    public static void handleHandshake(HandshakePayload payload) {
        Main.LOGGER.info(TextUtils.formatString("[PACKET_CLIENT] Received handshake (from server): {{}, {}}", payload.modVersionStr(), payload.modVersion()));
        sendHandshake();
    }

    public static void sendHandshake() {
        String clientVersionStr = Main.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCompatibilityMin();

        int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
        int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

        HandshakePayload sendPayload = new HandshakePayload(clientVersionStr, clientVersion, clientCompatibilityStr, clientCompatibility);
        ClientPlayNetworking.send(sendPayload);
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sent handshake");
    }

    /*
        Sending
     */

    public static void sendConfigUpdate(String configType, String id, List<String> args) {
        ConfigPayload configPacket = new ConfigPayload(configType, id, -1, "", "", args);
        ClientPlayNetworking.send(configPacket);
    }

    public static void sendTriviaAnswer(int answer) {
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sending trivia answer: {}", answer);
        ClientPlayNetworking.send(new NumberPayload(PacketNames.TRIVIA_ANSWER.getName(), answer));
    }

    public static void sendHoldingJumpPacket() {
        ClientPlayNetworking.send(new StringPayload(PacketNames.HOLDING_JUMP.getName(), "true"));
    }

    public static void pressSuperpowerKey() {
        ClientPlayNetworking.send(new StringPayload(PacketNames.SUPERPOWER_KEY.getName(), "true"));
    }
    public static void pressRunCommandKey() {
        ClientUtils.runCommand(MainClient.RUN_COMMAND);
    }
    public static void pressOpenConfigKey() {
        ClientUtils.runCommand("/lifeseries config");
    }

    public static void sendStringPacket(PacketNames name, String value) {
        ClientPlayNetworking.send(new StringPayload(name.getName(), value));
    }

    public static void sendNumberPacket(PacketNames name, double value) {
        ClientPlayNetworking.send(new NumberPayload(name.getName(), value));
    }
}
