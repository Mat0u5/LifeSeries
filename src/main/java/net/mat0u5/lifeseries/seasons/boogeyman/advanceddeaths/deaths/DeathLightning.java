package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.deaths;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeath;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeaths;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static net.mat0u5.lifeseries.Main.server;

public class DeathLightning extends AdvancedDeath {
    private Random rnd = new Random();
    private ServerLevel level;
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
        ServerLevel level = player.ls$getServerLevel();
        if (ticks > 160) {
            LevelUtils.summonHarmlessLightning(player);
            PlayerUtils.killFromSource(player, player.damageSources().lightningBolt());
        }
        else if (ticks > 80) {
            int distanceFromTarget = rnd.nextInt(15, 100);
            Vec3 offset = new Vec3(
                    rnd.nextDouble() * 2 - 1,
                    0,
                    rnd.nextDouble() * 2 - 1
            ).normalize().scale(distanceFromTarget);

            Vec3 pos = player.position().add(offset.x(), 0, offset.z());
            Vec3 lightningPos = new Vec3(pos.x, LevelUtils.findTopSafeY(level, pos), pos.z);
            LevelUtils.summonHarmlessLightning(level, lightningPos);
        }
    }

    @Override
    protected void begin(ServerPlayer player) {
        level = player.ls$getServerLevel();
        //? if <= 1.21.11 {
        if (level == null) return;
        level.setWeatherParameters(0, 200, true, true);
        //?} else {
        /*if (server == null) return;
        server.setWeatherParameters(0, 200, true, true);
        *///?}
    }

    @Override
    protected void end() {
        //? if <= 1.21.11 {
        if (level == null) return;
        level.setWeatherParameters(12000, 0, false, false);
        //?} else {
        /*if (server == null) return;
        server.setWeatherParameters(12000, 0, false, false);
        *///?}
    }
}
