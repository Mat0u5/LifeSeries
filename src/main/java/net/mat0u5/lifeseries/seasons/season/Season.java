package net.mat0u5.lifeseries.seasons.season;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.events.Events;
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
import net.mat0u5.lifeseries.utils.player.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;
import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;
//? if <= 1.21.9
import net.minecraft.world.level.GameRules;
//? if > 1.21.9
/*import net.minecraft.world.level.gamerules.GameRules;*/

public abstract class Season {
    public static final String RESOURCEPACK_MAIN_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-main-fc0fa2a3efe2aefdba5a3c0deda61039fc43a008/main.zip";
    public static final String RESOURCEPACK_MAIN_SHA ="56d78f9818d17c461d00bee1cc505a0b2da96353";
    public static final String RESOURCEPACK_SECRETLIFE_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-secretlife-fc0fa2a3efe2aefdba5a3c0deda61039fc43a008/secretlife.zip";
    public static final String RESOURCEPACK_SECRETLIFE_SHA ="1befd668fa775f2b8715b348172e1ba776e57294";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-minimal_armor-47b0d2488897145e7fd8b0b7da48033a097148e8/minimal_armor.zip";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_SHA ="3696f47350d675bae8b09b73163dca7051ac9bd6";

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

    public BoogeymanManager boogeymanManager = createBoogeymanManager();
    public SecretSociety secretSociety = createSecretSociety();
    public LivesManager livesManager = createLivesManager();

    public abstract Seasons getSeason();
    public abstract ConfigManager createConfig();
    public abstract String getAdminCommands();
    public abstract String getNonAdminCommands();

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
        return seasonConfig.DEFAULT_LIVES.get(seasonConfig);
    }

    public void initialize() {
        reload();
    }

    public void updateStuff() {
        if (server == null) return;

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld != null) overworld.getWorldBorder().setSize(seasonConfig.WORLDBORDER_SIZE.get(seasonConfig));
        //? if >= 1.21.9 {
        /*ServerLevel nether = server.getLevel(Level.NETHER);
        ServerLevel end = server.getLevel(Level.END);
        if (nether != null) nether.getWorldBorder().setSize(seasonConfig.WORLDBORDER_NETHER_SIZE.get(seasonConfig));
        if (end != null) end.getWorldBorder().setSize(seasonConfig.WORLDBORDER_END_SIZE.get(seasonConfig));
        *///?}

        if (overworld != null) {
            //? if <= 1.21.9 {
            OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_KEEPINVENTORY, seasonConfig.KEEP_INVENTORY.get(seasonConfig));
            OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_ANNOUNCE_ADVANCEMENTS, seasonConfig.SHOW_ADVANCEMENTS.get(seasonConfig));
            OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_NATURAL_REGENERATION, getSeason() != Seasons.SECRET_LIFE);
            //?} else {
            /*OtherUtils.setBooleanGameRule(overworld, GameRules.KEEP_INVENTORY, seasonConfig.KEEP_INVENTORY.get(seasonConfig));
            OtherUtils.setBooleanGameRule(overworld, GameRules.SHOW_ADVANCEMENT_MESSAGES, seasonConfig.SHOW_ADVANCEMENTS.get(seasonConfig));
            OtherUtils.setBooleanGameRule(overworld, GameRules.NATURAL_HEALTH_REGENERATION, getSeason() != Seasons.SECRET_LIFE);
            *///?}

            //? if >= 1.21.6 {
            /*boolean locatorBarEnabled = seasonConfig.LOCATOR_BAR.get(seasonConfig);
            if (!locatorBarEnabled && this instanceof DoubleLife) {
                locatorBarEnabled = DoubleLife.SOULMATE_LOCATOR_BAR;
            }
            //? if <= 1.21.9 {
            OtherUtils.setBooleanGameRule(overworld, GameRules.RULE_LOCATOR_BAR, locatorBarEnabled);
            //?} else {
            /^OtherUtils.setBooleanGameRule(overworld, GameRules.LOCATOR_BAR, locatorBarEnabled);
            ^///?}
            *///?}
        }

        Objective currentListObjective = ScoreboardUtils.getObjectiveInSlot(DisplaySlot.LIST);
        if (TAB_LIST_SHOW_LIVES) {
            ScoreboardUtils.setObjectiveInSlot(DisplaySlot.LIST, LivesManager.SCOREBOARD_NAME);
        }
        else if (currentListObjective != null) {
            if (currentListObjective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                ScoreboardUtils.setObjectiveInSlot(DisplaySlot.LIST, null);
            }
        }

        Objective currentBelowNameObjective = ScoreboardUtils.getObjectiveInSlot(DisplaySlot.BELOW_NAME);
        if (getSeason() == Seasons.LIMITED_LIFE && LimitedLife.SHOW_TIME_BELOW_NAME) {
            ScoreboardUtils.setObjectiveInSlot(DisplaySlot.BELOW_NAME, LivesManager.SCOREBOARD_NAME);
        }
        else if (SHOW_HEALTH_BELOW_NAME) {
            ScoreboardUtils.setObjectiveInSlot(DisplaySlot.BELOW_NAME, "HP");
        }
        else if (currentBelowNameObjective != null) {
            if (currentBelowNameObjective.getName().equals("HP")) {
                ScoreboardUtils.setObjectiveInSlot(DisplaySlot.BELOW_NAME, null);
            }
            if (currentBelowNameObjective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                ScoreboardUtils.setObjectiveInSlot(DisplaySlot.BELOW_NAME, null);
            }
        }

        if (getSeason() != Seasons.SIMPLE_LIFE) {
            OtherUtils.executeCommand("/kill @e[type=wandering_trader,tag=SimpleLifeTrader]");
        }
    }

    public void reload() {
        MUTE_DEAD_PLAYERS = seasonConfig.MUTE_DEAD_PLAYERS.get(seasonConfig);
        GIVELIFE_MAX_LIVES = seasonConfig.GIVELIFE_LIVES_MAX.get(seasonConfig);
        TAB_LIST_SHOW_LIVES = seasonConfig.TAB_LIST_SHOW_LIVES.get(seasonConfig);
        TAB_LIST_SHOW_DEAD_PLAYERS = seasonConfig.TAB_LIST_SHOW_DEAD_PLAYERS.get(seasonConfig);
        TAB_LIST_SHOW_EXACT_LIVES = seasonConfig.TAB_LIST_SHOW_EXACT_LIVES.get(seasonConfig);
        SHOW_HEALTH_BELOW_NAME = seasonConfig.SHOW_HEALTH_BELOW_NAME.get(seasonConfig);
        WATCHERS_IN_TAB = seasonConfig.WATCHERS_IN_TAB.get(seasonConfig);
        WATCHERS_MUTED = seasonConfig.WATCHERS_MUTED.get(seasonConfig);
        ALLOW_SELF_DEFENSE = seasonConfig.ALLOW_SELF_DEFENSE.get(seasonConfig);
        GIVELIFE_CAN_REVIVE = seasonConfig.GIVELIFE_CAN_REVIVE.get(seasonConfig);
        SHOW_LOGIN_COMMAND_INFO = seasonConfig.SHOW_LOGIN_COMMAND_INFO.get(seasonConfig);
        HIDE_UNJUSTIFIED_KILL_MESSAGES = seasonConfig.HIDE_UNJUSTIFIED_KILL_MESSAGES.get(seasonConfig);
        Session.TICK_FREEZE_NOT_IN_SESSION = seasonConfig.TICK_FREEZE_NOT_IN_SESSION.get(seasonConfig);

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
    }


    public void createScoreboards() {
        ScoreboardUtils.createObjective("HP", "§c❤", ObjectiveCriteria.HEALTH);
        WatcherManager.createScoreboards();
        livesManager.createScoreboards();
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
        PlayerTeam currentTeam = player.getTeam();

        if (currentTeam == null || !currentTeam.getName().equals(team)) {
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


    public void dropItemsOnLastDeath(ServerPlayer player) {
        boolean doDrop = seasonConfig.PLAYERS_DROP_ITEMS_ON_FINAL_DEATH.get(seasonConfig);
        //? if <= 1.21.9 {
        boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.RULE_KEEPINVENTORY);
        //?} else {
        /*boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.KEEP_INVENTORY);
        *///?}

        if (doDrop && keepInventory) {
            for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
                //? if <= 1.21 {
                player.spawnAtLocation(item);
                //?} else
                /*player.spawnAtLocation(player.ls$getServerLevel(), item);*/
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
        return false;
    }

    public void sessionEnd() {
        boogeymanManager.sessionEnd();
        secretSociety.sessionEnd();
    }

    public boolean sessionStart() {
        boogeymanManager.resetBoogeymen();
        secretSociety.resetMembers();
        addSessionActions();
        return true;
    }

    public void sessionChangeStatus(SessionStatus newStatus) {
    }

    private long ticks = 0;
    public void tick(MinecraftServer server) {
        ticks++;
        boogeymanManager.tick();
        secretSociety.tick();
        if (ticks % 100 == 0 || reloadPlayerTeams) {
            reloadPlayerTeams = false;
            reloadAllPlayerTeams();
        }
    }
    public void tickSessionOn(MinecraftServer server) {}
    public void addSessionActions() {
        boogeymanManager.addSessionActions();
        secretSociety.addSessionActions();
    }

    /*
        Events
     */

    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        boolean soulmateKill = source.type().msgId().equalsIgnoreCase("soulmate");
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
            onPlayerDiedNaturally(player);
        }
        if (livesManager.canChangeLivesNaturally(player) && player.ls$hasAssignedLives()) {
            player.ls$removeLife();
        }
    }

    public void onPlayerDiedNaturally(ServerPlayer player) {
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
            int minY = player.ls$getServerLevel().getMinBuildHeight();
            //?} else {
            /*int minY = player.ls$getServerLevel().getMinY();
            *///?}
            if (pos.y <= minY) continue;

            PlayerUtils.teleport(player, player.ls$getServerLevel(), pos, entry.getValue().get(0), entry.getValue().get(1));
            break;
        }
    }

    public void onClaimKill(ServerPlayer killer, ServerPlayer victim) {
        SessionTranscript.claimKill(killer, victim);
        if (boogeymanManager.isBoogeymanThatCanBeCured(killer, victim)) {
            boogeymanManager.onBoogeymanKill(killer);
        }

        killer.awardStat(Stats.PLAYER_KILLS);
        //? if <= 1.21 {
        killer.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, killer, ScoreAccess::increment);
        //?} else {
        /*killer.level().getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, killer, ScoreAccess::increment);
        *///?}
    }

    public void onPlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfo ci) {
    }

    public void onPrePlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypes.OUTSIDE_BORDER)) {
            cir.setReturnValue(false);
        }
    }

    public void onPlayerHeal(ServerPlayer player, float amount) {
    }

    public void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        if (!isAllowedToAttack(killer, victim) && !HIDE_UNJUSTIFIED_KILL_MESSAGES) {
            PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§c [Unjustified Kill?] {}§7 was killed by {}", victim, killer));
        }

        if (boogeymanManager.isBoogeymanThatCanBeCured(killer, victim)) {
            boogeymanManager.onBoogeymanKill(killer);
        }
        SessionTranscript.onPlayerKilledByPlayer(victim, killer);
    }

    public void onMobDeath(LivingEntity entity, DamageSource damageSource) {
    }

    public void onEntityDropItems(LivingEntity entity, DamageSource damageSource) {
        modifyEntityDrops(entity, damageSource);
    }

    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
        if (!entity.level().isClientSide() && (damageSource.getEntity() instanceof ServerPlayer)) {
            spawnEggChance(entity);
        }
    }

    private void spawnEggChance(LivingEntity entity) {
        double chance = seasonConfig.SPAWN_EGG_DROP_CHANCE.get(seasonConfig);
        boolean onlyNatural = seasonConfig.SPAWN_EGG_DROP_ONLY_NATURAL.get(seasonConfig);
        if (chance <= 0) return;
        if (entity instanceof EnderDragon) return;
        if (entity instanceof WitherBoss) return;
        if (entity instanceof Warden) return;
        if (entity instanceof ElderGuardian) return;
        if (entity instanceof Snail) return;
        if (entity instanceof TriviaBot) return;
        if (entity.getTags().contains("notNatural") && onlyNatural) return;

        EntityType<?> entityType = entity.getType();
        SpawnEggItem spawnEgg = SpawnEggItem.byId(entityType);


        if (spawnEgg == null) return;
        ItemStack spawnEggItem = spawnEgg.getDefaultInstance();
        if (spawnEggItem == null) return;
        if (spawnEggItem.isEmpty()) return;

        if (Math.random() <= chance) {
            //? if <=1.21 {
            entity.spawnAtLocation(spawnEggItem);
            //?} else
            /*entity.spawnAtLocation((ServerLevel) entity.level(), spawnEggItem);*/
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
