package net.mat0u5.lifeseries.utils.interfaces;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.matlib.utils.enums.HandshakeStatus;

import java.util.List;


public interface LifeSeriesClientAccessor {
    boolean isReplay();
    HandshakeStatus serverHandshake();
    boolean isDisabledServerSide();
    Seasons getCurrentSeason();
    List<Wildcards> getActiveWildcards();
}
