package net.mat0u5.lifeseries.entity.pathfinder;

import net.mat0u5.lifeseries.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PathFinder extends AmbientEntity {
    public static final Identifier ID = Identifier.of(Main.MOD_ID, "pathfinder");
    public static final float MOVEMENT_SPEED = 0.35f;
    public static final float FLYING_SPEED = 0.3f;
    private int despawnTimer = 0;

    public PathFinder(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
        setInvulnerable(true);
        setNoGravity(true);
        setPersistent();
        setInvisible(true);
        noClip = true;
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        //? if <= 1.21 {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, FLYING_SPEED)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 150)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20);
        //?} else {
        /*return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10000)
                .add(EntityAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.FLYING_SPEED, FLYING_SPEED)
                .add(EntityAttributes.STEP_HEIGHT, 1)
                .add(EntityAttributes.FOLLOW_RANGE, 150)
                .add(EntityAttributes.ATTACK_DAMAGE, 20);
        *///?}
    }
    @Override
    public void tick() {
        setOnGround(true);
        despawnTimer++;
        if (despawnTimer > 100) {
            discard();
        }
    }

    public void setNavigation(boolean flying) {
        despawnTimer = 0;
        setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
        setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
        setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
        setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        if (flying) {
            moveControl = new FlightMoveControl(this, 20, true);
            navigation = new BirdNavigation(this, ls$getEntityWorld());
            navigation.setCanSwim(true);
        }
        else {
            moveControl = new MoveControl(this);
            navigation = new MobNavigation(this, ls$getEntityWorld());
            navigation.setCanSwim(true);
        }
    }

    public boolean canPathfind(Entity pathfindTo, boolean flying) {
        despawnTimer = 0;
        if (pathfindTo == null) return false;
        setNavigation(flying);
        Path path = navigation.findPathTo(pathfindTo, 0);
        if (path == null) return false;
        PathNode end = path.getEnd();
        if (end == null) return false;
        return end.getBlockPos().equals(pathfindTo.getBlockPos());
    }

    public void resetDespawnTimer() {
        despawnTimer = 0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
