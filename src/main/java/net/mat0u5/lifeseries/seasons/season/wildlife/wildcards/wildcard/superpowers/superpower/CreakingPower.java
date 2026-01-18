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
/*import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.phys.AABB;
import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.server;
*///?}

//? if = 1.21.2
/*import net.minecraft.core.particles.TargetColorParticleOption;*/
//? if >= 1.21.4
/*import net.minecraft.core.particles.TrailParticleOption;*/

public class CreakingPower extends ToggleableSuperpower {
    public static final List<UUID> allCreatedEntities = new ArrayList<>();

    private final List<String> createdTeams = new ArrayList<>();
    //? if >= 1.21.2 {
    /*private final List<Creaking> createdEntities = new ArrayList<>();
    *///?}

    public CreakingPower(ServerPlayer player) {
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
        ServerLevel playerLevel = player.ls$getServerLevel();

        PlayerTeam playerTeam = TeamUtils.getPlayerTeam(player);
        if (playerTeam == null) return;
        String newTeamName = "creaking_"+player.getScoreboardName();
        TeamUtils.deleteTeam(newTeamName);
        TeamUtils.createTeam(newTeamName, playerTeam.getDisplayName().getString(), playerTeam.getColor());
        createdTeams.add(newTeamName);

        //? if >= 1.21.2 {
        /*for (int i = 0; i < 3; i++) {
            BlockPos spawnPos =  LevelUtils.getCloseBlockPos(playerLevel, player.blockPosition(), 6, 3, true);
            Creaking creaking = EntityType.CREAKING.spawn(playerLevel, spawnPos, EntitySpawnReason.COMMAND);
            if (creaking != null) {
                creaking.setInvulnerable(true);
                creaking.addTag("creakingFromSuperpower");
                createdEntities.add(creaking);
                allCreatedEntities.add(creaking.getUUID());
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
        Vec3 entityPos = entity.position();
        player.ls$getServerLevel().sendParticles(
                ParticleTypes.EXPLOSION,
                entityPos.x(), entityPos.y(), entityPos.z(),
                1, 0, 0, 0, 0
        );
    }
    //? if >= 1.21.2 {
    /*public void spawnTrailParticles() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel level = player.ls$getServerLevel();
        if (level == null) return;
        for (Creaking creakingEntity : createdEntities) {
            if (creakingEntity.getRandom().nextInt(50)==0) {
                spawnTrailParticles(creakingEntity, 1, false);
            }
            if (creakingEntity.hurtTime > 0) {
                spawnTrailParticles(creakingEntity, 4, true);
            }
        }
    }

    public void spawnTrailParticles(Creaking creaking, int count, boolean towardsPlayer) {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel level = player.ls$getServerLevel();
        if (level == null) return;

        int i = towardsPlayer ? 16545810 : 6250335;
        RandomSource random = creaking.getRandom();

        for(double d = 0.0; d < count; d++) {
            AABB box = creaking.getBoundingBox();
            Vec3 vec3d = box.getMinPosition().add(random.nextDouble() * box.getXsize(), random.nextDouble() * box.getYsize(), random.nextDouble() * box.getZsize());
            Vec3 vec3d2 = player.position().add(random.nextDouble() - 0.5, random.nextDouble(), random.nextDouble() - 0.5);

            if (!towardsPlayer) {
                Vec3 vec3d3 = vec3d;
                vec3d = vec3d2;
                vec3d2 = vec3d3;
            }

            //? if = 1.21.2 {
            /^TargetColorParticleOption trailParticleEffect2 = new TargetColorParticleOption(vec3d2, i);
            level.sendParticles(trailParticleEffect2, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            ^///?} else if >= 1.21.4 {
            /^TrailParticleOption trailParticleEffect2 = new TrailParticleOption(vec3d2, i, random.nextInt(40) + 10);
            level.sendParticles(trailParticleEffect2, true, true, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            ^///?}
        }
    }

    public static void killUnassignedMobs() {
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> toKill = new ArrayList<>();
            level.getAllEntities().forEach(entity -> {
                if (!(entity instanceof Creaking)) return;
                if (allCreatedEntities.contains(entity.getUUID())) return;
                //? if <= 1.21.11 {
                if (!entity.getTags().contains("creakingFromSuperpower")) return;
                //?} else {
                /^if (!entity.entityTags().contains("creakingFromSuperpower")) return;
                ^///?}
                toKill.add(entity);
            });
            toKill.forEach(Entity::discard);
        }
    }
    *///?}
}
