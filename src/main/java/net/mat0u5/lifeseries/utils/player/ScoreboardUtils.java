package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Collection;
import java.util.Collections;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class ScoreboardUtils {

    public static void createObjective(String name) {
        createObjective(name, name, ObjectiveCriteria.DUMMY);
    }

    public static void createObjective(String name, String displayName, ObjectiveCriteria criterion) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard.getObjective(name) != null) return;
        scoreboard.addObjective(name, criterion, Component.literal(displayName), criterion.getDefaultRenderType(), false, null);
    }

    public static boolean existsObjective(String name) {
        if (server == null) return false;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getObjective(name) != null;
    }

    public static Objective getObjective(String name) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getObjective(name);
    }

    public static Objective getObjectiveInSlot(DisplaySlot slot) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getDisplayObjective(slot);
    }

    public static void setObjectiveInSlot(DisplaySlot slot, String name) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        scoreboard.setDisplayObjective(slot, scoreboard.getObjective(name));
    }

    public static void removeObjective(String name) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(name);
        if (objective == null) return;
        scoreboard.removeObjective(objective);
    }

    public static void setScore(ServerPlayer player, String objectiveName, int score) {
        setScore(ScoreHolder.forNameOnly(player.getScoreboardName()), objectiveName, score);
    }

    public static void setScore(ScoreHolder holder, String objectiveName, int score) {
        if (livesManager != null && livesManager.LIVES_SYSTEM_DISABLED && objectiveName.equals(LivesManager.SCOREBOARD_NAME)) {
            return;
        }
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return;
        scoreboard.getOrCreatePlayerScore(holder, objective).set(score);
    }

    public static Collection<PlayerScoreEntry> getScores(String objectiveName) {
        if (server == null) return Collections.emptyList();
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return Collections.emptyList();
        return scoreboard.listPlayerScores(objective);
    }

    public static Integer getScore(ScoreHolder holder, String objectiveName) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return -1;
        ReadOnlyScoreInfo score = scoreboard.getPlayerScoreInfo(holder, objective);
        if (score == null) return null;
        return score.value();
    }

    public static void setScore(ServerPlayer player, String objectiveName) {
        resetScore(ScoreHolder.forNameOnly(player.getScoreboardName()), objectiveName);
    }

    public static void resetScore(ScoreHolder holder, String objectiveName) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return;
        scoreboard.resetSinglePlayerScore(holder, objective);
    }
}
