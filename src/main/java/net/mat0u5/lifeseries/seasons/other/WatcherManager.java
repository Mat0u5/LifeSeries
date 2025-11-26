package net.mat0u5.lifeseries.seasons.other;

import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.livesManager;
//? if > 1.20.2
/*import net.minecraft.world.scores.PlayerScoreEntry;*/

public class WatcherManager {
    public static final String SCOREBOARD_NAME = "Watchers";
    public static final String TEAM_NAME = "watcher";
    public static final String TEAM_DISPLAY_NAME = "Watcher";
    private static List<String> watchers = new ArrayList<>();

    public static void createTeams() {
        TeamUtils.createTeam(WatcherManager.TEAM_NAME, WatcherManager.TEAM_DISPLAY_NAME, ChatFormatting.DARK_GRAY);
    }

    public static void createScoreboards() {
        ScoreboardUtils.createObjective(WatcherManager.SCOREBOARD_NAME);
    }

    public static void reloadWatchers() {
        watchers.clear();
        //? if <= 1.20.2 {
        Collection<Score> entries = ScoreboardUtils.getScores(SCOREBOARD_NAME);
        if (entries == null || entries.isEmpty()) return;
        for (Score entry : entries) {
            if (entry.getScore() <= 0) continue;
            watchers.add(entry.getOwner());
        }
        //?} else {
        /*Collection<PlayerScoreEntry> entries = ScoreboardUtils.getScores(SCOREBOARD_NAME);
        if (entries == null || entries.isEmpty()) return;
        for (PlayerScoreEntry entry : entries) {
            if (entry.value() <= 0) continue;
            watchers.add(entry.owner());
        }
        *///?}
    }

    public static void addWatcher(ServerPlayer player) {
        watchers.add(player.getScoreboardName());
        ScoreboardUtils.setScore(player, SCOREBOARD_NAME, 1);
        livesManager.resetPlayerLife(player);
        player.setGameMode(GameType.SPECTATOR);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.resetSoulmate(player);
        }
        player.sendSystemMessage(Component.nullToEmpty("§7§nYou are now a Watcher.\n"));
        player.sendSystemMessage(Component.nullToEmpty("§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe - this is very useful for spectators and for admins."));
        //player.sendMessage(Text.of("§8§oNOTE: This is an experimental feature, report any bugs you find!"));
    }

    public static void removeWatcher(ServerPlayer player) {
        watchers.remove(player.getScoreboardName());
        ScoreboardUtils.resetScore(player, SCOREBOARD_NAME);
        livesManager.resetPlayerLife(player);
        player.sendSystemMessage(Component.nullToEmpty("§7You are no longer a Watcher."));
    }

    public static boolean isWatcher(Player player) {
        return watchers.contains(player.getScoreboardName());
    }

    public static boolean isWatcher(String playerName) {
        return watchers.contains(playerName);
    }

    public static List<String> getWatchers() {
        return watchers;
    }

    public static List<ServerPlayer> getWatcherPlayers() {
        List<ServerPlayer> watcherPlayers = PlayerUtils.getAllPlayers();
        watcherPlayers.removeIf(player -> !isWatcher(player));
        return watcherPlayers;
    }
}
