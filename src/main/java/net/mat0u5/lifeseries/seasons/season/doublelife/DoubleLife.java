package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.interfaces.IHungerManager;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

//? if <= 1.21.9
//import net.minecraft.world.level.GameRules;
//? if > 1.21.9
import net.minecraft.world.level.gamerules.GameRules;

public class DoubleLife extends Season {
    public static final ResourceKey<DamageType> SOULMATE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,  IdentifierHelper.mod("soulmate"));
    StringListConfig soulmateConfig;
    public boolean ANNOUNCE_SOULMATES = false;
    public boolean SOULBOUND_FOOD = false;
    public boolean SOULBOUND_EFFECTS = false;
    public boolean SOULBOUND_INVENTORIES = false;
    public static boolean SOULBOUND_BOOGEYMAN = false;
    public boolean BREAKUP_LAST_PAIR_STANDING = false;
    public boolean DISABLE_START_TELEPORT = false;
    public static boolean SOULMATE_LOCATOR_BAR = false;
    public boolean SOULMATES_PVP_ALLOWED = true;

    public SessionAction actionChooseSoulmates = new SessionAction(Time.minutes(1), "Assign Soulmates if necessary") {
        @Override
        public void trigger() {
            rollSoulmates();
        }
    };
    public SessionAction actionRandomTP = new SessionAction(Time.ticks(5), "Random teleport distribution") {
        @Override
        public void trigger() {
            distributePlayers();
        }
    };

    public Map<UUID, UUID> soulmates = new TreeMap<>();
    public Map<UUID, UUID> soulmatesOrdered = new TreeMap<>();
    public static Map<UUID, UUID> soulmatesForce = new HashMap<>();
    public static Map<UUID, UUID> soulmatesPrevent = new HashMap<>();

    @Override
    public void initialize() {
        super.initialize();
        soulmateConfig = getSoulmateConfig();
    }

    public StringListConfig getSoulmateConfig() {
        return new StringListConfig("./config/lifeseries/main", "DO_NOT_MODIFY_doublelife_soulmates.properties");
    }

    @Override
    public Seasons getSeason() {
        return Seasons.DOUBLE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        getSoulmateConfig();
        return new DoubleLifeConfig();
    }

    @Override
    public BoogeymanManager createBoogeymanManager() {
        return new DoubleLifeBoogeymanManager();
    }

    @Override
    public void reloadStart() {
        loadSoulmates();
    }

    @Override
    public void onPlayerJoin(ServerPlayer player) {
        super.onPlayerJoin(player);

        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        syncPlayer(player);
    }

    @Override
    public void addSessionActions() {
        super.addSessionActions();
        currentSession.addSessionAction(actionChooseSoulmates);
        if (!DISABLE_START_TELEPORT) {
            currentSession.addSessionAction(actionRandomTP);
        }
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayer attacker, ServerPlayer victim, boolean allowSelfDefense) {
        ServerPlayer soulmate = getSoulmate(victim);
        if (soulmate != null && soulmate == attacker) return true;
        return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
    }

    @Override
    public void onPlayerRespawn(ServerPlayer player) {
        super.onPlayerRespawn(player);
        syncPlayer(player);
    }

    @Override
    public void reload() {
        SOULMATE_LOCATOR_BAR = DoubleLifeConfig.SOULMATE_LOCATOR_BAR.get();
        super.reload();
        ANNOUNCE_SOULMATES = DoubleLifeConfig.ANNOUNCE_SOULMATES.get();
        SOULBOUND_FOOD = DoubleLifeConfig.SOULBOUND_FOOD.get();
        SOULBOUND_EFFECTS = DoubleLifeConfig.SOULBOUND_EFFECTS.get();
        SOULBOUND_INVENTORIES = DoubleLifeConfig.SOULBOUND_INVENTORIES.get();
        BREAKUP_LAST_PAIR_STANDING = DoubleLifeConfig.BREAKUP_LAST_PAIR_STANDING.get();
        DISABLE_START_TELEPORT = DoubleLifeConfig.DISABLE_START_TELEPORT.get();
        SOULBOUND_BOOGEYMAN = DoubleLifeConfig.SOULBOUND_BOOGEYMAN.get();
        SOULMATES_PVP_ALLOWED = DoubleLifeConfig.SOULMATES_PVP_ALLOWED.get();
        syncAllPlayers();
    }

    public void loadSoulmates() {
        soulmates = getAllSoulmates();
        updateOrderedSoulmates();
    }

    public void updateOrderedSoulmates() {
        soulmatesOrdered = new HashMap<>();
        for (Map.Entry<UUID, UUID> entry : soulmates.entrySet()) {
            if (soulmatesOrdered.containsKey(entry.getKey()) || soulmatesOrdered.containsValue(entry.getKey())) continue;
            if (soulmatesOrdered.containsKey(entry.getValue()) || soulmatesOrdered.containsValue(entry.getValue())) continue;
            soulmatesOrdered.put(entry.getKey(),entry.getValue());
        }

        removeSoulmateTags();

        int index = 1;
        for (Map.Entry<UUID, UUID> entry : soulmatesOrdered.entrySet()) {
            ServerPlayer key = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayer value = PlayerUtils.getPlayer(entry.getValue());
            if (key != null) {
                key.addTag("soulmate_" + index);
            }
            if (value != null) {
                value.addTag("soulmate_" + index);
            }
            index++;
        }
    }

    public void removeSoulmateTags() {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            //? if <= 1.21.11 {
            List<String> tagsCopy = new ArrayList<>(player.getTags());
            //?} else {
            /*List<String> tagsCopy = new ArrayList<>(player.entityTags());
            *///?}
            for (String tag : tagsCopy) {
                if (tag.startsWith("soulmate_")) {
                    player.removeTag(tag);
                }
            }
        }
    }

    public void saveSoulmates() {
        updateOrderedSoulmates();
        setAllSoulmates(soulmatesOrdered);
    }

    public boolean isMainSoulmate(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        if (SubInManager.isSubbingIn(playerUUID)) {
            playerUUID = SubInManager.getSubstitutedPlayerUUID(playerUUID);
        }
        if (playerUUID == null) return false;
        return soulmatesOrdered.containsKey(playerUUID);
    }

    public boolean hasSoulmate(ServerPlayer player) {
        if (player == null) return false;
        return hasSoulmate(player.getUUID());
    }
    public boolean hasSoulmate(UUID playerUUID) {
        if (SubInManager.isSubbingIn(playerUUID)) {
            playerUUID = SubInManager.getSubstitutedPlayerUUID(playerUUID);
        }
        if (playerUUID == null) return false;
        return soulmates.containsKey(playerUUID);
    }

    public boolean isSoulmateOnline(ServerPlayer player) {
        return isSoulmateOnline(player.getUUID());
    }

    public boolean isSoulmateOnline(UUID playerUUID) {
        if (!hasSoulmate(playerUUID)) return false;
        if (SubInManager.isSubbingIn(playerUUID)) {
            playerUUID = SubInManager.getSubstitutedPlayerUUID(playerUUID);
        }
        if (playerUUID == null) return false;
        UUID soulmateUUID = soulmates.get(playerUUID);
        if (SubInManager.isBeingSubstituted(soulmateUUID)) {
            soulmateUUID = SubInManager.getSubstitutingPlayerUUID(soulmateUUID);
        }
        return PlayerUtils.getPlayer(soulmateUUID) != null;
    }

    @Nullable
    public ServerPlayer getSoulmate(ServerPlayer player) {
        return getSoulmate(player.getUUID());
    }

    @Nullable
    public ServerPlayer getSoulmate(UUID playerUUID) {
        if (!isSoulmateOnline(playerUUID)) return null;
        if (SubInManager.isSubbingIn(playerUUID)) {
            playerUUID = SubInManager.getSubstitutedPlayerUUID(playerUUID);
        }
        if (playerUUID == null) return null;
        UUID soulmateUUID = soulmates.get(playerUUID);
        if (SubInManager.isBeingSubstituted(soulmateUUID)) {
            soulmateUUID = SubInManager.getSubstitutingPlayerUUID(soulmateUUID);
        }
        return PlayerUtils.getPlayer(soulmateUUID);
    }

    @Nullable
    public UUID getSoulmateUUID(UUID playerUUID) {
        if (playerUUID == null) return null;
        if (SubInManager.isSubbingIn(playerUUID)) {
            playerUUID = SubInManager.getSubstitutedPlayerUUID(playerUUID);
        }
        if (playerUUID == null) return null;
        if (!soulmates.containsKey(playerUUID)) return null;
        UUID soulmateUUID = soulmates.get(playerUUID);
        if (SubInManager.isBeingSubstituted(soulmateUUID)) {
            soulmateUUID = SubInManager.getSubstitutingPlayerUUID(soulmateUUID);
        }
        return soulmateUUID;
    }

    public void setOfflineSoulmate(UUID player1UUID, UUID player2UUID) {
        soulmates.put(player1UUID, player2UUID);
        soulmates.put(player2UUID, player1UUID);
        updateOrderedSoulmates();
    }
    public void setSoulmate(ServerPlayer player1, ServerPlayer player2) {
        soulmates.put(player1.getUUID(), player2.getUUID());
        soulmates.put(player2.getUUID(), player1.getUUID());
        SessionTranscript.soulmate(player1, player2);
        syncPlayers(player1, player2);
        updateOrderedSoulmates();
    }

    public void resetSoulmate(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        Map<UUID, UUID> newSoulmates = new HashMap<>();
        for (Map.Entry<UUID, UUID> entry : soulmates.entrySet()) {
            if (entry.getKey().equals(playerUUID)) continue;
            if (entry.getValue().equals(playerUUID)) continue;
            newSoulmates.put(entry.getKey(), entry.getValue());
        }
        soulmates = newSoulmates;
        updateOrderedSoulmates();
    }

    public void resetAllSoulmates() {
        soulmates = new HashMap<>();
        soulmatesOrdered = new HashMap<>();
        soulmateConfig.resetProperties("-- DO NOT MODIFY --");
    }

    public void rollSoulmates() {
        List<ServerPlayer> playersToRoll = getNonAssignedPlayers();
        PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvents.UI_BUTTON_CLICK.value());
        PlayerUtils.sendTitleToPlayers(playersToRoll, Component.literal("3").withStyle(ChatFormatting.GREEN),5,20,5);
        TaskScheduler.scheduleTask(25, () -> {
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(playersToRoll, Component.literal("2").withStyle(ChatFormatting.GREEN),5,20,5);
        });
        TaskScheduler.scheduleTask(50, () -> {
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(playersToRoll, Component.literal("1").withStyle(ChatFormatting.GREEN),5,20,5);
        });
        TaskScheduler.scheduleTask(75, () -> {
            PlayerUtils.sendTitleToPlayers(playersToRoll, Component.literal("Your soulmate is...").withStyle(ChatFormatting.GREEN),10,50,20);
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("doublelife_soulmate_wait")));
        });
        TaskScheduler.scheduleTask(165, () -> {
            chooseRandomSoulmates();
            for (ServerPlayer player : playersToRoll) {
                Component text = ModifiableText.DOUBLELIFE_SOULMATE_TITLE_UNKNOWN.get();
                if (hasSoulmate(player) && ANNOUNCE_SOULMATES) {
                    ServerPlayer soulmate = getSoulmate(player);
                    if (soulmate != null) {
                        text = ModifiableText.DOUBLELIFE_SOULMATE_TITLE_PLAYER.get(soulmate);
                    }
                }
                PlayerUtils.sendTitle(player, text,20,60,20);
                PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("doublelife_soulmate_chosen")));
            }
        });
    }

    public List<ServerPlayer> getNonAssignedPlayers() {
        List<ServerPlayer> playersToRoll = new ArrayList<>();
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (player.ls$isDead()) continue;
            if (hasSoulmate(player)) continue;
            playersToRoll.add(player);
        }
        return playersToRoll;
    }

    public void distributePlayers() {
        if (DISABLE_START_TELEPORT) return;
        if (server == null) return;
        List<ServerPlayer> players = getNonAssignedPlayers();
        if (players.isEmpty()) return;
        if (players.size() == 1) return;
        PlayerUtils.playSoundToPlayers(players, SoundEvents.ENDERMAN_TELEPORT);

        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            player.removeTag("randomTeleport");
        }

        for (ServerPlayer player : players) {
            player.addTag("randomTeleport");
            player.sendSystemMessage(ModifiableText.DOUBLELIFE_TELEPORT.get());
        }
        WorldBorder border = server.overworld().getWorldBorder();
        OtherUtils.executeCommand(TextUtils.formatString("spreadplayers {} {} 0 {} false @a[tag=randomTeleport]", border.getCenterX(), border.getCenterZ(), (border.getSize()/2)));
        PlayerUtils.broadcastMessageToAdmins(ModifiableText.DOUBLELIFE_TELEPORT_SUCCESS.get());

        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            player.removeTag("randomTeleport");
        }
    }

    public void chooseRandomSoulmates() {
        List<ServerPlayer> playersToRoll = getNonAssignedPlayers();

        for (Map.Entry<UUID, UUID> entry : soulmatesForce.entrySet()) {
            ServerPlayer player1 = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayer player2 = PlayerUtils.getPlayer(entry.getValue());
            if (player1 != null && player2 != null && playersToRoll.contains(player1) &&  playersToRoll.contains(player2)) {
                setSoulmate(player1,player2);
            }
            else {
                setOfflineSoulmate(entry.getKey(),entry.getValue());
            }
            if (player1 != null) playersToRoll.remove(player1);
            if (player2 != null) playersToRoll.remove(player2);
        }

        while(!playersToRoll.isEmpty()) {
            Collections.shuffle(playersToRoll);
            ServerPlayer player1 = playersToRoll.get(0);
            ServerPlayer player2 = null;
            playersToRoll.remove(0);
            for (ServerPlayer player : playersToRoll) {
                if (Objects.equals(soulmatesPrevent.get(player1.getUUID()), player.getUUID())) continue;
                if (Objects.equals(soulmatesPrevent.get(player.getUUID()), player1.getUUID())) continue;
                player2 = player;
                break;
            }
            if (player2 != null) {
                playersToRoll.remove(player2);
                setSoulmate(player1,player2);
            }
        }

        saveSoulmates();

        for (ServerPlayer remaining : getNonAssignedPlayers()) {
            PlayerUtils.broadcastMessageToAdmins(Component.literal("[Double Life] ").append(remaining.getDisplayName()).append(" was not paired with anyone."));
        }
        soulmatesForce.clear();
        soulmatesPrevent.clear();
    }

    @Override
    public void onPlayerHeal(ServerPlayer player, float amount) {
        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;
        if (!soulmate.isAlive()) return;

        float newHealth = Math.min(soulmate.getHealth() + amount, soulmate.getMaxHealth());
        soulmate.setHealth(newHealth);
        TaskScheduler.scheduleTask(1,()-> syncPlayers(player, soulmate));
    }

    @Override
    public void onPrePlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        super.onPrePlayerDamage(player, source, amount, cir);
        if (SOULMATES_PVP_ALLOWED) return;

        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;

        if (source.getEntity() instanceof ServerPlayer attacker) {
            if (soulmate == attacker) {
                cir.setReturnValue(false);
            }
        }
    }

    @Override
    public void onPlayerDamage(ServerPlayer player, DamageSource source, float amount, CallbackInfo ci) {
        super.onPlayerDamage(player, source, amount, ci);
        if (source.is(DoubleLife.SOULMATE_DAMAGE)) return;
        if (amount == 0) return;
        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;
        if (!soulmate.isAlive()) return;

        if (soulmate.hurtTime == 0) {
            //? if <=1.21 {
            /*DamageSource damageSource = new DamageSource( soulmate.level().registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SOULMATE_DAMAGE));
            soulmate.hurt(damageSource, 0.0000001F);
            *///?} else {
            DamageSource damageSource = new DamageSource( soulmate.ls$getServerLevel().registryAccess()
                    .lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SOULMATE_DAMAGE));
            soulmate.hurtServer(soulmate.ls$getServerLevel(), damageSource, 0.0000001F);
            //?}
        }

        float newHealth = player.getHealth();
        if (newHealth <= 0.0F) newHealth = 0.01F;
        soulmate.setHealth(newHealth);

        TaskScheduler.scheduleTask(1,() -> syncPlayers(player, soulmate));
    }

    @Override
    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        super.onPlayerDeath(player, source);

        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayer soulmate = getSoulmate(player);

        if (soulmate == null) return;
        if (!soulmate.isAlive()) return;
        //? if <= 1.21.9 {
        /*boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.RULE_KEEPINVENTORY);
        *///?} else {
        boolean keepInventory = OtherUtils.getBooleanGameRule(player.ls$getServerLevel(), GameRules.KEEP_INVENTORY);
        //?}
        if (SOULBOUND_INVENTORIES && server != null && !keepInventory) {
            soulmate.getInventory().clearContent();
        }

        //? if <=1.21 {
        /*DamageSource damageSource = new DamageSource( soulmate.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SOULMATE_DAMAGE));
        soulmate.setLastHurtByMob(player);
        soulmate.setLastHurtByPlayer(player);
        soulmate.hurt(damageSource, 1000);
         *///?} else {
        DamageSource damageSource = new DamageSource( soulmate.ls$getServerLevel().registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SOULMATE_DAMAGE));
        soulmate.setLastHurtByMob(player);
        //? if <= 1.21.4 {
        /*soulmate.setLastHurtByPlayer(player);
        *///?} else {
        soulmate.setLastHurtByPlayer(player, 100);
        //?}
        soulmate.hurtServer(soulmate.ls$getServerLevel(), damageSource, 1000);
        //?}


        TaskScheduler.scheduleTask(1, this::checkForEnding);
    }

    public void syncAllPlayers() {
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            syncPlayer(player);
        }
    }

    public void syncPlayer(ServerPlayer player) {
        ServerPlayer soulmate = getSoulmate(player);
        syncPlayers(soulmate, player);
    }

    public void syncPlayers(ServerPlayer player, ServerPlayer soulmate) {
        if (player == null || soulmate == null) return;
        if (!player.isAlive() || !soulmate.isAlive()) return;
        if (player.getHealth() != soulmate.getHealth()) {
            float sharedHealth = Math.min(player.getHealth(), soulmate.getHealth());
            if (sharedHealth != 0.0F) {
                player.setHealth(sharedHealth);
                soulmate.setHealth(sharedHealth);
            }
        }
        
        Integer soulmateLives = soulmate.ls$getLives();
        Integer playerLives = player.ls$getLives();
        if (soulmateLives != null && playerLives != null)  {
            if (!Objects.equals(soulmateLives, playerLives)) {
                int minLives = Math.min(soulmateLives,playerLives);
                player.ls$setLives(minLives);
                soulmate.ls$setLives(minLives);
            }
        }

        updateFood(player, soulmate);
        syncPlayerInventory(player, soulmate);
    }

    public void syncSoulboundLives(ServerPlayer player) {
        if (player == null) return;
        Integer lives = player.ls$getLives();
        ServerPlayer soulmate = getSoulmate(player);
        if (lives == null) return;
        if (soulmate == null) return;
        if (!player.isAlive() || !soulmate.isAlive()) return;
        soulmate.ls$setLives(lives);
    }

    public void canFoodHeal(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        boolean orig =  player.getHealth() > 0.0F && player.getHealth() < player.getMaxHealth();
        if (!orig) {
            cir.setReturnValue(false);
            return;
        }

        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;
        if (isMainSoulmate(player)) return;
        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;
        if (!soulmate.isAlive()) return;

        boolean canHealWithSaturationOther = soulmate.getFoodData().getSaturationLevel() > 2.0F && soulmate.getFoodData().getFoodLevel() >= 20;

        if (canHealWithSaturationOther) {
            cir.setReturnValue(false);
        }
        else {
            cir.setReturnValue(true);
        }
    }

    public void setAllSoulmates(Map<UUID, UUID> soulmates) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<UUID, UUID> entry : soulmates.entrySet()) {
            list.add(entry.getKey().toString()+"_"+entry.getValue().toString());
        }
        soulmateConfig.save(list);
    }

    public Map<UUID, UUID> getAllSoulmates() {
        Map<UUID, UUID> loadedSoulmates = new HashMap<>();
        List<String> list = soulmateConfig.load();
        for (String str : list) {
            try {
                if (!str.contains("_")) continue;
                String[] split = str.split("_");
                if (split.length != 2) continue;
                UUID key = UUID.fromString(split[0]);
                UUID value = UUID.fromString(split[1]);
                loadedSoulmates.put(key, value);
                loadedSoulmates.put(value, key);
            }catch(Exception ignored) {}
        }
        return loadedSoulmates;
    }

    public void updateFood(ServerPlayer player, ServerPlayer soulmate) {
        if (!SOULBOUND_FOOD) return;
        IHungerManager hungerManager1 = (IHungerManager) player.getFoodData();
        IHungerManager hungerManager2 = (IHungerManager) soulmate.getFoodData();
        if (hungerManager1 == null || hungerManager2 == null) return;

        int foodLevel = Math.min(hungerManager1.ls$getFoodLevel(), hungerManager2.ls$getFoodLevel());
        float saturation = Math.max(hungerManager1.ls$getSaturationLevel(), hungerManager2.ls$getSaturationLevel());
        setHungerManager(hungerManager1, hungerManager2, foodLevel, saturation);
    }

    public void updateFoodFrom(ServerPlayer player) {
        if (!SOULBOUND_FOOD) return;
        if (player == null) return;
        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;
        IHungerManager hungerManager1 = (IHungerManager) player.getFoodData();
        IHungerManager hungerManager2 = (IHungerManager) soulmate.getFoodData();
        if (hungerManager1 == null || hungerManager2 == null) return;

        hungerManager2.ls$setFoodLevel(hungerManager1.ls$getFoodLevel());
        hungerManager2.ls$setSaturationLevel(hungerManager1.ls$getSaturationLevel());
    }

    public void setHungerManager(IHungerManager hungerManager1, IHungerManager hungerManager2, int foodLevel, float saturation) {
        hungerManager1.ls$setFoodLevel(foodLevel);
        hungerManager1.ls$setSaturationLevel(saturation);

        hungerManager2.ls$setFoodLevel(foodLevel);
        hungerManager2.ls$setSaturationLevel(saturation);
    }

    @Override
    public void onUpdatedInventory(ServerPlayer player) {
        super.onUpdatedInventory(player);
        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;
        syncPlayerInventory(player, soulmate);
    }

    public void syncPlayerInventory(ServerPlayer player, ServerPlayer soulmate) {
        if (!SOULBOUND_INVENTORIES) return;
        if (isRecentlyDead(player) && isRecentlyDead(soulmate)) return;
        boolean swapDirection = false;
        if (isRecentlyDead(player) && !isRecentlyDead(soulmate)) swapDirection = true;

        if (!swapDirection) {
            setPlayerInventory(soulmate, player.getInventory());
        }
        else {
            setPlayerInventory(player, soulmate.getInventory());
        }
    }

    public boolean isRecentlyDead(ServerPlayer player) {
        return !player.isAlive() || player.tickCount <= 1;
    }

    public void setPlayerInventory(ServerPlayer player, Inventory inventory) {
        List<ItemStack> newInventory = getPlayerInventory(inventory);
        Inventory playerInventory = player.getInventory();
        for (int i = 0; i < Math.min(newInventory.size(), playerInventory.getContainerSize()); i++) {
            ItemStack newStack = newInventory.get(i).copy();
            if (ItemStack.matches(playerInventory.getItem(i), newStack)) continue;
            playerInventory.setItem(i, newStack);
        }
        player.onUpdateAbilities();
    }

    public List<ItemStack> getPlayerInventory(Inventory inventory) {
        //? if <= 1.21.4 {
        /*List<ItemStack> result = new ArrayList<>(inventory.items);
        result.addAll(inventory.armor);
        result.addAll(inventory.offhand);
        *///?} else {
        List<ItemStack> result = new ArrayList<>(inventory.getNonEquipmentItems());
        for (int i = result.size(); i < inventory.getContainerSize(); i++) {
            result.add(inventory.getItem(i));
        }
        //?}
        return result;
    }

    public void syncStatusEffectsFrom(ServerPlayer player, MobEffectInstance effect, boolean add) {
        TaskScheduler.scheduleTask(0, () -> delayedSyncStatusEffectsFrom(player, effect, add));
    }

    public void delayedSyncStatusEffectsFrom(ServerPlayer player, MobEffectInstance effect, boolean add) {
        if (!SOULBOUND_EFFECTS) return;
        ServerPlayer soulmate = getSoulmate(player);
        if (soulmate == null) return;

        if (add) {
            if (!soulmate.getActiveEffects().contains(effect)) {
                soulmate.addEffect(effect);
            }
        }
        else {
            if (soulmate.hasEffect(effect.getEffect())) {
                soulmate.removeEffect(effect.getEffect());
            }
            if (player.hasEffect(effect.getEffect())) {
                soulmate.addEffect(player.getEffect(effect.getEffect()));
            }
        }
    }

    public void checkForEnding() {
        List<ServerPlayer> remainingPlayers = livesManager.getAlivePlayers();
        if (remainingPlayers.size() == 2 && BREAKUP_LAST_PAIR_STANDING) {
            ServerPlayer player1 = remainingPlayers.get(0);
            ServerPlayer player2 = remainingPlayers.get(1);
            if (hasSoulmate(player1) && hasSoulmate(player2)) {
                if (getSoulmate(player1) == player2) {
                    resetSoulmate(player1);
                    List<ServerPlayer> allPlayers = PlayerUtils.getAllPlayers();
                    TaskScheduler.scheduleTask(Time.seconds(10), () -> {
                        PlayerUtils.sendTitleWithSubtitleToPlayers(allPlayers, Component.empty(), Component.nullToEmpty("§aYour fate is your own..."), 20, 40, 20);
                    });
                    TaskScheduler.scheduleTask(Time.seconds(15), () -> {
                        PlayerUtils.sendTitleWithSubtitleToPlayers(allPlayers, Component.empty(), Component.nullToEmpty("§cThere can only be one winner."), 20, 40, 20);
                    });
                    TaskScheduler.scheduleTask(Time.seconds(19), () -> {
                        LevelUtils.summonHarmlessLightning(player1);
                        LevelUtils.summonHarmlessLightning(player2);
                        player1.ls$hurt(player1.damageSources().lightningBolt(), 0.0000001F);
                        player2.ls$hurt(player2.damageSources().lightningBolt(), 0.0000001F);
                    });
                }
            }
        }
    }

    public void forceSoulmates(ServerPlayer player, ServerPlayer soulmate) {
        soulmatesForce.put(player.getUUID(), soulmate.getUUID());
    }

    public void preventSoulmates(ServerPlayer player, ServerPlayer soulmate) {
        soulmatesPrevent.put(player.getUUID(), soulmate.getUUID());
    }
    @Override
    public void tryKillLifeGain(ServerPlayer killer, ServerPlayer victim) {
        super.tryKillLifeGain(killer, victim);
        syncSoulboundLives(killer);
    }
}
