package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PastLifeBoogeymanManager extends BoogeymanManager {
    @Override
    public void messageBoogeyman(Boogeyman boogeyman, ServerPlayerEntity boogey) {
        TaskScheduler.scheduleTask(100, () -> {
            boogey.sendMessage(Text.of("§7You are the boogeyman."));
        });
        TaskScheduler.scheduleTask(140, () -> {
            boogey.sendMessage(Text.of("§7You must by any means necessary kill a §agreen§7 or §eyellow§7 name"));
            boogey.sendMessage(Text.of("§7by direct action to be cured of the curse."));
        });
        TaskScheduler.scheduleTask(220, () -> {
            boogey.sendMessage(Text.of("§7If you fail, you will become a §cred name§7."));
        });
        TaskScheduler.scheduleTask(280, () -> {
            boogey.sendMessage(Text.of("§7Other players may defend themselves."));
        });
        TaskScheduler.scheduleTask(340, () -> {
            boogey.sendMessage(Text.of("§7Voluntary sacrifices will not cure the curse."));
        });

        if (boogeyman != null && boogeyman.killsNeeded != 1) {
            TaskScheduler.scheduleTask(400, () -> {
                boogey.sendMessage(TextUtils.formatLoosely("§7You need {} {} to be cured of the curse.", boogeyman.killsNeeded, TextUtils.pluralize("kill", boogeyman.killsNeeded)));
            });
        }
    }
}
