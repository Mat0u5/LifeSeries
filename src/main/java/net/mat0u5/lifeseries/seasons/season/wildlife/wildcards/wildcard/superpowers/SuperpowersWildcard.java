package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

    public static void resetSuperpower(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
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
        List<ServerPlayerEntity> allPlayers = livesManager.getAlivePlayers();
        Collections.shuffle(allPlayers);
        for (ServerPlayerEntity player : allPlayers) {
            Superpowers power = implemented.get(pos%implemented.size());
            if (power == Superpowers.LISTENING && !VoicechatMain.isConnectedToSVC(player.getUuid())) {
                pos++;
                power = implemented.get(pos%implemented.size());
            }
            if (assignedSuperpowers.containsKey(player.getUuid())) {
                power = assignedSuperpowers.get(player.getUuid());
                assignedSuperpowers.remove(player.getUuid());
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
            if (instance != null) playerSuperpowers.put(player.getUuid(), instance);
            pos++;
        }
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.of(Identifier.of("minecraft","wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void rollRandomSuperpowerForPlayer(ServerPlayerEntity player) {
        List<Superpowers> implemented = new java.util.ArrayList<>(Superpowers.getImplemented());
        implemented.remove(Superpowers.NECROMANCY);
        if (!VoicechatMain.isConnectedToSVC(player.getUuid())) {
            implemented.remove(Superpowers.LISTENING);
        }
        Collections.shuffle(implemented);

        Superpowers power = implemented.getFirst();
        if (assignedSuperpowers.containsKey(player.getUuid())) {
            power = assignedSuperpowers.get(player.getUuid());
            assignedSuperpowers.remove(player.getUuid());
        }

        Superpower instance = power.getInstance(player);
        if (instance != null) playerSuperpowers.put(player.getUuid(), instance);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("minecraft","wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void setSuperpower(ServerPlayerEntity player, Superpowers superpower) {
        if (playerSuperpowers.containsKey(player.getUuid())) {
            playerSuperpowers.get(player.getUuid()).turnOff();
        }
        Superpower instance = superpower.getInstance(player);
        if (instance != null) playerSuperpowers.put(player.getUuid(), instance);
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("minecraft","wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void pressedSuperpowerKey(ServerPlayerEntity player) {
        if (playerSuperpowers.containsKey(player.getUuid())) {
            if (player.ls$isAlive()) {
                playerSuperpowers.get(player.getUuid()).onKeyPressed();
            }
            else {
                PlayerUtils.displayMessageToPlayer(player, Text.literal("Dead players can't use superpowers!"), 60);
            }
        }
    }

    public static boolean hasPower(ServerPlayerEntity player) {
        return playerSuperpowers.containsKey(player.getUuid());
    }

    public static boolean hasActivePower(ServerPlayerEntity player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return false;
        Superpower power = playerSuperpowers.get(player.getUuid());
        if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
            return mimicry.getMimickedPower().getSuperpower() == superpower;
        }
        return power.getSuperpower() == superpower;
    }

    public static boolean hasActivatedPower(ServerPlayerEntity player, Superpowers superpower) {
        if (!hasActivePower(player, superpower)) return false;
        Superpower power = playerSuperpowers.get(player.getUuid());
        if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
            return mimicry.getMimickedPower().active;
        }
        return power.active;
    }

    public static Superpowers getSuperpower(ServerPlayerEntity player) {
        if (playerSuperpowers.containsKey(player.getUuid())) {
            Superpower power = playerSuperpowers.get(player.getUuid());
            if (power instanceof Mimicry mimicry) {
                return mimicry.getMimickedPower().getSuperpower();
            }
            return power.getSuperpower();
        }
        return Superpowers.NULL;
    }

    @Nullable
    public static Superpower getSuperpowerInstance(ServerPlayerEntity player) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return null;
        Superpower power = playerSuperpowers.get(player.getUuid());
        if (power instanceof Mimicry mimicry) {
            return mimicry.getMimickedPower();
        }
        return power;
    }
}
