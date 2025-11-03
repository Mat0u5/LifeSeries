package net.mat0u5.lifeseries.seasons.season;

import net.mat0u5.lifeseries.seasons.season.aprilfools.reallife.RealLife;
import net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife.SimpleLife;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.lastlife.LastLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.season.unassigned.UnassignedSeason;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public enum Seasons {
    UNASSIGNED("Unassigned", "unassigned"),

    THIRD_LIFE("Third Life", "thirdlife"),
    LAST_LIFE("Last Life", "lastlife"),
    DOUBLE_LIFE("Double Life", "doublelife"),
    LIMITED_LIFE("Limited Life", "limitedlife"),
    SECRET_LIFE("Secret Life", "secretlife"),
    WILD_LIFE("Wild Life", "wildlife"),
    PAST_LIFE("Past Life", "pastlife"),

    REAL_LIFE("Real Life", "reallife"),
    SIMPLE_LIFE("Simple Life", "simplelife");

    private String name;
    private String id;

    Seasons(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Season getSeasonInstance() {
        if (this == THIRD_LIFE) return new ThirdLife();
        if (this == LAST_LIFE) return new LastLife();
        if (this == DOUBLE_LIFE) return new DoubleLife();
        if (this == LIMITED_LIFE) return new LimitedLife();
        if (this == SECRET_LIFE) return new SecretLife();
        if (this == WILD_LIFE) return new WildLife();
        if (this == PAST_LIFE) return new PastLife();

        if (this == REAL_LIFE) return new RealLife();
        if (this == SIMPLE_LIFE) return new SimpleLife();
        return new UnassignedSeason();
    }

    public ResourceLocation getLogo() {
        return ResourceLocation.fromNamespaceAndPath("lifeseries","textures/gui/"+this.getId()+".png");
    }

    public static Seasons getSeasonFromStringName(String name) {
        for (Seasons season : Seasons.values()) {
            if (season.getName().equalsIgnoreCase(name) || season.getId().equalsIgnoreCase(name)) {
                return season;
            }
        }
        return UNASSIGNED;
    }

    public static List<Seasons> getSeasons() {
        List<Seasons> allSeasons = new ArrayList<>(List.of(Seasons.values()));
        allSeasons.remove(UNASSIGNED);
        return allSeasons;
    }

    public static List<Seasons> getAprilFoolsSeasons() {
        return new ArrayList<>(List.of(REAL_LIFE, SIMPLE_LIFE));
    }

    public static List<String> getSeasonIds() {
        List<String> seasonNames = new ArrayList<>();
        for (Seasons season : getSeasons()) {
            seasonNames.add(season.getId());
        }
        return seasonNames;
    }
}
