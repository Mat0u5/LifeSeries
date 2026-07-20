package net.mat0u5.lifeseries.utils.enums;

public enum SessionTimerStates {
    INFINITE(-4),
    ENDED(-3),
    PAUSED(-2),
    NOT_STARTED(-1),
    OFF(0);

    private int value;

    SessionTimerStates(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
