package net.mat0u5.lifeseries.entity.triviabot.goal;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("resource")
public final class TriviaBotTeleportGoal extends Goal {
    @NotNull
    private final TriviaBot mob;
    private int teleportCooldown = 0;
    @NotNull
    private BlockPos lastPosition;
    private int ticksSinceLastPositionChange;
    private final int maxTicksSinceLastPositionChange;

    public TriviaBotTeleportGoal(@NotNull TriviaBot mob) {
        this.mob = mob;
        this.maxTicksSinceLastPositionChange = TriviaBot.STATIONARY_TP_COOLDOWN;
        this.lastPosition = BlockPos.ZERO;
    }

    @Override
    public boolean canUse() {
        if (mob.getBotWorld().isClientSide()) return false;
        if (mob.interactedWith()) {
            return false;
        }
        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }

        if (!mob.serverData.shouldPathfind()) {
            return false;
        }

        LivingEntity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) return false;

        float distFromPlayer = mob.distanceTo(boundEntity);
        if (distFromPlayer > TriviaBot.MAX_DISTANCE) return true;


        if (!mob.blockPosition().equals(this.lastPosition) || distFromPlayer < 4) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = mob.blockPosition();
        }

        this.ticksSinceLastPositionChange++;
        if (this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange) return true;


        boolean dimensionsAreSame = mob.ls$getEntityWorld().dimension().equals(boundEntity.ls$getEntityWorld().dimension());
        return !dimensionsAreSame;
    }

    @Override
    public void start() {
        teleportCooldown = 20;
        mob.pathfinding.fakeTeleportToPlayer();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}