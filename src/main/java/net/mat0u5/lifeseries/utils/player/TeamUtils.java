package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.server;

public class TeamUtils {

    public static void createTeam(String teamName, ChatFormatting color) {
        createTeam(teamName, teamName, color);
    }

    public static boolean createTeam(String teamName, String displayName, ChatFormatting color) {
        if (server == null) return false;
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard.getPlayerTeam(teamName) != null) {
            // A team with this name already exists
            return false;
        }
        PlayerTeam team = scoreboard.addPlayerTeam(teamName);
        team.setDisplayName(Component.literal(displayName).withStyle(color));
        team.setColor(color);
        team.setSeeFriendlyInvisibles(false);
        return true;
    }

    public static void addEntityToTeam(String teamName, Entity entity) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);

        if (team == null) {
            // A team with this name does not exist
            return;
        }

        scoreboard.addPlayerToTeam(entity.getScoreboardName(), team);
    }

    public static boolean removePlayerFromTeam(ServerPlayer player) {
        if (server == null) return false;
        Scoreboard scoreboard = server.getScoreboard();
        String playerName = player.getScoreboardName();

        PlayerTeam team = scoreboard.getPlayersTeam(playerName);
        if (team == null) {
            Main.LOGGER.warn(TextUtils.formatString("Player {} is not part of any team!", playerName));
            return false;
        }

        scoreboard.removePlayerFromTeam(playerName, team);
        return true;
    }

    public static boolean deleteTeam(String teamName) {
        if (server == null) return false;
        Scoreboard scoreboard = server.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);

        if (team == null) {
            return false;
        }

        scoreboard.removePlayerTeam(team);
        return true;
    }

    public static PlayerTeam getTeam(String teamName) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getPlayerTeam(teamName);
    }

    public static PlayerTeam getPlayerTeam(ServerPlayer player) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getPlayersTeam(player.getScoreboardName());
    }

    public static Collection<PlayerTeam> getAllTeams() {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getPlayerTeams();
    }
}
