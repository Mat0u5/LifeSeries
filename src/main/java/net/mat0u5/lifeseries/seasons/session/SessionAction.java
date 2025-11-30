package net.mat0u5.lifeseries.seasons.session;

import net.mat0u5.lifeseries.utils.other.Time;
import static net.mat0u5.lifeseries.Main.currentSession;

public abstract class SessionAction {
    public boolean hasTriggered = false;
    private Time triggerTime;
    public String sessionMessage;
    public boolean showTime = false;

    public SessionAction(Time triggerTime) {
        this.triggerTime = triggerTime;
    }

    public SessionAction(Time triggerTime, String message) {
        this.triggerTime = triggerTime;
        this.sessionMessage = message;
        this.showTime = true;
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
            // Trigger before end
            int remainingTime = currentSession.getRemainingTime().getTicks();
            return remainingTime <= Math.abs(triggerAtTicks);
        }
    }

    public Time getTriggerTime() {
        return triggerTime;
    }

    public abstract void trigger();
}
