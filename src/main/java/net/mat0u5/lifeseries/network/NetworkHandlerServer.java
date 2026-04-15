package net.mat0u5.lifeseries.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.config.StringListManager;
import net.mat0u5.lifeseries.mixin.ServerLoginPacketListenerImplAccessor;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeVotingManager;
import net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager;
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
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.*;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.scores.PlayerTeam;
//? if > 1.20.5 {
import net.minecraft.network.DisconnectionDetails;
 //?}

//? if <= 1.20.3 {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import java.util.function.Function;
*///?} else {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?}

import java.util.*;

import static net.mat0u5.lifeseries.LifeSeries.*;

public class NetworkHandlerServer {
    public static final String preLoginPacketID = "preloginpacket";
    public static final List<UUID> handshakeSuccessful = new ArrayList<>();
    public static final List<UUID> preLoginHandshake = new ArrayList<>();
    public static RegistryOverrideBahaviours REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.LOGIN;
    public static boolean PRE_LOGIN_OVERRIDE_KICK = false;

    //? if <= 1.20.3 {
    /*public static final Map<Identifier, Function<FriendlyByteBuf, CustomPacketPayload>> PAYLOAD_READERS = new HashMap<>();
    static {
        PAYLOAD_READERS.put(NumberPayload.ID, NumberPayload::read);
        PAYLOAD_READERS.put(StringPayload.ID, StringPayload::read);
        PAYLOAD_READERS.put(StringListPayload.ID, StringListPayload::read);
        PAYLOAD_READERS.put(HandshakePayload.ID, HandshakePayload::read);
        PAYLOAD_READERS.put(TriviaQuestionPayload.ID, TriviaQuestionPayload::read);
        PAYLOAD_READERS.put(LongPayload.ID, LongPayload::read);
        PAYLOAD_READERS.put(PlayerDisguisePayload.ID, PlayerDisguisePayload::read);
        PAYLOAD_READERS.put(ConfigPayload.ID, ConfigPayload::read);
        PAYLOAD_READERS.put(SidetitlePacket.ID, SidetitlePacket::read);
        PAYLOAD_READERS.put(SnailTexturePacket.ID, SnailTexturePacket::read);
        PAYLOAD_READERS.put(VoteScreenPayload.ID, VoteScreenPayload::read);
        PAYLOAD_READERS.put(EmptyPayload.ID, EmptyPayload::read);
        PAYLOAD_READERS.put(BooleanPayload.ID, BooleanPayload::read);
        PAYLOAD_READERS.put(IntPayload.ID, IntPayload::read);
        PAYLOAD_READERS.put(LifeSkinsTexturePayload.ID, LifeSkinsTexturePayload::read);
    }
    *///?} else {
    public static final List<CustomPacketPayload.TypeAndCodec<? super RegistryFriendlyByteBuf, ? extends CustomPacketPayload>> PAYLOADS = List.of(
            new CustomPacketPayload.TypeAndCodec<>(NumberPayload.ID, NumberPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(StringPayload.ID, StringPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(StringListPayload.ID, StringListPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(HandshakePayload.ID, HandshakePayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(LongPayload.ID, LongPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(ConfigPayload.ID, ConfigPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(SidetitlePacket.ID, SidetitlePacket.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(SnailTexturePacket.ID, SnailTexturePacket.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(VoteScreenPayload.ID, VoteScreenPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(EmptyPayload.ID, EmptyPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(BooleanPayload.ID, BooleanPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(IntPayload.ID, IntPayload.CODEC)
            , new CustomPacketPayload.TypeAndCodec<>(LifeSkinsTexturePayload.ID, LifeSkinsTexturePayload.CODEC)
    );
    //?}

    public enum RegistryOverrideBahaviours {
        NEVER,
        ALWAYS,
        LOGIN,
        SEASON
    }

    public static void reload() {
        String registryOverrideBehaviour = LifeSeries.getMainConfig().getOrCreateProperty("registry_override_behavior", "login");
        if (registryOverrideBehaviour.equalsIgnoreCase("never")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.NEVER;
        else if (registryOverrideBehaviour.equalsIgnoreCase("always")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.ALWAYS;
        else if (registryOverrideBehaviour.equalsIgnoreCase("login")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.LOGIN;
        else if (registryOverrideBehaviour.equalsIgnoreCase("season")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.SEASON;
        else {
            LifeSeries.getMainConfig().setProperty("registry_override_behavior", "login");
            REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.LOGIN;
        }

        PRE_LOGIN_OVERRIDE_KICK = LifeSeries.getMainConfig().getOrCreateBoolean("pre_login_override_kick", false);
    }

    public static void initializeSimplePacketReceivers() {

        SimplePackets.TRIVIA_ANSWER.setServerReceive((player, payload) -> {
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received trivia answer (from {}): {}", player, payload.number()));
            if (currentSeason.getSeason() == Seasons.NICE_LIFE) {
                NiceLifeTriviaManager.handleAnswer(player, payload.number());
            }
            else {
                TriviaWildcard.handleAnswer(player, payload.number());
            }
        });

        SimplePackets.HOLDING_JUMP.setServerReceive((player, payload) -> {
            if (currentSeason.getSeason() == Seasons.WILD_LIFE && WildcardManager.isActiveWildcard(Wildcards.SIZE_SHIFTING)) {
                SizeShifting.onHoldingJump(player);
            }
        });
        SimplePackets.SUPERPOWER_KEY.setServerReceive((player, payload) -> {
            if (currentSeason.getSeason() == Seasons.WILD_LIFE) {
                SuperpowersWildcard.pressedSuperpowerKey(player);
            }
        });
        SimplePackets.TRANSCRIPT.setServerReceive((player, payload) -> {
            player.ls$message(SessionTranscript.getTranscriptMessage());
        });
        SimplePackets.SELECTED_WILDCARD.setServerReceive((player, payload) -> {
            if (PermissionManager.isAdmin(player)) {
                Wildcards wildcard = Wildcards.getFromString(payload.value());
                if (wildcard != null && wildcard != Wildcards.NULL) {
                    WildcardManager.chosenWildcard(wildcard);
                }
            }
        });
        SimplePackets.SET_SEASON.setServerReceive((player, payload) -> {
            if (PermissionManager.isAdmin(player) || currentSeason.getSeason() == Seasons.UNASSIGNED) {
                Seasons newSeason = Seasons.getSeasonFromStringName(payload.value());
                if (newSeason == Seasons.UNASSIGNED) return;
                boolean prevTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;
                if (LifeSeries.changeSeasonTo(newSeason.getId())) {
                    boolean currentTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;
                    PlayerUtils.broadcastMessage(ModifiableText.SEASON_CHANGE.get(payload.value()));
                    if (prevTickFreeze != currentTickFreeze) {
                        OtherUtils.setFreezeGame(currentTickFreeze);
                    }
                }
            }
        });
        SimplePackets.TRIPLE_JUMP.setServerReceive((player, payload) -> {
            if (currentSeason.getSeason() == Seasons.WILD_LIFE && SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP)) {
                Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
                if (power instanceof TripleJump tripleJump) {
                    tripleJump.isInAir = true;
                }
            }
        });
        SimplePackets.SUBMIT_VOTE.setServerReceive((player, payload) -> {
            NiceLifeVotingManager.handleVote(player, payload.value());
        });

        SimplePackets.SET_LIVES.setServerReceive((player, payload) -> {
            if (PermissionManager.isAdmin(player) && payload.value().size() >= 2) {
                ServerPlayer settingPlayer = PlayerUtils.getPlayer(payload.value().get(0));
                if (settingPlayer != null) {
                    try {
                        int lives = Integer.parseInt(payload.value().get(1));
                        settingPlayer.ls$setLives(lives);
                    }catch(Exception e) {
                        ScoreboardUtils.resetScore(settingPlayer, LivesManager.SCOREBOARD_NAME);
                    }
                }
                else {
                    try {
                        int lives = Integer.parseInt(payload.value().get(1));
                        livesManager.setScore(payload.value().get(0), lives);
                    }catch(Exception e) {
                        ScoreboardUtils.resetScore(payload.value().get(0), LivesManager.SCOREBOARD_NAME);
                    }
                }

                Season.reloadPlayerTeams = true;
            }
        });
        SimplePackets.SET_TEAM.setServerReceive((player, payload) -> {
            if (PermissionManager.isAdmin(player) && payload.value().size() >= 6) {
                List<String> teamNames = Arrays.asList(payload.value().get(0).split(";"));
                String packetTeamName = "lives_" + payload.value().get(1);
                String packetTeamDisplayName = payload.value().get(2);
                String packetTeamColor = payload.value().get(3);
                String packetAllowedKill = payload.value().get(4);
                String packetGainLifeKill = payload.value().get(5);

                ChatFormatting newTeamColor = ChatFormatting.getByName(packetTeamColor);
                if (newTeamColor == null) newTeamColor = ChatFormatting.WHITE;

                Integer allowedKill = null;
                Integer gainLife = null;
                try {
                    allowedKill = Integer.parseInt(packetAllowedKill);
                } catch(Exception e) {}
                try {
                    gainLife = Integer.parseInt(packetGainLifeKill);
                } catch(Exception e) {}

                boolean teamModified = false;
                for (PlayerTeam livesTeam : new ArrayList<>(livesManager.getLivesTeams().values())) {
                    String teamName = livesTeam.getName();
                    if (!teamNames.contains(teamName)) {
                        livesManager.updateTeamConfig(teamName, null, null);
                        TeamUtils.deleteTeam(teamName);
                        continue;
                    }
                    if (!teamName.equals(packetTeamName)) continue;

                    livesTeam.setColor(newTeamColor);
                    livesTeam.setDisplayName(Component.literal(packetTeamDisplayName).withStyle(newTeamColor));
                    livesManager.updateTeamConfig(teamName, allowedKill, gainLife);
                    teamModified = true;
                }
                if (!teamModified) {
                    TeamUtils.createTeam(packetTeamName, packetTeamDisplayName, newTeamColor);
                    livesManager.updateTeamConfig(packetTeamName, allowedKill, gainLife);
                }
                Season.reloadPlayerTeams = true;
            }
        });
        SimplePackets.CONFIG_SECRET_TASK.setServerReceive((player, payload) -> {
            if (PermissionManager.isAdmin(player)) {
                String type = payload.value().remove(0);
                try {
                    StringListManager manager = new StringListManager("./config/lifeseries/secretlife",type+"-tasks.json");
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    manager.setFileContent(gson.toJson(payload.value()));
                }catch(Exception ignored) {}
                TaskManager.reloadTasks();
            }
        });
        SimplePackets.CONFIG_TRIVIA.setServerReceive((player, payload) -> {
            if (PermissionManager.isAdmin(player)) {
                String type = payload.value().remove(0);
                List<TriviaQuestion> triviaQuestions = new ArrayList<>();
                for (String questionStr : payload.value()) {
                    try {
                        if (!questionStr.contains("~~~")) continue;
                        String[] splitQuestion = questionStr.split("~~~");
                        if (splitQuestion.length < 3) continue;
                        String questionText = splitQuestion[0];
                        int correctAnswerIndex = Integer.parseInt(splitQuestion[1]);
                        List<String> answers = new ArrayList<>();
                        for (int i = 2; i < splitQuestion.length; i++) {
                            answers.add(splitQuestion[i]);
                        }
                        triviaQuestions.add(new TriviaQuestion(questionText, answers, correctAnswerIndex-1));
                    }catch(Exception e) {}
                }
                TriviaQuestionManager manager = null;
                if (currentSeason.getSeason() == Seasons.WILD_LIFE) {
                    if (type.equalsIgnoreCase("easy")) {
                        manager = TriviaWildcard.easyTrivia;
                    }
                    else if (type.equalsIgnoreCase("normal")) {
                        manager = TriviaWildcard.normalTrivia;
                    }
                    else {
                        manager = TriviaWildcard.hardTrivia;
                    }
                }
                else {
                    manager = NiceLifeTriviaManager.triviaQuestions;
                }
                if (manager == null) return;
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    manager.setFileContent(gson.toJson(triviaQuestions));
                }catch(Exception ignored) {}
            }
        });

        /*

        SimplePackets._______.setServerReceive((player, payload) -> {
        });

        SimplePackets._______.setServerReceive((player, payload) -> );

         */
    }

    public static void onCustomPayload(CustomPacketPayload customPacketPayload, ServerPlayer player) {
        if (customPacketPayload instanceof HandshakePayload payload) {
            server.execute(() -> handleHandshakeResponse(player, payload));
        }
        if (customPacketPayload instanceof ConfigPayload payload) {
            server.execute(() -> handleConfigPacket(player, payload));
        }

        //Simple Packets
        if (customPacketPayload instanceof NumberPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
        if (customPacketPayload instanceof StringPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
        if (customPacketPayload instanceof StringListPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
        if (customPacketPayload instanceof LongPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
        if (customPacketPayload instanceof BooleanPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
        if (customPacketPayload instanceof EmptyPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
        if (customPacketPayload instanceof IntPayload payload) {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        }
    }

    public static void handlePreLogin(boolean understood, ServerLoginPacketListenerImpl handler) {
        GameProfile profile = ((ServerLoginPacketListenerImplAccessor) handler).getGameProfile();
        if (understood) {
            preLoginHandshake.add(OtherUtils.profileId(profile));
            LOGGER.info("Received pre-login packet from " + OtherUtils.profileName(profile));
        }
        else if (currentSeason.getSeason().requiresClient()) {
            LOGGER.info("Did not receive pre-login packet from " + OtherUtils.profileName(profile));
            if (!PRE_LOGIN_OVERRIDE_KICK) {
                handler.disconnect(getDisconnectClientText());
            }
        }
    }

    public static boolean updatedConfigThisTick = false;
    public static boolean configNeedsReload = false;
    public static void handleConfigPacket(ServerPlayer player, ConfigPayload payload) {
        if (PermissionManager.isAdmin(player)) {
            ConfigTypes configType = ConfigTypes.getFromString(payload.configType());
            String id = payload.configId();
            List<String> args = payload.args();
            if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received config update from {}: {{}, {}, {}}", player, configType, id, args));

            if (configType == ConfigTypes.EVENT_ENTRY && args.size() >= 2) {
                String command = args.get(0).strip();
                String canceled = args.get(1);
                seasonConfig.setOrRemoveProperty(id, command);
                seasonConfig.setOrRemoveProperty(id+"_canceled", canceled);
                updatedConfigThisTick = true;
            }
            else if (configType.parentString() && !args.isEmpty()) {
                seasonConfig.setProperty(id, args.get(0));
                updatedConfigThisTick = true;
            }
            else if (configType.parentBoolean() && !args.isEmpty()) {
                boolean boolValue = args.get(0).equalsIgnoreCase("true");
                seasonConfig.setProperty(id,String.valueOf(boolValue));
                updatedConfigThisTick = true;
                TaskScheduler.schedulePriorityTask(1, () -> {
                    ConfigManager.onUpdatedBoolean(id, boolValue);
                });
            }
            else if (configType.parentDouble() && !args.isEmpty()) {
                try {
                    double value = Double.parseDouble(args.get(0));
                    seasonConfig.setProperty(id, String.valueOf(value));
                    updatedConfigThisTick = true;
                }catch(Exception e){}
            }
            else if ((configType.parentInteger() && !args.isEmpty()) || (configType.parentNullableInteger() && !args.isEmpty())) {
                try {
                    int value = Integer.parseInt(args.get(0));
                    seasonConfig.setProperty(id, String.valueOf(value));
                    updatedConfigThisTick = true;
                    TaskScheduler.schedulePriorityTask(1, () -> {
                        ConfigManager.onUpdatedInteger(id, value);
                    });
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
        PlayerUtils.broadcastMessageToAdmins(ModifiableText.CONFIG_UPDATED.get());
        try {
            if (configNeedsReload) {
                OtherUtils.reloadServer();
            }
            else {
                LifeSeries.softReloadStart();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        updatedConfigThisTick = false;
        configNeedsReload = false;
    }

    public static void handleHandshakeResponse(ServerPlayer player, HandshakePayload payload) {
        String clientVersionStr = payload.modVersionStr();
        String clientCompatibilityStr = payload.compatibilityStr();
        String serverVersionStr = LifeSeries.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        if (!LifeSeries.ISOLATED_ENVIRONMENT) {
            int clientVersion = payload.modVersion();
            int clientCompatibility = payload.compatibility();
            int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
            int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

            //Check if client version is compatible with the server version
            if (clientVersion < serverCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "Update the client version to at least version "+serverCompatibilityStr);
                //? if <= 1.20.5 {
                /*player.connection.disconnect(disconnectText);
                *///?} else {
                player.connection.disconnect(new DisconnectionDetails(disconnectText));
                //?}
                return;
            }

            //Check if server version is compatible with the client version
            if (serverVersion < clientCompatibility) {
                Component disconnectText = Component.literal("[Life Series Mod] Server-Client version mismatch!\n" +
                        "The client version is too new for the server.\n" +
                        "Either update the server, or downgrade the client version to " + serverVersionStr);
                //? if <= 1.20.5 {
                /*player.connection.disconnect(disconnectText);
                *///?} else {
                player.connection.disconnect(new DisconnectionDetails(disconnectText));
                 //?}
                return;
            }
        }
        else {
            //Isolated enviroment -> mod versions must be IDENTICAL between client and server
            //Check if client version is the same as the server version
            if (!clientVersionStr.equalsIgnoreCase(serverVersionStr)) {
                Component disconnectText = Component.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                        "You must join with version "+serverCompatibilityStr);
                //? if <= 1.20.5 {
                /*player.connection.disconnect(disconnectText);
                *///?} else {
                player.connection.disconnect(new DisconnectionDetails(disconnectText));
                 //?}
                return;
            }
        }

        LifeSeries.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received handshake (from {}): {{}, {}}", player, payload.modVersionStr(), payload.modVersion()));
        handshakeSuccessful.add(player.getUUID());
        PlayerUtils.resendCommandTree(player);
    }

    /*
        Sending
     */
    public static void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        Objects.requireNonNull(player, "Server player cannot be null");
        Objects.requireNonNull(payload, "Payload cannot be null");

        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }
    
    public static void sendTriviaPacket(ServerPlayer player, String question, int difficulty, long timestamp, int timeToComplete, List<String> answers) {
        TriviaQuestionPayload triviaQuestionPacket = new TriviaQuestionPayload(question, difficulty, timestamp, timeToComplete, answers);
        if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Sending trivia question packet to {}): {{}, {}, {}, {}, {}}", player, question, difficulty, timestamp, timeToComplete, answers));

        sendPacket(player, triviaQuestionPacket);
    }

    public static void sendVoteScreenPacket(ServerPlayer player, String screenName, boolean requiresSleep, boolean closesWithEsc, boolean showTimer, List<String> players) {
        VoteScreenPayload voteScreenPayload = new VoteScreenPayload(screenName, requiresSleep, closesWithEsc, showTimer, players);
        sendPacket(player, voteScreenPayload);
    }

    public static void sendConfig(ServerPlayer player, ConfigPayload configPacket) {
        sendPacket(player, configPacket);
    }

    public static void sendHandshake(ServerPlayer player) {
        String serverVersionStr = LifeSeries.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
        int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

        HandshakePayload payload = new HandshakePayload(serverVersionStr, serverVersion, serverCompatibilityStr, serverCompatibility);
        sendPacket(player, payload);
        handshakeSuccessful.remove(player.getUUID());
        if (VersionControl.isDevVersion()) LifeSeries.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Sending handshake to {}: {{}, {}}", player, serverVersionStr, serverVersion));

    }

    public static void sendUpdatePacketTo(ServerPlayer player) {
        if (currentSeason instanceof WildLife) {
            SimplePackets.PLAYER_MIN_MSPT.target(player).sendToClient(TimeDilation.MIN_PLAYER_MSPT);

            List<String> activeWildcards = new ArrayList<>();
            for (Wildcards wildcard : WildcardManager.activeWildcards.keySet()) {
                activeWildcards.add(wildcard.getStringName());
            }
            SimplePackets.ACTIVE_WILDCARDS.target(player).sendToClient(activeWildcards);
        }
        SimplePackets.CURRENT_SEASON.target(player).sendToClient(currentSeason.getSeason().getId());
        SimplePackets.TABLIST_SHOW_EXACT.target(player).sendToClient(Season.TAB_LIST_SHOW_EXACT_LIVES);
        SimplePackets.TAB_LIST_LIVES_CUTOFF.target(player).sendToClient(LivesManager.MAX_TAB_NUMBER);
        SimplePackets.FIX_SIZECHANGING_BUGS.target(player).sendToClient(SizeShifting.FIX_SIZECHANGING_BUGS);
        SimplePackets.SIZESHIFTING_CHANGE.target(player).sendToClient(SizeShifting.SIZE_CHANGE_STEP * SizeShifting.SIZE_CHANGE_MULTIPLIER);

        SimplePackets.ANIMAL_DISGUISE_ARMOR.target(player).sendToClient(AnimalDisguise.SHOW_ARMOR);
        SimplePackets.ANIMAL_DISGUISE_HANDS.target(player).sendToClient(AnimalDisguise.SHOW_HANDS);
        SimplePackets.HUNGER_NON_EDIBLE.target(player).sendToClient(Hunger.nonEdibleStr);
        SimplePackets.SNOWY_NETHER.target(player).sendToClient(NiceLife.SNOWY_NETHER);

        if (Season.skyColor != null) {
            SimplePackets.SKYCOLOR.target(player).sendToClient(List.of(String.valueOf(Season.skyColorSetMode), String.valueOf((int)Season.skyColor.x), String.valueOf((int)Season.skyColor.y), String.valueOf((int)Season.skyColor.z)));
        }
        else {
            SimplePackets.SKYCOLOR.target(player).sendToClient(List.of(String.valueOf(Season.skyColorSetMode)));
        }
        if (Season.fogColor != null) {
            SimplePackets.FOGCOLOR.target(player).sendToClient(List.of(String.valueOf(Season.fogColorSetMode), String.valueOf((int)Season.fogColor.x), String.valueOf((int)Season.fogColor.y), String.valueOf((int)Season.fogColor.z)));
        }
        else {
            SimplePackets.FOGCOLOR.target(player).sendToClient(List.of(String.valueOf(Season.fogColorSetMode)));
        }
        if (Season.cloudColor != null) {
            SimplePackets.CLOUDCOLOR.target(player).sendToClient(List.of(String.valueOf(Season.cloudColorSetMode), String.valueOf((int)Season.cloudColor.x), String.valueOf((int)Season.cloudColor.y), String.valueOf((int)Season.cloudColor.z)));
        }
        else {
            SimplePackets.CLOUDCOLOR.target(player).sendToClient(List.of(String.valueOf(Season.cloudColorSetMode)));
        }

        SimplePackets.ADMIN_INFO.target(player).sendToClient(PermissionManager.isAdmin(player));
        SimplePackets.MOD_DISABLED.target(player).sendToClient(LifeSeries.MOD_DISABLED);
        Season.updateClientPlayerTeam(player);
        LifeSkinsManager.sendTeamNumUpdatesTo(player);
        if (blacklist != null) {
            List<ResourceKey<Enchantment>> lvl1ClampedEnchants = blacklist.getClampedEnchants().get(1);
            if (lvl1ClampedEnchants != null) {
                List<String> result = new ArrayList<>();
                for (ResourceKey<Enchantment> enchant : lvl1ClampedEnchants) {
                    //? if <= 1.21.9 {
                    /*result.add(enchant.location().toString());
                    *///?} else {
                    result.add(enchant.identifier().toString());
                    //?}
                }
                SimplePackets.LVL1_CLAMPED_ENCHANTS.target(player).sendToClient(result);
            }
        }
    }

    public static void sendUpdatePackets() {
        PlayerUtils.getAllPlayers().forEach(NetworkHandlerServer::sendUpdatePacketTo);
    }

    public static void sendPlayerDisguise(String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        PlayerDisguisePayload payload = new PlayerDisguisePayload(hiddenUUID, hiddenName, shownUUID, shownName);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            sendPacket(player, payload);
        }
    }

    public static void sendPlayerInvisible(UUID uuid, long timestamp) {
        SimplePackets.PLAYER_INVISIBLE.sendToClient(List.of(uuid.toString(), String.valueOf(timestamp)));
    }

    public static void sendVignette(ServerPlayer player, long durationMillis) {
        SimplePackets.SHOW_VIGNETTE.target(player).sendToClient(durationMillis);
    }

    public static void tryKickFailedHandshake(ServerPlayer player) {
        if (server == null) return;
        if (!currentSeason.getSeason().requiresClient()) return;
        if (wasHandshakeSuccessful(player)) return;
        //? if <= 1.20.5 {
        /*player.connection.disconnect(getDisconnectClientText());
        *///?} else {
        player.connection.disconnect(new DisconnectionDetails(getDisconnectClientText()));
         //?}
    }

    public static Component getDisconnectClientText() {
        return Component.literal("You must have the §2Life Series mod\n§l installed on the client§r§r§f to play "+currentSeason.getSeason().getName()+"!\n").append(
                Component.literal("§9§nThe Life Series mod is available on Modrinth."));
    }

    public static boolean wasHandshakeSuccessful(ServerPlayer player) {
        if (player == null) return false;
        return wasHandshakeSuccessful(player.getUUID());
    }

    public static boolean wasHandshakeSuccessful(UUID uuid) {
        if (uuid == null) return false;
        return handshakeSuccessful.contains(uuid) || preLoginHandshake.contains(uuid);
    }

    public static void sideTitle(ServerPlayer player, Component text) {
        sendPacket(player, new SidetitlePacket(text));
    }
}