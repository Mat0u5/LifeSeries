package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.deaths;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeath;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeaths;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class DeathLightning extends AdvancedDeath {
    private Random rnd = new Random();
    private ServerLevel world;
    public DeathLightning(ServerPlayer player) {
        super(player);
    }

    @Override
    public AdvancedDeaths getDeathType() {
        return AdvancedDeaths.LIGHTNING;
    }

    @Override
    protected int maxTime() {
        return 180;
    }

    @Override
    protected DamageSource damageSource(ServerPlayer player) {
        return player.damageSources().lightningBolt();
    }

    @Override
    protected void tick(ServerPlayer player) {
        ServerLevel world = PlayerUtils.getServerWorld(player);
        if (ticks > 160) {
            WorldUtils.summonHarmlessLightning(player);
            PlayerUtils.killFromSource(player, player.damageSources().lightningBolt());
        }
        else if (ticks > 80) {
            int distanceFromTarget = rnd.nextInt(15, 100);
            Vec3 offset = new Vec3(
                    world.random.nextDouble() * 2 - 1,
                    0,
                    world.random.nextDouble() * 2 - 1
            ).normalize().scale(distanceFromTarget);

            Vec3 pos = player.ls$getEntityPos().add(offset.x(), 0, offset.z());
            Vec3 lightningPos = new Vec3(pos.x, WorldUtils.findTopSafeY(world, pos), pos.z);
            WorldUtils.summonHarmlessLightning(world, lightningPos);
        }
    }

    @Override
    protected void begin(ServerPlayer player) {
        world = PlayerUtils.getServerWorld(player);
        world.setWeatherParameters(0, 200, true, true);
    }

    @Override
    protected void end() {
        if (world == null) return;
        world.setWeatherParameters(12000, 0, false, false);
    }
}
