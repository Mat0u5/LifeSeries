package net.mat0u5.lifeseries.seasons.season.unassigned;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class UnassignedSeason extends Season {
    @Override
    public Seasons getSeason() {
        return Seasons.UNASSIGNED;
    }
    @Override
    public ConfigManager createConfig() {
        return new ConfigManager(null, null) {
            @Override
            public void instantiateProperties() {}

            @Override
            protected List<ConfigFileEntry<?>> getDefaultConfigEntries() { return new ArrayList<>(List.of()); }
        };
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        TaskScheduler.scheduleTask(100, this::broadcastNotice);
    }
    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        super.onPlayerFinishJoining(player);
        if (!Main.modDisabled()) {
            NetworkHandlerServer.sendStringPacket(player, PacketNames.SELECT_SEASON, "");
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        broadcastNotice();
    }

    @Override
    public String getAdminCommands() {
        return "";
    }

    @Override
    public String getNonAdminCommands() {
        return "";
    }

    @Override
    public Integer getDefaultLives() {
        return null;
    }

    public void broadcastNotice() {
        if (Main.modDisabled()) return;
        if (currentSeason.getSeason() != Seasons.UNASSIGNED) return;
        PlayerUtils.broadcastMessage(Text.literal("[LifeSeries] You must select a season with ").formatted(Formatting.RED)
                .append(Text.literal("'/lifeseries setSeries <series>'").formatted(Formatting.GRAY)), 120);
        PlayerUtils.broadcastMessage(Text.literal("You must have §noperator permissions§r to use most commands in this mod.").formatted(Formatting.RED), 120);
        Text text = TextUtils.format("§7Click {}§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)\"", TextUtils.openURLText("https://discord.gg/QWJxfb4zQZ"));
        PlayerUtils.broadcastMessage(text, 120);
    }
}
