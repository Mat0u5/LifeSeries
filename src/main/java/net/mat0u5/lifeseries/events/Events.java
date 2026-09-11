package net.mat0u5.lifeseries.events;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretKeeper;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaSkins;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.util.SeasonChanger;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.LifeSkinsManager;
import net.mat0u5.lifeseries.utils.player.PlayerReference;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.matlib.events.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import static net.mat0u5.lifeseries.LifeSeries.*;
import static net.mat0u5.lifeseries.utils.player.PlayerUtils.isFakePlayer;

//? if >= 1.21.2 {
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
//?}

public class Events {
    public static boolean skipNextTickReload = false;
    public static boolean updatePlayerListsNextTick = false;

    public static void onReloadStart(MinecraftServer server, CloseableResourceManager resourceManager) {
        if (LifeSeries.modDisabled()) return;
        if (!LifeSeries.isLogicalSide()) return;
        if (!Events.skipNextTickReload) {
            SeasonChanger.reloadConfig();
            DatapackManager.onReloadStart();
        }
    }

    public static void onReloadEnd(MinecraftServer server, CloseableResourceManager resourceManager, boolean success) {
        if (LifeSeries.modDisabled()) return;
        if (!LifeSeries.isLogicalSide()) return;
        DatapackManager.onReloadEnd();
    }

    public static void onPlayerJoin(ServerPlayer player) {
        if (isFakePlayer(player)) return;

        playerStartJoining(player);
        if (LifeSeries.modDisabled()) return;
        LifeSkinsManager.onPlayerJoin(player);
        currentSeason.onPlayerJoin(player);
        currentSeason.onUpdatedInventory(player);
        SessionTranscript.playerJoin(player);
        MorphManager.onPlayerJoin(player);
        DatapackIntegration.EVENT_PLAYER_JOIN.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
    }

    public static void onPlayerFinishJoining(ServerPlayer player) {
        if (isFakePlayer(player) || LifeSeries.modDisabled()) return;

        try {
            UpdateChecker.onPlayerJoin(player);
            currentSeason.onPlayerFinishJoining(player);
            PlayerReference ref = PlayerReference.of(player);
            TaskScheduler.scheduleTask(10, () -> PlayerUtils.resendCommandTree(ref.get()));
            MorphManager.onPlayerDisconnect(player);
            MorphManager.syncToPlayer(player);
        } catch(Exception e) {e.printStackTrace();}
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        ProfileManager.onPlayerDisconnect(player);
        if (LifeSeries.modDisabled()) return;
        if (isFakePlayer(player)) return;

        currentSeason.onPlayerDisconnect(player);
        SessionTranscript.playerLeave(player);
        NetworkHandlerServer.preLoginHandshake.remove(player.getUUID());
        DatapackIntegration.EVENT_PLAYER_LEAVE.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
    }

    public static void onServerStopping(MinecraftServer server) {
        try {
            //ProfileManager.resetAll();
            UpdateChecker.shutdownExecutor();
            if (LifeSeries.modDisabled()) return;
            currentSession.sessionEnd();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LifeSeries.server = null;
            LifeSeries.serverThread = null;
            TaskScheduler.clearTasks();
        }
    }

    public static void onServerStarting(MinecraftServer server) {
        LifeSeries.server = server;
        LifeSeries.serverThread = Thread.currentThread();
    }

    public static void onServerStart(MinecraftServer server) {
        LifeSeries.server = server;
        DatapackManager.onServerStarted(server);
        if (LifeSeries.modDisabled()) return;
        currentSeason.initialize();
        blacklist.reloadBlacklist();
        if (LifeSeries.isSeason(Seasons.DOUBLE_LIFE)) {
            ((DoubleLife) currentSeason).loadSoulmates();
        }
    }

    public static void onServerTickEnd(MinecraftServer server) {
        skipNextTickReload = false;
        if (!LifeSeries.isLogicalSide()) return;
        checkPlayerFinishJoiningTick();
        if (LifeSeries.modDisabled()) return;
        if (updatePlayerListsNextTick) {
            updatePlayerListsNextTick = false;
            PlayerUtils.updatePlayerLists();
        }
        if (LifeSeries.currentSession != null) {
            LifeSeries.currentSession.tick(server);
        }
        //? if < 1.20.3 {
        /*boolean gameFrozen = false;
        *///?} else {
        boolean gameFrozen = server.tickRateManager().isFrozen();
        //?}
        if (!gameFrozen) {
            if (LifeSeries.currentSession != null) {
                currentSeason.tick(server);
            }
            if (NetworkHandlerServer.updatedConfigThisTick) {
                NetworkHandlerServer.onUpdatedConfig();
            }
            AdvancedDeathsManager.tick();
        }
        PlayerUtils.onTick();

        TaskScheduler.onTick(gameFrozen);
    }

    public static void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (LifeSeries.isClientOrDisabled() || isFakePlayer(entity)) return;
        if (entity instanceof ServerPlayer player) {
            Events.onPlayerDeath(player, source);
            return;
        }
        currentSeason.onMobDeath(entity, source);
    }

    public static EventResult onEntityDropItems(LivingEntity entity, DamageSource source) {
        if (isClientOrDisabled() || isFakePlayer(entity)) return EventResult.PASS;
        return currentSeason.modifyEntityDrops(entity, source);
    }

    public static void onPlayerDeath(ServerPlayer player, DamageSource source) {
        if (isExcludedPlayer(player)) return;

        if (!LifeSeries.isLogicalSide()) return;
        currentSeason.onPlayerDeath(player, source);
        AdvancedDeathsManager.onPlayerDeath(player);
    }

    public static InteractionResult onBlockUse(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult) {
        if (LifeSeries.modDisabled()) return InteractionResult.PASS;
        if (isFakePlayer(player)) return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel && LifeSeries.isLogicalSide()) {
            if (currentSeason instanceof SecretLife) {
                SecretKeeper.onBlockUse(player, serverLevel, hitResult);
            }
            if (blacklist == null) return InteractionResult.PASS;
            return blacklist.onBlockUse(player,serverLevel,hand,hitResult);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onItemUse(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand) {
        if (isFakePlayer(player) || modDisabled()) return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel && LifeSeries.isLogicalSide()) {
            ItemStack itemStack = player.getItemInHand(hand);
            //? if >= 1.21.2 {
            if (itemStack.is(Items.FIREWORK_ROCKET)) {
                if (ItemStackUtils.hasCustomComponentEntry(PlayerUtils.getEquipmentSlot(player, 3), "FlightSuperpower")) {
                    if (!(LivingEntity.canGlideUsing(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST) ||
                            LivingEntity.canGlideUsing(player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS) ||
                            LivingEntity.canGlideUsing(player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET))) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            //?}
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onBlockAttack(ServerPlayer player, ServerLevel level, BlockPos pos, Direction direction) {
        if (modDisabled()) return InteractionResult.PASS;
        if (isFakePlayer(player)) return InteractionResult.PASS;

        if (!LifeSeries.isLogicalSide()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.PASS;
        if (currentSeason instanceof SecretLife && SecretKeeper.preventBlockBreak(player, pos)) {
            return InteractionResult.FAIL;
        }
        if (blacklist == null) return InteractionResult.PASS;
        return blacklist.onBlockAttack(player, level,pos);
    }

    public static InteractionResult onRightClickEntity(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (isFakePlayer(player) || modDisabled()) return InteractionResult.PASS;

        try {
            if (!LifeSeries.isLogicalSide()) return InteractionResult.PASS;
            if (player instanceof ServerPlayer serverPlayer) {
                currentSeason.onRightClickEntity(serverPlayer, level, hand, entity, hitResult);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return InteractionResult.PASS;
    }
    public static InteractionResult onAttackEntity(Player player, Entity entity) {
        if (isFakePlayer(player) || modDisabled()) return InteractionResult.PASS;

        try {
            if (!LifeSeries.isLogicalSide()) return InteractionResult.PASS;
            if (player instanceof ServerPlayer serverPlayer) {
                currentSeason.onAttackEntity(serverPlayer, serverPlayer.level(), entity);
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
    public static final Map<UUID, Vec3> joiningPlayersPos = new HashMap<>();
    public static final Map<UUID, Float> joiningPlayersYaw = new HashMap<>();
    public static final Map<UUID, Float> joiningPlayersPitch = new HashMap<>();
    public static void playerStartJoining(ServerPlayer player) {
        NetworkHandlerServer.sendHandshake(player);
        NetworkHandlerServer.sendUpdatePacket(List.of(player));
        SnailSkins.sendTexturesTo(player);
        TriviaSkins.sendTexturesTo(player);
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
            if (((IPlayer) player).ls$isWatcher()) {
                return true;
            }
        }
        return isFakePlayer(entity);
    }
}
