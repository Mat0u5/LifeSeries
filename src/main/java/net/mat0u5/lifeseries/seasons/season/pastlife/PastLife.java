package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PastLife extends Season {
    @Override
    public Seasons getSeason() {
        return Seasons.PAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new PastLifeConfig();
    }

    @Override
    public BoogeymanManager createBoogeymanManager() {
        return new PastLifeBoogeymanManager();
    }

    @Override
    public void addSessionActions() {
        if (boogeymanManager.BOOGEYMAN_ENABLED && secretSociety.SOCIETY_ENABLED) {
            TaskScheduler.scheduleTask(Time.seconds(1), this::requestSessionAction);
            return;
        }
        super.addSessionActions();
    }

    public void requestSessionAction() {
        for (ServerPlayer player : PlayerUtils.getAdminPlayers()) {
            if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                SimplePackets.PAST_LIFE_CHOOSE_TWIST.target(player).sendToClient();
            }
            else {
                player.sendSystemMessage(Component.nullToEmpty("§7Past Life session started:"));
                player.sendSystemMessage(Component.nullToEmpty("§7 Type §f\"/pastlife boogeyman\"§7 to have the Boogeyman in this session."));
                player.sendSystemMessage(Component.nullToEmpty("§7 Type §f\"/pastlife society\"§7 to have the Secret Society in this session."));
                player.sendSystemMessage(Component.nullToEmpty("§7 Or type §f\"/pastlife pickRandom\"§7 if you want the game to pick randomly."));
            }
        }
    }
}
