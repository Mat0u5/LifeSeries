package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//? if >= 1.21.2 {
//?}


public class Creaking extends ToggleableSuperpower {
    public static final List<UUID> allCreatedEntities = new ArrayList<>();

    private final List<String> createdTeams = new ArrayList<>();
    //? if >= 1.21.2 {
    /*private final List<CreakingEntity> createdEntities = new ArrayList<>();
    *///?}

    public Creaking(ServerPlayer player) {
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
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel playerWorld = PlayerUtils.getServerWorld(player);

        PlayerTeam playerTeam = TeamUtils.getPlayerTeam(player);
        if (playerTeam == null) return;
        String newTeamName = "creaking_"+player.getScoreboardName();
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

    private static void makeFriendly(String teamName, Entity entity, ServerPlayer player) {
        TeamUtils.addEntityToTeam(teamName, player);
        TeamUtils.addEntityToTeam(teamName, entity);
        Vec3 entityPos = entity.ls$getEntityPos();
        PlayerUtils.getServerWorld(player).sendParticles(
                ParticleTypes.EXPLOSION,
                entityPos.x(), entityPos.y(), entityPos.z(),
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
            Vec3d vec3d2 = player.ls$getEntityPos().add(random.nextDouble() - 0.5, random.nextDouble(), random.nextDouble() - 0.5);

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
