package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.fakeplayer.FakePlayer;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;
//? if <= 1.21.2
import net.fabricmc.fabric.api.event.player.*;
//? if >= 1.21.2
/*import net.fabricmc.fabric.api.event.player.*;*/

public class Events {
    public static boolean skipNextTickReload = false;
    public static boolean updatePlayerListsNextTick = false;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(Events::onReloadStart);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(Events::onReloadEnd);

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS; // Only handle server-side events
            }

            return Events.onBlockAttack((ServerPlayerEntity) player, world, pos);
        });
        UseBlockCallback.EVENT.register(Events::onBlockUse);
        //? if >= 1.21.2 {
        /*UseItemCallback.EVENT.register(Events::onItemUse);
        *///?}
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerDisconnect(handler.getPlayer()));
        ServerTickEvents.END_SERVER_TICK.register(Events::onServerTickEnd);

        ServerLivingEntityEvents.AFTER_DEATH.register(Events::onEntityDeath);
        UseEntityCallback.EVENT.register(Events::onRightClickEntity);
        AttackEntityCallback.EVENT.register(Events::onAttackEntity);
    }

    private static void onReloadStart(MinecraftServer server, LifecycledResourceManager resourceManager) {
        try {
            if (!Main.isLogicalSide()) return;
            Main.reloadStart();
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onReloadEnd(MinecraftServer server, LifecycledResourceManager resourceManager, boolean success) {
        try {
            if (!Main.isLogicalSide()) return;
            Main.reloadEnd();
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onPlayerJoin(ServerPlayerEntity player) {
        if (isFakePlayer(player)) return;

        try {
            playerStartJoining(player);
            currentSeason.onPlayerJoin(player);
            currentSeason.onUpdatedInventory(player);
            SessionTranscript.playerJoin(player);
            MorphManager.onPlayerJoin(player);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (isFakePlayer(player)) return;

        try {
            UpdateChecker.onPlayerJoin(player);
            currentSeason.onPlayerFinishJoining(player);
            TaskScheduler.scheduleTask(10, () -> {
                NetworkHandlerServer.tryKickFailedHandshake(player);
                PlayerUtils.resendCommandTree(player);
            });
            MorphManager.onPlayerDisconnect(player);
            MorphManager.syncToPlayer(player);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onPlayerDisconnect(ServerPlayerEntity player) {
        if (isFakePlayer(player)) return;

        try {
            currentSeason.onPlayerDisconnect(player);
            SessionTranscript.playerLeave(player);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onServerStopping(MinecraftServer server) {
        try {
            UpdateChecker.shutdownExecutor();
            currentSession.sessionEnd();
        }catch (Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onServerStarting(MinecraftServer server) {
        Main.server = server;
    }

    private static void onServerStart(MinecraftServer server) {
        try {
            Main.server = server;
            currentSeason.initialize();
            blacklist.reloadBlacklist();
            if (currentSeason.getSeason() == Seasons.DOUBLE_LIFE) {
                ((DoubleLife) currentSeason).loadSoulmates();
            }
            DatapackManager.onServerStarted(server);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onServerTickEnd(MinecraftServer server) {
        try {
            skipNextTickReload = false;
            if (!Main.isLogicalSide()) return;
            if (updatePlayerListsNextTick) {
                updatePlayerListsNextTick = false;
                PlayerUtils.updatePlayerLists();
            }
            checkPlayerFinishJoiningTick();
            if (server.getTickManager().isFrozen()) return;
            if (Main.currentSession != null) {
                Main.currentSession.tick(server);
                currentSeason.tick(server);
            }
            PlayerUtils.onTick();
            if (NetworkHandlerServer.updatedConfigThisTick) {
                NetworkHandlerServer.onUpdatedConfig();
            }
            AdvancedDeathsManager.tick();
        }catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public static void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (isFakePlayer(entity)) return;
        try {
            if (!Main.isLogicalSide()) return;
            if (entity instanceof ServerPlayerEntity player) {
                Events.onPlayerDeath(player, source);
                return;
            }
            currentSeason.onMobDeath(entity, source);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }
    public static void onEntityDropItems(LivingEntity entity, DamageSource source) {
        if (isFakePlayer(entity)) return;
        try {
            if (!Main.isLogicalSide()) return;
            currentSeason.onEntityDropItems(entity, source);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    public static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        if (isExcludedPlayer(player)) return;

        try {
            if (!Main.isLogicalSide()) return;
            currentSeason.onPlayerDeath(player, source);
            AdvancedDeathsManager.onPlayerDeath(player);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (isFakePlayer(player)) return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity serverPlayer &&
                world instanceof ServerWorld serverWorld && Main.isLogicalSide()) {
            try {
                if (currentSeason instanceof SecretLife) {
                    TaskManager.onBlockUse(
                            serverPlayer,
                            serverWorld,
                            hitResult);
                }
                if (blacklist == null) return ActionResult.PASS;
                return blacklist.onBlockUse(serverPlayer,serverWorld,hand,hitResult);
            } catch(Exception e) {
                Main.LOGGER.error(e.getMessage());
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }

    public static ActionResult onItemUse(PlayerEntity player, World world, Hand hand) {
        if (isFakePlayer(player)) return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity serverPlayer &&
                world instanceof ServerWorld serverWorld && Main.isLogicalSide()) {
            try {
                ItemStack itemStack = player.getStackInHand(hand);
                if (ItemStackUtils.hasCustomComponentEntry(PlayerUtils.getEquipmentSlot(serverPlayer, 3), "FlightSuperpower") &&
                        itemStack.isOf(Items.FIREWORK_ROCKET)) {
                    return ActionResult.FAIL;
                }
            } catch(Exception e) {
                Main.LOGGER.error(e.getMessage());
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }

    public static ActionResult onBlockAttack(ServerPlayerEntity player, World world, BlockPos pos) {
        if (isFakePlayer(player)) return ActionResult.PASS;

        try {
            if (!Main.isLogicalSide()) return ActionResult.PASS;
            if (blacklist == null) return ActionResult.PASS;
            if (world.isClient()) return ActionResult.PASS;
            return blacklist.onBlockAttack(player,world,pos);
        } catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
            return ActionResult.PASS;
        }
    }

    private static ActionResult onRightClickEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (isFakePlayer(player)) return ActionResult.PASS;

        try {
            if (!Main.isLogicalSide()) return ActionResult.PASS;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                currentSeason.onRightClickEntity(serverPlayer, world, hand, entity, hitResult);
            }
        } catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
        return ActionResult.PASS;
    }
    private static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (isFakePlayer(player)) return ActionResult.PASS;

        try {
            if (!Main.isLogicalSide()) return ActionResult.PASS;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                currentSeason.onAttackEntity(serverPlayer, world, hand, entity, hitResult);
            }
        } catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
        return ActionResult.PASS;
    }

    /*
        Non-events
     */
    public static final List<UUID> joiningPlayers = new ArrayList<>();
    private static final Map<UUID, Vec3d> joiningPlayersPos = new HashMap<>();
    private static final Map<UUID, Float> joiningPlayersYaw = new HashMap<>();
    private static final Map<UUID, Float> joiningPlayersPitch = new HashMap<>();
    public static void playerStartJoining(ServerPlayerEntity player) {
        NetworkHandlerServer.sendHandshake(player);
        NetworkHandlerServer.sendUpdatePacketTo(player);
        SnailSkinsServer.sendStoredImages(List.of(player));
        joiningPlayers.add(player.getUuid());
        joiningPlayersPos.put(player.getUuid(), player.getPos());
        joiningPlayersYaw.put(player.getUuid(), player.getYaw());
        joiningPlayersPitch.put(player.getUuid(), player.getPitch());
    }
    public static void checkPlayerFinishJoiningTick() {
        for (Map.Entry<UUID, Vec3d> entry : joiningPlayersPos.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getPos().equals(entry.getValue())) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUuid());
            return;
        }
        //Yaw
        for (Map.Entry<UUID, Float> entry : joiningPlayersYaw.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getYaw() == entry.getValue()) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUuid());
            return;
        }
        //Pitch
        for (Map.Entry<UUID, Float> entry : joiningPlayersPitch.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getPitch() == entry.getValue()) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUuid());
            return;
        }

    }

    public static void finishedJoining(UUID uuid) {
        joiningPlayers.remove(uuid);
        joiningPlayersPos.remove(uuid);
        joiningPlayersYaw.remove(uuid);
        joiningPlayersPitch.remove(uuid);
    }

    public static boolean isExcludedPlayer(Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            if (WatcherManager.isWatcher(player)) {
                return true;
            }
        }
        return isFakePlayer(entity);
    }
    public static boolean isFakePlayer(Entity entity) {
        return entity instanceof FakePlayer;
    }
}
