package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.mixin.ServerLevelAccessor;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import static net.mat0u5.lifeseries.Main.*;

//? if <= 1.21.9
import net.minecraft.world.level.GameRules;
//? if > 1.21.9
/*import net.minecraft.world.level.gamerules.GameRules;*/

public class NiceLife extends Season {

    public static boolean SNOWY_NETHER = true;
    public static boolean LIGHT_MELTS_SNOW = false;
    public boolean SNOW_WHEN_NOT_IN_SESSION = false;
    public static boolean ADVANCE_TIME_WHEN_NOT_IN_SESSION = false;
    public Time SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(600);
    public Time snowTicks = Time.zero();
    public double snowLayerTickChance = 1.0 / 43;
    public int currentMaxSnowLayers = -1;
    public static boolean triviaInProgress = false;

    @Override
    public Seasons getSeason() {
        return Seasons.NICE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new NiceLifeConfig();
    }
    @Override
    public void reload() {
        LIGHT_MELTS_SNOW = NiceLifeConfig.LIGHT_MELTS_SNOW.get(seasonConfig);
        SNOW_WHEN_NOT_IN_SESSION = NiceLifeConfig.SNOW_WHEN_NOT_IN_SESSION.get(seasonConfig);
        SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(Math.max(13, NiceLifeConfig.SNOW_LAYER_INCREMENT_DELAY.get(seasonConfig)));
        ADVANCE_TIME_WHEN_NOT_IN_SESSION = NiceLifeConfig.ADVANCE_TIME_WHEN_NOT_IN_SESSION.get(seasonConfig);
        SNOWY_NETHER = NiceLifeConfig.SNOWY_NETHER.get(seasonConfig);
        snowLayerTickChance = 280.0 / Math.max(SNOW_LAYER_INCREASE_INTERVAL.getTicks(), 1);
        if (currentMaxSnowLayers == -1) {
            currentMaxSnowLayers = seasonConfig.getOrCreateInt("current_snow_layers", 1);
        }
    }

    public static Time tempSleepTimer = Time.zero();
    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        if (currentSession.statusStarted() || SNOW_WHEN_NOT_IN_SESSION) {
            snowTicks.tick();
            if (snowTicks.isLarger(SNOW_LAYER_INCREASE_INTERVAL)) {
                snowTicks = Time.zero();
                currentMaxSnowLayers++;
                if (currentMaxSnowLayers > 8) {
                    currentMaxSnowLayers = 1;
                }
                seasonConfig.setProperty("current_snow_layers", String.valueOf(currentMaxSnowLayers));
            }
        }
        ServerLevel overworld = server.overworld();
        overworld.setWeatherParameters(0, 1000, true, false);

        boolean advanceTime = !isMidnight() && (currentSession.statusStarted() || ADVANCE_TIME_WHEN_NOT_IN_SESSION);
        //? if <= 1.21.9 {
        OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_DAYLIGHT, advanceTime);
        //?} else {
        /*OtherUtils.setBooleanGameRule(overworld, GameRules.ADVANCE_TIME, advanceTime);
         *///?}

        if (!isMidnight()) {
            for(ServerPlayer serverPlayer : PlayerUtils.getAllPlayers()) {
                if (serverPlayer.isSleeping()) {
                    serverPlayer.displayClientMessage(Component.nullToEmpty("You are too excited to fall asleep"), true);
                }
            }
        }

        if (overworld instanceof ServerLevelAccessor accessor) {
            SleepStatus sleepStatus = accessor.ls$getSleepStatus();
            //? if <= 1.21.9 {
            int percentage = overworld.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
            //?} else {
            /*int percentage = overworld.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
            *///?}
            if (sleepStatus.areEnoughSleeping(percentage) && isMidnight()) {
                //TODO spawn trivia bots, prevent players from getting out of beds.
                triviaInProgress = true;
                tempSleepTimer.tick();
                if (tempSleepTimer.isLarger(Time.seconds(5))) {
                    long newTime = overworld.getDayTime() + 24000L;
                    overworld.setDayTime(newTime - newTime % 24000L);
                    accessor.ls$wakeUpAllPlayers();
                    tempSleepTimer = Time.zero();
                    triviaInProgress = false;
                }
            }
            else {
                tempSleepTimer = Time.zero();
            }
        }
    }

    public boolean isMidnight() {
        if (server == null) return false;
        long dayTime = server.overworld().getDayTime() % 24000L;
        return dayTime >= 18000 && dayTime <= 20000;
    }

    public void tickChunk(ServerLevel level, ChunkPos chunkPos) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        if (currentSession.statusStarted()  || SNOW_WHEN_NOT_IN_SESSION) {
            int minX = chunkPos.getMinBlockX();
            int maxX = chunkPos.getMinBlockZ();
            if (level.random.nextDouble() <= snowLayerTickChance) {
                customPrecipitation(level, level.getBlockRandomPos(minX, 0, maxX, 15));
            }
        }
    }

    public void customPrecipitation(ServerLevel level, BlockPos pos) {
        BlockPos topPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
        BlockPos belowPos = topPos.below();
        Biome biome = level.getBiome(topPos).value();
        if (shouldFreeze(level, belowPos)) {
            level.setBlockAndUpdate(belowPos, Blocks.ICE.defaultBlockState());
        }

        if (shouldSnow(level, topPos)) {
            BlockState state = level.getBlockState(topPos);
            if (currentMaxSnowLayers > 0) {
                if (state.is(Blocks.SNOW)) {
                    int currentLayers = state.getValue(SnowLayerBlock.LAYERS);
                    if (currentLayers < Math.min(currentMaxSnowLayers, 8)) {
                        if (currentLayers == 7) {
                            level.setBlockAndUpdate(topPos, Blocks.SNOW_BLOCK.defaultBlockState());
                        }
                        else {
                            BlockState newState = state.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
                            Block.pushEntitiesUp(state, newState, level, topPos);
                            level.setBlockAndUpdate(topPos, newState);
                        }
                    }
                }
                else if (!(level.getBlockState(belowPos).is(Blocks.SNOW_BLOCK) && currentMaxSnowLayers == 8)) {
                    level.setBlockAndUpdate(topPos, Blocks.SNOW.defaultBlockState());
                }
            }
        }

        //? if <= 1.21 {
        Biome.Precipitation precipitation = biome.getPrecipitationAt(belowPos);
        //?} else {
        /*Biome.Precipitation precipitation = biome.getPrecipitationAt(belowPos, level.getSeaLevel());
         *///?}
        if (precipitation != Biome.Precipitation.NONE) {
            BlockState belowState = level.getBlockState(belowPos);
            belowState.getBlock().handlePrecipitation(belowState, level, belowPos, precipitation);
        }
    }

    private boolean shouldFreeze(ServerLevel level, BlockPos blockPos) {
        boolean darkEnough = level.getBrightness(LightLayer.BLOCK, blockPos) < 10 || !LIGHT_MELTS_SNOW;
        //? if <= 1.21 {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        //?} else {
        /*int minY = level.getMinY();
        int maxY = level.getMaxY();
        *///?}
        if (blockPos.getY() >= minY && blockPos.getY() < maxY && darkEnough) {
            BlockState blockState = level.getBlockState(blockPos);
            FluidState fluidState = level.getFluidState(blockPos);
            if (fluidState.getType() == Fluids.WATER && blockState.getBlock() instanceof LiquidBlock) {
                boolean middleLiquid = level.isWaterAt(blockPos.west()) && level.isWaterAt(blockPos.east()) && level.isWaterAt(blockPos.north()) && level.isWaterAt(blockPos.south());
                if (!middleLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldSnow(ServerLevel level, BlockPos blockPos) {
        boolean darkEnough = level.getBrightness(LightLayer.BLOCK, blockPos) < 10 || !LIGHT_MELTS_SNOW;
        //? if <= 1.21 {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        //?} else {
        /*int minY = level.getMinY();
        int maxY = level.getMaxY();
        *///?}
        if (blockPos.getY() >= minY && blockPos.getY() < maxY && darkEnough) {
            BlockState blockState = level.getBlockState(blockPos);
            boolean canSnowOverride = blockState.isAir() ||
                    blockState.is(Blocks.SNOW) ||
                    //? if <= 1.20.2 {
                    /*blockState.is(Blocks.GRASS) ||
                    *///?} else {
                    blockState.is(Blocks.SHORT_GRASS) ||
                    //?}
                    blockState.is(Blocks.TALL_GRASS) ||
                    blockState.is(Blocks.DANDELION) ||
                    blockState.is(Blocks.TORCHFLOWER) ||
                    blockState.is(Blocks.POPPY) ||
                    blockState.is(Blocks.BLUE_ORCHID) ||
                    blockState.is(Blocks.ALLIUM) ||
                    blockState.is(Blocks.AZURE_BLUET) ||
                    blockState.is(Blocks.RED_TULIP) ||
                    blockState.is(Blocks.ORANGE_TULIP) ||
                    blockState.is(Blocks.WHITE_TULIP) ||
                    blockState.is(Blocks.PINK_TULIP) ||
                    blockState.is(Blocks.OXEYE_DAISY) ||
                    blockState.is(Blocks.CORNFLOWER) ||
                    blockState.is(Blocks.WITHER_ROSE) ||
                    blockState.is(Blocks.LILY_OF_THE_VALLEY);
            if (canSnowOverride && Blocks.SNOW.defaultBlockState().canSurvive(level, blockPos)) {
                return true;
            }
        }
        return false;
    }

    public static final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
    public static final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();
    public static final BlockState air = Blocks.AIR.defaultBlockState();
    public static final BlockState froglight = Blocks.PEARLESCENT_FROGLIGHT.defaultBlockState();
    public BlockState transformBlockState(BlockState oldState) {
        if (oldState.is(Blocks.NETHERRACK) ||
                oldState.is(Blocks.BASALT) ||
                oldState.is(Blocks.SOUL_SAND) ||
                oldState.is(Blocks.SOUL_SOIL) ||
                oldState.is(Blocks.CRIMSON_NYLIUM) ||
                oldState.is(Blocks.WARPED_NYLIUM) ||
                oldState.is(Blocks.MAGMA_BLOCK) ||
                oldState.is(Blocks.GRAVEL)) {
            return snowBlock;
        }
        if (oldState.is(Blocks.LAVA)) {
            return packedIce;
        }
        if (oldState.is(Blocks.CRIMSON_FUNGUS) ||
                oldState.is(Blocks.CRIMSON_ROOTS) ||
                oldState.is(Blocks.WARPED_FUNGUS) ||
                oldState.is(Blocks.WARPED_ROOTS) ||
                oldState.is(Blocks.NETHER_SPROUTS)) {
            return air;
        }
        if (oldState.is(Blocks.GLOWSTONE) || oldState.is(Blocks.SHROOMLIGHT)) {
            return froglight;
        }
        return null;
    }
}
