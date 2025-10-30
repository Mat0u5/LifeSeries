package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.livesManager;

public class AdvancedDeathsManager {
    private static final Random rnd = new Random();
    private static final Map<UUID, PlayerAdvancedDeath> queuedDeaths = new HashMap<>();

    public static void tick() {
        if (queuedDeaths.isEmpty()) return;
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, PlayerAdvancedDeath> entry : queuedDeaths.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerAdvancedDeath playerAdvancedDeath = entry.getValue();

            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            Integer playerLives = player == null ? null : player.ls$getLives();
            if (player == null || playerAdvancedDeath.queuedDeaths().isEmpty() || playerLives == null
                    || playerLives <= playerAdvancedDeath.lives()) {
                toRemove.add(uuid);
                continue;
            }
            if (playerAdvancedDeath.queuedDeaths().getFirst().isFinished() ||
                    (playerAdvancedDeath.queuedDeaths().getFirst().started && playerAdvancedDeath.queuedDeaths().getFirst().playerNotFound())) {
                playerAdvancedDeath.queuedDeaths().removeFirst();
                if (playerAdvancedDeath.queuedDeaths().isEmpty()) {
                    toRemove.add(uuid);
                    continue;
                }
            }
            playerAdvancedDeath.queuedDeaths().getFirst().onTick();
        }
        for (UUID uuid : toRemove) {
            PlayerAdvancedDeath playerAdvancedDeath = queuedDeaths.get(uuid);
            livesManager.setScore(playerAdvancedDeath.nameForScoreboard(), playerAdvancedDeath.lives());
            playerAdvancedDeath.queuedDeaths().forEach(AdvancedDeath::onEnd);
            queuedDeaths.remove(uuid);
        }
    }

    public static void setPlayerLives(ServerPlayerEntity player, int lives) {
        Integer currentLives = player.ls$getLives();
        if (currentLives == null || currentLives <= lives) {
            player.ls$setLives(lives);
            return;
        }
        int amountOfDeaths = currentLives - lives;
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            amountOfDeaths = (int) Math.ceil(((double) amountOfDeaths) / Math.abs(LimitedLife.DEATH_NORMAL));
        }

        List<AdvancedDeath> queuedPlayerDeaths = getRandomDeaths(player, amountOfDeaths);

        if (queuedPlayerDeaths.isEmpty()) {
            player.ls$setLives(lives);
            return;
        }

        queuedDeaths.put(player.getUuid(), new PlayerAdvancedDeath(player.getNameForScoreboard(), lives, queuedPlayerDeaths));
    }

    public static void onPlayerDeath(ServerPlayerEntity player) {
        if (queuedDeaths.isEmpty()) return;
        for (Map.Entry<UUID, PlayerAdvancedDeath> entry : queuedDeaths.entrySet()) {
            UUID uuid = entry.getKey();
            if (uuid != player.getUuid()) continue;
            PlayerAdvancedDeath playerAdvancedDeath = entry.getValue();
            playerAdvancedDeath.queuedDeaths().getFirst().onEnd();
            playerAdvancedDeath.queuedDeaths().removeFirst();
        }
    }

    public static boolean hasQueuedDeath(ServerPlayerEntity player) {
        return queuedDeaths.containsKey(player.getUuid());
    }

    public static List<AdvancedDeath> getRandomDeaths(ServerPlayerEntity player, int deathCount) {
        List<AdvancedDeaths> allDeaths = AdvancedDeaths.getAllDeaths();
        List<AdvancedDeath> result = new ArrayList<>();
        Collections.shuffle(allDeaths);

        AdvancedDeaths lastDeath = null;
        for (int i = 0; i < deathCount; i++) {
            AdvancedDeaths death;
            if (i < allDeaths.size()) {
                death = allDeaths.get(i);
            }
            else {
                List<AdvancedDeaths> newDeaths = AdvancedDeaths.getAllDeaths();
                if (lastDeath != null) {
                    newDeaths.remove(lastDeath);
                }
                if (newDeaths.isEmpty()) {
                    death = lastDeath;
                }
                else {
                    death = newDeaths.get(rnd.nextInt(newDeaths.size()));
                }
            }

            lastDeath = death;
            if (death == null) continue;
            AdvancedDeath deathInstance = death.getInstance(player);
            if (deathInstance == null) continue;
            result.add(deathInstance);
        }

        return result;
    }

    public record PlayerAdvancedDeath(String nameForScoreboard, int lives, List<AdvancedDeath> queuedDeaths) {}
}
