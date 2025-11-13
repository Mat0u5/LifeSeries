package net.mat0u5.lifeseries.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.*;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.mixin.ServerLoginPacketListenerImplAccessor;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AnimalDisguise;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.TripleJump;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.ScoreHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class NetworkHandlerServer {
    public static final List<UUID> handshakeSuccessful = new ArrayList<>();
    public static final List<UUID> preLoginHandshake = new ArrayList<>();

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(NumberPayload.ID, NumberPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StringPayload.ID, StringPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StringListPayload.ID, StringListPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LongPayload.ID, LongPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SidetitlePacket.ID, SidetitlePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SnailTexturePacket.ID, SnailTexturePacket.CODEC);

        PayloadTypeRegistry.playC2S().register(NumberPayload.ID, NumberPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StringPayload.ID, StringPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StringListPayload.ID, StringListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LongPayload.ID, LongPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ConfigPayload.ID, ConfigPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SidetitlePacket.ID, SidetitlePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SnailTexturePacket.ID, SnailTexturePacket.CODEC);
    }
    public static void registerServerReceiver() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            sender.sendPacket(IdentifierHelper.mod("preloginpacket"), PacketByteBufs.create());
        });

        // Handle the response
        ServerLoginNetworking.registerGlobalReceiver(IdentifierHelper.mod("preloginpacket"),
                (server, handler, understood, buf, synchronizer, responseSender) -> {
                    if (understood) {
                        GameProfile profile = ((ServerLoginPacketListenerImplAccessor) handler).getGameProfile();
                        preLoginHandshake.add(OtherUtils.profileId(profile));
                        LOGGER.info("Received pre-login packet from " + OtherUtils.profileName(profile));
                    }
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleHandshakeResponse(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleNumberPacket(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleStringPacket(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleStringListPacket(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleConfigPacket(player, payload));
        });
    }

    public static boolean updatedConfigThisTick = false;
    public static boolean configNeedsReload = false;
    public static void handleConfigPacket(ServerPlayer player, ConfigPayload payload) {
        if (PermissionManager.isAdmin(player)) {
            ConfigTypes configType = ConfigTypes.getFromString(payload.configType());
            String id = payload.id();
            List<String> args = payload.args();
            if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received config update from {}: {{}, {}, {}}", player, configType, id, args));

            if (configType.parentString() && !args.isEmpty()) {
                seasonConfig.setProperty(id, args.getFirst());
                updatedConfigThisTick = true;
            }
            else if (configType.parentBoolean() && !args.isEmpty()) {
                boolean boolValue = args.getFirst().equalsIgnoreCase("true");
                seasonConfig.setProperty(id,String.valueOf(boolValue));
                updatedConfigThisTick = true;
                TaskScheduler.schedulePriorityTask(1, () -> {
                    ConfigManager.onUpdatedBoolean(id, boolValue);
                });
            }
            else if (configType.parentDouble() && !args.isEmpty()) {
                try {
                    double value = Double.parseDouble(args.getFirst());
                    seasonConfig.setProperty(id, String.valueOf(value));
                    updatedConfigThisTick = true;
                }catch(Exception e){}
            }
            else if ((configType.parentInteger() && !args.isEmpty()) || (configType.parentNullableInteger() && !args.isEmpty())) {
                try {
                    int value = Integer.parseInt(args.getFirst());
                    seasonConfig.setProperty(id, String.valueOf(value));
                    updatedConfigThisTick = true;
                }catch(Exception e){}
            }
            else if (configType.parentNullableInteger() && args.isEmpty()) {
                try {
                    seasonConfig.removeProperty(id);
                }catch(Exception e){}
            }

            if (updatedConfigThisTick && DefaultConfigValues.RELOAD_NEEDED.contains(id)) {
                configNeedsReload = true;
            }
        }
    }

    public static void onUpdatedConfig() {
        PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§7Config has been successfully updated."));
        if (configNeedsReload) {
            OtherUtils.reloadServer();
            //PlayerUtils.broadcastMessageToAdmins(Text.of("Run §7'/lifeseries reload'§r to apply all the changes."));
        }
        else {
            Main.softReloadStart();
        }
        updatedConfigThisTick = false;
        configNeedsReload = false;
    }

    public static void handleNumberPacket(ServerPlayer player, NumberPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        double value = payload.number();

        int intValue = (int) value;
        if (name == PacketNames.TRIVIA_ANSWER) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received trivia answer (from {}): {}", player, intValue));
            TriviaWildcard.handleAnswer(player, intValue);
        }
    }
    public static void handleStringPacket(ServerPlayer player, StringPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        String value = payload.value();

        if (name == PacketNames.HOLDING_JUMP && currentSeason.getSeason() == Seasons.WILD_LIFE && WildcardManager.isActiveWildcard(Wildcards.SIZE_SHIFTING)) {
            SizeShifting.onHoldingJump(player);
        }
        if (name == PacketNames.SUPERPOWER_KEY && currentSeason.getSeason() == Seasons.WILD_LIFE) {
            SuperpowersWildcard.pressedSuperpowerKey(player);
        }
        if (name == PacketNames.TRANSCRIPT) {
            player.sendSystemMessage(SessionTranscript.getTranscriptMessage());
        }
        if (PermissionManager.isAdmin(player)) {
            if (name == PacketNames.SELECTED_WILDCARD) {
                Wildcards wildcard = Wildcards.getFromString(value);
                if (wildcard != null && wildcard != Wildcards.NULL) {
                    WildcardManager.chosenWildcard(wildcard);
                }
            }
        }
        if (name == PacketNames.SET_SEASON) {
            if (PermissionManager.isAdmin(player) || currentSeason.getSeason() == Seasons.UNASSIGNED) {
                Seasons newSeason = Seasons.getSeasonFromStringName(value);
                if (newSeason == Seasons.UNASSIGNED) return;
                if (Main.changeSeasonTo(newSeason.getId())) {
                    PlayerUtils.broadcastMessage(TextUtils.formatLoosely("§aSuccessfully changed the season to {}.", value));
                }
            }
        }
        if (name == PacketNames.TRIPLE_JUMP) {
            if (currentSeason.getSeason() == Seasons.WILD_LIFE && SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP)) {
                Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
                if (power instanceof TripleJump tripleJump) {
                    tripleJump.isInAir = true;
                }
            }
        }
    }

    public static void handleStringListPacket(ServerPlayer player, StringListPayload payload) {
        String nameStr = payload.name();
        PacketNames name = PacketNames.fromName(nameStr);
        List<String> value = payload.value();

        if (PermissionManager.isAdmin(player)) {
            if (name == PacketNames.SET_LIVES && value.size() >= 2) {
                try {
                    int lives = Integer.parseInt(value.get(1));
                    livesManager.setScore(value.getFirst(), lives);
                }catch(Exception e) {
                    ScoreboardUtils.resetScore(ScoreHolder.forNameOnly(value.getFirst()), LivesManager.SCOREBOARD_NAME);

                }
                Season.reloadPlayerTeams = true;
            }
        }
    }

    public static void handleHandshakeResponse(ServerPlayer player, HandshakePayload payload) {
        String clientVersionStr = payload.modVersionStr();
        String clientCompatibilityStr = payload.compatibilityStr();
        String serverVersionStr = Main.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        if (!Main.ISOLATED_ENVIRONMENT) {
            int clientVersion = payload.modVersion();
            int clientCompatibility = payload.compatibility();
            int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
            int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

            //Check if client version is compatible with the server version
            if (clientVersion < serverCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "Update the client version to at least version "+serverCompatibilityStr);
                player.connection.disconnect(new DisconnectionDetails(disconnectText));
                return;
            }

            //Check if server version is compatible with the client version
            if (serverVersion < clientCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Server-Client version mismatch!\n" +
                        "The client version is too new for the server.\n" +
                        "Either update the server, or downgrade the client version to " + serverVersionStr);
                player.connection.disconnect(new DisconnectionDetails(disconnectText));
                return;
            }
        }
        else {
            //Isolated enviroment -> mod versions must be IDENTICAL between client and server
            //Check if client version is the same as the server version
            if (!clientVersionStr.equalsIgnoreCase(serverVersionStr)) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "You must join with version "+serverCompatibilityStr);
                player.connection.disconnect(new DisconnectionDetails(disconnectText));
                return;
            }
        }

        Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received handshake (from {}): {{}, {}}", player, payload.modVersionStr(), payload.modVersion()));
        handshakeSuccessful.add(player.getUUID());
        PlayerUtils.resendCommandTree(player);
    }

    /*
        Sending
     */
    public static void sendTriviaPacket(ServerPlayer player, String question, int difficulty, long timestamp, int timeToComplete, List<String> answers) {
        TriviaQuestionPayload triviaQuestionPacket = new TriviaQuestionPayload(question, difficulty, timestamp, timeToComplete, answers);
        if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Sending trivia question packet to {}): {{}, {}, {}, {}, {}}", player, question, difficulty, timestamp, timeToComplete, answers));

        ServerPlayNetworking.send(player, triviaQuestionPacket);
    }

    public static void sendConfig(ServerPlayer player, ConfigPayload configPacket) {
        ServerPlayNetworking.send(player, configPacket);
    }

    public static void sendHandshake(ServerPlayer player) {
        String serverVersionStr = Main.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
        int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

        HandshakePayload payload = new HandshakePayload(serverVersionStr, serverVersion, serverCompatibilityStr, serverCompatibility);
        ServerPlayNetworking.send(player, payload);
        handshakeSuccessful.remove(player.getUUID());
        if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Sending handshake to {}: {{}, {}}", player, serverVersionStr, serverVersion));

    }

    public static void sendStringPacket(ServerPlayer player, PacketNames name, String value) {
        StringPayload payload = new StringPayload(name.getName(), value);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendStringListPacket(ServerPlayer player, PacketNames name, List<String> value) {
        StringListPayload payload = new StringListPayload(name.getName(), value);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendStringListPackets(PacketNames name, List<String> value) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            StringListPayload payload = new StringListPayload(name.getName(), value);
            ServerPlayNetworking.send(player, payload);
        }
    }
    public static void sendNumberPackets(PacketNames name, double number) {
        NumberPayload payload = new NumberPayload(name.getName(), number);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendNumberPacket(ServerPlayer player, PacketNames name, double number) {
        if (player == null) return;
        NumberPayload payload = new NumberPayload(name.getName(), number);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendLongPacket(ServerPlayer player, PacketNames name, long number) {
        if (player == null) return;
        LongPayload payload = new LongPayload(name.getName(), number);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendLongPackets(PacketNames name, long number) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            sendLongPacket(player, name, number);
        }
    }

    public static void sendUpdatePacketTo(ServerPlayer player) {
        if (currentSeason instanceof WildLife) {
            sendNumberPacket(player, PacketNames.PLAYER_MIN_MSPT, TimeDilation.MIN_PLAYER_MSPT);

            List<String> activeWildcards = new ArrayList<>();
            for (Wildcards wildcard : WildcardManager.activeWildcards.keySet()) {
                activeWildcards.add(wildcard.getStringName());
            }
            sendStringPacket(player, PacketNames.ACTIVE_WILDCARDS, String.join("__", activeWildcards));
        }
        sendStringPacket(player, PacketNames.CURRENT_SEASON, currentSeason.getSeason().getId());
        sendStringPacket(player, PacketNames.TABLIST_SHOW_EXACT, String.valueOf(Season.TAB_LIST_SHOW_EXACT_LIVES));
        sendNumberPacket(player, PacketNames.TAB_LIVES_CUTOFF, LivesManager.MAX_TAB_NUMBER);
        sendStringPacket(player, PacketNames.FIX_SIZECHANGING_BUGS, String.valueOf(SizeShifting.FIX_SIZECHANGING_BUGS));
        sendNumberPacket(player, PacketNames.SIZESHIFTING_CHANGE, SizeShifting.SIZE_CHANGE_STEP * SizeShifting.SIZE_CHANGE_MULTIPLIER);

        sendStringPacket(player, PacketNames.ANIMAL_DISGUISE_ARMOR, String.valueOf(AnimalDisguise.SHOW_ARMOR));
        sendStringPacket(player, PacketNames.ANIMAL_DISGUISE_HANDS, String.valueOf(AnimalDisguise.SHOW_HANDS));
        sendStringListPacket(player, PacketNames.HUNGER_NON_EDIBLE, Hunger.nonEdibleStr);
    }

    public static void sendUpdatePackets() {
        PlayerUtils.getAllPlayers().forEach(NetworkHandlerServer::sendUpdatePacketTo);
    }

    public static void sendPlayerDisguise(String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        PlayerDisguisePayload payload = new PlayerDisguisePayload(PacketNames.PLAYER_DISGUISE.getName(), hiddenUUID, hiddenName, shownUUID, shownName);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendPlayerInvisible(UUID uuid, long timestamp) {
        LongPayload payload = new LongPayload(PacketNames.PLAYER_INVISIBLE.getName()+uuid.toString(), timestamp);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendVignette(ServerPlayer player, long durationMillis) {
        LongPayload payload = new LongPayload(PacketNames.SHOW_VIGNETTE.getName(), durationMillis);
        ServerPlayNetworking.send(player, payload);
    }

    public static void tryKickFailedHandshake(ServerPlayer player) {
        if (server == null) return;
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) return;
        if (wasHandshakeSuccessful(player)) return;
        Component disconnectText = Component.literal("You must have the §2Life Series mod\n§l installed on the client§r§r§f to play Wild Life!\n").append(
                Component.literal("§9§nThe Life Series mod is available on Modrinth."));
        player.connection.disconnect(new DisconnectionDetails(disconnectText));
    }

    public static boolean wasHandshakeSuccessful(ServerPlayer player) {
        if (player == null) return false;
        return wasHandshakeSuccessful(player.getUUID());
    }

    public static boolean wasHandshakeSuccessful(UUID uuid) {
        if (uuid == null) return false;
        return NetworkHandlerServer.handshakeSuccessful.contains(uuid);
    }

    public static void sideTitle(ServerPlayer player, Component text) {
        ServerPlayNetworking.send(player, new SidetitlePacket(text));
    }
}