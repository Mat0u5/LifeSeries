package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeConfig;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class Necromancy extends Superpower {
    private static final List<UUID> ressurectedPlayers = new ArrayList<>();
    private static final List<UUID> queuedRessurectedPlayers = new ArrayList<>();
    public static final List<UUID> clearedPlayers = new ArrayList<>();
    private List<UUID> perPlayerRessurections = new ArrayList<>();

    public Necromancy(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.NECROMANCY;
    }

    @Override
    public int getCooldownMillis() {
        return 300000;
    }

    @Override
    public void activate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;

        if (getDeadSpectatorPlayers().isEmpty()) {
            PlayerUtils.displayMessageToPlayer(player, Component.nullToEmpty("There are no dead players."), 80);
            return;
        }

        ServerLevel playerWorld = PlayerUtils.getServerWorld(player);
        playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_EMERGE, SoundSource.MASTER, 1, 1);

        List<ServerPlayer> affectedPlayers = playerWorld.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(10), playerEntity -> playerEntity.distanceTo(player) <= 10);
        MobEffectInstance blindness = new MobEffectInstance(MobEffects.BLINDNESS, 115, 0);
        for (ServerPlayer affectedPlayer : affectedPlayers) {
            affectedPlayer.addEffect(blindness);
        }

        for (ServerPlayer deadPlayer : getDeadSpectatorPlayers()) {
            queuedRessurectedPlayers.add(deadPlayer.getUUID());
        }

        TaskScheduler.scheduleTask(100, () -> {
            ServerPlayer updatedPlayer = getPlayer();
            if (updatedPlayer != null) {
                ServerLevel updatedPlayerWorld = PlayerUtils.getServerWorld(updatedPlayer);
                List<ServerPlayer> deadPlayers = getDeadSpectatorPlayers();
                for (ServerPlayer deadPlayer : deadPlayers) {
                    BlockPos tpTo = WorldUtils.getCloseBlockPos(updatedPlayerWorld, updatedPlayer.blockPosition(), 3, 2, true);
                    PlayerUtils.teleport(deadPlayer, updatedPlayerWorld, tpTo);
                    deadPlayer.setGameMode(GameType.SURVIVAL);
                    if (seasonConfig instanceof WildLifeConfig config) {
                        if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS.get(config) && !clearedPlayers.contains(deadPlayer.getUUID())) {
                            clearedPlayers.add(deadPlayer.getUUID());
                            deadPlayer.getInventory().clearContent();
                        }
                    }
                    AttributeUtils.setMaxPlayerHealth(deadPlayer, 8);
                    deadPlayer.setHealth(8);
                    WorldUtils.summonHarmlessLightning(deadPlayer);
                    ressurectedPlayers.add(deadPlayer.getUUID());
                    perPlayerRessurections.add(deadPlayer.getUUID());
                    queuedRessurectedPlayers.remove(deadPlayer.getUUID());
                }
            }
        });
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        List<UUID> deadAgain = new ArrayList<>();
        for (ServerPlayer player : livesManager.getDeadPlayers()) {
            if (player.isSpectator()) continue;
            UUID uuid = player.getUUID();
            if (perPlayerRessurections.contains(uuid) && ressurectedPlayers.contains(uuid)) {
                WorldUtils.summonHarmlessLightning(player);
                player.setGameMode(GameType.SPECTATOR);
                deadAgain.add(uuid);
            }
        }
        ressurectedPlayers.removeAll(deadAgain);
        perPlayerRessurections.removeAll(deadAgain);
        queuedRessurectedPlayers.removeAll(deadAgain);
    }

    @Override
    public void tick() {
        for (UUID uuid : new ArrayList<>(perPlayerRessurections)) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player != null && player.ls$isAlive()) {
                perPlayerRessurections.remove(uuid);
                ressurectedPlayers.remove(uuid);
                queuedRessurectedPlayers.remove(uuid);
                AttributeUtils.resetAttributesOnPlayerJoin(player);
            }
        }
    }

    public static List<ServerPlayer> getDeadSpectatorPlayers() {
        List<ServerPlayer> deadPlayers = new ArrayList<>();
        for (ServerPlayer player : livesManager.getDeadPlayers()) {
            if (!player.isSpectator()) continue;
            deadPlayers.add(player);
        }
        return deadPlayers;
    }

    public static boolean shouldBeIncluded() {
        return !livesManager.getDeadPlayers().isEmpty();
    }

    public static boolean isRessurectedPlayer(ServerPlayer player) {
        return ressurectedPlayers.contains(player.getUUID());
    }

    public static boolean preIsRessurectedPlayer(ServerPlayer player) {
        return queuedRessurectedPlayers.contains(player.getUUID()) || ressurectedPlayers.contains(player.getUUID());
    }
}
