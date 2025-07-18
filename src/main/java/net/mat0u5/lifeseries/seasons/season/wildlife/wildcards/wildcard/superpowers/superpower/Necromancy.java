package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeConfig;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUitls;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class Necromancy extends Superpower {
    public static final List<UUID> ressurectedPlayers = new ArrayList<>();
    private List<UUID> perPlayerRessurections = new ArrayList<>();

    public Necromancy(ServerPlayerEntity player) {
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
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;

        if (getDeadSpectatorPlayers().isEmpty()) {
            PlayerUtils.displayMessageToPlayer(player, Text.of("There are no dead players."), 80);
            return;
        }

        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
        playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1, 1);

        List<ServerPlayerEntity> affectedPlayers = playerWorld.getEntitiesByClass(ServerPlayerEntity.class, player.getBoundingBox().expand(10), playerEntity -> playerEntity.distanceTo(player) <= 10);
        StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 115, 0);
        for (ServerPlayerEntity affectedPlayer : affectedPlayers) {
            affectedPlayer.addStatusEffect(blindness);
        }

        TaskScheduler.scheduleTask(100, () -> {
            ServerPlayerEntity updatedPlayer = getPlayer();
            if (updatedPlayer != null) {
                ServerWorld updatedPlayerWorld = PlayerUtils.getServerWorld(updatedPlayer);
                List<ServerPlayerEntity> deadPlayers = getDeadSpectatorPlayers();
                for (ServerPlayerEntity deadPlayer : deadPlayers) {
                    BlockPos tpTo = WorldUitls.getCloseBlockPos(updatedPlayerWorld, updatedPlayer.getBlockPos(), 3, 2, true);
                    //? if <= 1.21 {
                    deadPlayer.teleport(updatedPlayerWorld, tpTo.getX(), tpTo.getY(), tpTo.getZ(), EnumSet.noneOf(PositionFlag.class), deadPlayer.getYaw(), deadPlayer.getPitch());
                    //?} else {
                    /*deadPlayer.teleport(updatedPlayerWorld, tpTo.getX(), tpTo.getY(), tpTo.getZ(), EnumSet.noneOf(PositionFlag.class), deadPlayer.getYaw(), deadPlayer.getPitch(), true);
                    *///?}
                    deadPlayer.changeGameMode(GameMode.SURVIVAL);
                    if (seasonConfig instanceof WildLifeConfig config) {
                        if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS.get(config)) {
                            deadPlayer.getInventory().clear();
                        }
                    }
                    AttributeUtils.setMaxPlayerHealth(deadPlayer, 8);
                    WorldUitls.summonHarmlessLightning(PlayerUtils.getServerWorld(deadPlayer), deadPlayer.getPos());
                    ressurectedPlayers.add(deadPlayer.getUuid());
                    perPlayerRessurections.add(deadPlayer.getUuid());
                }
            }
        });
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        List<UUID> deadAgain = new ArrayList<>();
        for (ServerPlayerEntity player : getDeadPlayers()) {
            if (player.isSpectator()) continue;
            UUID uuid = player.getUuid();
            if (perPlayerRessurections.contains(uuid) && ressurectedPlayers.contains(uuid)) {
                WorldUitls.summonHarmlessLightning(PlayerUtils.getServerWorld(player), player.getPos());
                player.changeGameMode(GameMode.SPECTATOR);
                deadAgain.add(uuid);
            }
        }
        ressurectedPlayers.removeAll(deadAgain);
        perPlayerRessurections.removeAll(deadAgain);
    }


    public static List<ServerPlayerEntity> getDeadSpectatorPlayers() {
        List<ServerPlayerEntity> deadPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (currentSeason.isAlive(player)) continue;
            if (!player.isSpectator()) continue;
            deadPlayers.add(player);
        }
        return deadPlayers;
    }

    public static List<ServerPlayerEntity> getDeadPlayers() {
        List<ServerPlayerEntity> deadPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (currentSeason.isAlive(player)) continue;
            deadPlayers.add(player);
        }
        return deadPlayers;
    }

    public static boolean shouldBeIncluded() {
        return !getDeadPlayers().isEmpty();
    }

    public static boolean isRessurectedPlayer(ServerPlayerEntity player) {
        return ressurectedPlayers.contains(player.getUuid());
    }

    public static boolean removeRessurectedPlayer(ServerPlayerEntity player) {
        return ressurectedPlayers.remove(player.getUuid());
    }
}
