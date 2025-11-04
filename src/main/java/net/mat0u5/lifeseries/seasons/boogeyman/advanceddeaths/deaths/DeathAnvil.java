package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.deaths;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeath;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeaths;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class DeathAnvil extends AdvancedDeath {
    private int anvilAmount = 20;
    private Vec3 playerPos = null;
    public DeathAnvil(ServerPlayer player) {
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
    protected DamageSource damageSource(ServerPlayer player) {
        return player.damageSources().anvil(player);
    }

    @Override
    protected void tick(ServerPlayer player) {
        if (ticks > 80) {
            if (playerPos == null) {
                playerPos = player.position();
            }
            player.setPos(playerPos);
        }
        if (ticks % 5 == 0 && anvilAmount > 0) {
            BlockPos spawnPos = player.blockPosition().offset(anvilAmount, 15, 0);
            ServerLevel level = player.ls$getServerLevel();
            FallingBlockEntity entity = FallingBlockEntity.fall(level, spawnPos, Blocks.ANVIL.defaultBlockState());
            PlayerUtils.playSoundWithSourceToPlayers(entity, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, 1);
            entity.disableDrop();
            anvilAmount--;
        }
    }

    @Override
    protected void begin(ServerPlayer player) {
    }

    @Override
    protected void end() {
    }
}
