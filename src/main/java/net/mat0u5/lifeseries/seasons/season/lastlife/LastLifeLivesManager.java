package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class LastLifeLivesManager extends LivesManager {

    public boolean assignedLives = false;

    public SessionAction actionChooseLives = new SessionAction(
            OtherUtils.minutesToTicks(1),"§7Assign lives if necessary §f[00:01:00]", "Assign lives if necessary"
    ) {
        @Override
        public void trigger() {
            assignRandomLivesToUnassignedPlayers();
        }
    };
    Random rnd = new Random();

    public void assignRandomLivesToUnassignedPlayers() {
        assignedLives = true;
        List<ServerPlayerEntity> assignTo = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerUtils.getAllFunctioningPlayers()) {
            if (player.ls$hasAssignedLives()) continue;
            assignTo.add(player);
        }
        if (assignTo.isEmpty()) return;
        assignRandomLives(assignTo);
    }

    public void assignRandomLives(List<ServerPlayerEntity> players) {
        players.forEach(this::resetPlayerLife);
        PlayerUtils.sendTitleToPlayers(players, Text.literal("You will have...").formatted(Formatting.GRAY), 10, 40, 10);
        int delay = 60;
        TaskScheduler.scheduleTask(delay, ()-> rollLives(players));
    }

    public void rollLives(List<ServerPlayerEntity> players) {
        int delay = showRandomNumbers(players) + 20;

        HashMap<ServerPlayerEntity, Integer> lives = new HashMap<>();

        int totalSize = players.size();
        int chosenNotRandomly = LastLife.ROLL_MIN_LIVES;
        for (ServerPlayerEntity player : players) {
            int diff = LastLife.ROLL_MAX_LIVES-LastLife.ROLL_MIN_LIVES+2;
            if (chosenNotRandomly <= LastLife.ROLL_MAX_LIVES && totalSize > diff) {
                lives.put(player, chosenNotRandomly);
                chosenNotRandomly++;
                continue;
            }

            int randomLives = getRandomLife();
            lives.put(player, randomLives);
        }

        TaskScheduler.scheduleTask(delay, () -> {
            //Show the actual amount of lives for one cycle
            for (Map.Entry<ServerPlayerEntity, Integer> playerEntry : lives.entrySet()) {
                Integer livesNum = playerEntry.getValue();
                ServerPlayerEntity player = playerEntry.getKey();
                Text textLives = getFormattedLives(livesNum);
                PlayerUtils.sendTitle(player, textLives, 0, 25, 0);
            }
            PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
        });

        delay += 20;

        TaskScheduler.scheduleTask(delay, () -> {
            //Show "x lives." screen
            for (Map.Entry<ServerPlayerEntity, Integer> playerEntry : lives.entrySet()) {
                Integer livesNum = playerEntry.getValue();
                ServerPlayerEntity player = playerEntry.getKey();
                Text textLives = TextUtils.format("{}§a lives.", getFormattedLives(livesNum));
                PlayerUtils.sendTitle(player, textLives, 0, 60, 20);
                SessionTranscript.assignRandomLives(player, livesNum);
                setPlayerLives(player, livesNum);
            }
            PlayerUtils.playSoundToPlayers(lives.keySet(), SoundEvents.BLOCK_END_PORTAL_SPAWN);
            currentSeason. reloadAllPlayerTeams();
        });
    }

    public int showRandomNumbers(List<ServerPlayerEntity> players) {
        int currentDelay = 0;
        int lastLives = -1;
        for (int i = 0; i < 80; i++) {
            if (i >= 75) currentDelay += 20;
            else if (i >= 65) currentDelay += 8;
            else if (i >= 50) currentDelay += 4;
            else if (i >= 30) currentDelay += 2;
            else currentDelay += 1;

            int lives = getRandomLife(lastLives);
            lastLives = lives;

            TaskScheduler.scheduleTask(currentDelay, () -> {
                PlayerUtils.sendTitleToPlayers(players, getFormattedLives(lives), 0, 25, 0);
                PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
            });
        }

        return currentDelay;
    }

    public int getRandomLife() {
        int minLives = LastLife.ROLL_MIN_LIVES;
        int maxLives = LastLife.ROLL_MAX_LIVES;
        return rnd.nextInt(minLives, maxLives+1);
    }

    public boolean onlyOnePossibleLife() {
        return LastLife.ROLL_MIN_LIVES == LastLife.ROLL_MAX_LIVES;
    }

    public int getRandomLife(int except) {
        if (!onlyOnePossibleLife()){
            int tries = 0;
            while (tries < 100) {
                tries++;
                int lives = getRandomLife();
                if (lives != except) {
                    return lives;
                }
            }
        }
        return getRandomLife();
    }

    public void reset() {
        assignedLives = false;
    }

    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (!assignedLives) return;
        if (hasAssignedLives(player)) return;
        if (WatcherManager.isWatcher(player)) return;
        PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§7Assigning random lives to {}§7...", player));
        assignRandomLives(new ArrayList<>(List.of(player)));
    }
}
