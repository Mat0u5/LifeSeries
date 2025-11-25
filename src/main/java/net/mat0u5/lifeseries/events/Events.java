package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.utils.player.PlayerUtils.isFakePlayer;

//? if >= 1.21.2 {
/*import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.event.player.*;
*///?}

public class Events {
    public static boolean skipNextTickReload = false;
    public static boolean updatePlayerListsNextTick = false;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(Events::onReloadStart);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(Events::onReloadEnd);

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!(player instanceof ServerPlayer) || modDisabled()) {
                return InteractionResult.PASS; // Only handle server-side events
            }

            return Events.onBlockAttack((ServerPlayer) player, level, pos);
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

    private static void onReloadStart(MinecraftServer server, CloseableResourceManager resourceManager) {
        try {
            if (Main.modDisabled()) return;
            if (!Main.isLogicalSide()) return;
            Main.reloadStart();
        } catch(Exception e) {e.printStackTrace();}
    }

    private static void onReloadEnd(MinecraftServer server, CloseableResourceManager resourceManager, boolean success) {
        try {
            if (Main.modDisabled()) return;
            if (!Main.isLogicalSide()) return;
            Main.reloadEnd();
        } catch(Exception e) {e.printStackTrace();}
    }

    private static void onPlayerJoin(ServerPlayer player) {
        if (isFakePlayer(player)) return;

        try {
            playerStartJoining(player);
            if (Main.modDisabled()) return;
            currentSeason.onPlayerJoin(player);
            currentSeason.onUpdatedInventory(player);
            SessionTranscript.playerJoin(player);
            MorphManager.onPlayerJoin(player);
            DatapackIntegration.EVENT_PLAYER_JOIN.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        } catch(Exception e) {e.printStackTrace();}
    }

    private static void onPlayerFinishJoining(ServerPlayer player) {
        if (isFakePlayer(player) || Main.modDisabled()) return;

        try {
            UpdateChecker.onPlayerJoin(player);
            currentSeason.onPlayerFinishJoining(player);
            TaskScheduler.scheduleTask(10, () -> {
                NetworkHandlerServer.tryKickFailedHandshake(player);
                PlayerUtils.resendCommandTree(player);
            });
            MorphManager.onPlayerDisconnect(player);
            MorphManager.syncToPlayer(player);
        } catch(Exception e) {e.printStackTrace();}
    }

    private static void onPlayerDisconnect(ServerPlayer player) {
        if (Main.modDisabled()) return;
        if (isFakePlayer(player)) return;

        try {
            currentSeason.onPlayerDisconnect(player);
            SessionTranscript.playerLeave(player);
            NetworkHandlerServer.preLoginHandshake.remove(player.getUUID());
            DatapackIntegration.EVENT_PLAYER_LEAVE.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        } catch(Exception e) {e.printStackTrace();}
    }

    private static void onServerStopping(MinecraftServer server) {
        try {
            UpdateChecker.shutdownExecutor();
            if (Main.modDisabled()) return;
            currentSession.sessionEnd();
        }catch (Exception e) {e.printStackTrace();}
    }

    private static void onServerStarting(MinecraftServer server) {
        Main.server = server;
    }

    private static void onServerStart(MinecraftServer server) {
        try {
            Main.server = server;
            DatapackManager.onServerStarted(server);
            if (Main.modDisabled()) return;
            currentSeason.initialize();
            blacklist.reloadBlacklist();
            if (currentSeason.getSeason() == Seasons.DOUBLE_LIFE) {
                ((DoubleLife) currentSeason).loadSoulmates();
            }
        } catch(Exception e) {e.printStackTrace();}
    }

    private static void onServerTickEnd(MinecraftServer server) {
        try {
            skipNextTickReload = false;
            if (!Main.isLogicalSide()) return;
            checkPlayerFinishJoiningTick();
            if (Main.modDisabled()) return;
            if (updatePlayerListsNextTick) {
                updatePlayerListsNextTick = false;
                PlayerUtils.updatePlayerLists();
            }
            if (Main.currentSession != null) {
                Main.currentSession.tick(server);
            }
            //? if >= 1.20.3 {
            /*if (server.tickRateManager().isFrozen()) return;
            *///?}
            if (Main.currentSession != null) {
                currentSeason.tick(server);
            }
            PlayerUtils.onTick();
            if (NetworkHandlerServer.updatedConfigThisTick) {
                NetworkHandlerServer.onUpdatedConfig();
            }
            AdvancedDeathsManager.tick();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (Main.modDisabled()) return;
        if (isFakePlayer(entity)) return;
        try {
            if (!Main.isLogicalSide()) return;
            if (entity instanceof ServerPlayer player) {
                Events.onPlayerDeath(player, source);
                return;
            }
            currentSeason.onMobDeath(entity, source);
        } catch(Exception e) {e.printStackTrace();}
    }
    public static void onEntityDropItems(LivingEntity entity, DamageSource source) {
        if (isFakePlayer(entity)) return;
        try {
            if (!Main.isLogicalSide()) return;
            currentSeason.onEntityDropItems(entity, source);
        } catch(Exception e) {e.printStackTrace();}
    }

    public static void onPlayerDeath(ServerPlayer player, DamageSource source) {
        if (isExcludedPlayer(player)) return;

        try {
            if (!Main.isLogicalSide()) return;
            currentSeason.onPlayerDeath(player, source);
            AdvancedDeathsManager.onPlayerDeath(player);
        } catch(Exception e) {e.printStackTrace();}
    }

    public static InteractionResult onBlockUse(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (Main.modDisabled()) return InteractionResult.PASS;
        if (isFakePlayer(player)) return InteractionResult.PASS;

        if (player instanceof ServerPlayer serverPlayer &&
                level instanceof ServerLevel serverLevel && Main.isLogicalSide()) {
            try {
                if (currentSeason instanceof SecretLife) {
                    TaskManager.onBlockUse(serverPlayer, serverLevel, hitResult);
                }
                if (blacklist == null) return InteractionResult.PASS;
                return blacklist.onBlockUse(serverPlayer,serverLevel,hand,hitResult);
            } catch(Exception e) {
                e.printStackTrace();
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onItemUse(Player player, Level level, InteractionHand hand) {
        if (isFakePlayer(player) || modDisabled()) return InteractionResult.PASS;

        if (player instanceof ServerPlayer serverPlayer &&
                level instanceof ServerLevel serverLevel && Main.isLogicalSide()) {
            try {
                ItemStack itemStack = player.getItemInHand(hand);
                //? if >= 1.21.2 {
                /*if (itemStack.is(Items.FIREWORK_ROCKET)) {
                    if (ItemStackUtils.hasCustomComponentEntry(PlayerUtils.getEquipmentSlot(serverPlayer, 3), "FlightSuperpower")) {
                        if (!(LivingEntity.canGlideUsing(serverPlayer.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST) ||
                                LivingEntity.canGlideUsing(serverPlayer.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS) ||
                                LivingEntity.canGlideUsing(serverPlayer.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET))) {
                            return InteractionResult.FAIL;
                        }
                    }
                }
                *///?}
            } catch(Exception e) {
                e.printStackTrace();
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onBlockAttack(ServerPlayer player, Level level, BlockPos pos) {
        if (isFakePlayer(player)) return InteractionResult.PASS;

        try {
            if (!Main.isLogicalSide()) return InteractionResult.PASS;
            if (blacklist == null) return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.PASS;
            return blacklist.onBlockAttack(player, level,pos);
        } catch(Exception e) {
            e.printStackTrace();
            return InteractionResult.PASS;
        }
    }

    private static InteractionResult onRightClickEntity(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (isFakePlayer(player) || modDisabled()) return InteractionResult.PASS;

        try {
            if (!Main.isLogicalSide()) return InteractionResult.PASS;
            if (player instanceof ServerPlayer serverPlayer) {
                currentSeason.onRightClickEntity(serverPlayer, level, hand, entity, hitResult);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return InteractionResult.PASS;
    }
    private static InteractionResult onAttackEntity(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (isFakePlayer(player) || modDisabled()) return InteractionResult.PASS;

        try {
            if (!Main.isLogicalSide()) return InteractionResult.PASS;
            if (player instanceof ServerPlayer serverPlayer) {
                currentSeason.onAttackEntity(serverPlayer, level, hand, entity, hitResult);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return InteractionResult.PASS;
    }

    /*
        Non-events
     */
    public static final List<UUID> joiningPlayers = new ArrayList<>();
    private static final Map<UUID, Vec3> joiningPlayersPos = new HashMap<>();
    private static final Map<UUID, Float> joiningPlayersYaw = new HashMap<>();
    private static final Map<UUID, Float> joiningPlayersPitch = new HashMap<>();
    public static void playerStartJoining(ServerPlayer player) {
        NetworkHandlerServer.sendHandshake(player);
        NetworkHandlerServer.sendUpdatePacketTo(player);
        SnailSkins.sendTexturesTo(player);
        if (!joiningPlayers.contains(player.getUUID())) joiningPlayers.add(player.getUUID());
        joiningPlayersPos.put(player.getUUID(), player.position());
        joiningPlayersYaw.put(player.getUUID(), player.getYRot());
        joiningPlayersPitch.put(player.getUUID(), player.getXRot());
    }
    public static void checkPlayerFinishJoiningTick() {
        for (Map.Entry<UUID, Vec3> entry : joiningPlayersPos.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.position().equals(entry.getValue())) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUUID());
            return;
        }
        //Yaw
        for (Map.Entry<UUID, Float> entry : joiningPlayersYaw.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getYRot() == entry.getValue()) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUUID());
            return;
        }
        //Pitch
        for (Map.Entry<UUID, Float> entry : joiningPlayersPitch.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getXRot() == entry.getValue()) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUUID());
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
        if (entity instanceof ServerPlayer player) {
            if (player.ls$isWatcher()) {
                return true;
            }
        }
        return isFakePlayer(entity);
    }
}
