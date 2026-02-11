package net.mat0u5.lifeseries.compatibilities.voicechat;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import de.maxhenkel.voicechat.api.packets.LocationalSoundPacket;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.compatibilities.voicechat.soundeffects.RadioEffect;
import net.mat0u5.lifeseries.compatibilities.voicechat.soundeffects.RoboticVoice;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Listening;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class VoicechatMain implements VoicechatPlugin {

    private static Map<UUID, VoicechatConnection> connectedPlayers = new HashMap();
    private static List<UUID> tempMutedPlayers = new ArrayList<>();

    private OpusEncoder encoder;
    private OpusDecoder decoder;
    private static VoicechatServerApi serverApi;

    @Override
    public String getPluginId() {
        return "lifeseries";
    }

    @Override
    public void initialize(VoicechatApi api) {
        Main.LOGGER.info("Life Series Voice Chat plugin initialized!");
        this.encoder = api.createEncoder();
        this.decoder = api.createDecoder();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted, 100);
        registration.registerEvent(MicrophonePacketEvent.class, this::onAudioPacket);
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        serverApi = event.getVoicechat();
    }
    
    private void onPlayerConnected(PlayerConnectedEvent event) {
        UUID uuid = event.getConnection().getPlayer().getUuid();
        connectedPlayers.put(uuid, event.getConnection());
    }

    public static boolean isConnectedToSVC(UUID uuid) {
        return connectedPlayers.containsKey(uuid);
    }


    public static void niceLifeTriviaStart(List<ServerPlayer> triviaPlayers) {
        if (serverApi == null) return;
        tempMutedPlayers.clear();

        for (ServerPlayer player : triviaPlayers) {
            if (connectedPlayers.containsKey(player.getUUID())) {
                if (!tempMutedPlayers.contains(player.getUUID())) {
                    tempMutedPlayers.add(player.getUUID());
                }
                SimplePackets.MIC_MUTED.target(player).sendToClient(true);
            }
        }
    }

    public static void niceLifeTick() {
        if (serverApi == null) return;
        for (Map.Entry<UUID, VoicechatConnection> entry : connectedPlayers.entrySet()) {
            UUID playerUUID = entry.getKey();
            if (tempMutedPlayers.contains(playerUUID)) {
                ServerPlayer player = PlayerUtils.getPlayer(playerUUID);
                if (player != null) {
                    if (!player.isSleeping()) {
                        tempMutedPlayers.remove(playerUUID);
                        SimplePackets.MIC_MUTED.target(player).sendToClient(false);
                    }
                }
            }
        }
    }

    private void onAudioPacket(MicrophonePacketEvent event) {
        mutedVoice(event);
        roboticVoice(event);
        listeningPower(event);
    }

    private void mutedVoice(MicrophonePacketEvent event) {
        VoicechatConnection connection = event.getSenderConnection();
        if (connection == null) return;
        UUID senderUUID = connection.getPlayer().getUuid();
        if (tempMutedPlayers.contains(senderUUID)) {
            event.cancel();
        }
    }
    private void roboticVoice(MicrophonePacketEvent event) {
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
            return;
        }
        if (!WildcardManager.isActiveWildcard(Wildcards.TRIVIA)) {
            return;
        }
        try {
            VoicechatConnection connection = event.getSenderConnection();
            if (connection == null) return;
            UUID senderUUID = connection.getPlayer().getUuid();
            if (!WildLifeTriviaHandler.cursedRoboticVoicePlayers.contains(senderUUID)) {
                return;
            }

            byte[] opusData = event.getPacket().getOpusEncodedData();
            byte[] processedOpusData = processOpusAudioForRobot(senderUUID, opusData);

            event.getPacket().setOpusEncodedData(processedOpusData);
        } catch (Exception e) {
            Main.LOGGER.error("Error processing audio", e);
        }
    }

    private void listeningPower(MicrophonePacketEvent event) {
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
            return;
        }
        if (Listening.listeningPlayers.isEmpty()) {
            return;
        }

        MicrophonePacket voicePacket = event.getPacket();
        VoicechatConnection connection = event.getSenderConnection();
        if (connection == null) {
            return;
        }

        UUID senderUUID = connection.getPlayer().getUuid();
        Position senderPosition = connection.getPlayer().getPosition();

        VoicechatServerApi api = event.getVoicechat();
        for (UUID uuid : Listening.listeningPlayers) {
            if (uuid == senderUUID) {
                continue;
            }
            VoicechatConnection playerConnection = api.getConnectionOf(uuid);
            ServerPlayer player = PlayerUtils.getPlayer(uuid);

            if (playerConnection == null || player == null) {
                continue;
            }
            Vec3 lookingAt = null;
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.LISTENING)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Listening listeningPower) {
                    lookingAt = listeningPower.lookingAt;
                }
            }

            if (lookingAt == null || api.getBroadcastRange() == 0) {
                continue;
            }

            Vec3 senderPos = new Vec3(senderPosition.getX(), senderPosition.getY(), senderPosition.getZ());
            double distanceFromSound = senderPos.distanceTo(lookingAt);
            double maxDistance = Math.min(api.getBroadcastRange(), Listening.MAX_RANGE);
            if (distanceFromSound > maxDistance) {
                continue;
            }
            double scaled = api.getBroadcastRange()/Listening.MAX_RANGE;
            if ((distanceFromSound*scaled) > player.position().distanceTo(senderPos)) {
                continue;
            }

            byte[] processedAudio = processOpusAudioForRadio(voicePacket.getOpusEncodedData().clone());
            LocationalSoundPacket processedPacket = voicePacket.locationalSoundPacketBuilder()
                    .position(api.createPosition(player.getX(), player.getY() + (distanceFromSound*scaled), player.getZ()))
                    .distance((float)api.getBroadcastRange())
                    .opusEncodedData(processedAudio)
                    .build();
            api.sendLocationalSoundPacketTo(playerConnection, processedPacket);
        }
    }

    private byte[] processOpusAudioForRadio(byte[] opusData) {
        try {
            short[] pcmData = decoder.decode(opusData);
            if (pcmData == null) {
                Main.LOGGER.warn("Failed to decode Opus data");
                return opusData;
            }

            short[] processedPcm = RadioEffect.applyEffect(pcmData);

            return encoder.encode(processedPcm);
        } catch (Exception e) {
            Main.LOGGER.error("Error processing Opus audio", e);
            return opusData;
        }
    }

    private byte[] processOpusAudioForRobot(UUID uuid, byte[] opusData) {
        try {
            short[] pcmData = decoder.decode(opusData);
            if (pcmData == null) {
                Main.LOGGER.warn("Failed to decode Opus data");
                return opusData;
            }

            short[] processedPcm = RoboticVoice.applyEffect(uuid, pcmData);

            return encoder.encode(processedPcm);
        } catch (Exception e) {
            Main.LOGGER.error("Error processing Opus audio", e);
            return opusData;
        }
    }
}