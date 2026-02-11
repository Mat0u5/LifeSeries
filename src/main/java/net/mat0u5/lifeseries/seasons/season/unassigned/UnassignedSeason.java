package net.mat0u5.lifeseries.seasons.season.unassigned;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class UnassignedSeason extends Season {
    @Override
    public Seasons getSeason() {
        return Seasons.UNASSIGNED;
    }
    @Override
    public ConfigManager createConfig() {
        return new ConfigManager(null, null) {
            @Override
            public void instantiateProperties() {
                WORLDBORDER_SIZE.defaultValue = 30_000_000;
                //? if >= 1.21.11 {
                WORLDBORDER_NETHER_SIZE.defaultValue = 30_000_000;
                WORLDBORDER_END_SIZE.defaultValue = 30_000_000;
                //?}
                KEEP_INVENTORY.defaultValue = false;
                SHOW_ADVANCEMENTS.defaultValue = true;
                LOCATOR_BAR.defaultValue = true;
            }

            @Override
            protected List<ConfigFileEntry<?>> getDefaultConfigEntries() { return new ArrayList<>(List.of()); }
        };
    }

    @Override
    public void onPlayerJoin(ServerPlayer player) {
        TaskScheduler.scheduleTask(Time.seconds(5), this::broadcastNotice);
    }
    @Override
    public void onPlayerFinishJoining(ServerPlayer player) {
        super.onPlayerFinishJoining(player);
        if (!Main.modDisabled()) {
            SimplePackets.SELECT_SEASON.target(player).sendToClient("");
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        broadcastNotice();
    }

    @Override
    public Integer getDefaultLives() {
        return null;
    }

    public void broadcastNotice() {
        if (Main.modDisabled()) return;
        if (currentSeason.getSeason() != Seasons.UNASSIGNED) return;
        PlayerUtils.broadcastMessage(Component.literal("[LifeSeries] You must select a season with ").withStyle(ChatFormatting.RED)
                .append(Component.literal("'/lifeseries setSeries <series>'").withStyle(ChatFormatting.GRAY)), 120);
        PlayerUtils.broadcastMessage(Component.literal("You must have §noperator permissions§r to use most commands in this mod.").withStyle(ChatFormatting.RED), 120);
        Component text = TextUtils.format("§7Click {}§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)", TextUtils.openURLText("https://discord.gg/QWJxfb4zQZ"));
        PlayerUtils.broadcastMessage(text, 120);
    }
}
