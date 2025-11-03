package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeLivesManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class LastLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /givelife, /boogeyman";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /givelife";
    public static int ROLL_MAX_LIVES = 6;
    public static int ROLL_MIN_LIVES = 2;

    @Override
    public Seasons getSeason() {
        return Seasons.LAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new LastLifeConfig();
    }

    @Override
    public LivesManager createLivesManager() {
        return new LastLifeLivesManager();
    }

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
    }

    @Override
    public void addSessionActions() {
        super.addSessionActions();
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            currentSession.addSessionAction(lastLifeLivesManager.actionChooseLives);
        }
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof LastLifeConfig config)) return;
        int minLivesConfig = config.RANDOM_LIVES_MIN.get(config);
        int maxLivesConfig = config.RANDOM_LIVES_MAX.get(config);
        ROLL_MIN_LIVES = Math.min(minLivesConfig, maxLivesConfig);
        ROLL_MAX_LIVES = Math.max(minLivesConfig, maxLivesConfig);
    }

    @Override
    public Integer getDefaultLives() {
        return null;
    }

    @Override
    public boolean sessionStart() {
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            lastLifeLivesManager.reset();
        }
        return super.sessionStart();
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            lastLifeLivesManager.reset();
        }
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayer player) {
        super.onPlayerFinishJoining(player);
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            lastLifeLivesManager.onPlayerFinishJoining(player);
        }
    }
}
