package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//? if >= 1.21.2 {
/*import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.server;
*///?}


public class Creaking extends ToggleableSuperpower {
    public static final List<UUID> allCreatedEntities = new ArrayList<>();

    private final List<String> createdTeams = new ArrayList<>();
    //? if >= 1.21.2 {
    /*private final List<CreakingEntity> createdEntities = new ArrayList<>();
    *///?}

    public Creaking(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.CREAKING;
    }

    @Override
    public void tick() {
        if (!active) return;
        //? if >= 1.21.2 {
        /*spawnTrailParticles();
        *///?}
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);

        Team playerTeam = TeamUtils.getPlayerTeam(player);
        if (playerTeam == null) return;
        String newTeamName = "creaking_"+player.getNameForScoreboard();
        TeamUtils.deleteTeam(newTeamName);
        TeamUtils.createTeam(newTeamName, playerTeam.getDisplayName().getString(), playerTeam.getColor());
        createdTeams.add(newTeamName);

        //? if >= 1.21.2 {
        /*for (int i = 0; i < 3; i++) {
            BlockPos spawnPos =  WorldUtils.getCloseBlockPos(playerWorld, player.getBlockPos(), 6, 3, true);
            CreakingEntity creaking = EntityType.CREAKING.spawn(playerWorld, spawnPos, SpawnReason.COMMAND);
            if (creaking != null) {
                creaking.setInvulnerable(true);
                creaking.addCommandTag("creakingFromSuperpower");
                createdEntities.add(creaking);
                allCreatedEntities.add(creaking.getUuid());
                makeFriendly(newTeamName, creaking, player);
            }
        }
        *///?}
    }

    @Override
    public void deactivate() {
        // Also gets triggered when the players team is changed.
        super.deactivate();
        //? if >= 1.21.2 {
        /*if (server != null) {
            createdEntities.forEach(Entity::discard);
            createdEntities.clear();
        }
        for (String teamAdded : createdTeams) {
            TeamUtils.deleteTeam(teamAdded);
        }
        createdTeams.clear();
        if (getPlayer() != null) {
            if (TeamUtils.getPlayerTeam(getPlayer()) == null) {
                currentSeason.reloadPlayerTeam(getPlayer());
            }
        }
        *///?}
    }

    @Override
    public int deactivateCooldownMillis() {
        return 10000;
    }

    private static void makeFriendly(String teamName, Entity entity, ServerPlayerEntity player) {
        TeamUtils.addEntityToTeam(teamName, player);
        TeamUtils.addEntityToTeam(teamName, entity);
        PlayerUtils.getServerWorld(player).spawnParticles(
                ParticleTypes.EXPLOSION,
                entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ(),
                1, 0, 0, 0, 0
        );
    }
    //? if >= 1.21.2 {
    /*public void spawnTrailParticles() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld world = PlayerUtils.getServerWorld(player);
        if (world == null) return;
        for (CreakingEntity creakingEntity : createdEntities) {
            if (creakingEntity.getRandom().nextInt(50)==0) {
                spawnTrailParticles(creakingEntity, 1, false);
            }
            if (creakingEntity.hurtTime > 0) {
                spawnTrailParticles(creakingEntity, 4, true);
            }
        }
    }

    public void spawnTrailParticles(CreakingEntity creaking, int count, boolean towardsPlayer) {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld world = PlayerUtils.getServerWorld(player);
        if (world == null) return;

        int i = towardsPlayer ? 16545810 : 6250335;
        Random random = world.random;

        for(double d = 0.0; d < count; d++) {
            Box box = creaking.getBoundingBox();
            Vec3d vec3d = box.getMinPos().add(random.nextDouble() * box.getLengthX(), random.nextDouble() * box.getLengthY(), random.nextDouble() * box.getLengthZ());
            Vec3d vec3d2 = player.getPos().add(random.nextDouble() - 0.5, random.nextDouble(), random.nextDouble() - 0.5);

            if (!towardsPlayer) {
                Vec3d vec3d3 = vec3d;
                vec3d = vec3d2;
                vec3d2 = vec3d3;
            }

            //? if = 1.21.2 {
            /^TrailParticleEffect trailParticleEffect2 = new TrailParticleEffect(vec3d2, i);
            world.spawnParticles(trailParticleEffect2, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            ^///?} else if >= 1.21.4 {
            /^TrailParticleEffect trailParticleEffect2 = new TrailParticleEffect(vec3d2, i, random.nextInt(40) + 10);
            world.spawnParticles(trailParticleEffect2, true, true, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            ^///?}
        }
    }

    public static void killUnassignedMobs() {
        if (server == null) return;
        for (ServerWorld world : server.getWorlds()) {
            List<Entity> toKill = new ArrayList<>();
            world.iterateEntities().forEach(entity -> {
                if (!(entity instanceof CreakingEntity)) return;
                if (allCreatedEntities.contains(entity.getUuid())) return;
                if (!entity.getCommandTags().contains("creakingFromSuperpower")) return;
                toKill.add(entity);
            });
            toKill.forEach(Entity::discard);
        }
    }
    *///?}
}
