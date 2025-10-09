package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

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
        for (ServerPlayerEntity player : livesManager.getAlivePlayers()) {
            if (!player.isAlive()) continue;
            spawnSnailFor(player);
        }
        loadSnailNames();
        if (!currentSession.statusStarted()) {
            PlayerUtils.broadcastMessageToAdmins(Text.of("§7Use the §f'/snail ...'§7 command to modify snail names and to get info on how to change snail textures."));
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
            for (ServerPlayerEntity player : livesManager.getAlivePlayers()) {
                if (!player.isAlive()) continue;
                UUID playerUUID = player.getUuid();
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

    public static void spawnSnailFor(ServerPlayerEntity player) {
        BlockPos pos = Snail.getBlockPosNearTarget(player, 30);
        if (pos == null) pos = player.getBlockPos().add(0,30,0);
        spawnSnailFor(player, pos);
    }

    public static void spawnSnailFor(ServerPlayerEntity player, BlockPos pos) {
        if (player == null || pos == null) return;
        Snail snail = MobRegistry.SNAIL.spawn(PlayerUtils.getServerWorld(player), pos, SpawnReason.COMMAND);
        if (snail != null) {
            snail.setBoundPlayer(player);
            snail.updateSkin(player);
            snails.put(player.getUuid(), snail);
        }
    }

    public static void killAllSnails() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof Snail snail && !snail.fromTrivia) {
                        toKill.add(entity);
                    }

                if (entity instanceof PathFinder) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void reloadSnailNames() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.updateSnailName();
        }
    }

    public static void reloadSnailSkins() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.updateSkin(snail.getActualBoundPlayer());
        }
    }

    public static void setSnailName(ServerPlayerEntity player, String name) {
        snailNames.put(player.getUuid(), name);
        reloadSnailNames();
        saveSnailNames();
    }

    public static void resetSnailName(ServerPlayerEntity player) {
        snailNames.remove(player.getUuid());
        reloadSnailNames();
        saveSnailNames();
    }

    public static String getSnailName(PlayerEntity player) {
        if (snailNames.containsKey(player.getUuid())) {
            return snailNames.get(player.getUuid());
        }
        return TextUtils.formatString("{}'s Snail", player);
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
