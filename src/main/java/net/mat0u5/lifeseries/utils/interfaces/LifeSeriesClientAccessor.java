package net.mat0u5.lifeseries.utils.interfaces;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;

import java.util.List;

public interface LifeSeriesClientAccessor {
    boolean isReplay();
    boolean isDisabledServerSide();
    Seasons getCurrentSeason();
    List<Wildcards> getActiveWildcards();
}
