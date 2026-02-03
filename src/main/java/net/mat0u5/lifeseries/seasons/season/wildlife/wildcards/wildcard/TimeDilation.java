package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.server;
//? if >= 1.20.3
import net.minecraft.server.ServerTickRateManager;
//? if <= 1.21.9
//import net.minecraft.world.level.GameRules;
//? if > 1.21.9
import net.minecraft.world.level.gamerules.GameRules;
//? if >= 26.1
//import net.minecraft.world.clock.WorldClocks;

public class TimeDilation extends Wildcard {
    public static float MIN_TICK_RATE = 1;
    public static float NORMAL_TICK_RATE = 20;
    public static float MAX_TICK_RATE = 100;

    public static float MIN_TICK_RATE_NERFED = 10;
    public static float MAX_TICK_RATE_NERFED = 30;

    public static float MIN_PLAYER_MSPT = 25.0F;

    public static int updateRate = 100;
    public static int lastDiv = -1;
    public static Time activatedAt = Time.nullTime();
    public static float weatherTicksBacklog = 0;

    @Override
    public Wildcards getType() {
        return Wildcards.TIME_DILATION;
    }

    @Override
    public void tick() {
        if (server == null) return;
        //? if >= 1.20.3 {
        ServerTickRateManager serverTickManager = server.tickRateManager();
        float rate = serverTickManager.tickrate();
        if (rate > 20) {
            if (rate > 30) {
                adjustCreeperFuseTimes();
            }
            weatherTicksBacklog += (rate-20) / 2.0f;
            int weatherTicks = (int) weatherTicksBacklog;
            if (weatherTicks >= 1) {
                weatherTicksBacklog -= weatherTicks;
                for (ServerLevel serverLevel : server.getAllLevels()) {
                    //? if <= 1.21.11 {
                    long newTicks = serverLevel.getDayTime() + weatherTicks;
                    serverLevel.setDayTime(newTicks);
                    //?} else {
                    /*long newTicks = serverLevel.getOverworldClockTime() + weatherTicks;
                    serverLevel.clockManager().setTotalTicks(serverLevel.registryAccess().getOrThrow(WorldClocks.OVERWORLD), newTicks);
                    *///?}
                    for (ServerPlayer player : serverLevel.players()) {
                        //? if <= 1.21.9 {
                        /*boolean daylightCycle = OtherUtils.getBooleanGameRule(serverLevel, GameRules.RULE_DAYLIGHT);
                        *///?} else {
                        boolean daylightCycle = OtherUtils.getBooleanGameRule(serverLevel, GameRules.ADVANCE_TIME);
                         //?}
                        //? if <= 1.21.11 {
                        player.connection.send(new ClientboundSetTimePacket(serverLevel.getGameTime(), serverLevel.getDayTime(), daylightCycle));
                        //?} else {
                        /*player.connection.send(serverLevel.clockManager().createFullSyncPacket());
                        *///?}
                    }
                }
            }
        }
        //?}
    }

    @Override
    public void tickSessionOn() {
        if (!active) return;
        float sessionPassedTime = currentSession.getPassedTime().diff(activatedAt).getTicks();
        if (sessionPassedTime < 0) return;
        if (sessionPassedTime > 3600 && sessionPassedTime < 3700 && !isFinale()) OtherUtils.executeCommand("weather clear");
        int currentDiv = (int) (((double)currentSession.getPassedTime().getTicks()) / updateRate);
        if (lastDiv != currentDiv) {
            lastDiv = currentDiv;

            float progress = (float) currentSession.progress(activatedAt);
            if (isFinale()) {
                progress = sessionPassedTime / (20*60*5);
                if (progress >= 1 && !Callback.allWildcardsPhaseReached) {
                    deactivate();
                    WildcardManager.fadedWildcard();
                    NetworkHandlerServer.sendUpdatePackets();
                    return;
                }
            }
            progress = OtherUtils.clamp(progress, 0, 1);
            /*
            if (progress < 0.492f) {
                progress = 0.311774f * (float) Math.pow(progress, 0.7);
            }
            else {
                progress = (float) Math.pow(1.8*progress-0.87f, 3) + 0.19f;
            }
            */
            float rate;
            if (progress < 0.5f) {
                rate = getMinTickRate() + (NORMAL_TICK_RATE - getMinTickRate()) * (progress * 2);
            }
            else {
                rate = NORMAL_TICK_RATE + (getMaxTickRate() - NORMAL_TICK_RATE) * (progress * 2 - 1);
            }
            rate = Math.min(rate, getMaxTickRate());
            setWorldSpeed(rate);
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        setWorldSpeed(NORMAL_TICK_RATE);
        lastDiv = -1;
        OtherUtils.executeCommand("/execute as @e[type=minecraft:creeper] run data modify entity @s Fuse set value 30s");
    }

    @Override
    public void activate() {
        if (!isFinale()) TaskScheduler.scheduleTask(50, () -> OtherUtils.executeCommand("weather rain"));
        TaskScheduler.scheduleTask(115, () -> {
            activatedAt = currentSession.getPassedTime().add(Time.seconds(20));
            lastDiv = -1;
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_time_slow_down")));
            slowlySetWorldSpeed(getMinTickRate(), 18);
            if (!isFinale() && getMinTickRate() <= 4) TaskScheduler.scheduleTask(18, () -> NetworkHandlerServer.sendLongPackets(PacketNames.TIME_DILATION, System.currentTimeMillis()));
            TaskScheduler.scheduleTask(19, super::activate);
        });
    }

    public static void slowlySetWorldSpeed(float rate, int ticks) {
        if (server == null) return;
        //? if >= 1.20.3 {
        ServerTickRateManager serverTickManager = server.tickRateManager();
        float currentRate = serverTickManager.tickrate();
        float step = (rate - currentRate) / (ticks);
        for (int i = 0; i < ticks; i++) {
            int finalI = i;
            TaskScheduler.scheduleTask(i, () -> serverTickManager.setTickRate(currentRate + (step * finalI)));
        }
        TaskScheduler.scheduleTask(ticks+1, () -> serverTickManager.setTickRate(rate));
        //?}
    }

    public static void setWorldSpeed(float rate) {
        if (server == null) return;
        //? if >= 1.20.3 {
        ServerTickRateManager serverTickManager = server.tickRateManager();
        serverTickManager.setTickRate(rate);
        //?}
    }

    public static float getWorldSpeed() {
        if (server == null) return 20;
        //? if >= 1.20.3 {
        ServerTickRateManager serverTickManager = server.tickRateManager();
        return serverTickManager.tickrate();
        //?} else {
        /*return 20;
        *///?}
    }

    private static void adjustCreeperFuseTimes() {
        if (server == null) return;
        //? if >= 1.20.3 {
        ServerTickRateManager serverTickManager = server.tickRateManager();
        float tickRate = serverTickManager.tickrate();
        short fuseTime = (short) (20 * (tickRate / 20.0f));
        OtherUtils.executeCommand("/execute as @e[type=minecraft:creeper] run data modify entity @s Fuse set value "+fuseTime+"s");
        //?}
    }

    public static float getMaxTickRate() {
        if (isFinale()) return MAX_TICK_RATE_NERFED;
        return MAX_TICK_RATE;
    }

    public static float getMinTickRate() {
        if (isFinale()) return MIN_TICK_RATE_NERFED;
        return Math.max(MIN_TICK_RATE, 1);
    }
}
