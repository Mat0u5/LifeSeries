package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.util.other.Time;

public class TaskScheduler {

    private static final net.mat0u5.matlib.util.other.TaskScheduler sharedTaskScheduler = new net.mat0u5.matlib.util.other.TaskScheduler() {
        @Override
        public boolean isDisabled() {
            return LifeSeries.modDisabled();
        }
    };

    public static void scheduleTask(int ticks, Runnable goal) {
        sharedTaskScheduler.scheduleTask(ticks, goal);
    }

    public static void scheduleTask(Time time, Runnable goal) {
        sharedTaskScheduler.scheduleTask(time, goal);
    }

    public static void schedulePriorityTask(int ticks, Runnable goal) {
        sharedTaskScheduler.schedulePriorityTask(ticks, goal);
    }

    public static void schedulePriorityTask(Time time, Runnable goal) {
        sharedTaskScheduler.schedulePriorityTask(time, goal);
    }

    public static void clearTasks() {
        sharedTaskScheduler.clearTasks();
    }

    public static void onTick(boolean gameFrozen) {
        sharedTaskScheduler.onTick(gameFrozen);
    }
}
