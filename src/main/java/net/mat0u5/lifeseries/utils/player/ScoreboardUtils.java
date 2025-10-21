package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class ScoreboardUtils {

    public static void createObjective(String name) {
        createObjective(name, name, ScoreboardCriterion.DUMMY);
    }

    public static void createObjective(String name, String displayName, ScoreboardCriterion criterion) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard.getNullableObjective(name) != null) return;
        scoreboard.addObjective(name, criterion, Text.literal(displayName), criterion.getDefaultRenderType(), false, null);
    }

    public static boolean existsObjective(String name) {
        if (server == null) return false;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getNullableObjective(name) != null;
    }

    public static ScoreboardObjective getObjective(String name) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getNullableObjective(name);
    }

    public static ScoreboardObjective getObjectiveInSlot(ScoreboardDisplaySlot slot) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getObjectiveForSlot(slot);
    }

    public static void setObjectiveInSlot(ScoreboardDisplaySlot slot, String name) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        scoreboard.setObjectiveSlot(slot, scoreboard.getNullableObjective(name));
    }

    public static void removeObjective(String name) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(name);
        if (objective == null) return;
        scoreboard.removeObjective(objective);
    }

    public static void setScore(ServerPlayerEntity player, String objectiveName, int score) {
        setScore(ScoreHolder.fromName(player.getNameForScoreboard()), objectiveName, score);
    }

    public static void setScore(ScoreHolder holder, String objectiveName, int score) {
        if (livesManager != null && livesManager.LIVES_SYSTEM_DISABLED && objectiveName.equals(LivesManager.SCOREBOARD_NAME)) {
            return;
        }
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective == null) return;
        scoreboard.getOrCreateScore(holder, objective).setScore(score);
    }

    public static Collection<ScoreboardEntry> getScores(String objectiveName) {
        if (server == null) return Collections.emptyList();
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective == null) return Collections.emptyList();
        return scoreboard.getScoreboardEntries(objective);
    }

    public static Integer getScore(ScoreHolder holder, String objectiveName) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective == null) return -1;
        ReadableScoreboardScore score = scoreboard.getScore(holder, objective);
        if (score == null) return null;
        return score.getScore();
    }

    public static void setScore(ServerPlayerEntity player, String objectiveName) {
        resetScore(ScoreHolder.fromName(player.getNameForScoreboard()), objectiveName);
    }

    public static void resetScore(ScoreHolder holder, String objectiveName) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective == null) return;
        scoreboard.removeScore(holder, objective);
    }
}
