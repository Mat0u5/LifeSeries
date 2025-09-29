package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.deaths;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeath;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeaths;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DeathAnvil extends AdvancedDeath {
    private int anvilAmount = 20;
    private Vec3d playerPos = null;
    public DeathAnvil(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public AdvancedDeaths getDeathType() {
        return AdvancedDeaths.ANVIL;
    }

    @Override
    protected int maxTime() {
        return 123;
    }

    @Override
    protected DamageSource damageSource(ServerPlayerEntity player) {
        return player.getDamageSources().fallingAnvil(player);
    }

    @Override
    protected void tick(ServerPlayerEntity player) {
        if (ticks > 80) {
            if (playerPos == null) {
                playerPos = WorldUtils.getEntityPos(player);
            }
            player.setPosition(playerPos);
        }
        if (ticks % 5 == 0 && anvilAmount > 0) {
            BlockPos spawnPos = player.getBlockPos().add(anvilAmount, 15, 0);
            ServerWorld world = PlayerUtils.getServerWorld(player);
            FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, spawnPos, Blocks.ANVIL.getDefaultState());
            PlayerUtils.playSoundWithSourceToPlayers(entity, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1, 1);
            entity.setDestroyedOnLanding();
            anvilAmount--;
        }
    }

    @Override
    protected void begin(ServerPlayerEntity player) {
    }

    @Override
    protected void end() {
    }
}
