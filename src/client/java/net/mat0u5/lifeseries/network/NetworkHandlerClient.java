package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.config.ClientConfigGuiManager;
import net.mat0u5.lifeseries.config.ClientConfigNetwork;
import net.mat0u5.lifeseries.features.Morph;
import net.mat0u5.lifeseries.features.SnailSkinsClient;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.gui.other.ChooseWildcardScreen;
import net.mat0u5.lifeseries.gui.other.PastLifeChooseTwistScreen;
import net.mat0u5.lifeseries.gui.seasons.ChooseSeasonScreen;
import net.mat0u5.lifeseries.gui.seasons.SeasonInfoScreen;
import net.mat0u5.lifeseries.mixin.client.GuiAccessor;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.render.TextHud;
import net.mat0u5.lifeseries.render.VignetteRenderer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.ClientResourcePacks;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NetworkHandlerClient {
    public static void registerClientReceiver() {
        ClientLoginNetworking.registerGlobalReceiver(IdentifierHelper.mod("preloginpacket"),
                (client, handler, buf, listenerAdder) -> {
                    return CompletableFuture.completedFuture(
                            PacketByteBufs.create().writeBoolean(true)
                    );
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleNumberPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleStringPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleHandshake(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> Trivia.receiveTrivia(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleLongPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handlePlayerDisguise(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleConfigPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleStringListPacket(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(SidetitlePacket.ID, (payload, context) -> {
            Minecraft client = context.client();
            client.execute(() -> handleSidetitle(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(SnailTexturePacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                SnailSkinsClient.handleSnailTexture(payload.skinName(), payload.textureData());
            });
        });
    }

    public static void handleSidetitle(SidetitlePacket payload) {
        MainClient.sideTitle = payload.text();
        Minecraft client = Minecraft.getInstance();
        if (client.gui instanceof GuiAccessor hudAccessor) {
            TextHud.sideTitleRemainTicks = hudAccessor.ls$titleFadeInTicks() + hudAccessor.ls$titleStayTicks() + hudAccessor.ls$titleFadeOutTicks();
        }
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
                    //? if <= 1.21 {
                    morphType = BuiltInRegistries.ENTITY_TYPE.get(IdentifierHelper.parse(morphTypeStr));
                    //?} else {
                    /*morphType = BuiltInRegistries.ENTITY_TYPE.getValue(IdentifierHelper.parse(morphTypeStr));
                    *///?}
                }
                if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Received morph packet: {} ({})", morphType, morphUUID);
                MorphManager.setFromPacket(morphUUID, morphType);
            } catch (Exception e) {}
        }

        if (name == PacketNames.HUNGER_NON_EDIBLE) {
            Hunger.nonEdible.clear();
            for (String itemId : value) {
                if (!itemId.contains(":")) itemId = "minecraft:" + itemId;

                try {
                    var id = IdentifierHelper.parse(itemId);
                    ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);

                    //? if <= 1.21 {
                    Item item = BuiltInRegistries.ITEM.get(key);
                    //?} else {
                    /*Item item = BuiltInRegistries.ITEM.getValue(key);
                     *///?}
                    if (item != null) {
                        Hunger.nonEdible.add(item);
                    } else {
                        OtherUtils.throwError("[CONFIG] Invalid item: " + itemId);
                    }
                } catch (Exception e) {
                    OtherUtils.throwError("[CONFIG] Error parsing item ID: " + itemId);
                }
            }
        }
    }

    public static void handleConfigPacket(ConfigPayload payload) {
        ClientConfigNetwork.handleConfigPacket(payload, false);
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

        if (name == PacketNames.JUMP && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.jumpFromGround();
        }

        if (name == PacketNames.RESET_TRIVIA) {
            Trivia.resetTrivia();
        }

        if (name == PacketNames.SELECT_WILDCARDS) {
            Minecraft.getInstance().setScreen(new ChooseWildcardScreen());
        }

        if (name == PacketNames.CLEAR_CONFIG) {
            ClientConfigNetwork.load();
        }
        if (name == PacketNames.OPEN_CONFIG) {
            ClientConfigGuiManager.openConfig();
        }


        if (name == PacketNames.SELECT_SEASON) {
            Minecraft.getInstance().setScreen(new ChooseSeasonScreen(!value.isEmpty()));
        }
        if (name == PacketNames.SEASON_INFO) {
            Seasons season = Seasons.getSeasonFromStringName(value);
            if (season != Seasons.UNASSIGNED) Minecraft.getInstance().setScreen(new SeasonInfoScreen(season));
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
            ItemStack totemItem = Items.TOTEM_OF_UNDYING.getDefaultInstance();
            if (value.equalsIgnoreCase("task") || value.equalsIgnoreCase("task_red")) {
                totemItem = AnimationUtils.getSecretLifeTotemItem(value.equalsIgnoreCase("task_red"));
            }
            Minecraft.getInstance().gameRenderer.displayItemActivation(totemItem);
        }
        if (name == PacketNames.PAST_LIFE_CHOOSE_TWIST) {
            Minecraft.getInstance().setScreen(new PastLifeChooseTwistScreen());
        }
        if (name == PacketNames.FIX_SIZECHANGING_BUGS) {
            MainClient.FIX_SIZECHANGING_BUGS = value.equalsIgnoreCase("true");
        }
        if (name == PacketNames.ANIMAL_DISGUISE_ARMOR) {
            Morph.showArmor = value.equalsIgnoreCase("true");
        }
        if (name == PacketNames.ANIMAL_DISGUISE_HANDS) {
            Morph.showHandItems = value.equalsIgnoreCase("true");
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
        if (name == PacketNames.FAKE_THUNDER && Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.setSkyFlashTime(intNumber);
        }
        if (name == PacketNames.TAB_LIVES_CUTOFF) {
            MainClient.TAB_LIST_LIVES_CUTOFF = intNumber;
        }
        if (name == PacketNames.SIZESHIFTING_CHANGE) {
            MainClient.SIZESHIFTING_CHANGE = (float) number;
        }
        if (name == PacketNames.TRIVIA_TIMER) {
            Trivia.updateTicksPassed(intNumber);
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
        MainClient.serverHandshake = HandshakeStatus.RECEIVED;

        String serverVersionStr = payload.modVersionStr();
        String serverCompatibilityStr = payload.compatibilityStr();
        String clientVersionStr = Main.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCompatibilityMin();

        if (!Main.ISOLATED_ENVIRONMENT) {
            int serverVersion = payload.modVersion();
            int serverCompatibility = payload.compatibility();
            int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
            int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

            //Check if client version is compatible with the server version
            if (clientVersion < serverCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "Update the client version to at least version "+serverCompatibilityStr);
                ClientUtils.disconnect(disconnectText);
                return;
            }

            //Check if server version is compatible with the client version
            if (serverVersion < clientCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Server-Client version mismatch!\n" +
                        "The client version is too new for the server.\n" +
                        "Either update the server, or downgrade the client version to " + serverVersionStr);
                ClientUtils.disconnect(disconnectText);
                return;
            }
        }
        else {
            //Isolated enviroment -> mod versions must be IDENTICAL between client and server
            //Check if client version is the same as the server version
            if (!clientVersionStr.equalsIgnoreCase(serverVersionStr)) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "You must join with version "+serverCompatibilityStr);
                ClientUtils.disconnect(disconnectText);
                return;
            }
        }

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

    public static void sendStringListPacket(PacketNames name, List<String> value) {
        ClientPlayNetworking.send(new StringListPayload(name.getName(), value));
    }

    public static void sendNumberPacket(PacketNames name, double value) {
        ClientPlayNetworking.send(new NumberPayload(name.getName(), value));
    }
}
