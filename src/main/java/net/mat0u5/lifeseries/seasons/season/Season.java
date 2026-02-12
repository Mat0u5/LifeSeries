package net.mat0u5.lifeseries.seasons.season;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.command.manager.CommandManager;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.*;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;
import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;

//? if <= 1.20
/*import net.minecraft.world.scores.Scoreboard;*/
//? if <= 1.21.9
//import net.minecraft.world.level.GameRules;
//? if > 1.21.9
import net.minecraft.world.level.gamerules.GameRules;
//? if > 1.20
import net.minecraft.world.scores.DisplaySlot;
//? if <= 1.21.9 {
/*import net.minecraft.world.entity.monster.WitherSkeleton;
*///?} else {
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
//?}

public abstract class Season {
    public static final String RESOURCEPACK_MAIN_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-main-27d9e98eb6009401319a5256f7695f1bba902412/main.zip";
    public static final String RESOURCEPACK_MAIN_SHA = "6b3b1e6225156de119926dd32003060b42dbab82";
    public static final String RESOURCEPACK_SECRETLIFE_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-secretlife-fc0fa2a3efe2aefdba5a3c0deda61039fc43a008/secretlife.zip";
    public static final String RESOURCEPACK_SECRETLIFE_SHA = "1befd668fa775f2b8715b348172e1ba776e57294";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-minimal_armor-27d9e98eb6009401319a5256f7695f1bba902412/minimal_armor.zip";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_SHA = "e078d9085ea74891ebc4f8ec5686c4da08176a38";
    public static final String RESOURCEPACK_COMBINED_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-combined-27d9e98eb6009401319a5256f7695f1bba902412/combined.zip";
    public static final String RESOURCEPACK_COMBINED_SHA = "d9ef5b0c1074c006820494ca0524de8f8810a77c";

    public int GIVELIFE_MAX_LIVES = 99;
    public boolean TAB_LIST_SHOW_DEAD_PLAYERS = true;
    public boolean TAB_LIST_SHOW_LIVES = false;
    public static boolean TAB_LIST_SHOW_EXACT_LIVES = false;
    public static boolean SHOW_HEALTH_BELOW_NAME = false;
    public boolean WATCHERS_IN_TAB = true;
    public boolean MUTE_DEAD_PLAYERS = false;
    public boolean WATCHERS_MUTED = false;
    public boolean ALLOW_SELF_DEFENSE = true;
    public static boolean GIVELIFE_CAN_REVIVE = false;
    public boolean SHOW_LOGIN_COMMAND_INFO = true;
    public boolean HIDE_UNJUSTIFIED_KILL_MESSAGES = false;
    public static boolean reloadPlayerTeams = false;
    private static boolean BROADCAST_LIFE_GAIN = false;
    private double ADDITIONAL_WITHER_SKULL_RATE = 0.05;
    public static Random rnd = new Random();
    public static Vec3 skyColor = null;
    public static boolean skyColorSetMode = false;
    public static Vec3 fogColor = null;
    public static boolean fogColorSetMode = false;
    public static Vec3 cloudColor = null;
    public static boolean cloudColorSetMode = false;

    public BoogeymanManager boogeymanManager = createBoogeymanManager();
    public SecretSociety secretSociety = createSecretSociety();
    public LivesManager livesManager = createLivesManager();

    public abstract Seasons getSeason();
    public abstract ConfigManager createConfig();

    public Blacklist createBlacklist() {
        return new Blacklist();
    }

    public BoogeymanManager createBoogeymanManager() {
        return new BoogeymanManager();
    }
    public SecretSociety createSecretSociety() {
        return new SecretSociety();
    }

    public LivesManager createLivesManager() {
        return new LivesManager();
    }

    public Integer getDefaultLives() {
        if (livesManager.ROLL_LIVES) {
            return null;
        }
        return seasonConfig.DEFAULT_LIVES.get();
    }

    public void initialize() {
        reload();
    }

    public void seasonSwitched(Seasons changedTo) {
    }

    public void reloadStart() {
    }

    public void updateStuff() {
        if (server == null) return;

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld != null && overworld.getWorldBorder().getStatus() == BorderStatus.STATIONARY) overworld.getWorldBorder().setSize(seasonConfig.WORLDBORDER_SIZE.get());
        //? if >= 1.21.9 {
        ServerLevel nether = server.getLevel(Level.NETHER);
        ServerLevel end = server.getLevel(Level.END);
        if (nether != null && nether.getWorldBorder().getStatus() == BorderStatus.STATIONARY) nether.getWorldBorder().setSize(seasonConfig.WORLDBORDER_NETHER_SIZE.get());
        if (end != null && end.getWorldBorder().getStatus() == BorderStatus.STATIONARY) end.getWorldBorder().setSize(seasonConfig.WORLDBORDER_END_SIZE.get());
        //?}

        if (overworld != null) {
            //? if <= 1.21.9 {
            /*OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_KEEPINVENTORY, seasonConfig.KEEP_INVENTORY.get());
            OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_ANNOUNCE_ADVANCEMENTS, seasonConfig.SHOW_ADVANCEMENTS.get());
            OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_NATURAL_REGENERATION, getSeason() != Seasons.SECRET_LIFE);
            *///?} else {
            OtherUtils.setBooleanGameRule(overworld, GameRules.KEEP_INVENTORY, seasonConfig.KEEP_INVENTORY.get());
            OtherUtils.setBooleanGameRule(overworld, GameRules.SHOW_ADVANCEMENT_MESSAGES, seasonConfig.SHOW_ADVANCEMENTS.get());
            OtherUtils.setBooleanGameRule(overworld, GameRules.NATURAL_HEALTH_REGENERATION, getSeason() != Seasons.SECRET_LIFE);
            //?}

            //? if >= 1.21.6 {
            boolean locatorBarEnabled = seasonConfig.LOCATOR_BAR.get();
            if (!locatorBarEnabled && this instanceof DoubleLife) {
                locatorBarEnabled = DoubleLife.SOULMATE_LOCATOR_BAR;
            }
            if (!locatorBarEnabled && boogeymanManager.BOOGEYMAN_ENABLED) {
                locatorBarEnabled = boogeymanManager.BOOGEYMAN_LOCATOR_BAR;
            }
            //? if <= 1.21.9 {
            /*OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_LOCATOR_BAR, locatorBarEnabled);
            *///?} else {
            OtherUtils.setBooleanGameRule(overworld, GameRules.LOCATOR_BAR, locatorBarEnabled);
            //?}
            //?}
        }

        //? if <= 1.20 {
        /*int listSlot = Scoreboard.DISPLAY_SLOT_LIST;
        int belowNameSlot = Scoreboard.DISPLAY_SLOT_BELOW_NAME;
        *///?} else {
        DisplaySlot listSlot = DisplaySlot.LIST;
        DisplaySlot belowNameSlot = DisplaySlot.BELOW_NAME;
        //?}

        Objective currentListObjective = ScoreboardUtils.getObjectiveInSlot(listSlot);
        if (TAB_LIST_SHOW_LIVES) {
            ScoreboardUtils.setObjectiveInSlot(listSlot, LivesManager.SCOREBOARD_NAME);
        }
        else if (currentListObjective != null) {
            if (currentListObjective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                ScoreboardUtils.setObjectiveInSlot(listSlot, null);
            }
        }

        Objective currentBelowNameObjective = ScoreboardUtils.getObjectiveInSlot(belowNameSlot);
        if (getSeason() == Seasons.LIMITED_LIFE && LimitedLife.SHOW_TIME_BELOW_NAME) {
            ScoreboardUtils.setObjectiveInSlot(belowNameSlot, LivesManager.SCOREBOARD_NAME);
        }
        else if (SHOW_HEALTH_BELOW_NAME) {
            ScoreboardUtils.setObjectiveInSlot(belowNameSlot, "HP");
        }
        else if (currentBelowNameObjective != null) {
            if (currentBelowNameObjective.getName().equals("HP")) {
                ScoreboardUtils.setObjectiveInSlot(belowNameSlot, null);
            }
            if (currentBelowNameObjective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                ScoreboardUtils.setObjectiveInSlot(belowNameSlot, null);
            }
        }

        if (getSeason() != Seasons.SIMPLE_LIFE) {
            OtherUtils.executeCommand("/kill @e[type=wandering_trader,tag=SimpleLifeTrader]");
        }
    }

    public void reload() {
        MUTE_DEAD_PLAYERS = seasonConfig.MUTE_DEAD_PLAYERS.get();
        GIVELIFE_MAX_LIVES = seasonConfig.GIVELIFE_LIVES_MAX.get();
        TAB_LIST_SHOW_LIVES = seasonConfig.TAB_LIST_SHOW_LIVES.get();
        TAB_LIST_SHOW_DEAD_PLAYERS = seasonConfig.TAB_LIST_SHOW_DEAD_PLAYERS.get();
        TAB_LIST_SHOW_EXACT_LIVES = seasonConfig.TAB_LIST_SHOW_EXACT_LIVES.get();
        SHOW_HEALTH_BELOW_NAME = seasonConfig.SHOW_HEALTH_BELOW_NAME.get();
        WATCHERS_IN_TAB = seasonConfig.WATCHERS_IN_TAB.get();
        WATCHERS_MUTED = seasonConfig.WATCHERS_MUTED.get();
        ALLOW_SELF_DEFENSE = seasonConfig.ALLOW_SELF_DEFENSE.get();
        GIVELIFE_CAN_REVIVE = seasonConfig.GIVELIFE_CAN_REVIVE.get();
        SHOW_LOGIN_COMMAND_INFO = seasonConfig.SHOW_LOGIN_COMMAND_INFO.get();
        HIDE_UNJUSTIFIED_KILL_MESSAGES = seasonConfig.HIDE_UNJUSTIFIED_KILL_MESSAGES.get();
        Session.TICK_FREEZE_NOT_IN_SESSION = seasonConfig.TICK_FREEZE_NOT_IN_SESSION.get();
        Session.WORLDBORDER_OUTSIDE_TELEPORT = seasonConfig.WORLDBORDER_OUTSIDE_TELEPORT.get();
        Session.SESSION_START_COUNTDOWN = seasonConfig.SESSION_START_COUNTDOWN.get();
        BROADCAST_LIFE_GAIN = seasonConfig.BROADCAST_LIFE_GAIN.get();
        ADDITIONAL_WITHER_SKULL_RATE = seasonConfig.ADDITIONAL_WITHER_SKULL_RATE.get();

        NetworkHandlerServer.reload();
        boogeymanManager.onReload();
        secretSociety.onReload();
        createTeams();
        createScoreboards();
        updateStuff();
        reloadAllPlayerTeams();
        reloadPlayers();
        Events.updatePlayerListsNextTick = true;
        WatcherManager.reloadWatchers();
        livesManager.reload();
        currentSession.freezeIfNecessary();
        DatapackIntegration.reload();
        PlayerUtils.resendCommandTrees();
        SubInManager.reload();
    }

    public static void setSkyColor(Vec3 color, boolean setMode) {
        skyColor = color;
        skyColorSetMode = setMode;
        NetworkHandlerServer.sendUpdatePackets();
    }

    public static void setFogColor(Vec3 color, boolean setMode) {
        fogColor = color;
        fogColorSetMode = setMode;
        NetworkHandlerServer.sendUpdatePackets();
    }

    public static void setCloudColor(Vec3 color, boolean setMode) {
        cloudColor = color;
        cloudColorSetMode = setMode;
        NetworkHandlerServer.sendUpdatePackets();
    }

    public String getAdminCommands() {
        List<String> allCommands = new ArrayList<>();
        for (Command command : CommandManager.commands) {
            if (!command.isAllowed()) continue;
            for (String commandStr : command.getAdminCommands()) {
                allCommands.add("/"+commandStr);
            }
        }
        return String.join(", ", allCommands);
    }

    public String getNonAdminCommands() {
        List<String> allCommands = new ArrayList<>();
        for (Command command : CommandManager.commands) {
            if (!command.isAllowed()) continue;
            for (String commandStr : command.getNonAdminCommands()) {
                allCommands.add("/"+commandStr);
            }
        }
        return String.join(", ", allCommands);
    }

    public void sendSetSeasonPacket(ServerPlayer player) {
        SimplePackets.SEASON_INFO.target(player).sendToClient(List.of(currentSeason.getSeason().getId(), currentSeason.getAdminCommands(), currentSeason.getNonAdminCommands()));
    }

    public void reloadPlayers() {
        PlayerUtils.getAllPlayers().forEach(AttributeUtils::resetAttributesOnPlayerJoin);
    }

    public void createTeams() {
        Collection<PlayerTeam> allTeams = TeamUtils.getAllTeams();
        if (allTeams != null) {
            for (PlayerTeam team : allTeams) {
                if (team.getName().startsWith("creaking_")) {
                    TeamUtils.deleteTeam(team.getName());
                }
            }
        }

        WatcherManager.createTeams();
        livesManager.createTeams();
        TeamUtils.createTeam("zombie", "Zombie", ChatFormatting.GRAY);
    }


    public void createScoreboards() {
        ScoreboardUtils.createObjective("HP", "§c❤", ObjectiveCriteria.HEALTH);
        WatcherManager.createScoreboards();
        livesManager.createScoreboards();
        DatapackIntegration.createScoreboards();
    }

    public void reloadAllPlayerTeams() {
        PlayerUtils.getAllPlayers().forEach(this::reloadPlayerTeam);
    }

    public void reloadPlayerTeam(ServerPlayer player) {
        reloadPlayerTeam(player, false);
    }

    private void reloadPlayerTeam(ServerPlayer player, boolean waited) {
        if (player == null) return;

        if (!player.isAlive() && !waited) {
            TaskScheduler.scheduleTask(1, () -> reloadPlayerTeam(player, true));
            return;
        }

        String team = getTeamForPlayer(player);
        Team currentTeam = player.getTeam();

        if (team != null && (currentTeam == null || !currentTeam.getName().equals(team))) {
            TeamUtils.addEntityToTeam(team, player);
            playerChangedTeam(player);
        }
    }

    public void playerChangedTeam(ServerPlayer player) {
        Events.updatePlayerListsNextTick = true;
    }

    public String getTeamForPlayer(ServerPlayer player) {
        if (isWatcher(player)) {
            return WatcherManager.TEAM_NAME;
        }

        return livesManager.getTeamForPlayer(player);
    }

    public boolean modifyKeepInventory(ServerPlayer player, boolean originalKeepInventory) {
        return originalKeepInventory;
    }

    public void dropItemsOnLastDeath(ServerPlayer player) {
        boolean doDrop = seasonConfig.PLAYERS_DROP_ITEMS_ON_FINAL_DEATH.get();
        //? if <= 1.21.9 {
        /*boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.RULE_KEEPINVENTORY);
        *///?} else {
        boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.KEEP_INVENTORY);
        //?}

        if (doDrop && keepInventory) {
            for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
                //? if <= 1.21 {
                /*player.spawnAtLocation(item);
                *///?} else
                player.spawnAtLocation(player.ls$getServerLevel(), item);
            }
            player.getInventory().clearContent();
        }
    }

    public boolean isAllowedToAttack(ServerPlayer attacker, ServerPlayer victim) {
        return isAllowedToAttack(attacker, victim, ALLOW_SELF_DEFENSE);
    }

    public boolean isAllowedToAttack(ServerPlayer attacker, ServerPlayer victim, boolean allowSelfDefense) {
        if (attacker.ls$isOnLastLife(false)) {
            return true;
        }
        if (boogeymanManager.isBoogeymanThatCanBeCured(attacker, victim)) {
            return true;
        }
        if (allowSelfDefense) {
             if (attacker.getKillCredit() == victim && isAllowedToAttack(victim, attacker, false)) {
                 return true;
             }
        }

        Team team = attacker.getTeam();
        if (team != null) {
            Integer canKillLives = livesManager.getTeamCanKill(team.getName());
            if (canKillLives != null && victim.ls$isOnAtLeastLives(canKillLives, false)) {
                return true;
            }
        }

        return false;
    }

    public void sessionEnd() {
        boogeymanManager.sessionEnd();
        secretSociety.sessionEnd();
        livesManager.assignedLives = false;
    }

    public boolean sessionStart() {
        boogeymanManager.resetBoogeymen();
        secretSociety.resetMembers();
        addSessionActions();
        livesManager.assignedLives = false;
        return true;
    }

    public void sessionChangeStatus(SessionStatus newStatus) {
    }

    private Time timer = Time.zero();
    public void tick(MinecraftServer server) {
        timer.tick();
        boogeymanManager.tick();
        secretSociety.tick();
        if (timer.isMultipleOf(Time.seconds(5)) || reloadPlayerTeams) {
            reloadPlayerTeams = false;
            reloadAllPlayerTeams();
        }
    }
    public void tickSessionOn(MinecraftServer server) {}
    public void addSessionActions() {
        boogeymanManager.addSessionActions();
        secretSociety.addSessionActions();
        livesManager.addSessionActions();
    }

    /*
        Events
     */

    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        boolean soulmateKill = source.is(DoubleLife.SOULMATE_DAMAGE);
        SessionTranscript.onPlayerDeath(player, source);
        boolean killedByPlayer = false;
        if (source.getEntity() instanceof ServerPlayer serverAttacker) {
            if (player != source.getEntity() && !soulmateKill) {
                onPlayerKilledByPlayer(player, serverAttacker);
                killedByPlayer = true;
            }
        }
        if (player.getKillCredit() != null && !killedByPlayer) {
            if (player.getKillCredit() instanceof ServerPlayer serverAdversary) {
                if (player != player.getKillCredit() && !soulmateKill) {
                    onPlayerKilledByPlayer(player, serverAdversary);
                    killedByPlayer = true;
                }
            }
        }
        if (!killedByPlayer) {
            onPlayerDiedNaturally(player, source);
        }
        DatapackIntegration.EVENT_PLAYER_DEATH.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        if (!DatapackIntegration.EVENT_PLAYER_DEATH.isCanceled() && livesManager.canChangeLivesNaturally(player) && player.ls$hasAssignedLives()) {
            player.ls$removeLife();
        }
    }

    public void onPlayerDiedNaturally(ServerPlayer player, DamageSource source) {
        if (source.getEntity() instanceof TamableAnimal tamableAnimal) {
            //? if <= 1.21.4 {
            /*ServerPlayer owner = PlayerUtils.getPlayer(tamableAnimal.getOwnerUUID());
            *///?} else {
            ServerPlayer owner = null;
            if (tamableAnimal.getOwnerReference() != null) owner = PlayerUtils.getPlayer(tamableAnimal.getOwnerReference().getUUID());
            //?}
            if (owner != null) {
                //? if <= 1.21.2 {
                /*owner.awardKillScore(player, 1, source);
                *///?} else {
                owner.awardKillScore(player, source);
                //?}
            }
        }
        if (server == null) return;
        currentSession.playerNaturalDeathLog.remove(player.getUUID());
        currentSession.playerNaturalDeathLog.put(player.getUUID(), server.getTickCount());
    }

    public final Map<UUID, HashMap<Vec3,List<Float>>> respawnPositions = new HashMap<>();
    public void onPlayerRespawn(ServerPlayer player) {
        if (!respawnPositions.containsKey(player.getUUID())) return;
        HashMap<Vec3, List<Float>> info = respawnPositions.get(player.getUUID());
        respawnPositions.remove(player.getUUID());
        if (player.ls$isAlive()) return;
        for (Map.Entry<Vec3, List<Float>> entry : info.entrySet()) {
            Vec3 pos = entry.getKey();
            //? if <= 1.21 {
            /*int minY = player.ls$getServerLevel().getMinBuildHeight();
            *///?} else {
            int minY = player.ls$getServerLevel().getMinY();
            //?}
            if (pos.y <= minY) continue;

            LevelUtils.teleport(player, player.ls$getServerLevel(), pos, entry.getValue().get(0), entry.getValue().get(1));
            break;
        }
    }

    public void onClaimKill(ServerPlayer killer, ServerPlayer victim) {
        SessionTranscript.claimKill(killer, victim);
        boolean isBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        if (isBoogeyCure) {
            boogeymanManager.onBoogeymanKill(killer, victim);
        }

        DatapackIntegration.EVENT_CLAIM_KILL.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Killer", killer.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("Victim", victim.getScoreboardName())
        ));
        if (!DatapackIntegration.EVENT_CLAIM_KILL.isCanceled() && !isBoogeyCure) {
            tryClaimKillLifeGain(killer, victim);
        }

        //? if <= 1.21.2 {
        /*killer.awardKillScore(victim, 1, killer.damageSources().playerAttack(killer));
        *///?} else {
        killer.awardKillScore(victim, killer.damageSources().playerAttack(killer));
        //?}
    }

    public void tryClaimKillLifeGain(ServerPlayer killer, ServerPlayer victim) {
        Team team = killer.getTeam();
        if (team != null) {
            Integer canGainLife = livesManager.getTeamGainLives(team.getName());
            Integer victimLives = victim.ls$getLives();
            if (canGainLife != null && victimLives != null && victimLives > 0) {
                if (victimLives + 1 >= canGainLife) { // +1 because the victim already lost a life
                    broadcastLifeGain(killer, victim);
                    killer.ls$addLife();
                }
            }
        }
    }

    public void tryKillLifeGain(ServerPlayer killer, ServerPlayer victim) {
        Team team = killer.getTeam();
        if (team != null) {
            Integer canGainLife = livesManager.getTeamGainLives(team.getName());
            if (canGainLife != null && victim.ls$isOnAtLeastLives(canGainLife, false)) {
                broadcastLifeGain(killer, victim);
                killer.ls$addLife();
            }
        }
    }


    public void broadcastLifeGain(ServerPlayer player, ServerPlayer victim) {
        if (BROADCAST_LIFE_GAIN) {
            PlayerUtils.broadcastMessage(TextUtils.format("{}§7 gained a life for killing {}.", player, victim));
        }
    }

    public void onPlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfo ci) {
        DatapackIntegration.EVENT_PLAYER_TAKE_DAMAGE.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("Amount", String.valueOf(amount))
        ));
    }

    public void onPrePlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypes.OUTSIDE_BORDER)) {
            cir.setReturnValue(false);
        }
    }

    public void onPlayerHeal(ServerPlayer player, float amount) {
    }

    public void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        boolean isAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean isBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);

        if (!isAllowedToAttack(killer, victim) && !HIDE_UNJUSTIFIED_KILL_MESSAGES) {
            PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§c [Unjustified Kill?] {}§7 was killed by {}", victim, killer));
        }

        if (isBoogeyCure) {
            boogeymanManager.onBoogeymanKill(killer, victim);
        }
        SessionTranscript.onPlayerKilledByPlayer(victim, killer);

        DatapackIntegration.EVENT_PLAYER_PVP_KILLED.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Killer", killer.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("Victim", victim.getScoreboardName())
        ));
        if (!DatapackIntegration.EVENT_PLAYER_PVP_KILLED.isCanceled() && !isBoogeyCure && isAllowedToAttack) {
            tryKillLifeGain(killer, victim);
        }
    }

    public void onMobDeath(LivingEntity entity, DamageSource damageSource) {
    }

    public void onEntityDropItems(LivingEntity entity, DamageSource damageSource, CallbackInfo ci) {
        modifyEntityDrops(entity, damageSource, ci);
    }

    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource, CallbackInfo ci) {
        if (!entity.level().isClientSide() && (damageSource.getEntity() instanceof ServerPlayer)) {
            spawnEggChance(entity);
            if (entity instanceof WitherSkeleton && rnd.nextDouble() <= ADDITIONAL_WITHER_SKULL_RATE) {
                ItemStack skullItem = Items.WITHER_SKELETON_SKULL.getDefaultInstance();
                //? if <=1.21 {
                /*entity.spawnAtLocation(skullItem);
                *///?} else
                entity.spawnAtLocation((ServerLevel) entity.level(), skullItem);
                ci.cancel();
            }
        }
    }

    private void spawnEggChance(LivingEntity entity) {
        double chance = seasonConfig.SPAWN_EGG_DROP_CHANCE.get();
        boolean onlyNatural = seasonConfig.SPAWN_EGG_DROP_ONLY_NATURAL.get();
        if (chance <= 0) return;
        if (entity instanceof EnderDragon) return;
        if (entity instanceof WitherBoss) return;
        if (entity instanceof Warden) return;
        if (entity instanceof ElderGuardian) return;
        if (entity instanceof Snail) return;
        if (entity instanceof TriviaBot) return;
        //? if <= 1.21.11 {
        if (entity.getTags().contains("notNatural") && onlyNatural) return;
        //?} else {
        /*if (entity.entityTags().contains("notNatural") && onlyNatural) return;
        *///?}

        EntityType<?> entityType = entity.getType();

        //? if <= 1.21.11 {
        SpawnEggItem spawnEgg = SpawnEggItem.byId(entityType);
        if (spawnEgg == null) return;
        ItemStack spawnEggItem = spawnEgg.getDefaultInstance();
        //?} else {
        /*ItemStack spawnEggItem = SpawnEggItem.byId(entityType).map(ItemStack::new).orElse(null);
        *///?}

        if (spawnEggItem == null) return;
        if (spawnEggItem.isEmpty()) return;

        if (Math.random() <= chance) {
            //? if <=1.21 {
            /*entity.spawnAtLocation(spawnEggItem);
            *///?} else
            entity.spawnAtLocation((ServerLevel) entity.level(), spawnEggItem);
        }
    }

    public void learnRecipes() {
        OtherUtils.executeCommand("recipe give @a lifeseries:name_tag_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:saddle_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:spawner_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:tnt_recipe_variation");
        OtherUtils.executeCommand("recipe give @a lifeseries:bundle_recipe");
    }

    public void onPlayerJoin(ServerPlayer player) {
        AttributeUtils.resetAttributesOnPlayerJoin(player);
        reloadPlayerTeam(player);
        TaskScheduler.scheduleTask(2, () -> PlayerUtils.applyResourcepack(player.getUUID()));
        if (!player.ls$hasAssignedLives()) {
            assignDefaultLives(player);
        }
        if (player.ls$hasAssignedLives() && player.ls$isDead() && !PermissionManager.isAdmin(player)) {
            player.setGameMode(GameType.SPECTATOR);
        }

        if (player.ls$isWatcher()) {
            if (this instanceof DoubleLife doubleLife) {
                doubleLife.resetSoulmate(player);
            }
        }

        TaskScheduler.scheduleTask(1, () -> {
            if (SubInManager.isBeingSubstituted(player.getUUID())) {
                SubInManager.removeSubIn(player);
            }
            if (SubInManager.isSubbingIn(player.getUUID())) {
                SubInManager.reloadPlayerProfile(player);
            }
        });
    }

    public void assignDefaultLives(ServerPlayer player) {
        Integer lives = getDefaultLives();
        if (lives != null) {
            player.ls$setLives(lives);
        }
    }

    public void onPlayerFinishJoining(ServerPlayer player) {
        if (getSeason() != Seasons.UNASSIGNED && SHOW_LOGIN_COMMAND_INFO && !Main.modDisabled()) {
            if (PermissionManager.isAdmin(player)) {
                player.sendSystemMessage(TextUtils.formatLoosely("§7{} commands: §r{}", getSeason().getName(), getAdminCommands()));
            }
            else {
                player.sendSystemMessage(TextUtils.formatLoosely("§7{} non-admin commands: §r{}", getSeason().getName(), getNonAdminCommands()));
            }
        }

        learnRecipes();
        if (currentSession.statusNotStarted() && PermissionManager.isAdmin(player) && !Main.modDisabled()) {
            player.sendSystemMessage(Component.nullToEmpty("\nUse §b'/session timer set <time>'§f to set the desired session time."));
            player.sendSystemMessage(Component.nullToEmpty("After that, use §b'/session start'§f to start the session."));
        }
        boogeymanManager.onPlayerFinishJoining(player);
        livesManager.onPlayerFinishJoining(player);
    }

    public void onPlayerDisconnect(ServerPlayer player) {
    }

    public void onRightClickEntity(ServerPlayer player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
    }

    public void onAttackEntity(ServerPlayer player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
    }

    public void onUpdatedInventory(ServerPlayer player) {
        if (blacklist != null) {
            blacklist.onInventoryUpdated(player);
        }
    }
}
