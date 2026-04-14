package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.LifeSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientTaskScheduler {

    private static final List<Task> tasks = new ArrayList<>();
    private static final List<Task> newTasks = new ArrayList<>();

    public static void scheduleTask(int tickNumber, Runnable goal) {
        if (LifeSeries.modDisabled()) return;
        Task task = new Task(tickNumber, goal);
        newTasks.add(task);
    }

    public static void schedulePriorityTask(int tickNumber, Runnable goal) {
        Task task = new Task(tickNumber, goal);
        task.priority = true;
        newTasks.add(task);
    }

    public static void onClientTick() {
        try {
            Iterator<Task> iterator = tasks.iterator();

            while (iterator.hasNext()) {
                Task task = iterator.next();
                task.tickCount--;

                if (task.tickCount <= 0) {
                    try {
                        //Inner try-catch to prevent errors from preventing the task from being removed
                        if (!LifeSeries.modDisabled() || task.priority) {
                            task.goal.run();
                        }
                    }catch (Exception e) {
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
