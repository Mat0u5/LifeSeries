package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.snail.server.SnailPathfinding;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.server;

public class Snails extends Wildcard {
    public static StringListConfig snailNameConfig;

    public static Map<UUID, Snail> snails = new HashMap<>();
    public static Map<UUID, String> snailNames = new HashMap<>();
    long ticks = 0;

    @Override
    public Wildcards getType() {
        return Wildcards.SNAILS;
    }

    @Override
    public void activate() {
        snails.clear();
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (!canHaveSnail(player)) continue;
            spawnSnailFor(player);
        }
        loadSnailNames();
        if (!currentSession.statusStarted()) {
            PlayerUtils.broadcastMessageToAdmins(ModifiableText.WILDLIFE_SNAIL_INFO.get());
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        snails.clear();
        killAllSnails();
        super.deactivate();
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks % 100 == 0) {
            for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
                if (!canHaveSnail(player)) continue;
                UUID playerUUID = player.getUUID();
                if (snails.containsKey(playerUUID)) {
                    Snail snail = snails.get(playerUUID);
                    if (snail == null || !snail.isAlive()) {
                        snails.remove(playerUUID);
                        spawnSnailFor(player);
                    }
                }
                else {
                    spawnSnailFor(player);
                }
            }
        }
    }
    public static boolean canHaveSnail(ServerPlayer player) {
        if (player.isCreative()) return false;
        if (!player.isAlive()) return false;
        if (Events.joiningPlayers.contains(player.getUUID())) return false;
        if (player.isSpectator() && !SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) return false;
        return true;
    }

    public static void spawnSnailFor(ServerPlayer player) {
        BlockPos pos = SnailPathfinding.getBlockPosNearPlayer(player, 30);
        if (pos == null) pos = player.blockPosition().offset(0,30,0);
        spawnSnailFor(player, pos);
    }

    public static void spawnSnailFor(ServerPlayer player, BlockPos pos) {
        if (player == null || pos == null) return;
        Snail snail = LevelUtils.spawnEntity(MobRegistry.SNAIL, player.ls$getServerLevel(), pos);
        if (snail != null) {
            snail.serverData.setBoundPlayer(player);
            snails.put(player.getUUID(), snail);
        }
    }

    public static void killAllSnails() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Snail snail && !snail.isFromTrivia()) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void reloadSnails() {
        reloadSnailNames();
        reloadSnailSkins();
    }

    public static void reloadSnailNames() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.serverData.updateSnailName();
        }
    }

    public static void reloadSnailSkins() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.serverData.updateSkin(snail.serverData.getBoundPlayer());
        }
    }

    public static void setSnailName(ServerPlayer player, String name) {
        snailNames.put(player.getUUID(), name);
        reloadSnailNames();
        saveSnailNames();
    }

    public static void resetSnailName(ServerPlayer player) {
        snailNames.remove(player.getUUID());
        reloadSnailNames();
        saveSnailNames();
    }

    public static String getSnailName(Player player) {
        if (player == null) return "Snail";
        if (snailNames.containsKey(player.getUUID())) {
            return snailNames.get(player.getUUID());
        }
        return ModifiableText.WILDLIFE_SNAIL_DEFAULT_NAME.getString(player);
    }

    public static void saveSnailNames() {
        if (snailNameConfig == null) loadConfig();
        List<String> names = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : snailNames.entrySet()) {
            names.add(entry.getKey().toString()+"_"+entry.getValue().replaceAll("_",""));
        }
        snailNameConfig.save(names);
    }

    public static void loadSnailNames() {
        if (snailNameConfig == null) loadConfig();
        HashMap<UUID, String> newNames = new HashMap<>();
        for (String entry : snailNameConfig.load()) {
            if (!entry.contains("_")) continue;
            String[] split = entry.split("_");
            if (split.length != 2) continue;
            try {
                UUID uuid = UUID.fromString(split[0]);
                newNames.put(uuid, split[1]);
            } catch(Exception ignored) {}
        }
        snailNames = newNames;
    }

    public static void loadConfig() {
        snailNameConfig = new StringListConfig("./config/lifeseries/main", "DO_NOT_MODIFY_wildlife_snailnames.properties");
    }
}
