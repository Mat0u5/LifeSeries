package net.mat0u5.lifeseries.entity.triviabot.goal;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

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
        this.lastPosition = BlockPos.ORIGIN;
    }

    @Override
    public boolean canStart() {
        if (mob.interactedWith()) {
            return false;
        }
        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }



        if (mob.serverData.getBoundPlayer() == null) {
            return false;
        }
        PlayerEntity boundPlayer = mob.serverData.getBoundPlayer();

        float distFromPlayer = mob.distanceTo(boundPlayer);
        if (distFromPlayer > TriviaBot.MAX_DISTANCE) return true;


        if (!mob.getBlockPos().equals(this.lastPosition) || distFromPlayer < 4) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = mob.getBlockPos();
        }

        this.ticksSinceLastPositionChange++;
        if (this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange) return true;


        boolean dimensionsAreSame = WorldUtils.getEntityWorld(mob).getRegistryKey().equals(WorldUtils.getEntityWorld(boundPlayer).getRegistryKey());
        return !dimensionsAreSame;
    }

    @Override
    public void start() {
        teleportCooldown = 20;
        mob.pathfinding.fakeTeleportToPlayer();
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}