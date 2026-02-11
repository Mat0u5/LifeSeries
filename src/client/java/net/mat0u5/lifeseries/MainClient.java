package net.mat0u5.lifeseries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.registries.ClientRegistries;
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.mat0u5.lifeseries.render.TextHud;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.interfaces.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.*;

//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
*///?} else {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
 //?}

public class MainClient implements ClientModInitializer, IClientHelper {

    public static Seasons clientCurrentSeason = Main.DEFAULT_SEASON;
    public static SessionStatus clientSessionStatus = SessionStatus.NOT_STARTED;
    public static List<Wildcards> clientActiveWildcards = new ArrayList<>();
    public static long TIME_DILATION_TIMESTAMP = 0;
    public static long SUPERPOWER_COOLDOWN_TIMESTAMP = 0;
    public static long MIMICRY_COOLDOWN_TIMESTAMP = 0;
    public static long CURSE_SLIDING = 0;
    public static boolean NICELIFE_SNOWY_NETHER = true;

    public static Map<String, String> playerDisguiseNames = new HashMap<>();
    public static Map<UUID, UUID> playerDisguiseUUIDs = new HashMap<>();
    public static Map<UUID, Long> invisiblePlayers = new HashMap<>();
    public static int snailAir = 300;
    public static long snailAirTimestamp = 0;
    public static boolean preventGliding = false;
    public static long sessionTime = 0;
    public static long sessionTimeLastUpdated = 0;

    public static String limitedLifeTimerColor = "";
    public static long limitedLifeTimeLastUpdated = 0;
    public static long limitedLifeLives = 0;
    public static Component sideTitle = null;
    public static boolean hideSleepDarkness = false;
    public static Vec3 skyColor = null;
    public static boolean skyColorSetMode = false;
    public static Vec3 fogColor = null;
    public static boolean fogColorSetMode = false;
    public static Vec3 cloudColor = null;
    public static boolean cloudColorSetMode = false;
    public static Vec3 cachedFogRenderColor = null;
    public static boolean isAdmin = false;
    public static boolean tripleJumpActive = false;
    public static boolean modDisabledServerSide = false;

    public static ClientConfig clientConfig;

    //Config
    public static boolean COLORBLIND_SUPPORT = false;
    public static boolean SESSION_TIMER = false;
    public static boolean TAB_LIST_SHOW_EXACT_LIVES = false;
    public static String RUN_COMMAND = "lifeseries config";
    public static boolean COLORED_HEARTS = false;
    public static boolean COLORED_HEARTS_HARDCORE_LAST_LIFE = true;
    public static boolean COLORED_HEARTS_HARDCORE_ALL_LIVES = false;
    public static int TAB_LIST_LIVES_CUTOFF = 4;
    public static boolean FIX_SIZECHANGING_BUGS = false;
    public static float SIZESHIFTING_CHANGE = 0;
    public static double TEXT_HUD_SCALE = 1.0;
    public static boolean NICE_LIFE_LESS_SNOW = true;

    public static boolean isReplay = false;
    public static HandshakeStatus serverHandshake = HandshakeStatus.WAITING;

    @Override
    public void onInitializeClient() {
        ClientRegistries.registerModStuff();
        NetworkHandlerClient.registerClientReceiver();
        NetworkHandlerClient.initializeSimplePacketReceivers();
        ClientRenderer.onInitialize();
        Main.setClientHelper(this);

        clientConfig = new ClientConfig();
        reloadConfig();
    }

    public static boolean isClientPlayer(UUID uuid) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        return client.player.getUUID().equals(uuid);
    }

    @Override
    public boolean isRunningIntegratedServer() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return false;
        return client.hasSingleplayerServer();
    }

    @Override
    public boolean isReplay() {
        return isReplay;
    }

    @Override
    public HandshakeStatus serverHandshake() {
        return serverHandshake;
    }

    @Override
    public boolean isMainClientPlayer(UUID uuid) {
        return isClientPlayer(uuid);
    }

    @Override
    public Seasons getCurrentSeason() {
        return clientCurrentSeason;
    }

    @Override
    public List<Wildcards> getActiveWildcards() {
        return clientActiveWildcards;
    }

    @Override
    //? if <= 1.20.3 {
    /*public void sendPacket(FabricPacket payload) {
    *///?} else {
    public void sendPacket(CustomPacketPayload payload) {
    //?}
        ClientPlayNetworking.send(payload);
    }

    @Override
    public boolean isDisabledServerSide() {
        return modDisabledServerSide;
    }

    public static void reloadConfig() {
        COLORBLIND_SUPPORT = ClientConfig.COLORBLIND_SUPPORT.get(clientConfig);
        SESSION_TIMER = ClientConfig.SESSION_TIMER.get(clientConfig);
        RUN_COMMAND = ClientConfig.RUN_COMMAND.get(clientConfig);
        if (RUN_COMMAND.startsWith("/")) {
            RUN_COMMAND = RUN_COMMAND.substring(1);
        }
        COLORED_HEARTS = ClientConfig.COLORED_HEARTS.get(clientConfig);
        COLORED_HEARTS_HARDCORE_LAST_LIFE = ClientConfig.COLORED_HEARTS_HARDCORE_LAST_LIFE.get(clientConfig);
        COLORED_HEARTS_HARDCORE_ALL_LIVES = ClientConfig.COLORED_HEARTS_HARDCORE_ALL_LIVES.get(clientConfig);

        TEXT_HUD_SCALE = ClientConfig.TEXT_HUD_SCALE.get(clientConfig);

        NICE_LIFE_LESS_SNOW = ClientConfig.NICE_LIFE_LESS_SNOW.get(clientConfig);
    }

    public static void resetClientData() {
        clientCurrentSeason = Seasons.UNASSIGNED;
        clientSessionStatus = SessionStatus.NOT_STARTED;
        clientActiveWildcards = new ArrayList<>();
        TIME_DILATION_TIMESTAMP = 0;
        SUPERPOWER_COOLDOWN_TIMESTAMP = 0;
        MIMICRY_COOLDOWN_TIMESTAMP = 0;
        CURSE_SLIDING = 0;


        playerDisguiseNames = new HashMap<>();
        playerDisguiseUUIDs = new HashMap<>();
        invisiblePlayers = new HashMap<>();
        snailAir = 300;
        snailAirTimestamp = 0;
        preventGliding = false;
        sessionTime = 0;
        sessionTimeLastUpdated = 0;

        limitedLifeTimerColor = "";
        limitedLifeTimeLastUpdated = 0;
        limitedLifeLives = 0;
        serverHandshake = HandshakeStatus.WAITING;
        sideTitle = null;
        TextHud.sideTitleRemainTicks = 0;

        skyColor = null;
        skyColorSetMode = false;
        fogColor = null;
        fogColorSetMode = false;
        cloudColor = null;
        cloudColorSetMode = false;
        isAdmin = false;
        tripleJumpActive = false;
        modDisabledServerSide = false;

        MorphManager.resetMorphs();
    }
}
