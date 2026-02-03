package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

//? if >= 1.20.5
import net.minecraft.core.component.DataComponents;
//? if <= 1.21 && > 1.20.3
//import net.minecraft.world.item.component.CustomModelData;
//? if >= 1.21.2 {
import java.awt.Color;
//?}

public class AnimationUtils {
    private static int spiralDuration = 175;
    public static void playTotemAnimation(ServerPlayer player) {
        //The animation lasts about 40 ticks.
        player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));
    }

    public static void playRealTotemAnimation(ServerPlayer player) {
        // Visible by other players too
        player.ls$getServerLevel().broadcastEntityEvent(player, (byte) 35);
    }

    public static void playSecretLifeTotemAnimation(ServerPlayer player, boolean red) {
        if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
            NetworkHandlerServer.sendStringPacket(player, PacketNames.SHOW_TOTEM, red ? "task_red" : "task");
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.parse("secretlife_task_totem")));
            return;
        }

        ItemStack totemItem = getSecretLifeTotemItem(red);
        ItemStack mainhandItem = player.getMainHandItem().copy();
        player.setItemInHand(InteractionHand.MAIN_HAND, totemItem);
        TaskScheduler.scheduleTask(1, () -> {
            player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.parse("secretlife_task_totem")));
        });
        TaskScheduler.scheduleTask(2, () -> {
            player.setItemInHand(InteractionHand.MAIN_HAND, mainhandItem);
        });
    }

    public static ItemStack getSecretLifeTotemItem(boolean red) {
        ItemStack totemItem = Items.TOTEM_OF_UNDYING.getDefaultInstance();
        ItemStackUtils.setCustomComponentBoolean(totemItem, "FakeTotem", true);
        //? if <= 1.20.3 {
        /*ItemStackUtils.setCustomComponentInt(totemItem, "CustomModelData", red ? 2 : 1);
        *///?} else if <= 1.21 {
        /*totemItem.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(red ? 2 : 1));
        *///?} else {
        totemItem.set(DataComponents.ITEM_MODEL, IdentifierHelper.mod(red ? "task_red_totem" : "task_totem"));
        //PlaySoundConsumeEffect playSoundEvent = new PlaySoundConsumeEffect(RegistryEntry.of(SoundEvent.of(Identifier.of("secretlife_task_totem"))));
        //totemItem.set(DataComponentTypes.DEATH_PROTECTION, new DeathProtectionComponent(List.of(playSoundEvent)));
        //?}
        return totemItem;
    }

    public static void createSpiral(ServerPlayer player, int duration) {
        spiralDuration = duration;
        TaskScheduler.scheduleTask(1, () -> startSpiral(player));
    }

    private static void startSpiral(ServerPlayer player) {
        TaskScheduler.scheduleTask(1, () -> runSpiralStep(player, 0));
    }

    private static void runSpiralStep(ServerPlayer player, int step) {
        if (player == null) return;

        processSpiral(player, step);
        processSpiral(player, step+1);
        processSpiral(player, step+2);
        processSpiral(player, step+3);

        if (step <= spiralDuration) {
            TaskScheduler.scheduleTask(1, () -> runSpiralStep(player, step + 4));
        }
    }

    private static void processSpiral(ServerPlayer player, int step) {
        ServerLevel level = player.ls$getServerLevel();
        double x = player.getX();
        double z = player.getZ();
        double yStart = player.getY();
        double height = 1.0;
        double radius = 0.8;
        int pointsPerCircle = 40;

        double angle = 2 * Math.PI * (step % pointsPerCircle) / pointsPerCircle + step / 4.0;
        double y = yStart + height * Math.sin(Math.PI * (step - 20) / 20) + 1;

        double offsetX = radius * Math.cos((float) angle);
        double offsetZ = radius * Math.sin((float) angle);

        level.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                x + offsetX, y, z + offsetZ,
                1, 0, 0, 0, 0
        );
    }

    public static void createGlyphAnimation(ServerLevel level, Vec3 target, int duration) {
        if (level == null || target == null || duration <= 0) return;

        double radius = 7.5; // Radius of the glyph starting positions

        for (int step = 0; step < duration; step++) {
            TaskScheduler.scheduleTask(step, () -> spawnGlyphParticles(level, target, radius));
        }
    }

    private static void spawnGlyphParticles(ServerLevel level, Vec3 target, double radius) {
        int particlesPerTick = 50; // Number of glyphs spawned per tick
        RandomSource random = level.getRandom();

        for (int i = 0; i < particlesPerTick; i++) {
            // Randomize starting position around the target block
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = radius * (random.nextDouble() * 0.5); // Random distance within radius

            double startX = target.x() + distance * Math.cos(angle);
            double startY = target.y() + random.nextDouble()*2+1; // Random height variation
            double startZ = target.z() + distance * Math.sin(angle);

            // Compute the velocity vector toward the target
            double targetX = target.x();
            double targetY = target.y();
            double targetZ = target.z();

            double dx = targetX - startX;
            double dy = targetY - startY;
            double dz = targetZ - startZ;

            // Normalize velocity to control particle speed
            double velocityScale = -50; // Adjust speed (lower values for slower movement)
            double vx = dx * velocityScale;
            double vy = dy * velocityScale;
            double vz = dz * velocityScale;

            // Spawn the particle with velocity
            level.sendParticles(
                    ParticleTypes.ENCHANT, // Glyph particle
                    startX, startY, startZ, // Starting position
                    0, // Number of particles to display as a burst (keep 0 for velocity to work)
                    vx, vy, vz, // Velocity components
                    0.2 // Spread (keep non-zero to activate velocity)
            );
        }
    }

    public static void spawnFireworkBall(ServerLevel level, Vec3 position, int duration, double radius, Vector3f color) {
        if (level == null || position == null || duration <= 0 || radius <= 0) return;

        RandomSource random = level.getRandom();

        for (int step = 0; step < duration; step++) {
            TaskScheduler.scheduleTask(step, () -> {
                // Spawn particles in a spherical pattern for the current step
                for (int i = 0; i < 50; i++) { // 50 particles per tick
                    double theta = random.nextDouble() * 2 * Math.PI; // Angle in the XY plane
                    double phi = random.nextDouble() * Math.PI; // Angle from the Z-axis
                    double r = radius * (0.8 + 0.2 * random.nextDouble()); // Slight variation in radius

                    // Spherical coordinates to Cartesian
                    double x = r * Math.sin(phi) * Math.cos(theta);
                    double y = r * Math.sin(phi) * Math.sin(theta);
                    double z = r * Math.cos(phi);

                    // Create the particle effect with the generated color and size
                    //? if <= 1.21 {
                    /*DustParticleOptions particleEffect = new DustParticleOptions(color, 1.0f);
                    *///?} else
                    DustParticleOptions particleEffect = new DustParticleOptions(new Color(color.x, color.y, color.z).getRGB(), 1.0f);

                    // Spawn particle with random offset
                    level.sendParticles(
                            particleEffect, // Colored particle effect
                            position.x() + x,
                            position.y() + y,
                            position.z() + z,
                            1, // Particle count
                            0, 0, 0, // No velocity
                            0 // No spread
                    );
                }
            });
        }
    }

    public static void spawnTeleportParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(
                ParticleTypes.PORTAL,
                pos.x, pos.y, pos.z,
                30,
                0, 0, 0,
                0.35
        );
    }
}
