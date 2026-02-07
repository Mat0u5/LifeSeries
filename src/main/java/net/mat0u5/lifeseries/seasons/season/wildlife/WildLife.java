package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.mat0u5.lifeseries.Main.seasonConfig;
//? if >= 1.21.2 {
import net.minecraft.server.level.ServerLevel;
//?}

public class WildLife extends Season {
    @Override
    public Seasons getSeason() {
        return Seasons.WILD_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        Snails.loadConfig();
        return new WildLifeConfig();
    }

    @Override
    public void initialize() {
        super.initialize();
        Snails.loadConfig();
        Snails.loadSnailNames();
        WildLifeTriviaHandler.initializeItemSpawner();
    }

    @Override
    public void onPlayerJoin(ServerPlayer player) {
        super.onPlayerJoin(player);
        WildcardManager.onPlayerJoin(player);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayer player) {
        super.onPlayerFinishJoining(player);
        WildcardManager.onPlayerFinishJoining(player);
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayer attacker, ServerPlayer victim, boolean allowSelfDefense) {
        if (Necromancy.isRessurectedPlayer(victim) || Necromancy.isRessurectedPlayer(attacker)) {
            return true;
        }
        return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onPlayerKilledByPlayer(victim, killer);
        if (victim.ls$isOnAtLeastLives(4, false) && wasAllowedToAttack && !wasBoogeyCure) {
            if (Necromancy.isRessurectedPlayer(killer) && seasonConfig instanceof WildLifeConfig config) {
                if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN.get(config)) {
                    Integer currentLives = killer.ls$getLives();
                    if (currentLives == null) currentLives = 0;
                    int lives = currentLives + 1;
                    if (lives <= 0) {
                        ScoreboardUtils.setScore(killer.getScoreboardName(), LivesManager.SCOREBOARD_NAME, lives);
                    }
                    else {
                        broadcastLifeGain(killer, victim);
                        killer.ls$addLife();
                    }
                }
            }
        }
    }


    @Override
    public void tickSessionOn(MinecraftServer server) {
        super.tickSessionOn(server);
        WildcardManager.tickSessionOn();
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        WildcardManager.tick();
    }

    @Override
    public boolean sessionStart() {
        super.sessionStart();
        WildcardManager.onSessionStart();
        return true;
    }

    @Override
    public void addSessionActions() {
        super.addSessionActions();
        WildcardManager.addSessionActions();
    }

    @Override
    public void sessionEnd() {
        WildcardManager.onSessionEnd();
        super.sessionEnd();
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof WildLifeConfig config)) return;
        Hunger.SWITCH_DELAY = 20 * WildLifeConfig.WILDCARD_HUNGER_RANDOMIZE_INTERVAL.get(config);
        Hunger.HUNGER_EFFECT_LEVEL = WildLifeConfig.WILDCARD_HUNGER_EFFECT_LEVEL.get(config);
        Hunger.NUTRITION_CHANCE = WildLifeConfig.WILDCARD_HUNGER_NUTRITION_CHANCE.get(config);
        Hunger.SATURATION_CHANCE = WildLifeConfig.WILDCARD_HUNGER_SATURATION_CHANCE.get(config);
        Hunger.EFFECT_CHANCE = WildLifeConfig.WILDCARD_HUNGER_EFFECT_CHANCE.get(config);
        Hunger.AVG_EFFECT_DURATION = WildLifeConfig.WILDCARD_HUNGER_AVG_EFFECT_DURATION.get(config);
        Hunger.SOUND_CHANCE = WildLifeConfig.WILDCARD_HUNGER_SOUND_CHANCE.get(config);
        Hunger.newNonEdibleItems(WildLifeConfig.WILDCARD_HUNGER_NON_EDIBLE_ITEMS.get(config));

        SizeShifting.MIN_SIZE = WildLifeConfig.WILDCARD_SIZESHIFTING_MIN_SIZE.get(config);
        SizeShifting.MAX_SIZE = WildLifeConfig.WILDCARD_SIZESHIFTING_MAX_SIZE.get(config);
        SizeShifting.SIZE_CHANGE_MULTIPLIER = WildLifeConfig.WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER.get(config);
        SizeShifting.FIX_SIZECHANGING_BUGS = WildLifeConfig.WILDCARD_SIZESHIFTING_FIX_BUGS.get(config);


        Snail.GLOBAL_SPEED_MULTIPLIER = WildLifeConfig.WILDCARD_SNAILS_SPEED_MULTIPLIER.get(config);
        Snail.SHOULD_DROWN_PLAYER = WildLifeConfig.WILDCARD_SNAILS_DROWN_PLAYERS.get(config);
        Snail.ALLOW_POTION_EFFECTS = WildLifeConfig.WILDCARD_SNAILS_EFFECTS.get(config);

        TimeDilation.MIN_TICK_RATE = (float) (20.0 * WildLifeConfig.WILDCARD_TIMEDILATION_MIN_SPEED.get(config));
        TimeDilation.MAX_TICK_RATE = (float) (20.0 * WildLifeConfig.WILDCARD_TIMEDILATION_MAX_SPEED.get(config));
        TimeDilation.MIN_PLAYER_MSPT = (float) (50.0 / WildLifeConfig.WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED.get(config));

        MobSwap.MAX_DELAY = 20 * WildLifeConfig.WILDCARD_MOBSWAP_START_SPAWN_DELAY.get(config);
        MobSwap.MIN_DELAY = 20 * WildLifeConfig.WILDCARD_MOBSWAP_END_SPAWN_DELAY.get(config);
        MobSwap.SPAWN_MOBS = WildLifeConfig.WILDCARD_MOBSWAP_SPAWN_MOBS.get(config);
        MobSwap.BOSS_CHANCE_MULTIPLIER = WildLifeConfig.WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER.get(config);

        TriviaBot.CAN_START_RIDING = WildLifeConfig.WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.get(config);
        TriviaWildcard.TRIVIA_BOTS_PER_PLAYER = WildLifeConfig.WILDCARD_TRIVIA_BOTS_PER_PLAYER.get(config);
        WildLifeTriviaHandler.EASY_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_EASY.get(config);
        WildLifeTriviaHandler.NORMAL_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_NORMAL.get(config);
        WildLifeTriviaHandler.HARD_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_HARD.get(config);
        WindCharge.MAX_MACE_DAMAGE = WildLifeConfig.WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE.get(config);
        Superspeed.STEP_UP = WildLifeConfig.WILDCARD_SUPERPOWERS_SUPERSPEED_STEP.get(config);
        WildcardManager.ACTIVATE_WILDCARD_MINUTE = WildLifeConfig.ACTIVATE_WILDCARD_MINUTE.get(config);
        SuperpowersWildcard.WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = WildLifeConfig.WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME.get(config);
        SuperpowersWildcard.setBlacklist(WildLifeConfig.WILDCARD_SUPERPOWERS_POWER_BLACKLIST.get(config));
        SuperpowersWildcard.ZOMBIES_HEALTH = WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_HEALTH.get(config);
        Callback.setBlacklist(WildLifeConfig.WILDCARD_CALLBACK_WILDCARDS_BLACKLIST.get(config));
        Callback.TURN_OFF = WildLifeConfig.WILDCARD_CALLBACK_TURN_OFF.get(config);
        Callback.NERFED_WILDCARDS = WildLifeConfig.WILDCARD_CALLBACK_NERFED_WILDCARDS.get(config);

        AnimalDisguise.SHOW_ARMOR = WildLifeConfig.WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR.get(config);
        AnimalDisguise.SHOW_HANDS = WildLifeConfig.WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS.get(config);

        Snails.loadConfig();
        Snails.loadSnailNames();
        Snails.reloadSnailNames();
        Snails.reloadSnailSkins();
        TriviaWildcard.reload();
    }

    @Override
    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource, CallbackInfo ci) {
        super.modifyEntityDrops(entity, damageSource, ci);
        if (damageSource.getDirectEntity() instanceof Player) {
            if (entity instanceof Warden || entity instanceof WitherBoss || entity instanceof EnderDragon) {
                //? if <= 1.21 {
                /*entity.spawnAtLocation(Items.TOTEM_OF_UNDYING.getDefaultInstance());
                 *///?} else {
                entity.spawnAtLocation((ServerLevel) entity.level(), Items.TOTEM_OF_UNDYING.getDefaultInstance());
                //?}
            }
        }
    }

    @Override
    public boolean modifyKeepInventory(ServerPlayer player, boolean originalKeepInventory) {
        if (Necromancy.isRessurectedPlayer(player)) {
            return WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_KEEP_INVENTORY.get(seasonConfig);
        }
        return super.modifyKeepInventory(player, originalKeepInventory);
    }

    @Override
    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.CREAKING)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof CreakingPower creakingPower) {
                creakingPower.deactivate();
                reloadPlayerTeam(player);
            }
        }

        super.onPlayerDeath(player, source);

        WildLifeTriviaHandler.cursedGigantificationPlayers.remove(player.getUUID());
        WildLifeTriviaHandler.cursedHeartPlayers.remove(player.getUUID());
        AttributeUtils.resetMaxPlayerHealthIfNecessary(player);

        WildLifeTriviaHandler.cursedMoonJumpPlayers.remove(player.getUUID());
        AttributeUtils.resetPlayerJumpHeight(player);

        Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
        if (power != null) {
            power.deactivate();
        }
    }

    @Override
    public void onPlayerDisconnect(ServerPlayer player) {
        super.onPlayerDisconnect(player);

        Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
        if (power != null) {
            power.deactivate();
        }
    }

    @Override
    public String getTeamForPlayer(ServerPlayer player) {
        String team = super.getTeamForPlayer(player);

        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.CREAKING)) {
            return "creaking_"+player.getScoreboardName();
        }
        if (Necromancy.isRessurectedPlayer(player) && !player.isSpectator()) {
            return "zombie";
        }

        return team;
    }

    @Override
    public void onPlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfo ci) {
        super.onPlayerDamage(player, source, amount, ci);
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.PLAYER_DISGUISE)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof PlayerDisguise power) {
                power.onTakeDamage();
            }
        }
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ANIMAL_DISGUISE)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AnimalDisguise power) {
                power.onTakeDamage();
            }
        }
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
                power.onTakeDamage();
            }
        }
    }

    @Override
    public void onPrePlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        super.onPrePlayerDamage(player, source, amount, cir);
        if (source.is(DamageTypes.FALL) ||source.is(DamageTypes.STALAGMITE) || source.is(DamageTypes.FLY_INTO_WALL)) {
            if (SuperpowersWildcard.hasActivePower(player, Superpowers.FLIGHT)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Flight power) {
                    if (power.isLaunchedUp) {
                        if (!source.is(DamageTypes.FLY_INTO_WALL)) power.isLaunchedUp = false;
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof TripleJump power) {
                    if (power.isInAir) {
                        power.isInAir = false;
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH) && player.isPassenger()) {
                if (player.getVehicle() instanceof ServerPlayer) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Override
    public void onRightClickEntity(ServerPlayer player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof SuperPunch power) {
                power.tryRideEntity(entity);
            }
        }
    }

    @Override
    public void onAttackEntity(ServerPlayer player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
                power.onAttack();
            }
        }
    }

    @Override
    public void onUpdatedInventory(ServerPlayer player) {
        super.onUpdatedInventory(player);
        Hunger.updateInventory(player);
    }
}
