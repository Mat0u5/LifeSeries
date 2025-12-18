package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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
    }

    public void fakeTeleportToPlayer() {
        if (bot.level().isClientSide()) return;
        ServerPlayer boundPlayer = bot.serverData.getBoundPlayer();
        Entity boundEntity = bot.serverData.getBoundEntity();
        if (boundEntity == null) return;
        if (bot.level() instanceof ServerLevel level) {
            if (boundEntity.level() instanceof ServerLevel entityWorld) {
                BlockPos tpTo = getBlockPosNearTarget(boundEntity,5);
                //? if <= 1.20.2 {
                /*level.playSound(null, bot.getX(), bot.getY(), bot.getZ(), SoundEvents.ENDERMAN_TELEPORT, bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENDERMAN_TELEPORT, bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
                *///?} else {
                level.playSound(null, bot.getX(), bot.getY(), bot.getZ(), SoundEvents.PLAYER_TELEPORT, bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.PLAYER_TELEPORT, bot.getSoundSource(), bot.soundVolume(), bot.getVoicePitch());
                //?}
                AnimationUtils.spawnTeleportParticles(level, bot.position());
                AnimationUtils.spawnTeleportParticles(level, tpTo.getCenter());
                bot.serverData.despawn();
                TriviaWildcard.spawnBotFor(boundPlayer, tpTo);
            }
        }
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
        if (noPathfinding || bot.interactedWith() ||!bot.serverData.shouldPathfind() || targetPos == null ||
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
        //? if <= 1.21 {
        int minY = bot.level().getMinBuildHeight();
        //?} else {
        /*int minY = bot.level().getMinY();
        *///?}
        Vec3 endPos = startPos.add(0, minY, 0);

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
