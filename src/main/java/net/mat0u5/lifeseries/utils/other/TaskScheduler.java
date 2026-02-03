package net.mat0u5.lifeseries.utils.other;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mat0u5.lifeseries.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskScheduler {

    private static final List<Task> tasks = new ArrayList<>();
    private static final List<Task> newTasks = new ArrayList<>();
    private static boolean clearTasks = false;

    public static void scheduleTask(int ticks, Runnable goal) {
        if (Main.modDisabled()) return;
        Task task = new Task(ticks, goal);
        newTasks.add(task);
    }

    public static void scheduleTask(Time time, Runnable goal) {
        scheduleTask(time.getTicks(), goal);
    }

    public static void schedulePriorityTask(int ticks, Runnable goal) {
        Task task = new Task(ticks, goal);
        task.priority = true;
        newTasks.add(task);
    }

    public static void schedulePriorityTask(Time time, Runnable goal) {
        schedulePriorityTask(time.getTicks(), goal);
    }

    public static void clearTasks() {
        clearTasks = true;
        newTasks.clear();
    }

    public static void registerTickHandler() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            try {

                if (clearTasks) {
                    clearTasks = false;
                    tasks.clear();
                    return;
                }

                Iterator<Task> iterator = tasks.iterator();

                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    task.tickCount--;

                    if (task.tickCount <= 0) {
                        try {
                            //Inner try-catch to prevent errors from preventing the task from being removed
                            if (!Main.modDisabled() || task.priority) {
                                task.goal.run();
                            }
                        }catch(Exception e) {
                            Main.LOGGER.error("Fatal error while running task " + task);
                            e.printStackTrace();
                        }
                        iterator.remove();
                    }
                }

                tasks.addAll(newTasks);
                newTasks.clear();
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public static class Task {
        private int tickCount;
        private final Runnable goal;
        public boolean priority = false;

        public Task(int tickCount, Runnable goal) {
            this.tickCount = tickCount;
            this.goal = goal;
        }
    }
}
