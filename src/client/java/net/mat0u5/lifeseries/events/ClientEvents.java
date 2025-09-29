package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.dependencies.FlashbackCompatibility;
import net.mat0u5.lifeseries.features.SnailSkinsClient;
import net.mat0u5.lifeseries.gui.other.UpdateInfoScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.ClientResourcePacks;
import net.mat0u5.lifeseries.utils.ClientSounds;
import net.mat0u5.lifeseries.utils.ClientTaskScheduler;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;

//? if <= 1.21.6
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.util.math.Vec3d;
//? if >= 1.21.9
/*import net.minecraft.particle.TintedParticleEffect;*/

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
        boolean isReplay = false;
        if (DependencyManager.flashbackLoaded()) {
            if (FlashbackCompatibility.isReplayServer(server)) {
                Main.LOGGER.info("Detected Flashback Replay");
                isReplay = true;
            }
        }
        MainClient.isReplay = isReplay;
        if (Main.modDisabled()) return;
    }

    public static void onClientJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        ClientTaskScheduler.schedulePriorityTask(20, () -> {
            if (MainClient.serverHandshake == HandshakeStatus.WAITING) {
                Main.LOGGER.info("Disabling the Life Series on the client.");
                MainClient.serverHandshake = HandshakeStatus.NOT_RECEIVED;
            }
        });
        if (Main.modDisabled()) return;
    }

    public static void onClientDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        Main.LOGGER.info("Client disconnected from server, clearing some client data.");
        MainClient.resetClientData();
        if (Main.modDisabled()) return;
    }

    public static void onScreenOpen(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
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

    public static void onClientStart(MinecraftClient client) {
        if (Main.modDisabled()) return;
    }

    public static void onClientTickStart() {
        if (Main.modDisabled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player != null) {
            sendPackets(player);
        }
    }

    public static void onClientTickEnd() {
        try {
            ClientTaskScheduler.onClientTick();
            if (Main.modFullyDisabled()) return;
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            checkResourcepackReload();
            spawnInvisibilityParticles(client);

            if (Main.modDisabled()) return;

            if (player != null) {
                tryTripleJump(player);
                checkSnailInvisible(client, player);
                checkTriviaSnailInvisible(client, player);
                checkOnGroundFor(player);
            }
            ClientKeybinds.tick();
            ClientSounds.updateSingleSoundVolumes();
        }catch(Exception ignored) {}
    }

    public static void checkOnGroundFor(ClientPlayerEntity player) {
        if (!player.isOnGround()) {
            onGroundFor = 0;
        }
        else {
            onGroundFor++;
        }
    }

    public static void spawnInvisibilityParticles(MinecraftClient client) {
        if (client.world == null) return;
        if (client.world.random.nextInt(15) != 0) return;
        for (PlayerEntity player : client.world.getPlayers()) {
            if (MainClient.invisiblePlayers.containsKey(player.getUuid())) {
                long time = MainClient.invisiblePlayers.get(player.getUuid());
                if (time > System.currentTimeMillis() || time == -1) {
                    ParticleManager particleManager = client.particleManager;

                    double x = player.getX() + (Math.random() - 0.5) * 0.6;
                    double y = player.getY() + Math.random() * 1.8;
                    double z = player.getZ() + (Math.random() - 0.5) * 0.6;

                    //? if <= 1.21.6 {
                    ParticleEffect invisibilityParticle = EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0x208891b5);
                    //?} else {
                    /*ParticleEffect invisibilityParticle = TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0x208891b5);
                    *///?}
                    particleManager.addParticle(invisibilityParticle, x, y, z, 0, 0, 0);
                }
            }
        }
    }

    public static void sendPackets(ClientPlayerEntity player) {
        if (MainClient.clientCurrentSeason == Seasons.WILD_LIFE && MainClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) {
            //? if <= 1.21 {
            boolean jumping = player.input.jumping;
            //?} else {
            /*boolean jumping = player.input.playerInput.jump();
            *///?}
            if (jumping) {

                if (MainClient.FIX_SIZECHANGING_BUGS) {
                    EntityDimensions oldEntityDimensions = player.getBaseDimensions(player.getPose()).scaled(player.getScale());
                    Box oldBoundingBox = oldEntityDimensions.getBoxAt(WorldUtils.getEntityPos(player));
                    Vec3d velocity = player.getVelocity();

                    float newScale = player.getScale() + MainClient.SIZESHIFTING_CHANGE * 10;
                    EntityDimensions newEntityDimensions = player.getBaseDimensions(player.getPose()).scaled(newScale);
                    Box newBoundingBox = newEntityDimensions.getBoxAt(WorldUtils.getEntityPos(player));

                    boolean oldSpaceEmpty = ClientUtils.isSpaceEmpty(player, oldBoundingBox, 0, 1.0E-5, 0);
                    boolean newSpaceEmpty = ClientUtils.isSpaceEmpty(player, newBoundingBox, 0, 1.0E-5, 0);
                    boolean predictedSpaceEmpty = ClientUtils.isSpaceEmpty(player, newBoundingBox, velocity.x, velocity.y + 1.0E-5, velocity.z);

                    if (player.input.getMovementInput().length() != 0) {
                        if (player.isInsideWall()) return;
                        if (!oldSpaceEmpty) return;
                        if (!newSpaceEmpty) return;
                        if (!predictedSpaceEmpty) return;
                    }
                }

                NetworkHandlerClient.sendHoldingJumpPacket();
            }
        }
    }

    public static void onClientJump(Entity entity) {
        if (entity instanceof ClientPlayerEntity) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            jumpCooldown = 3;
        }
    }

    private static int jumpedInAir = 0;
    private static int jumpCooldown = 0;
    private static boolean lastJumping = false;
    private static void tryTripleJump(ClientPlayerEntity player) {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        if (player.isOnGround()) {
            jumpedInAir = 0;
            return;
        }

        if (jumpedInAir >= 2) return;

        boolean shouldJump = false;
        //? if <= 1.21 {
        boolean holdingJump = player.input.jumping;
        //?} else {
        /*boolean holdingJump = player.input.playerInput.jump();
        *///?}

        if (!lastJumping && holdingJump) {
            shouldJump = true;
        }
        lastJumping = holdingJump;
        if (!shouldJump) return;
        if (jumpCooldown > 0) return;

        if (!hasTripleJumpEffect(player)) return;
        jumpedInAir++;
        player.jump();
        player.playSoundToPlayer(SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.MASTER, 0.25f, 1f);
        NetworkHandlerClient.sendStringPacket(PacketNames.TRIPLE_JUMP,"");
    }

    private static boolean hasTripleJumpEffect(ClientPlayerEntity player) {
        for (Map.Entry<RegistryEntry<StatusEffect>, StatusEffectInstance> entry : player.getActiveStatusEffects().entrySet()) {
            if (entry.getKey() != StatusEffects.JUMP_BOOST) continue;
            StatusEffectInstance jumpBoost = entry.getValue();
            if (jumpBoost.getAmplifier() != 2) continue;
            if (jumpBoost.getDuration() > 220 || jumpBoost.getDuration() < 200) continue;
            return true;
        }
        return false;
    }

    private static int invisibleSnailFor = 0;
    public static void checkSnailInvisible(MinecraftClient client, ClientPlayerEntity player) {
        if (client.world == null) return;
        if (MainClient.snailPos == null) return;
        if (MainClient.snailPosTime == 0) return;
        if (player.squaredDistanceTo(MainClient.snailPos.toCenterPos()) > 2500) return;
        if (System.currentTimeMillis() - MainClient.snailPosTime > 2000) return;
        if (invisibleSnailFor > 60) {
            invisibleSnailFor = 0;
            NetworkHandlerClient.sendStringPacket(PacketNames.REQUEST_SNAIL_MODEL, "");
        }

        List<Entity> snailEntities = new ArrayList<>();
        for (DisplayEntity.ItemDisplayEntity entity : client.world.getEntitiesByClass(DisplayEntity.ItemDisplayEntity.class,
                new Box(MainClient.snailPos).expand(10), entity->true)) {
            if (MainClient.snailPartUUIDs.contains(entity.getUuid())) {
                snailEntities.add(entity);
            }
        }

        if (snailEntities.isEmpty()) {
            invisibleSnailFor++;
        }
        else {
            invisibleSnailFor = 0;
        }
    }

    private static int invisibleTriviaSnailFor = 0;
    public static void checkTriviaSnailInvisible(MinecraftClient client, ClientPlayerEntity player) {
        if (client.world == null) return;
        if (MainClient.triviaSnailPos == null) return;
        if (MainClient.triviaSnailPosTime == 0) return;
        if (player.squaredDistanceTo(MainClient.triviaSnailPos.toCenterPos()) > 2500) return;
        if (System.currentTimeMillis() - MainClient.triviaSnailPosTime > 2000) return;
        if (invisibleTriviaSnailFor > 60) {
            invisibleTriviaSnailFor = 0;
            NetworkHandlerClient.sendStringPacket(PacketNames.REQUEST_SNAIL_MODEL, "");
        }

        List<Entity> snailEntities = new ArrayList<>();
        for (DisplayEntity.ItemDisplayEntity entity : client.world.getEntitiesByClass(DisplayEntity.ItemDisplayEntity.class,
                new Box(MainClient.triviaSnailPos).expand(10), entity->true)) {
            if (MainClient.triviaSnailPartUUIDs.contains(entity.getUuid())) {
                snailEntities.add(entity);
            }
        }

        if (snailEntities.isEmpty()) {
            invisibleTriviaSnailFor++;
        }
        else {
            invisibleTriviaSnailFor = 0;
        }
    }

    public static void checkResourcepackReload() {
        if (SnailSkinsClient.skinReloadTicks <= 0) return;
        SnailSkinsClient.skinReloadTicks--;
        if (SnailSkinsClient.skinReloadTicks == 0) {
            ClientResourcePacks.enableClientResourcePack(ClientResourcePacks.SNAILS_RESOURCEPACK, true);
        }
    }
}
