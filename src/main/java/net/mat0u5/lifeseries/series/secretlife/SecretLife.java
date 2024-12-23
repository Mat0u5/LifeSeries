package net.mat0u5.lifeseries.series.secretlife;

import net.mat0u5.lifeseries.series.Blacklist;
import net.mat0u5.lifeseries.series.Series;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Objects;

public class SecretLife extends Series {
    public static final double MAX_HEALTH = 60.0d;
    @Override
    public SeriesList getSeries() {
        return SeriesList.SECRET_LIFE;
    }
    @Override
    public Blacklist createBlacklist() {
        return new SecretLifeBlacklist();
    }
    @Override
    public void initialize() {
        super.initialize();
        CUSTOM_ENCHANTMENT_TABLE_ALGORITHM = true;
        NO_HEALING = true;
        TaskManager.initialize();
    }
    @Override
    public String getResourcepackURL() {
        return "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-secretlife-88c7bc3cf13c71a3325103851754755e11ef3280/RP.zip";
    }
    @Override
    public String getResourcepackSHA1() {
        return "10cafd881ae1c8cdbc76b2627e175657cf3c7b52";
    }
    @Override
    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        syncPlayerHealth(player);
    }
    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        super.onPlayerDeath(player, source);
        setPlayerHealth(player, MAX_HEALTH);
    }
    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);
        if (!hasAssignedLives(player)) {
            setPlayerLives(player,3);
            setPlayerHealth(player, MAX_HEALTH);
            player.setHealth((float) MAX_HEALTH);
        }
    }
    @Override
    public void sessionStart() {
        super.sessionStart();
        activeActions = List.of(TaskManager.actionChooseTasks);
    }
    public void removePlayerHealth(ServerPlayerEntity player, double health) {
        addPlayerHealth(player,-health);
    }
    public void removePlayerHealthClamped(ServerPlayerEntity player, double health) {
        addPlayerHealthClamped(player,-health);
    }
    public void addPlayerHealth(ServerPlayerEntity player, double health) {
        double currentHealth = player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
        setPlayerHealth(player, currentHealth + health);
    }
    public void addPlayerHealthClamped(ServerPlayerEntity player, double health) {
        double currentHealth = player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
        setPlayerHealthClamped(player, currentHealth + health);
    }
    public void setPlayerHealth(ServerPlayerEntity player, double health) {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(health);
        if (player.getMaxHealth() > player.getHealth() && !player.isDead()) {
            player.setHealth(player.getMaxHealth());
        }
    }
    public void setPlayerHealthClamped(ServerPlayerEntity player, double health) {
        if (health > MAX_HEALTH) health = MAX_HEALTH;
        setPlayerHealth(player, health);
    }
    public double getPlayerHealth(ServerPlayerEntity player) {
        return player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
    }
    public double getRoundedHealth(ServerPlayerEntity player) {
        return Math.floor(player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)*10)/10.0;
    }
    public void syncPlayerHealth(ServerPlayerEntity player) {
        setPlayerHealth(player, player.getHealth());
    }
    public void syncAllPlayerHealth() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            setPlayerHealth(player, player.getHealth());
        }
    }
    public void resetPlayerHealth(ServerPlayerEntity player) {
        setPlayerHealth(player, MAX_HEALTH);
    }
    public void resetAllPlayerHealth() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            resetPlayerHealth(player);
        }
    }
}
