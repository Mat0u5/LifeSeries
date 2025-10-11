package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

public class TriviaBotPathfinding {
    private TriviaBot bot;
    public TriviaBotPathfinding(TriviaBot bot) {
        this.bot = bot;
    }
    public boolean navigationInit = false;

    public void tick() {
        if (bot.age % 5 == 0) {
            bot.pathfinding.updateNavigationTarget();
        }
        if (bot.age % 100 == 0 || !navigationInit) {
            navigationInit = true;
            updateNavigation();
        }
    }

    public void fakeTeleportToPlayer() {
        if (bot.getBotWorld().isClient()) return;
        ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
        Entity boundEntity = bot.serverData.getBoundEntity();
        if (boundEntity == null) return;
        if (bot.getBotWorld() instanceof ServerWorld world) {
            if (WorldUtils.getEntityWorld(boundEntity) instanceof ServerWorld entityWorld) {
                BlockPos tpTo = getBlockPosNearTarget(boundEntity,5);
                world.playSound(null, bot.getX(), bot.getY(), bot.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, bot.getSoundCategory(), bot.soundVolume(), bot.getSoundPitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, bot.getSoundCategory(), bot.soundVolume(), bot.getSoundPitch());
                AnimationUtils.spawnTeleportParticles(world, WorldUtils.getEntityPos(bot));
                AnimationUtils.spawnTeleportParticles(world, tpTo.toCenterPos());
                bot.serverData.despawn();
                TriviaWildcard.spawnBotFor(boundPlayer, tpTo);
            }
        }
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, BlockPos targetPos, double distanceFromTarget) {
        if (target == null) return targetPos;
        return WorldUtils.getCloseBlockPos(WorldUtils.getEntityWorld(target), targetPos, distanceFromTarget, 2, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3d targetPos = bot.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.ofFloored(targetPos.x, targetPos.y, targetPos.z);
        return WorldUtils.getCloseBlockPos(WorldUtils.getEntityWorld(target), targetBlockPos, distanceFromTarget, 2, false);
    }


    public void updateNavigation() {
        bot.setMoveControl(new MoveControl(bot));
        bot.setNavigation(new MobNavigation(bot, bot.getBotWorld()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        Vec3d targetPos = bot.serverData.getPlayerPos();
        if (bot.interactedWith() ||!bot.serverData.shouldPathfind() || targetPos == null ||
                bot.squaredDistanceTo(targetPos) > (TriviaBot.MAX_DISTANCE*TriviaBot.MAX_DISTANCE)) {
            bot.getNavigation().stop();
            return;
        }

        bot.getNavigation().setSpeed(TriviaBot.MOVEMENT_SPEED);
        Path path = bot.getNavigation().findPathTo(targetPos.x, targetPos.y, targetPos.z, 3);
        if (path != null) bot.getNavigation().startMovingAlong(path, TriviaBot.MOVEMENT_SPEED);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = WorldUtils.getEntityPos(bot);
        Vec3d endPos = startPos.add(0, bot.getBotWorld().getBottomY(), 0);

        BlockHitResult result = bot.getBotWorld().raycast(
                new RaycastContext(
                        startPos,
                        endPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        bot
                )
        );
        if (result.getType() == HitResult.Type.MISS) return null;
        return result.getBlockPos();
    }

    public double getDistanceToGroundBlock() {
        BlockPos belowBlock = getGroundBlock();
        if (belowBlock == null) return Double.NEGATIVE_INFINITY;
        return bot.getY() - belowBlock.getY() - 1;
    }
}
