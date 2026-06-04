package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.config.StringListConfig;

import java.util.ArrayList;
import java.util.List;

public class SecretLifeUsedTasks {

    public static void deleteAllTasks(StringListConfig config, List<String> tasks, double keepPercentage) {
        if (config == null) return;

        int keepLast = (int) Math.floor(tasks.size() * keepPercentage);

        List<String> usedTasks = getUsedTasks(config);

        List<String> usedFromPool = new ArrayList<>();
        for (String task : usedTasks) {
            if (tasks.contains(task)) {
                usedFromPool.add(task);
            }
        }

        int keepFrom = Math.max(0, usedFromPool.size() - keepLast);
        List<String> toKeep = usedFromPool.subList(keepFrom, usedFromPool.size());

        for (String task : tasks) {
            usedTasks.remove(task);
        }
        for (String task : toKeep) {
            if (!usedTasks.contains(task)) {
                usedTasks.add(task);
            }
        }

        config.save(usedTasks);
    }

    public static void deleteAllTasks(StringListConfig config, List<String> tasks) {
        deleteAllTasks(config, tasks, 0.0);
    }

    public static void deleteAllTasks(StringListConfig config) {
        config.save(List.of());
    }

    public static void addUsedTask(StringListConfig config, String task) {
        if (config == null) return;

        List<String> allTasks = getUsedTasks(config);
        if (!allTasks.contains(task)) allTasks.add(task);

        config.save(allTasks);
    }

    public static List<String> getUsedTasks(StringListConfig config) {
        if (config == null) return new ArrayList<>();

        return config.load();
    }
}
