package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaHandler;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaPath;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.matlib.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TriviaBotPathfinding {
    private TriviaBot bot;
    public TriviaBotPathfinding(TriviaBot bot) {
        this.bot = bot;
    }
    public boolean navigationInit = false;
    public boolean noPathfinding = false;

    public void tick() {
        if (bot.tickCount % 5 == 0) {
            updateNavigationTarget();
        }
        if (bot.tickCount % 100 == 0 || !navigationInit) {
            navigationInit = true;
            updateNavigation();
        }
        if (bot.inBetweenBot()) {
            tickInBetweenPath();
        }
    }

    private void tickInBetweenPath() {
        if (!bot.onGround()) return;
        Vec3 playerPos = bot.serverData.getPlayerPos();
        List<Vec3> path = InBetweenTriviaPath.getPath(bot);
        Vec3 target = null;
        boolean landing = bot.triviaHandler instanceof InBetweenTriviaHandler handler && handler.isLanding();
        if (!noPathfinding && !landing && !bot.interactedWith() && bot.serverData.shouldPathfind() && playerPos != null && path != null) {
            target = InBetweenTriviaPath.getNextTarget(path, bot.position(), playerPos);
        }
        if (target != null) {
            bot.getMoveControl().setWantedPosition(target.x, target.y, target.z, TriviaBot.MOVEMENT_SPEED);
        }
        else {
            bot.getMoveControl().setWantedPosition(bot.getX(), bot.getY(), bot.getZ(), 0); // Stand still
        }
    }

    public void fakeTeleportToPlayer() {
        if (bot.level().isClientSide()) return;
        if (bot.inBetweenBot()) {
            teleportOnPathToPlayer();
            return;
        }
        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        Entity boundEntity = bot.serverData.getBoundEntity();
        if (boundEntity == null) return;
        if (bot.level() instanceof ServerLevel level) {
            if (boundEntity.level() instanceof ServerLevel entityWorld) {
                BlockPos tpTo = getBlockPosNearTarget(boundEntity,5);
                level.playSound(null, bot.getX(), bot.getY(), bot.getZ(), ModifiableSound.WILDLIFE_TRIVIA_TELEPORT.get(), bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), ModifiableSound.WILDLIFE_TRIVIA_TELEPORT.get(), bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
                AnimationUtils.spawnTeleportParticles(level, bot.position());
                AnimationUtils.spawnTeleportParticles(level, OtherUtils.getCenter(tpTo));
                bot.serverData.despawn();
                TriviaWildcard.spawnBotFor(boundPlayer, tpTo);
            }
        }
    }

    private void teleportOnPathToPlayer() {
        Vec3 playerPos = bot.serverData.getPlayerPos();
        List<Vec3> path = InBetweenTriviaPath.getPath(bot);
        if (playerPos == null || path == null || !(bot.level() instanceof ServerLevel level)) return;
        Vec3 target = InBetweenTriviaPath.getClosestPathPoint(path, playerPos);
        if (target == null) return;
        level.playSound(null, bot.getX(), bot.getY(), bot.getZ(), ModifiableSound.FINALLIFE_TRIVIA_TELEPORT.get(), bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
        level.playSound(null, target.x, target.y, target.z, ModifiableSound.FINALLIFE_TRIVIA_TELEPORT.get(), bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
        AnimationUtils.spawnTeleportParticles(level, bot.position());
        AnimationUtils.spawnTeleportParticles(level, target);
        bot.getNavigation().stop();
        bot.teleportTo(target.x, target.y, target.z);
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, BlockPos targetPos, double distanceFromTarget) {
        if (target == null) return targetPos;
        return LevelUtils.getCloseBlockPos(target.level(), targetPos, distanceFromTarget, 2, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3 targetPos = bot.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.containing(targetPos.x, targetPos.y, targetPos.z);
        return LevelUtils.getCloseBlockPos(target.level(), targetBlockPos, distanceFromTarget, 2, false);
    }


    public void updateNavigation() {
        bot.setMoveControl(new MoveControl(bot));
        bot.setNavigation(new GroundPathNavigation(bot, bot.level()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        Vec3 targetPos = bot.serverData.getPlayerPos();
        if (bot.inBetweenBot() || noPathfinding || bot.interactedWith() ||!bot.serverData.shouldPathfind() || targetPos == null ||
                bot.distanceToSqr(targetPos) > (TriviaBot.MAX_DISTANCE*TriviaBot.MAX_DISTANCE)) {
            bot.getNavigation().stop();
            return;
        }

        bot.getNavigation().setSpeedModifier(TriviaBot.MOVEMENT_SPEED);
        Path path = bot.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 3);
        if (path != null) bot.getNavigation().moveTo(path, TriviaBot.MOVEMENT_SPEED);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3 startPos = bot.position();
        Vec3 endPos = new Vec3(startPos.x(), bot.level().getMinY(), startPos.z());

        BlockHitResult result = bot.level().clip(
                new ClipContext(
                        startPos,
                        endPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
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
