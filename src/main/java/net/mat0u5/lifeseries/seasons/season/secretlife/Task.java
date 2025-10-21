package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.mat0u5.lifeseries.Main.livesManager;

public class Task {
    public String rawTask;
    public TaskTypes type;
    public boolean anyPlayers = true;
    public boolean anyGreenPlayers = true;
    public boolean anyYellowPlayers = true;
    public boolean anyRedPlayers = true;
    public String formattedTask = "";
    public Task(String task, TaskTypes type) {
        this.rawTask = task;
        this.type = type;
    }

    public static boolean anyPlayersOnLives(ServerPlayerEntity exception, int lives) {
        for (ServerPlayerEntity player : livesManager.getAlivePlayers()) {
            if (player == exception) continue;
            if (livesManager.isOnSpecificLives(player, lives, false)) return true;
        }
        return false;
    }

    public static boolean anyAlivePlayers(ServerPlayerEntity exception) {
        for (ServerPlayerEntity player : livesManager.getAlivePlayers()) {
            if (player == exception) continue;
            return true;
        }
        return false;
    }

    public void checkPlayerColors(ServerPlayerEntity owner) {
        anyGreenPlayers = anyPlayersOnLives(owner, 3);
        anyYellowPlayers = anyPlayersOnLives(owner, 2);
        anyRedPlayers = anyPlayersOnLives(owner, 1);
        anyPlayers = anyAlivePlayers(owner);
    }

    public boolean isValid(ServerPlayerEntity owner) {
        if (rawTask == null) return false;
        if (rawTask.isEmpty()) return false;
        checkPlayerColors(owner);
        if (rawTask.contains("${random_player}") && !anyPlayers) return false;
        if (rawTask.contains("${green/yellow}") && !anyGreenPlayers && !anyYellowPlayers) return false;
        if (rawTask.contains("${green}") && !anyGreenPlayers) return false;
        if (rawTask.contains("${yellow}") && !anyYellowPlayers) return false;
        if (rawTask.contains("${red}") && !anyRedPlayers) return false;
        return true;
    }
    /*
    "\n" - Line Break
    "\\p" - Page Break
    ${random_player} - Replaced with random player name.
    ${green/yellow} - Replaced with "green" if there are any alive, or "yellow", if greens are dead. If both are dead, tasks are unavailable.
    ${green} - Replaced with "green". Tasks are only available when a green player is alive.
    ${yellow} - Replaced with "yellow". Tasks are only available when a yellow player is alive.
    ${red} - Replaced with "red". Tasks are only available when a red player is alive.
     */
    public List<RawFilteredPair<Text>> getBookLines(ServerPlayerEntity owner) {
        formattedTask = "";
        List<RawFilteredPair<Text>> lines = new ArrayList<>();
        int pageNum = 0;
        for (String page : rawTask.split("\\\\p")) {
            page = formatString(owner, page);
            lines.add(RawFilteredPair.of(Text.of(page)));

            if (pageNum != 0) {
                formattedTask += "\n";
            }
            formattedTask += page;

            pageNum++;
        }
        return lines;
    }

    public String formatString(ServerPlayerEntity owner, String page) {
        checkPlayerColors(owner);
        if (page.contains("${random_player}")) {
            List<ServerPlayerEntity> players = livesManager.getAlivePlayers();
            players.remove(owner);
            if (!players.isEmpty()) {
                Collections.shuffle(players);
                page = page.replaceAll("\\$\\{random_player}",players.getFirst().getNameForScoreboard());
            }
        }
        if (page.contains("${green/yellow}")) {
            if (anyGreenPlayers) page = page.replaceAll("\\$\\{green/yellow}","green");
            else if (anyYellowPlayers) page = page.replaceAll("\\$\\{green/yellow}","yellow");
        }
        if (page.contains("${green}")) {
            if (anyGreenPlayers) page = page.replaceAll("\\$\\{green}","green");
        }
        if (page.contains("${yellow}")) {
            if (anyYellowPlayers) page = page.replaceAll("\\$\\{yellow}","yellow");
        }
        if (page.contains("${red}")) {
            if (anyRedPlayers) page = page.replaceAll("\\$\\{red}","red");
        }
        if (page.contains("${kill_not_permitted}")) {
            if (anyYellowPlayers) page = page.replaceAll("\\$\\{kill_not_permitted}","");
        }
        return page;
    }

    public int getDifficulty() {
        if (type == TaskTypes.EASY) return 1;
        if (type == TaskTypes.HARD) return 2;
        if (type == TaskTypes.RED) return 3;
        return 0;
    }
}
