package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.FlashbackCompatibility;
import net.mat0u5.lifeseries.compatibilities.ReplayModCompatibility;
import net.mat0u5.lifeseries.compatibilities.VoicechatClient;
import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.gui.other.UpdateInfoScreen;
import net.mat0u5.lifeseries.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.gui.trivia.VotingScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.render.TextHud;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.utils.ClientSounds;
import net.mat0u5.lifeseries.utils.ClientTaskScheduler;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

//? if <= 1.20.3 {
/*import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;
*///?} else {
import net.minecraft.core.particles.ColorParticleOption;
 //?}

public class ClientEvents {
    public static long onGroundFor = 0;
    private static boolean hasShownUpdateScreen = false;

    public static void registerClientEvents() {
        ClientPlayConnectionEvents.JOIN.register(ClientEvents::onClientJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(ClientEvents::onClientDisconnect);
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientEvents::onClientStart);
        ScreenEvents.AFTER_INIT.register(ClientEvents::onScreenOpen);
        ServerLifecycleEvents.SERVER_STARTING.register(ClientEvents::onServerStart);
        ServerLifecycleEvents.SERVER_STARTED.register(ClientEvents::onServerStart);
    }

    private static void onServerStart(MinecraftServer server) {
        checkReplayServer(server);
    }

    private static void checkReplayServer(MinecraftServer server) {
        boolean isReplay = false;
        if (CompatibilityManager.flashbackLoaded()) {
            if (FlashbackCompatibility.isReplayServer(server)) {
                Main.LOGGER.info("Detected Flashback Replay");
                isReplay = true;
            }
        }
        if (CompatibilityManager.replayModLoaded()) {
            if (ReplayModCompatibility.isReplayServer()) {
                Main.LOGGER.info("Detected ReplayMod Replay");
                isReplay = true;
            }
        }
        MainClient.isReplay = isReplay;
    }

    public static void onClientJoin(ClientPacketListener handler, PacketSender sender, Minecraft client) {
        checkReplayServer(null);
        ClientTaskScheduler.schedulePriorityTask(20, () -> {
            if (MainClient.serverHandshake == HandshakeStatus.WAITING) {
                Main.LOGGER.info("Disabling the Life Series on the client.");
                MainClient.serverHandshake = HandshakeStatus.NOT_RECEIVED;
            }
        });
        if (Main.modDisabled()) return;
    }

    public static void onClientDisconnect(ClientPacketListener handler, Minecraft client) {
        checkReplayServer(null);
        Main.LOGGER.info("Client disconnected from server, clearing some client data.");
        MainClient.resetClientData();
        if (Main.modDisabled()) return;
    }

    public static void onScreenOpen(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
        if (Main.modDisabled()) return;
        if (UpdateChecker.updateAvailable) {
            int disableVersion = MainClient.clientConfig.getOrCreateInt("ignore_update", 0);
            if (UpdateChecker.version == disableVersion && !UpdateChecker.TEST_UPDATE_FAKE && !UpdateChecker.TEST_UPDATE_LAST) return;

            if (screen instanceof TitleScreen && !hasShownUpdateScreen) {
                client.execute(() -> {
                    client.setScreen(new UpdateInfoScreen(UpdateChecker.versionName, UpdateChecker.versionDescription));
                    hasShownUpdateScreen = true;
                });
            }
        }
    }

    public static void onClientStart(Minecraft client) {
        if (Main.modDisabled()) return;
    }

    public static void onClientTickStart() {
        if (Main.modDisabled()) return;
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player != null) {
            sendPackets(player);
        }
    }

    public static void onClientTickEnd() {
        try {
            ClientTaskScheduler.onClientTick();
            if (Main.modFullyDisabled()) return;
            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;

            spawnInvisibilityParticles(client);

            if (Main.modDisabled()) return;

            if (player != null) {
                tryTripleJump(player);
                checkOnGroundFor(player);
                if (!player.isSleeping() && (client.screen instanceof EmptySleepScreen || client.screen instanceof NewQuizScreen || (client.screen instanceof VotingScreen votingScreen && votingScreen.requiresSleep))) {
                    client.setScreen(null);
                }
            }
            ClientKeybinds.tick();
            ClientSounds.updateSingleSoundVolumes();
            TextHud.tick();
            if (CompatibilityManager.voicechatLoaded()) {
                VoicechatClient.checkMute();
            }
        }catch(Exception ignored) {}
    }

    public static void checkOnGroundFor(LocalPlayer player) {
        if (!player.onGround()) {
            onGroundFor = 0;
        }
        else {
            onGroundFor++;
        }
    }

    public static void spawnInvisibilityParticles(Minecraft client) {
        if (client.level == null) return;
        //? if <= 1.20.3 {
        /*if (client.level.random.nextInt(30) != 0) return;
        *///?} else {
        if (client.level.getRandom().nextInt(15) != 0) return;
        //?}
        for (Player player : client.level.players()) {
            if (MainClient.invisiblePlayers.containsKey(player.getUUID())) {
                long time = MainClient.invisiblePlayers.get(player.getUUID());
                if (time > System.currentTimeMillis() || time == -1) {
                    ParticleEngine particleManager = client.particleEngine;

                    double x = player.getX() + (Math.random() - 0.5) * 0.6;
                    double y = player.getY() + Math.random() * 1.8;
                    double z = player.getZ() + (Math.random() - 0.5) * 0.6;

                    //? if <= 1.20.3 {
                    /*int color = 0x208891b5;
                    double d = (double)(color >> 16 & 255) / (double)255.0F;
                    double e = (double)(color >> 8 & 255) / (double)255.0F;
                    double f = (double)(color >> 0 & 255) / (double)255.0F;
                    particleManager.createParticle(ParticleTypes.ENTITY_EFFECT, x, y, z, d, e, f);
                    *///?} else {
                    ParticleOptions invisibilityParticle = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0x208891b5);
                    particleManager.createParticle(invisibilityParticle, x, y, z, 0, 0, 0);
                    //?}
                }
            }
        }
    }

    public static void sendPackets(LocalPlayer player) {
        //? if > 1.20.3 {
        if (MainClient.clientCurrentSeason == Seasons.WILD_LIFE && MainClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) {
            //? if <= 1.21 {
            /*boolean jumping = player.input.jumping;
            *///?} else {
            boolean jumping = player.input.keyPresses.jump();
             //?}
            if (jumping) {

                if (MainClient.FIX_SIZECHANGING_BUGS) {
                    EntityDimensions oldEntityDimensions = player.getDefaultDimensions(player.getPose()).scale(player.getScale());
                    AABB oldBoundingBox = oldEntityDimensions.makeBoundingBox(player.position());
                    Vec3 velocity = player.getDeltaMovement();

                    float newScale = player.getScale() + MainClient.SIZESHIFTING_CHANGE * 10;
                    EntityDimensions newEntityDimensions = player.getDefaultDimensions(player.getPose()).scale(newScale);
                    AABB newBoundingBox = newEntityDimensions.makeBoundingBox(player.position());

                    boolean oldSpaceEmpty = ClientUtils.isSpaceEmpty(player, oldBoundingBox, 0, 1.0E-5, 0);
                    boolean newSpaceEmpty = ClientUtils.isSpaceEmpty(player, newBoundingBox, 0, 1.0E-5, 0);
                    boolean predictedSpaceEmpty = ClientUtils.isSpaceEmpty(player, newBoundingBox, velocity.x, velocity.y + 1.0E-5, velocity.z);

                    if (player.input.getMoveVector().length() != 0) {
                        if (player.isInWall()) return;
                        if (!oldSpaceEmpty) return;
                        if (!newSpaceEmpty) return;
                        if (!predictedSpaceEmpty) return;
                    }
                }

                NetworkHandlerClient.sendHoldingJumpPacket();
            }
        }
        //?}
    }

    public static void onClientJump(Entity entity) {
        if (entity instanceof LocalPlayer) {
            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;
            if (player == null) return;
            jumpCooldown = 3;
        }
    }

    private static int jumpedInAir = 0;
    private static int jumpCooldown = 0;
    private static boolean lastJumping = false;
    private static void tryTripleJump(LocalPlayer player) {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        if (player.onGround()) {
            jumpedInAir = 0;
            return;
        }

        if (jumpedInAir >= 2) return;

        boolean shouldJump = false;
        //? if <= 1.21 {
        /*boolean holdingJump = player.input.jumping;
        *///?} else {
        boolean holdingJump = player.input.keyPresses.jump();
        //?}

        if (!lastJumping && holdingJump) {
            shouldJump = true;
        }
        lastJumping = holdingJump;
        if (!shouldJump) return;
        if (jumpCooldown > 0) return;

        if (!hasTripleJumpEffect(player)) return;
        if (!MainClient.tripleJumpActive) return;
        jumpedInAir++;
        player.jumpFromGround();
        //? if < 1.21 {
        /*player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BAT_TAKEOFF, SoundSource.MASTER, 0.25f, 1f, false);
        *///?} else {
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.MASTER, 0.25f, 1f, false);
        //?}
        SimplePackets.TRIPLE_JUMP.sendToServer(true);
    }

    private static boolean hasTripleJumpEffect(LocalPlayer player) {
        //? if <= 1.20.3 {
        /*for (Map.Entry<MobEffect, MobEffectInstance> entry : player.getActiveEffectsMap().entrySet()) {
        *///?} else {
        for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : player.getActiveEffectsMap().entrySet()) {
        //?}
            //? if <= 1.21.4 {
            /*if (entry.getKey() != MobEffects.JUMP) continue;
            *///?} else {
            if (entry.getKey() != MobEffects.JUMP_BOOST) continue;
            //?}
            MobEffectInstance jumpBoost = entry.getValue();
            if (jumpBoost.getAmplifier() != 2) continue;
            if (jumpBoost.getDuration() > 220 || jumpBoost.getDuration() < 200) continue;
            return true;
        }
        return false;
    }
}
