package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class LastLifeLivesManager extends LivesManager {

    public boolean assignedLives = false;

    public SessionAction actionChooseLives = new SessionAction(Time.minutes(1),"Assign lives if necessary") {
        @Override
        public void trigger() {
            assignRandomLivesToUnassignedPlayers();
        }
    };
    Random rnd = new Random();

    public void assignRandomLivesToUnassignedPlayers() {
        assignedLives = true;
        List<ServerPlayer> assignTo = new ArrayList<>();
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (player.ls$hasAssignedLives()) continue;
            assignTo.add(player);
        }
        if (assignTo.isEmpty()) return;
        assignRandomLives(assignTo);
    }

    public void assignRandomLives(List<ServerPlayer> players) {
        players.forEach(this::resetPlayerLife);
        PlayerUtils.sendTitleToPlayers(players, Component.literal("You will have...").withStyle(ChatFormatting.GRAY), 10, 40, 10);
        TaskScheduler.scheduleTask(Time.seconds(3), ()-> rollLives(players));
    }

    public void rollLives(List<ServerPlayer> players) {
        int delay = showRandomNumbers(players) + 20;

        HashMap<ServerPlayer, Integer> lives = new HashMap<>();

        int totalSize = players.size();
        int chosenNotRandomly = LastLife.ROLL_MIN_LIVES;
        for (ServerPlayer player : players) {
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
            for (Map.Entry<ServerPlayer, Integer> playerEntry : lives.entrySet()) {
                Integer livesNum = playerEntry.getValue();
                ServerPlayer player = playerEntry.getKey();
                Component textLives = getFormattedLives(livesNum);
                PlayerUtils.sendTitle(player, textLives, 0, 25, 0);
            }
            PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
        });

        delay += 20;

        TaskScheduler.scheduleTask(delay, () -> {
            //Show "x lives." screen
            for (Map.Entry<ServerPlayer, Integer> playerEntry : lives.entrySet()) {
                Integer livesNum = playerEntry.getValue();
                ServerPlayer player = playerEntry.getKey();
                Component textLives = TextUtils.format("{}§a {}.", getFormattedLives(livesNum), TextUtils.pluralize("life","lives", livesNum));
                PlayerUtils.sendTitle(player, textLives, 0, 60, 20);
                SessionTranscript.assignRandomLives(player, livesNum);
                setPlayerLives(player, livesNum);
            }
            PlayerUtils.playSoundToPlayers(lives.keySet(), SoundEvents.END_PORTAL_SPAWN);
            currentSeason. reloadAllPlayerTeams();
        });
    }

    public int showRandomNumbers(List<ServerPlayer> players) {
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

    public void onPlayerFinishJoining(ServerPlayer player) {
        if (!assignedLives) return;
        if (hasAssignedLives(player)) return;
        if (player.ls$isWatcher()) return;
        PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§7Assigning random lives to {}§7...", player));
        assignRandomLives(new ArrayList<>(List.of(player)));
    }
}
