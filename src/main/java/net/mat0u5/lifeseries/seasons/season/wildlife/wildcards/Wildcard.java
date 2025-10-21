package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;

import static net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager.getSeason;

public abstract class Wildcard {

    public boolean active = false;

    public abstract Wildcards getType();

    public void activate() {
        WildLife season = getSeason();
        if (season == null) return;
        active = true;
        Main.LOGGER.info("[WildLife] Activated Wildcard: {}", getType());
        SessionTranscript.activateWildcard(getType());
    }

    public void deactivate() {
        WildLife season = getSeason();
        if (season == null) return;
        active = false;
        Main.LOGGER.info("[WildLife] Dectivated Wildcard: {}", getType());
        SessionTranscript.deactivateWildcard(getType());
    }

    public void tickSessionOn() {}
    public void tick() {}
    public void softTick() {}


    public static boolean isFinale() {
        return WildcardManager.FINALE || WildcardManager.isActiveWildcard(Wildcards.CALLBACK);
    }
}
