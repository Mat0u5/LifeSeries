package net.mat0u5.lifeseries.seasons.session;

import net.mat0u5.lifeseries.utils.other.Time;
import static net.mat0u5.lifeseries.LifeSeries.currentSession;

public abstract class SessionAction {
    private static int totalId = 1;
    public boolean hasTriggered = false;
    private final Time triggerTime;
    public String sessionMessage;
    public final boolean visible;
    public final int id;

    public abstract static class Invisible extends SessionAction {
        protected Invisible(Time triggerTime, String message) {
            super(triggerTime, message, false);
        }
    }

    protected SessionAction(Time triggerTime, String message) {
        this(triggerTime, message, true);
    }

    private SessionAction(Time triggerTime, String message, boolean visible) {
        this.triggerTime = triggerTime;
        this.sessionMessage = message;
        this.visible = visible;
        this.id = totalId++;
    }

    public boolean tick() {
        boolean shouldTrigger = shouldTrigger();
        if (hasTriggered && !shouldTrigger) hasTriggered = false;
        if (hasTriggered) return true;
        if (shouldTrigger) {
            hasTriggered = true;
            SessionTranscript.triggerSessionAction(sessionMessage);
            trigger();
            return true;
        }
        return false;
    }

    public boolean shouldTrigger() {
        int triggerAtTicks = triggerTime.getTicks();
        if (triggerAtTicks >= 0) {
            // Trigger after start
            int passedTime = currentSession.getPassedTime().getTicks();
            return passedTime >= triggerAtTicks;
        }
        else {
            if (currentSession().isInfiniteSession()) return false;
            // Trigger before end
            int remainingTime = currentSession.getRemainingTime().getTicks();
            return remainingTime <= Math.abs(triggerAtTicks);
        }
    }

    public Time getTriggerTime() {
        int triggerAtTicks = triggerTime.getTicks();
        if (triggerAtTicks >= 0) {
            return triggerTime;
        }
        else {
            if (currentSession().isInfiniteSession()) return Time.infinite();
            return currentSession.getSessionLength().add(triggerTime);
        }
    }

    public abstract void trigger();
}
