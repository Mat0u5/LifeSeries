package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class NiceLife extends Season {

    public boolean LIGHT_MELTS_SNOW = false; //TODO CONFIG
    public boolean SNOW_WHEN_NOT_IN_SESSION = false; //TODO CONFIG
    public Time SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(600); //TODO CONFIG
    public Time snowTicks = Time.zero(); //TODO CONFIG
    public double snowLayerTickChance = 1.0 / 48;
    public int currentMaxSnowLayers = -1;

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
        //SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(Math.max(13, ...));
        snowLayerTickChance = 250.0 / Math.max(SNOW_LAYER_INCREASE_INTERVAL.getTicks(), 1);
        if (currentMaxSnowLayers == -1) {
            currentMaxSnowLayers = seasonConfig.getOrCreateInt("current_snow_layers", 1);
        }
    }

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
                OtherUtils.log("Total Layers: "+currentMaxSnowLayers);
            }
        }
        long dayTime = server.overworld().getDayTime() % 24000L;
        /*
        boolean isNight =
        if (currentSession.statusStarted()) {

        }*/
        server.overworld().setWeatherParameters(0, 1000, true, false);
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
            //? if <= 1.20.2 {
            /*boolean canSnowOverride = (blockState.isAir() || blockState.is(Blocks.SNOW) || blockState.is(Blocks.GRASS) || blockState.is(Blocks.TALL_GRASS));
             *///?} else {
            boolean canSnowOverride = (blockState.isAir() || blockState.is(Blocks.SNOW) || blockState.is(Blocks.SHORT_GRASS) || blockState.is(Blocks.TALL_GRASS));
            //?}
            if (canSnowOverride && Blocks.SNOW.defaultBlockState().canSurvive(level, blockPos)) {
                return true;
            }
        }
        return false;
    }
}
