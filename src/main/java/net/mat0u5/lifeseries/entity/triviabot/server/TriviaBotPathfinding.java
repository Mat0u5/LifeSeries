package net.mat0u5.lifeseries.entity.triviabot.server;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TriviaBotPathfinding {
    private TriviaBot bot;
    public TriviaBotPathfinding(TriviaBot bot) {
        this.bot = bot;
    }
    public boolean navigationInit = false;

    public void fakeTeleportToPlayer() {
        if (bot.getWorldEntity().isClient()) return;
        ServerPlayerEntity player = bot.serverData.getBoundServerPlayer();
        if (player == null) return;
        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
        if (bot.getWorldEntity() instanceof ServerWorld world) {
            BlockPos tpTo = getBlockPosNearTarget(player, player.getBlockPos(),5);
            world.playSound(null, bot.getX(), bot.getY(), bot.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, bot.getSoundCategory(), bot.soundVolume(), bot.getSoundPitch());
            playerWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, bot.getSoundCategory(), bot.soundVolume(), bot.getSoundPitch());
            AnimationUtils.spawnTeleportParticles(world, WorldUtils.getEntityPos(bot));
            AnimationUtils.spawnTeleportParticles(world, tpTo.toCenterPos());
            bot.serverData.despawn();
            TriviaWildcard.spawnBotFor(player, tpTo);
        }
    }

    public static BlockPos getBlockPosNearTarget(PlayerEntity target, BlockPos targetPos, double distanceFromTarget) {
        if (target == null) return targetPos;
        return WorldUtils.getCloseBlockPos(PlayerUtils.getWorld(target), targetPos, distanceFromTarget, 2, false);
    }


    public void updateNavigation() {
        bot.setMoveControl(new MoveControl(bot));
        bot.setNavigation(new MobNavigation(bot, bot.getWorldEntity()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        if (bot.interactedWith()) {
            bot.getNavigation().stop();
            return;
        }

        if (bot.serverData.getBoundPlayer() == null) return;
        if (bot.distanceTo(bot.serverData.getBoundPlayer()) > TriviaBot.MAX_DISTANCE) return;
        bot.getNavigation().setSpeed(TriviaBot.MOVEMENT_SPEED);
        Path path = bot.getNavigation().findPathTo(bot.serverData.getBoundPlayer(), 3);
        if (path != null) bot.getNavigation().startMovingAlong(path, TriviaBot.MOVEMENT_SPEED);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = WorldUtils.getEntityPos(bot);
        Vec3d endPos = startPos.add(0, bot.getWorldEntity().getBottomY(), 0);

        BlockHitResult result = bot.getWorldEntity().raycast(
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
