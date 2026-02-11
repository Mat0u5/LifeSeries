package net.mat0u5.lifeseries.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.*;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
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
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.scores.PlayerTeam;
//? if > 1.20.5 {
import net.minecraft.network.DisconnectionDetails;
//?}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class NetworkHandlerServer {
    public static final List<UUID> handshakeSuccessful = new ArrayList<>();
    public static final List<UUID> preLoginHandshake = new ArrayList<>();
    public static RegistryOverrideBahaviours REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.LOGIN;
    public static boolean PRE_LOGIN_OVERRIDE_KICK = false;

    public enum RegistryOverrideBahaviours {
        NEVER,
        ALWAYS,
        LOGIN,
        SEASON
    }

    public static void reload() {
        String registryOverrideBehaviour = Main.getMainConfig().getOrCreateProperty("registry_override_behavior", "login");
        if (registryOverrideBehaviour.equalsIgnoreCase("never")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.NEVER;
        else if (registryOverrideBehaviour.equalsIgnoreCase("always")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.ALWAYS;
        else if (registryOverrideBehaviour.equalsIgnoreCase("login")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.LOGIN;
        else if (registryOverrideBehaviour.equalsIgnoreCase("season")) REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.SEASON;
        else {
            Main.getMainConfig().setProperty("registry_override_behavior", "login");
            REGISTRY_OVERRIDE_BEHAVIOR = RegistryOverrideBahaviours.LOGIN;
        }

        PRE_LOGIN_OVERRIDE_KICK = Main.getMainConfig().getOrCreateBoolean("pre_login_override_kick", false);
    }

    public static void initializeSimplePacketReceivers() {

        SimplePackets.TRIVIA_ANSWER.setServerReceive((player, payload) -> {
            if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received trivia answer (from {}): {}", player, payload.number()));
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
            player.sendSystemMessage(SessionTranscript.getTranscriptMessage());
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
                if (Main.changeSeasonTo(newSeason.getId())) {
                    boolean currentTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;
                    PlayerUtils.broadcastMessage(TextUtils.formatLoosely("§aSuccessfully changed the season to {}.", payload.value()));
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

    public static void registerPackets() {
        //? if > 1.20.3 {
        //? if <= 1.21.11 {
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
        PayloadTypeRegistry.playS2C().register(VoteScreenPayload.ID, VoteScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EmptyPayload.ID, EmptyPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BooleanPayload.ID, BooleanPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(IntPayload.ID, IntPayload.CODEC);

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
        PayloadTypeRegistry.playC2S().register(VoteScreenPayload.ID, VoteScreenPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(EmptyPayload.ID, EmptyPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BooleanPayload.ID, BooleanPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(IntPayload.ID, IntPayload.CODEC);
        //?} else {
        /*PayloadTypeRegistry.clientboundPlay().register(NumberPayload.ID, NumberPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StringPayload.ID, StringPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StringListPayload.ID, StringListPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(LongPayload.ID, LongPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ConfigPayload.ID, ConfigPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SidetitlePacket.ID, SidetitlePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SnailTexturePacket.ID, SnailTexturePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VoteScreenPayload.ID, VoteScreenPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EmptyPayload.ID, EmptyPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BooleanPayload.ID, BooleanPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(IntPayload.ID, IntPayload.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(NumberPayload.ID, NumberPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StringPayload.ID, StringPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StringListPayload.ID, StringListPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(LongPayload.ID, LongPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ConfigPayload.ID, ConfigPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SidetitlePacket.ID, SidetitlePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SnailTexturePacket.ID, SnailTexturePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VoteScreenPayload.ID, VoteScreenPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EmptyPayload.ID, EmptyPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(BooleanPayload.ID, BooleanPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(IntPayload.ID, IntPayload.CODEC);
        *///?}
        //?}
    }
    //? if <= 1.20.3 {
    /*public static void registerServerReceiver() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            sender.sendPacket(IdentifierHelper.mod("preloginpacket"), PacketByteBufs.create());
        });

        // Handle the response
        ServerLoginNetworking.registerGlobalReceiver(IdentifierHelper.mod("preloginpacket"),
                (server, handler, understood, buf, synchronizer, responseSender) -> {
                    handlePreLogin(understood, handler);
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (server, player, handler, buf, responseSender) -> {
            HandshakePayload payload = HandshakePayload.read(buf);
            server.execute(() -> handleHandshakeResponse(player, payload));
        });


        ServerPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (server, player, handler, buf, responseSender) -> {
            ConfigPayload payload = ConfigPayload.read(buf);
            server.execute(() -> handleConfigPacket(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (server, player, handler, buf, responseSender) -> {
            NumberPayload payload = NumberPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });

        ServerPlayNetworking.registerGlobalReceiver(StringPayload.ID, (server, player, handler, buf, responseSender) -> {
            StringPayload payload = StringPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });

        ServerPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (server, player, handler, buf, responseSender) -> {
            StringListPayload payload = StringListPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(LongPayload.ID, (server, player, handler, buf, responseSender) -> {
            LongPayload payload = LongPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(BooleanPayload.ID, (server, player, handler, buf, responseSender) -> {
            BooleanPayload payload = BooleanPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(EmptyPayload.ID, (server, player, handler, buf, responseSender) -> {
            EmptyPayload payload = EmptyPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(IntPayload.ID, (server, player, handler, buf, responseSender) -> {
            IntPayload payload = IntPayload.read(buf);
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(player, payload);
        });
    }
    *///?} else {
    public static void registerServerReceiver() {

        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            //? if <= 1.21.11 {
            sender.sendPacket(IdentifierHelper.mod("preloginpacket"), PacketByteBufs.create());
            //?} else {
            /*sender.sendPacket(IdentifierHelper.mod("preloginpacket"), FriendlyByteBufs.create());
            *///?}
        });

        // Handle the response
        ServerLoginNetworking.registerGlobalReceiver(IdentifierHelper.mod("preloginpacket"),
                (server, handler, understood, buf, synchronizer, responseSender) -> {
                    handlePreLogin(understood, handler);
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            server.execute(() -> handleHandshakeResponse(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            server.execute(() -> handleConfigPacket(player, payload));
        });

        //Simple Packets
        ServerPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(BooleanPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(EmptyPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(IntPayload.ID, (payload, context) -> {
            SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(payload.name());
            if (packet != null) packet.receiveServer(context.player(), payload);
        });
    }
    //?}

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
            String id = payload.id();
            List<String> args = payload.args();
            if (VersionControl.isDevVersion()) Main.LOGGER.info(TextUtils.formatString("[PACKET_SERVER] Received config update from {}: {{}, {}, {}}", player, configType, id, args));

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

    public static void sendVoteScreenPacket(ServerPlayer player, String screenName, boolean requiresSleep, boolean closesWithEsc, boolean showTimer, List<String> players) {
        VoteScreenPayload voteScreenPayload = new VoteScreenPayload(screenName, requiresSleep, closesWithEsc, showTimer, players);
        ServerPlayNetworking.send(player, voteScreenPayload);
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
        SimplePackets.MOD_DISABLED.target(player).sendToClient(Main.MOD_DISABLED);
    }

    public static void sendUpdatePackets() {
        PlayerUtils.getAllPlayers().forEach(NetworkHandlerServer::sendUpdatePacketTo);
    }

    public static void sendPlayerDisguise(String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        PlayerDisguisePayload payload = new PlayerDisguisePayload(hiddenUUID, hiddenName, shownUUID, shownName);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
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
        ServerPlayNetworking.send(player, new SidetitlePacket(text));
    }
}