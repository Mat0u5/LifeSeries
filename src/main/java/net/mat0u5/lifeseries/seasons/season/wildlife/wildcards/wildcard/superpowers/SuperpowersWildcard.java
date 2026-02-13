package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;

public class SuperpowersWildcard extends Wildcard {
    public static boolean WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = false;
    public static List<Superpowers> blacklistedPowers = List.of();
    private static final Map<UUID, Superpower> playerSuperpowers = new LinkedHashMap<>();
    public static final Map<UUID, Superpowers> preAssignedSuperpowers = new HashMap<>();
    public static int ZOMBIES_HEALTH = 8;

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
        Necromancy.checkRessurectedPlayersReset();
        DatapackIntegration.deactivateSuperpower(player);
    }

    public static void resetAllSuperpowers() {
        playerSuperpowers.values().forEach(Superpower::turnOff);
        playerSuperpowers.clear();
        Necromancy.checkRessurectedPlayersReset();
        DatapackIntegration.initSuperpowers();
    }

    public static void rollRandomSuperpowers() {
        rollRandomSuperpowers(livesManager.getAlivePlayers());
    }

    public static void rollRandomSuperpowers(List<ServerPlayer> allPlayers) {
        allPlayers.removeIf(ServerPlayer::ls$isDead);
        allPlayers.removeIf(ServerPlayer::ls$isWatcher);
        allPlayers.forEach(SuperpowersWildcard::resetSuperpower);

        List<ServerPlayer> prioritizedList = new ArrayList<>();
        //Put all the players with assigned superpowers first - to prevent duplicating powers.
        for (ServerPlayer player : allPlayers) {
            if (preAssignedSuperpowers.containsKey(player.getUUID())) prioritizedList.add(player);
        }
        for (ServerPlayer player : allPlayers) {
            if (!prioritizedList.contains(player)) prioritizedList.add(player);
        }

        for (ServerPlayer player : prioritizedList) {
            if (hasPower(player)) continue;
            Superpowers power = getRandomPower(player);
            Superpower instance = power.getInstance(player);
            if (instance != null) {
                playerSuperpowers.put(player.getUUID(), instance);
                DatapackIntegration.activateSuperpower(player, power);
            }
        }
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    private static Superpowers getRandomPower(ServerPlayer player) {
        if (preAssignedSuperpowers.containsKey(player.getUUID())) {
            Superpowers power = preAssignedSuperpowers.get(player.getUUID());
            preAssignedSuperpowers.remove(player.getUUID());
            return power;
        }

        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        blacklistedPowers.forEach(implemented::remove);
        if (CompatibilityManager.voicechatLoaded() && !VoicechatMain.isConnectedToSVC(player.getUUID())) {
            implemented.remove(Superpowers.LISTENING);
        }

        List<Superpowers> nonAssigned = new ArrayList<>(implemented);
        for (Superpower assignedPower : playerSuperpowers.values()) {
            Superpowers power = assignedPower.getSuperpower();
            nonAssigned.remove(power);
        }
        Collections.shuffle(nonAssigned);

        boolean canHaveNecromancy = false;
        if (nonAssigned.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded()) {
            int alivePlayersNum = livesManager.getAlivePlayers().size();
            int deadPlayersNum = livesManager.getDeadPlayers().size();
            int totalPlayersNum = alivePlayersNum + deadPlayersNum;
            if (totalPlayersNum >= 6) {
                canHaveNecromancy = true;
                if (player.getRandom().nextDouble() <= (double)deadPlayersNum / (double)alivePlayersNum) {
                    return Superpowers.NECROMANCY;
                }
            }
        }
        if (!canHaveNecromancy) {
            implemented.remove(Superpowers.NECROMANCY);
            nonAssigned.remove(Superpowers.NECROMANCY);
        }

        //A shuffled queue with assigned superpowers at the very end - we can just choose the first power in the list.
        List<Superpowers> queue = new ArrayList<>(nonAssigned);
        for (Superpower assignedPower : playerSuperpowers.values()) {
            //This puts all the assigned powers at the end of the queue.
            Superpowers power = assignedPower.getSuperpower();
            if (!implemented.contains(power)) continue;
            queue.remove(power);
            queue.add(power);
        }

        return queue.get(0);
    }

    public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
            playerSuperpowers.get(player.getUUID()).turnOff();
        }
        Superpower instance = superpower.getInstance(player);
        if (instance != null) {
            playerSuperpowers.put(player.getUUID(), instance);
            DatapackIntegration.activateSuperpower(player, superpower);
        }
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
        Necromancy.checkRessurectedPlayersReset();
    }

    public static void pressedSuperpowerKey(ServerPlayer player) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
            if (player.ls$isAlive()) {
                playerSuperpowers.get(player.getUUID()).onKeyPressed();
            }
            else {
                PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_SUPERPOWES_DEAD.get(), 60);
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
