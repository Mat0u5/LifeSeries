package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeConfig;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
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

import static net.mat0u5.lifeseries.Main.*;

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

        ServerLevel playerLevel = player.ls$getServerLevel();
        playerLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_EMERGE, SoundSource.MASTER, 1, 1);

        List<ServerPlayer> affectedPlayers = playerLevel.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(10), playerEntity -> playerEntity.distanceTo(player) <= 10);
        MobEffectInstance blindness = new MobEffectInstance(MobEffects.BLINDNESS, 115, 0);
        for (ServerPlayer affectedPlayer : affectedPlayers) {
            affectedPlayer.addEffect(blindness);
        }

        for (ServerPlayer deadPlayer : getDeadSpectatorPlayers()) {
            queuedRessurectedPlayers.add(deadPlayer.getUUID());
        }

        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            ServerPlayer updatedPlayer = getPlayer();
            if (updatedPlayer != null) {
                ServerLevel updatedPlayerLevel = updatedPlayer.ls$getServerLevel();
                List<ServerPlayer> deadPlayers = getDeadSpectatorPlayers();
                for (ServerPlayer deadPlayer : deadPlayers) {
                    BlockPos tpTo = LevelUtils.getCloseBlockPos(updatedPlayerLevel, updatedPlayer.blockPosition(), 3, 2, true);
                    LevelUtils.teleport(deadPlayer, updatedPlayerLevel, tpTo);
                    deadPlayer.setGameMode(GameType.SURVIVAL);
                    if (seasonConfig instanceof WildLifeConfig config) {
                        if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_FIRST_SPAWN_CLEAR_ITEMS.get(config) && !clearedPlayers.contains(deadPlayer.getUUID())) {
                            clearedPlayers.add(deadPlayer.getUUID());
                            deadPlayer.getInventory().clearContent();
                        }
                    }
                    AttributeUtils.setMaxPlayerHealth(deadPlayer, SuperpowersWildcard.ZOMBIES_HEALTH);
                    deadPlayer.setHealth(SuperpowersWildcard.ZOMBIES_HEALTH);
                    LevelUtils.summonHarmlessLightning(deadPlayer);
                    ressurectedPlayers.add(deadPlayer.getUUID());
                    perPlayerRessurections.add(deadPlayer.getUUID());
                    queuedRessurectedPlayers.remove(deadPlayer.getUUID());
                    currentSeason.reloadPlayerTeam(deadPlayer);
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
            UUID uuid = player.getUUID();
            if (perPlayerRessurections.contains(uuid) && ressurectedPlayers.contains(uuid)) {
                deadAgain.add(uuid);
                if (player.isSpectator()) continue;
                LevelUtils.summonHarmlessLightning(player);
                player.setGameMode(GameType.SPECTATOR);
            }
        }
        ressurectedPlayers.removeAll(deadAgain);
        perPlayerRessurections.removeAll(deadAgain);
        queuedRessurectedPlayers.removeAll(deadAgain);
        for (UUID uuid : deadAgain) {
            AttributeUtils.resetAttributesOnPlayerJoin(PlayerUtils.getPlayer(uuid));
        }
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

    public static void checkRessurectedPlayersReset() {
        if (ressurectedPlayers.isEmpty()) return;
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (SuperpowersWildcard.getSuperpower(player) == Superpowers.NECROMANCY) {
                return;
            }
        }
        List<UUID> copyPlayers = new ArrayList<>(ressurectedPlayers);
        ressurectedPlayers.clear();
        for (UUID uuid : copyPlayers) {
            AttributeUtils.resetAttributesOnPlayerJoin(PlayerUtils.getPlayer(uuid));
        }
    }

    public static boolean preIsRessurectedPlayer(ServerPlayer player) {
        return queuedRessurectedPlayers.contains(player.getUUID()) || ressurectedPlayers.contains(player.getUUID());
    }
}
