package net.mat0u5.lifeseries.entity.pathfinder;

import net.mat0u5.lifeseries.Main;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;

public class PathFinder extends AmbientCreature {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "pathfinder");
    public static final float MOVEMENT_SPEED = 0.35f;
    public static final float FLYING_SPEED = 0.3f;
    private int despawnTimer = 0;

    public PathFinder(EntityType<? extends AmbientCreature> entityType, Level world) {
        super(entityType, world);
        setInvulnerable(true);
        setNoGravity(true);
        setPersistenceRequired();
        setInvisible(true);
        noPhysics = true;
    }
    public static AttributeSupplier.Builder createAttributes() {
        //? if <= 1.21 {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10000)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, FLYING_SPEED)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.FOLLOW_RANGE, 150)
                .add(Attributes.ATTACK_DAMAGE, 20);
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
        setPathfindingMalus(PathType.BLOCKED, -1);
        setPathfindingMalus(PathType.TRAPDOOR, -1);
        setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1);
        setPathfindingMalus(PathType.WALKABLE_DOOR, -1);
        setPathfindingMalus(PathType.DOOR_OPEN, -1);
        setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        if (flying) {
            moveControl = new FlyingMoveControl(this, 20, true);
            navigation = new FlyingPathNavigation(this, ls$getEntityWorld());
            navigation.setCanFloat(true);
        }
        else {
            moveControl = new MoveControl(this);
            navigation = new GroundPathNavigation(this, ls$getEntityWorld());
            navigation.setCanFloat(true);
        }
    }

    public boolean canPathfind(Entity pathfindTo, boolean flying) {
        despawnTimer = 0;
        if (pathfindTo == null) return false;
        setNavigation(flying);
        Path path = navigation.createPath(pathfindTo, 0);
        if (path == null) return false;
        Node end = path.getEndNode();
        if (end == null) return false;
        return end.asBlockPos().equals(pathfindTo.blockPosition());
    }

    public void resetDespawnTimer() {
        despawnTimer = 0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
