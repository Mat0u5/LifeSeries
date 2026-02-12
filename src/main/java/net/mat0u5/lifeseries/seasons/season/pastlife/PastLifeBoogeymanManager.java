package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PastLifeBoogeymanManager extends BoogeymanManager {
    @Override
    public void messageBoogeyman(Boogeyman boogeyman, ServerPlayer boogey) {
        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            boogey.sendSystemMessage(ModifiableText.BOOGEYMAN_PASTLIFE_MESSAGE_PT1.get());
        });
        TaskScheduler.scheduleTask(Time.seconds(7), () -> {
            boogey.sendSystemMessage(ModifiableText.BOOGEYMAN_PASTLIFE_MESSAGE_PT2.get());
        });
        TaskScheduler.scheduleTask(Time.seconds(11), () -> {
            boogey.sendSystemMessage(ModifiableText.BOOGEYMAN_PASTLIFE_MESSAGE_PT3.get());
        });
        TaskScheduler.scheduleTask(Time.seconds(14), () -> {
            boogey.sendSystemMessage(ModifiableText.BOOGEYMAN_PASTLIFE_MESSAGE_PT4.get());
        });
        TaskScheduler.scheduleTask(Time.seconds(17), () -> {
            boogey.sendSystemMessage(ModifiableText.BOOGEYMAN_PASTLIFE_MESSAGE_PT5.get());
        });

        if (boogeyman != null && boogeyman.killsNeeded != 1) {
            TaskScheduler.scheduleTask(Time.seconds(20), () -> {
                boogey.sendSystemMessage(ModifiableText.BOOGEYMAN_PASTLIFE_MESSAGE_PT6.get(boogeyman.killsNeeded, TextUtils.pluralize("kill", boogeyman.killsNeeded)));
            });
        }
    }
}
