package net.mat0u5.lifeseries.seasons.season;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;
//? if >= 1.21.2
/*import net.minecraft.server.world.ServerWorld;*/

public abstract class Season {
    public static final String RESOURCEPACK_MAIN_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-main-a4e7ddfa4558be90d6259e0d655f9589cb60dd88/main.zip";
    public static final String RESOURCEPACK_MAIN_SHA ="2a1e2e58b330631370d9f95203618e86500c4397";
    public static final String RESOURCEPACK_SECRETLIFE_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-secretlife-a4e7ddfa4558be90d6259e0d655f9589cb60dd88/secretlife.zip";
    public static final String RESOURCEPACK_SECRETLIFE_SHA ="231313213c1cd24145506fb496db2880dd1f9c1c";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-minimal_armor-a4e7ddfa4558be90d6259e0d655f9589cb60dd88/minimal_armor.zip";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_SHA ="1d349628ab6121691fa991770289d01458d561f4";

    public boolean NO_HEALING = false;
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

        OtherUtils.executeCommand("worldborder set " + seasonConfig.WORLDBORDER_SIZE.get(seasonConfig));
        server.getGameRules().get(GameRules.KEEP_INVENTORY).set(seasonConfig.KEEP_INVENTORY.get(seasonConfig), server);
        server.getGameRules().get(GameRules.NATURAL_REGENERATION).set(!NO_HEALING, server);
        //? if >= 1.21.6 {
        /*boolean locatorBarEnabled = seasonConfig.LOCATOR_BAR.get(seasonConfig);
        if (!locatorBarEnabled && this instanceof DoubleLife) {
            locatorBarEnabled = DoubleLife.SOULMATE_LOCATOR_BAR;
        }
        server.getGameRules().get(GameRules.LOCATOR_BAR).set(locatorBarEnabled, server);
        *///?}

        ScoreboardObjective currentListObjective = ScoreboardUtils.getObjectiveInSlot(ScoreboardDisplaySlot.LIST);
        if (TAB_LIST_SHOW_LIVES) {
            ScoreboardUtils.setObjectiveInSlot(ScoreboardDisplaySlot.LIST, LivesManager.SCOREBOARD_NAME);
        }
        else if (currentListObjective != null) {
            if (currentListObjective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
                ScoreboardUtils.setObjectiveInSlot(ScoreboardDisplaySlot.LIST, null);
            }
        }

        ScoreboardObjective currentBelowNameObjective = ScoreboardUtils.getObjectiveInSlot(ScoreboardDisplaySlot.BELOW_NAME);
        if (SHOW_HEALTH_BELOW_NAME) {
            ScoreboardUtils.setObjectiveInSlot(ScoreboardDisplaySlot.BELOW_NAME, "HP");
        }
        else if (currentBelowNameObjective != null) {
            if (currentBelowNameObjective.getName().equals("HP")) {
                ScoreboardUtils.setObjectiveInSlot(ScoreboardDisplaySlot.BELOW_NAME, null);
            }
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
    }

    public void reloadPlayers() {
        PlayerUtils.getAllPlayers().forEach(AttributeUtils::resetAttributesOnPlayerJoin);
    }

    public void createTeams() {
        Collection<Team> allTeams = TeamUtils.getAllTeams();
        if (allTeams != null) {
            for (Team team : allTeams) {
                if (team.getName().startsWith("creaking_")) {
                    TeamUtils.deleteTeam(team.getName());
                }
            }
        }

        WatcherManager.createTeams();
        livesManager.createTeams();
    }


    public void createScoreboards() {
        ScoreboardUtils.createObjective("HP", "§c❤", ScoreboardCriterion.HEALTH);
        WatcherManager.createScoreboards();
        livesManager.createScoreboards();
    }

    public void reloadAllPlayerTeams() {
        PlayerUtils.getAllPlayers().forEach(this::reloadPlayerTeam);
    }

    public void reloadPlayerTeam(ServerPlayerEntity player) {
        if (player == null) return;
        if (player.isAlive()) {
            reloadPlayerTeamActual(player);
        }
        else {
            TaskScheduler.scheduleTask(2, () -> reloadPlayerTeamActual(player));
        }
    }

    public void reloadPlayerTeamActual(ServerPlayerEntity player) {
        String team = getTeamForPlayer(player);
        TeamUtils.addEntityToTeam(team, player);

        if (currentSeason.getSeason() == Seasons.WILD_LIFE) WildLife.changedPlayerTeam(player);
        Events.updatePlayerListsNextTick = true;
    }

    public String getTeamForPlayer(ServerPlayerEntity player) {
        if (isWatcher(player)) {
            return WatcherManager.TEAM_NAME;
        }

        return livesManager.getTeamForPlayer(player);
    }


    public void dropItemsOnLastDeath(ServerPlayerEntity player) {
        boolean doDrop = seasonConfig.PLAYERS_DROP_ITEMS_ON_FINAL_DEATH.get(seasonConfig);
        boolean keepInventory = player.getServer().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
        if (doDrop && keepInventory) {
            for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
                //? if <= 1.21 {
                player.dropStack(item);
                //?} else
                /*player.dropStack(PlayerUtils.getServerWorld(player), item);*/
            }
            player.getInventory().clear();
        }
    }

    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        return isAllowedToAttack(attacker, victim, ALLOW_SELF_DEFENSE);
    }

    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim, boolean allowSelfDefense) {
        if (livesManager.isOnLastLife(attacker, false)) {
            return true;
        }
        if (boogeymanManager.isBoogeymanThatCanBeCured(attacker, victim)) {
            return true;
        }
        if (allowSelfDefense) {
             if (attacker.getPrimeAdversary() == victim && isAllowedToAttack(victim, attacker, false)) {
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

    private long ticks = 0;
    public void tick(MinecraftServer server) {
        ticks++;
        boogeymanManager.tick();
        secretSociety.tick();
        if (ticks % 100 == 0) {
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

    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        boolean soulmateKill = source.getType().msgId().equalsIgnoreCase("soulmate");
        SessionTranscript.onPlayerDeath(player, source);
        boolean killedByPlayer = false;
        if (source.getAttacker() instanceof ServerPlayerEntity serverAttacker) {
            if (player != source.getAttacker() && !soulmateKill) {
                onPlayerKilledByPlayer(player, serverAttacker);
                killedByPlayer = true;
            }
        }
        if (player.getPrimeAdversary() != null && !killedByPlayer) {
            if (player.getPrimeAdversary() instanceof ServerPlayerEntity serverAdversary) {
                if (player != player.getPrimeAdversary() && !soulmateKill) {
                    onPlayerKilledByPlayer(player, serverAdversary);
                    killedByPlayer = true;
                }
            }
        }
        if (!killedByPlayer) {
            onPlayerDiedNaturally(player);
        }
        if (livesManager.canChangeLivesNaturally(player)) {
            livesManager.removePlayerLife(player);
        }
    }

    public void onPlayerDiedNaturally(ServerPlayerEntity player) {
        if (server == null) return;
        currentSession.playerNaturalDeathLog.remove(player.getUuid());
        currentSession.playerNaturalDeathLog.put(player.getUuid(), server.getTicks());
    }

    public final Map<UUID, HashMap<Vec3d,List<Float>>> respawnPositions = new HashMap<>();
    public void onPlayerRespawn(ServerPlayerEntity player) {

    }
    public void postPlayerRespawn(ServerPlayerEntity player) {
        if (!respawnPositions.containsKey(player.getUuid())) return;
        HashMap<Vec3d, List<Float>> info = respawnPositions.get(player.getUuid());
        respawnPositions.remove(player.getUuid());
        if (livesManager.isAlive(player)) return;
        for (Map.Entry<Vec3d, List<Float>> entry : info.entrySet()) {
            Vec3d pos = entry.getKey();
            if (pos.y <= PlayerUtils.getServerWorld(player).getBottomY()) continue;

            PlayerUtils.teleport(player, PlayerUtils.getServerWorld(player), pos, entry.getValue().get(0), entry.getValue().get(1));
            break;
        }
    }

    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        SessionTranscript.claimKill(killer, victim);
        if (boogeymanManager.isBoogeymanThatCanBeCured(killer, victim)) {
            boogeymanManager.cure(killer);
        }
    }

    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
    }

    public void onPrePlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    }

    public void onPlayerHeal(ServerPlayerEntity player, float amount) {
    }

    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        if (!isAllowedToAttack(killer, victim) && !HIDE_UNJUSTIFIED_KILL_MESSAGES) {
            PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§c [Unjustified Kill?] {}§7 was killed by {}", victim, killer));
        }

        if (boogeymanManager.isBoogeymanThatCanBeCured(killer, victim)) {
            boogeymanManager.cure(killer);
        }
    }

    public void onMobDeath(LivingEntity entity, DamageSource damageSource) {
    }

    public void onEntityDropItems(LivingEntity entity, DamageSource damageSource) {
        modifyEntityDrops(entity, damageSource);
    }

    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
        if (!entity.getWorld().isClient() && (damageSource.getAttacker() instanceof ServerPlayerEntity)) {
            spawnEggChance(entity);
        }
    }

    private void spawnEggChance(LivingEntity entity) {
        double chance = seasonConfig.SPAWN_EGG_DROP_CHANCE.get(seasonConfig);
        boolean onlyNatural = seasonConfig.SPAWN_EGG_DROP_ONLY_NATURAL.get(seasonConfig);
        if (chance <= 0) return;
        if (entity instanceof EnderDragonEntity) return;
        if (entity instanceof WitherEntity) return;
        if (entity instanceof WardenEntity) return;
        if (entity instanceof ElderGuardianEntity) return;
        if (entity instanceof Snail) return;
        if (entity instanceof TriviaBot) return;
        if (entity.getCommandTags().contains("notNatural") && onlyNatural) return;

        EntityType<?> entityType = entity.getType();
        SpawnEggItem spawnEgg = SpawnEggItem.forEntity(entityType);


        if (spawnEgg == null) return;
        ItemStack spawnEggItem = spawnEgg.getDefaultStack();
        if (spawnEggItem == null) return;
        if (spawnEggItem.isEmpty()) return;

        if (Math.random() <= chance) {
            //? if <=1.21 {
            entity.dropStack(spawnEggItem);
            //?} else
            /*entity.dropStack((ServerWorld) entity.getWorld(), spawnEggItem);*/
        }
    }

    public void learnRecipes() {
        OtherUtils.executeCommand("recipe give @a lifeseries:name_tag_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:saddle_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:spawner_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:tnt_recipe_variation");
        OtherUtils.executeCommand("recipe give @a lifeseries:bundle_recipe");
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        AttributeUtils.resetAttributesOnPlayerJoin(player);
        reloadPlayerTeam(player);
        TaskScheduler.scheduleTask(2, () -> PlayerUtils.applyResourcepack(player.getUuid()));
        if (!livesManager.hasAssignedLives(player)) {
            assignDefaultLives(player);
        }
        if (livesManager.hasAssignedLives(player) && !livesManager.isAlive(player) && !PermissionManager.isAdmin(player)) {
            player.changeGameMode(GameMode.SPECTATOR);
        }

        if (WatcherManager.isWatcher(player)) {
            if (this instanceof DoubleLife doubleLife) {
                doubleLife.resetSoulmate(player);
            }
        }
    }

    public void assignDefaultLives(ServerPlayerEntity player) {
        Integer lives = getDefaultLives();
        if (lives != null) {
            livesManager.setPlayerLives(player, lives);
        }
    }

    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (getSeason() != Seasons.UNASSIGNED && SHOW_LOGIN_COMMAND_INFO) {
            if (PermissionManager.isAdmin(player)) {
                player.sendMessage(TextUtils.formatLoosely("§7{} commands: §r{}", getSeason().getName(), getAdminCommands()));
            }
            else {
                player.sendMessage(TextUtils.formatLoosely("§7{} non-admin commands: §r{}", getSeason().getName(), getNonAdminCommands()));
            }
        }

        learnRecipes();
        if (currentSession.statusNotStarted() && PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("\nUse §b'/session timer set <time>'§f to set the desired session time."));
            player.sendMessage(Text.of("After that, use §b'/session start'§f to start the session."));
        }
        boogeymanManager.onPlayerFinishJoining(player);
    }

    public void onPlayerDisconnect(ServerPlayerEntity player) {
    }

    public void onRightClickEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
    }

    public void onAttackEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
    }

    public void onUpdatedInventory(ServerPlayerEntity player) {
        if (blacklist != null) {
            blacklist.onInventoryUpdated(player);
        }
    }
}
