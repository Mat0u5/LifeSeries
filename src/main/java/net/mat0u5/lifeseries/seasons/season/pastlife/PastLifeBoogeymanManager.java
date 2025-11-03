package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PastLifeBoogeymanManager extends BoogeymanManager {
    @Override
    public void messageBoogeyman(Boogeyman boogeyman, ServerPlayer boogey) {
        TaskScheduler.scheduleTask(100, () -> {
            boogey.sendSystemMessage(Component.nullToEmpty("§7You are the boogeyman."));
        });
        TaskScheduler.scheduleTask(140, () -> {
            boogey.sendSystemMessage(Component.nullToEmpty("§7You must by any means necessary kill a §agreen§7 or §eyellow§7 name"));
            boogey.sendSystemMessage(Component.nullToEmpty("§7by direct action to be cured of the curse."));
        });
        TaskScheduler.scheduleTask(220, () -> {
            boogey.sendSystemMessage(Component.nullToEmpty("§7If you fail, you will become a §cred name§7."));
        });
        TaskScheduler.scheduleTask(280, () -> {
            boogey.sendSystemMessage(Component.nullToEmpty("§7Other players may defend themselves."));
        });
        TaskScheduler.scheduleTask(340, () -> {
            boogey.sendSystemMessage(Component.nullToEmpty("§7Voluntary sacrifices will not cure the curse."));
        });

        if (boogeyman != null && boogeyman.killsNeeded != 1) {
            TaskScheduler.scheduleTask(400, () -> {
                boogey.sendSystemMessage(TextUtils.formatLoosely("§7You need {} {} to be cured of the curse.", boogeyman.killsNeeded, TextUtils.pluralize("kill", boogeyman.killsNeeded)));
            });
        }
    }
}
