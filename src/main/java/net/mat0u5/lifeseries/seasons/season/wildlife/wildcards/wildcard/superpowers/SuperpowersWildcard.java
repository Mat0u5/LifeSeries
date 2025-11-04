package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;

public class SuperpowersWildcard extends Wildcard {
    public static boolean WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = false;
    public static List<Superpowers> blacklistedPowers = List.of();
    private static final Map<UUID, Superpower> playerSuperpowers = new HashMap<>();
    public static final Map<UUID, Superpowers> assignedSuperpowers = new HashMap<>();

    public static void setBlacklist(String blacklist) {
        blacklistedPowers = new ArrayList<>();
        String[] powers = blacklist.replace("[","").replace("]","").split(",");
        for (String powerName : powers) {
            Superpowers power = Superpowers.fromString(powerName.trim());
            if (power == null || power == Superpowers.NULL) continue;
            blacklistedPowers.add(power);
        }
    }

    @Override
    public Wildcards getType() {
        return Wildcards.SUPERPOWERS;
    }

    @Override
    public void activate() {
        rollRandomSuperpowers();
        super.activate();
    }

    @Override
    public void deactivate() {
        resetAllSuperpowers();
        super.deactivate();
    }

    public static void onTick() {
        playerSuperpowers.values().forEach(Superpower::tick);
    }

    public static void resetSuperpower(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (!playerSuperpowers.containsKey(uuid)) {
            return;
        }
        playerSuperpowers.get(uuid).turnOff();
        playerSuperpowers.remove(uuid);
    }

    public static void resetAllSuperpowers() {
        playerSuperpowers.values().forEach(Superpower::turnOff);
        playerSuperpowers.clear();
    }

    public static void rollRandomSuperpowers() {
        resetAllSuperpowers();
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        blacklistedPowers.forEach(implemented::remove);
        boolean shouldIncludeNecromancy = implemented.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded();
        boolean shouldRandomizeNecromancy = false;
        double necromancyRandomizeChance = 0;
        if (shouldIncludeNecromancy) {
            int alivePlayersNum = livesManager.getAlivePlayers().size();
            int deadPlayersNum = livesManager.getDeadPlayers().size();
            int totalPlayersNum = alivePlayersNum + deadPlayersNum;
            if (totalPlayersNum >= 6) {
                implemented.remove(Superpowers.NECROMANCY);
                shouldRandomizeNecromancy = true;
                necromancyRandomizeChance = (double)deadPlayersNum / (double)alivePlayersNum;
            }
        }
        else {
            implemented.remove(Superpowers.NECROMANCY);
        }

        Collections.shuffle(implemented);
        int pos = 0;
        List<ServerPlayer> allPlayers = livesManager.getAlivePlayers();
        Collections.shuffle(allPlayers);
        for (ServerPlayer player : allPlayers) {
            Superpowers power = implemented.get(pos%implemented.size());
            if (power == Superpowers.LISTENING && !VoicechatMain.isConnectedToSVC(player.getUUID())) {
                pos++;
                power = implemented.get(pos%implemented.size());
            }
            if (assignedSuperpowers.containsKey(player.getUUID())) {
                power = assignedSuperpowers.get(player.getUUID());
                assignedSuperpowers.remove(player.getUUID());
            }
            else if (shouldIncludeNecromancy && shouldRandomizeNecromancy) {
                if (player.getRandom().nextDouble() <= necromancyRandomizeChance) {
                    power = Superpowers.NECROMANCY;
                }
            }
            if (power == Superpowers.NECROMANCY) {
                implemented.remove(Superpowers.NECROMANCY);
                shouldIncludeNecromancy = false;
            }
            Superpower instance = power.getInstance(player);
            if (instance != null) playerSuperpowers.put(player.getUUID(), instance);
            pos++;
        }
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void rollRandomSuperpowerForPlayer(ServerPlayer player) {
        List<Superpowers> implemented = new java.util.ArrayList<>(Superpowers.getImplemented());
        implemented.remove(Superpowers.NECROMANCY);
        if (!VoicechatMain.isConnectedToSVC(player.getUUID())) {
            implemented.remove(Superpowers.LISTENING);
        }
        Collections.shuffle(implemented);

        Superpowers power = implemented.getFirst();
        if (assignedSuperpowers.containsKey(player.getUUID())) {
            power = assignedSuperpowers.get(player.getUUID());
            assignedSuperpowers.remove(player.getUUID());
        }

        Superpower instance = power.getInstance(player);
        if (instance != null) playerSuperpowers.put(player.getUUID(), instance);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
            playerSuperpowers.get(player.getUUID()).turnOff();
        }
        Superpower instance = superpower.getInstance(player);
        if (instance != null) playerSuperpowers.put(player.getUUID(), instance);
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void pressedSuperpowerKey(ServerPlayer player) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
            if (player.ls$isAlive()) {
                playerSuperpowers.get(player.getUUID()).onKeyPressed();
            }
            else {
                PlayerUtils.displayMessageToPlayer(player, Component.literal("Dead players can't use superpowers!"), 60);
            }
        }
    }

    public static boolean hasPower(ServerPlayer player) {
        return playerSuperpowers.containsKey(player.getUUID());
    }

    public static boolean hasActivePower(ServerPlayer player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return false;
        Superpower power = playerSuperpowers.get(player.getUUID());
        if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
            return mimicry.getMimickedPower().getSuperpower() == superpower;
        }
        return power.getSuperpower() == superpower;
    }

    public static boolean hasActivatedPower(ServerPlayer player, Superpowers superpower) {
        if (!hasActivePower(player, superpower)) return false;
        Superpower power = playerSuperpowers.get(player.getUUID());
        if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
            return mimicry.getMimickedPower().active;
        }
        return power.active;
    }

    public static Superpowers getSuperpower(ServerPlayer player) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
            Superpower power = playerSuperpowers.get(player.getUUID());
            if (power instanceof Mimicry mimicry) {
                return mimicry.getMimickedPower().getSuperpower();
            }
            return power.getSuperpower();
        }
        return Superpowers.NULL;
    }

    @Nullable
    public static Superpower getSuperpowerInstance(ServerPlayer player) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return null;
        Superpower power = playerSuperpowers.get(player.getUUID());
        if (power instanceof Mimicry mimicry) {
            return mimicry.getMimickedPower();
        }
        return power;
    }
}
