package net.mat0u5.lifeseries.series;

import java.util.List;

public enum SeriesList {
    UNASSIGNED,

    THIRD_LIFE,
    LAST_LIFE,
    DOUBLE_LIFE,
    LIMITED_LIFE,
    SECRET_LIFE,
    WILD_LIFE;

    public static String getFormattedStringNameFromSeries(SeriesList series) {
        if (series == THIRD_LIFE) return "Third Life";
        if (series == LAST_LIFE) return "Last Life";
        if (series == DOUBLE_LIFE) return "Double Life";
        if (series == LIMITED_LIFE) return "Limited Life";
        if (series == SECRET_LIFE) return "Secret Life";
        if (series == WILD_LIFE) return "Wild Life";
        return "unassigned";
    }

    public static String getStringNameFromSeries(SeriesList series) {
        if (series == THIRD_LIFE) return "thirdlife";
        if (series == LAST_LIFE) return "lastlife";
        if (series == DOUBLE_LIFE) return "doublelife";
        if (series == LIMITED_LIFE) return "limitedlife";
        if (series == SECRET_LIFE) return "secretlife";
        if (series == WILD_LIFE) return "wildlife";
        return "unassigned";
    }

    public static SeriesList getSeriesFromStringName(String name) {
        if (name.equalsIgnoreCase("thirdlife")) return THIRD_LIFE;
        if (name.equalsIgnoreCase("lastlife")) return LAST_LIFE;
        if (name.equalsIgnoreCase("doublelife")) return DOUBLE_LIFE;
        if (name.equalsIgnoreCase("limitedlife")) return LIMITED_LIFE;
        if (name.equalsIgnoreCase("secretlife")) return SECRET_LIFE;
        if (name.equalsIgnoreCase("wildlife")) return WILD_LIFE;
        return UNASSIGNED;
    }

    public static List<SeriesList> getAllImplemented() {
        return List.of(THIRD_LIFE,LAST_LIFE,DOUBLE_LIFE,LIMITED_LIFE,SECRET_LIFE,WILD_LIFE);
    }

    public static List<String> getImplementedSeriesNames() {
        return List.of("thirdlife", "lastlife", "doublelife", "limitedlife", "secretlife", "wildlife");
    }

    public static String getDatapackName(SeriesList series) {
        if (series == THIRD_LIFE) return "Third Life Recipe Datapack.zip";
        if (series == LAST_LIFE) return "Last Life Recipe Datapack.zip";
        if (series == DOUBLE_LIFE) return "Double Life Recipe Datapack.zip";
        if (series == LIMITED_LIFE) return "Limited Life Recipe Datapack.zip";
        if (series == SECRET_LIFE) return "Secret Life Recipe Datapack.zip";
        if (series == WILD_LIFE) return "Wild Life Recipe Datapack.zip";
        return "";
    }
}
