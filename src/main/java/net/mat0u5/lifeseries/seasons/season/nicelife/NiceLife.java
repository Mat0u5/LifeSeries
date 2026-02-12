package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.mixin.ServerLevelAccessor;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
//import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
//? if > 1.21.9
import net.minecraft.world.level.gamerules.GameRules;

//? if >= 26.1 {
/*import net.minecraft.world.clock.WorldClocks;
*///?}

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NiceLife extends Season {

    public static boolean SNOWY_NETHER = true;
    public static boolean LIGHT_MELTS_SNOW = false;
    public boolean SNOW_WHEN_NOT_IN_SESSION = false;
    public static boolean ADVANCE_TIME_WHEN_NOT_IN_SESSION = false;
    public Time SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(600);
    public Time snowTicks = Time.zero();
    public double snowLayerTickChance = 1.0 / 43;
    public int precipitationTicks = 1;
    public double chancePerTick = snowLayerTickChance;

    public int currentMaxSnowLayers = -1;
    public static boolean playedMidnightChimes = false;
    public static boolean reachedSunset = false;
    public static boolean reachedPreSunset = false;
    public static boolean redWinter = false;
    public static Time naughtyListGlowTimeInterval = Time.seconds(60);
    public static Time naughtyListGlowTime = Time.seconds(5);
    public static Time timePassed = Time.zero();
    public static Time triviaCannotStartFor = Time.zero();

    @Override
    public void initialize() {
        super.initialize();
        NiceLifeTriviaHandler.initializeItemSpawner();
        NiceLifeTriviaManager.initialize();
    }

    @Override
    public Seasons getSeason() {
        return Seasons.NICE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new NiceLifeConfig();
    }

    @Override
    public void seasonSwitched(Seasons changedTo) {
        if (changedTo != Seasons.NICE_LIFE) {
            if (server == null) return;
            //? if <= 1.21.9 {
            /*OtherUtils.setBooleanGameRule(server.overworld(), GameRules.RULE_DAYLIGHT, true);
            *///?} else {
            OtherUtils.setBooleanGameRule(server.overworld(), GameRules.ADVANCE_TIME, true);
             //?}
            NiceLifeTriviaManager.killAllSnowmen();
            NiceLifeTriviaManager.killAllBots();
            Season.setSkyColor(null, false);
            Season.setFogColor(null, false);
            Season.setCloudColor(null, false);
        }
    }

    @Override
    public void reload() {
        super.reload();
        NiceLifeVotingManager.createTeams();
        LIGHT_MELTS_SNOW = NiceLifeConfig.LIGHT_MELTS_SNOW.get();
        SNOW_WHEN_NOT_IN_SESSION = NiceLifeConfig.SNOW_WHEN_NOT_IN_SESSION.get();
        SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(NiceLifeConfig.SNOW_LAYER_INCREMENT_DELAY.get());
        ADVANCE_TIME_WHEN_NOT_IN_SESSION = NiceLifeConfig.ADVANCE_TIME_WHEN_NOT_IN_SESSION.get();
        SNOWY_NETHER = NiceLifeConfig.SNOWY_NETHER.get();
        snowLayerTickChance = 280.0 / Math.max(SNOW_LAYER_INCREASE_INTERVAL.getTicks(), 1);
        if (currentMaxSnowLayers == -1) {
            currentMaxSnowLayers = seasonConfig.getOrCreateInt("current_snow_layers", 1);
        }
        updateSnowTick();
        NiceLifeTriviaManager.initialize();

        NiceLifeTriviaManager.QUESTION_TIME = NiceLifeConfig.TRIVIA_QUESTION_TIME.get();
        NiceLifeTriviaManager.CAN_BREAK_BEDS = NiceLifeConfig.BOT_CAN_BREAK_BEDS.get();
        NiceLifeTriviaManager.BREAKING_DROPS_RESOURCES = NiceLifeConfig.BOT_BREAKING_BLOCKS_DROP_RESOURCES.get();
        NiceLifeVotingManager.NICE_LIST_CHANCE = NiceLifeConfig.NICE_LIST_CHANCE.get();
        NiceLifeVotingManager.VOTING_TIME = Time.seconds(NiceLifeConfig.VOTING_TIME.get());
        NiceLifeVotingManager.REDS_ON_NAUGHTY_LIST = NiceLifeConfig.ALLOW_REDS_ON_NAUGHTY_LIST.get();
        NiceLifeVotingManager.NAUGHTY_LIST_COUNT = NiceLifeConfig.NAUGHTY_LIST_PLAYERS.get();
        NiceLifeVotingManager.NICE_LIST_COUNT = NiceLifeConfig.NICE_LIST_PLAYERS.get();
    }

    public void updateSnowTick() {
        double chance = snowLayerTickChance;

        if (redWinter) {
            chance *= 5;
        }
        if (currentMaxSnowLayers >= 8) {
            chance *= 3;
        }

        precipitationTicks = Math.max(1, (int) Math.ceil(chance));
        chancePerTick = chance / precipitationTicks;
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        timePassed.tick();
        if (currentSession.statusStarted() || SNOW_WHEN_NOT_IN_SESSION) {
            snowTicks.tick();
            if (snowTicks.isLarger(SNOW_LAYER_INCREASE_INTERVAL)) {
                snowTicks = Time.zero();
                currentMaxSnowLayers++;
                if (currentMaxSnowLayers > 8) {
                    currentMaxSnowLayers = 1;
                }
                updateSnowTick();
                seasonConfig.setProperty("current_snow_layers", String.valueOf(currentMaxSnowLayers));
            }
        }
        ServerLevel overworld = server.overworld();
        //? if <= 1.21.11 {
        overworld.setWeatherParameters(0, 1000, true, false);
        //?} else {
        /*server.setWeatherParameters(0, 1000, true, false);
        *///?}

        boolean advanceTime = !isMidnight() && (currentSession.statusStarted() || ADVANCE_TIME_WHEN_NOT_IN_SESSION);
        //? if <= 1.21.9 {
        /*OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_DAYLIGHT, advanceTime);
        *///?} else {
        OtherUtils.setBooleanGameRule(overworld, GameRules.ADVANCE_TIME, advanceTime);
         //?}

        if (!isMidnight()) {
            for(ServerPlayer serverPlayer : PlayerUtils.getAllPlayers()) {
                if (serverPlayer.isSleeping()) {
                    serverPlayer.displayClientMessage(Component.nullToEmpty("You are too excited to fall asleep"), true);
                }
            }
            if (!playedMidnightChimes && isTimeBetween(18000-23*20, 20000)) {
                playedMidnightChimes = true;
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(),
                        SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_midnight_chimes")),
                        1f, 1);
                postponeTriviaStart(Time.ticks(779));
            }
        }

        if (triviaCannotStartFor.isSmaller(Time.zero())) {
            //? if <= 1.21.9 {
            /*int percentage = overworld.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
             *///?} else {
            int percentage = overworld.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
            //?}
            if (areEnoughSleeping(percentage) && isMidnight() && currentSession.statusStarted()) {
                if (!NiceLifeTriviaManager.triviaInProgress) {
                    List<ServerPlayer> triviaPlayers = new ArrayList<>();
                    for(ServerPlayer player : livesManager.getAlivePlayers()) {
                        if (player.isSpectator()) continue;
                        if (!player.isSleeping()) continue;
                        triviaPlayers.add(player);
                    }
                    if (!triviaPlayers.isEmpty()) {
                        NiceLifeTriviaManager.startTrivia(triviaPlayers);
                    }
                }
            }
        }
        else {
            triviaCannotStartFor.add(Time.ticks(-1));
        }

        if (isMidnight() && NiceLifeTriviaManager.triviaInProgress && !NiceLifeTriviaManager.preparingForSpawn) {
            List<ServerPlayer> remainingTriviaPlayers = new ArrayList<>();
            for (UUID playerUUID : NiceLifeTriviaManager.triviaPlayersUUID) {
                ServerPlayer player = PlayerUtils.getPlayer(playerUUID);
                if (player == null) continue;
                if (!player.isSleeping()) continue;
                if (player.ls$isDead()) continue;
                TriviaBot bot = NiceLifeTriviaManager.bots.get(playerUUID);
                if (bot == null) continue;
                if (!bot.isAlive()) continue;
                if (!(bot.triviaHandler instanceof NiceLifeTriviaHandler triviaHandler)) continue;
                if (triviaHandler.currentState == NiceLifeTriviaHandler.BotState.FINISHED) continue;
                remainingTriviaPlayers.add(player);
            }
            if (remainingTriviaPlayers.isEmpty()) {
                sleepThroughNight();
            }
        }
        if (!reachedPreSunset && isTimeBetween(11800, 13000)) {
            if (!NiceLifeVotingManager.niceListMembers.isEmpty()) {
                NiceLifeVotingManager.warnNiceListMembers();
            }
        }
        if (!reachedSunset && isSunset()) {
            NiceLifeVotingManager.endListsIfNecessary();
        }
        reachedSunset = isSunset();
        reachedPreSunset = isTimeBetween(11800, 13000);

        boolean shouldRedWinter = shouldRedWinter();
        if (redWinter != shouldRedWinter) {
            redWinter = shouldRedWinter;
            if (redWinter) {
                triggerRedWinter();
            }
            else {
                Season.setSkyColor(null, false);
                Season.setFogColor(null, false);
                Season.setCloudColor(null, false);
            }
        }
        if (timePassed.getTicks() % 20 == 0 && CompatibilityManager.voicechatLoaded()) {
            VoicechatMain.niceLifeTick();
        }
    }

    public static boolean areEnoughSleeping(int percentage) {
        List<ServerPlayer> players = Main.livesManager.getAlivePlayers();
        int allPlayers = 0;
        int sleepingPlayers = 0;
        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;
            allPlayers++;
            if (player.isSleeping()) {
                sleepingPlayers++;
            }
        }
        return sleepingPlayers >= Math.max(1, Mth.ceil((float)(allPlayers * percentage) / 100.0F));
    }

    public static void postponeTriviaStart(Time time) {
        if (!triviaCannotStartFor.isLarger(time)) {
            triviaCannotStartFor = time;
        }
    }

    @Override
    public void tickSessionOn(MinecraftServer server) {
        super.tickSessionOn(server);
        if (timePassed.isMultipleOf(naughtyListGlowTimeInterval)) {
            MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, naughtyListGlowTime.getTicks(), 0);
            for (UUID uuid : NiceLifeVotingManager.naughtyListMembers) {
                ServerPlayer player = PlayerUtils.getPlayer(uuid);
                if (player != null) {
                    player.addEffect(glowing);
                }
            }
        }
    }

    public void triggerRedWinter() {
        TaskScheduler.scheduleTask(20, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(),
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_red_winter")),
                    1f, 1);
        });
        TaskScheduler.scheduleTask(20 + 12, () -> {
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§eThe last yellow falls.."), 15, 65, 15);
        });
        TaskScheduler.scheduleTask(20 + 108, () -> {
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cRed winter is here.."), 15, 40, 15);
        });
        TaskScheduler.scheduleTask(20 + 215, () -> {
            SimplePackets.FAKE_THUNDER.sendToClient(7);
        });
        TaskScheduler.scheduleTask(20 + 224, () -> {
            Season.setSkyColor(new Vec3(15, -140, -255), false);
            Season.setFogColor(new Vec3(40, -104, -163), false);
            Season.setCloudColor(new Vec3(-255, -255, -255), true);
        });
    }

    public boolean shouldRedWinter() {
        if (currentSession.statusNotStarted()) return false;
        List<ServerPlayer> nonRedPlayers = livesManager.getNonRedPlayers();
        return nonRedPlayers.isEmpty();
    }

    public void sleepThroughNight() {
        if (server == null) return;
        ServerLevel overworld = server.overworld();
        //? if <= 1.21.11 {
        long newTime = overworld.getDayTime() + 24000L;
        overworld.setDayTime(newTime - newTime % 24000L);
        //?} else {
        /*long newTime = overworld.getOverworldClockTime() + 24000L;
        overworld.clockManager().setTotalTicks(overworld.registryAccess().getOrThrow(WorldClocks.OVERWORLD), newTime - newTime % 24000L);
        *///?}
        wakeUpAllPlayers();
        playedMidnightChimes = false;
        NiceLifeTriviaManager.endTrivia();
    }

    public void wakeUpAllPlayers() {
        if (server == null) return;
        ServerLevel overworld = server.overworld();
        if (overworld instanceof ServerLevelAccessor accessor) {
            accessor.ls$wakeUpAllPlayers();
        }
        // Just to be safe..
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (player.isSleeping()) {
                player.stopSleepInBed(false, true);
            }
            SimplePackets.REMOVE_SLEEP_SCREENS.target(player).sendToClient();
        }
    }


    @Override
    public boolean sessionStart() {
        super.sessionStart();
        NiceLifeTriviaManager.sessionStart();
        NiceLifeVotingManager.endListsIfNecessary();
        wakeUpAllPlayers();
        playedMidnightChimes = false;
        return true;
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        NiceLifeTriviaManager.sessionEnd();
        NiceLifeVotingManager.endListsIfNecessary();
        wakeUpAllPlayers();
    }

    public boolean isMidnight() {
        return isTimeBetween(18000, 20000);
    }

    public boolean isSunset() {
        return isTimeBetween(13000, 20000);
    }

    public boolean isTimeBetween(int minTime, int maxTime) {
        if (server == null) return false;
        //? if <= 1.21.11 {
        long dayTime = server.overworld().getDayTime() % 24000L;
        //?} else {
        /*long dayTime = server.overworld().getOverworldClockTime() % 24000L;
        *///?}
        return dayTime >= minTime && dayTime <= maxTime;
    }

    public void tickChunk(ServerLevel level, ChunkPos chunkPos) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        if (currentSession.statusStarted()  || SNOW_WHEN_NOT_IN_SESSION) {
            int minX = chunkPos.getMinBlockX();
            int maxX = chunkPos.getMinBlockZ();

            for (int i = 0; i < precipitationTicks; i++) {
                if (rnd.nextDouble() <= chancePerTick) {
                    customPrecipitation(level, level.getBlockRandomPos(minX, 0, maxX, 15));
                }
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
                            BlockState newState = Blocks.SNOW_BLOCK.defaultBlockState();
                            level.setBlockAndUpdate(topPos, newState);
                            Block.pushEntitiesUp(state, newState, level, topPos);
                        }
                        else {

                            /*
                                While this would make the overall area more consistent in snow height,
                                brand-new blocks would have a 3 layer difference in accumulation.
                             */
                            //int addLayers = Math.min(3, currentMaxSnowLayers - currentLayers);
                            //int newLayers = Math.min(7, currentLayers + addLayers);
                            //BlockState newState = state.setValue(SnowLayerBlock.LAYERS, newLayers);

                            BlockState newState = state.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
                            level.setBlockAndUpdate(topPos, newState);
                            Block.pushEntitiesUp(state, newState, level, topPos);
                        }
                    }
                }
                else if (!(level.getBlockState(belowPos).is(Blocks.SNOW_BLOCK) && currentMaxSnowLayers == 8)) {
                    BlockState newState = Blocks.SNOW.defaultBlockState();
                    level.setBlockAndUpdate(topPos, newState);
                    Block.pushEntitiesUp(state, newState, level, topPos);
                }
            }
        }

        //? if <= 1.21 {
        /*Biome.Precipitation precipitation = biome.getPrecipitationAt(belowPos);
        *///?} else {
        Biome.Precipitation precipitation = biome.getPrecipitationAt(belowPos, level.getSeaLevel());
         //?}
        if (precipitation != Biome.Precipitation.NONE) {
            BlockState belowState = level.getBlockState(belowPos);
            belowState.getBlock().handlePrecipitation(belowState, level, belowPos, precipitation);
        }
    }

    private boolean shouldFreeze(ServerLevel level, BlockPos blockPos) {
        boolean darkEnough = level.getBrightness(LightLayer.BLOCK, blockPos) < 10 || !LIGHT_MELTS_SNOW;
        //? if <= 1.21 {
        /*int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        *///?} else {
        int minY = level.getMinY();
        int maxY = level.getMaxY();
        //?}
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
        /*int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        *///?} else {
        int minY = level.getMinY();
        int maxY = level.getMaxY();
        //?}
        if (blockPos.getY() >= minY && blockPos.getY() < maxY && darkEnough) {
            BlockState blockState = level.getBlockState(blockPos);
            boolean canSnowOverride = blockState.isAir() ||
                    blockState.is(Blocks.SNOW) ||
                    //? if <= 1.20.2 {
                    /*blockState.is(Blocks.GRASS) ||
                    *///?} else {
                    blockState.is(Blocks.SHORT_GRASS) ||
                    //?}
                    //? if >= 1.21.5 {
                    blockState.is(Blocks.LEAF_LITTER) ||
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

    public static final BlockState blueIce = Blocks.BLUE_ICE.defaultBlockState();
    public static final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();
    public static final BlockState air = Blocks.AIR.defaultBlockState();
    public static final BlockState froglight = Blocks.PEARLESCENT_FROGLIGHT.defaultBlockState();
    public BlockState transformBlockState(BlockState oldState) {
        if (oldState.is(Blocks.NETHERRACK) ||
                oldState.is(Blocks.BLACKSTONE) ||
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
            return blueIce;
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

    @Override
    public String getTeamForPlayer(ServerPlayer player) {
        if (NiceLifeVotingManager.naughtyListMembers.contains(player.getUUID())) {
            return NiceLifeVotingManager.NAUGHTY_LIST_TEAM;
        }
        if (NiceLifeVotingManager.niceListMembers.contains(player.getUUID())) {
            return NiceLifeVotingManager.NICE_LIST_TEAM;
        }

        return super.getTeamForPlayer(player);
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayer attacker, ServerPlayer victim, boolean allowSelfDefense) {
        if (NiceLifeVotingManager.niceListMembers.contains(victim.getUUID())) {
            return false;
        }
        if (NiceLifeVotingManager.naughtyListMembers.contains(victim.getUUID())) {
            return true;
        }
        return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
    }

    @Override
    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        super.onPlayerDeath(player, source);
        player.removeTag("naughty_list");
        NiceLifeVotingManager.naughtyListMembers.remove(player.getUUID());
        if (player.ls$isDead()) {
            player.removeTag("nice_list");
            NiceLifeVotingManager.niceListMembers.remove(player.getUUID());
        }
        reloadPlayerTeam(player);
    }
}
