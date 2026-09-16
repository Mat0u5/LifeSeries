package net.mat0u5.lifeseries.client.utils;

import net.mat0u5.lifeseries.LifeSeries;

public class ClientTaskScheduler {

    private static final net.mat0u5.matlib.util.other.TaskScheduler clientTaskScheduler = new net.mat0u5.matlib.util.other.TaskScheduler() {
        @Override
        public boolean isDisabled() {
            return LifeSeries.modDisabled();
        }
    };

    public static void scheduleTask(int ticks, Runnable goal) {
        clientTaskScheduler.scheduleTask(ticks, goal);
    }

    public static void schedulePriorityTask(int ticks, Runnable goal) {
        clientTaskScheduler.schedulePriorityTask(ticks, goal);
    }

    public static void clearTasks() {
        clientTaskScheduler.clearTasks();
    }

    public static void onClientTick() {
        clientTaskScheduler.onTick(false);
    }
}
