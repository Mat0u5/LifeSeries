package net.mat0u5.lifeseries.utils.other;

public class Time {
    public static long CONVERT_MILLIS = 1000L;
    public static long CONVERT_TICKS = 50_000L;
    public static long CONVERT_SECONDS = 1_000_000L;
    public static long CONVERT_MINUTES = 60_000_000L;
    public static long CONVERT_HOURS = 3_600_000_000L;
    private Long nanos;

    public Time(Long nanos) {
        this.nanos = nanos;
    }

    public Time(int nanos) {
        this.nanos = (long) nanos;
    }



    public long getNanos() {
        return nanos;
    }

    public long getMillis() {
        return nanos / CONVERT_MILLIS;
    }

    public int getTicks() {
        return (int) (nanos / CONVERT_TICKS);
    }

    public int getSeconds() {
        return (int) (nanos / CONVERT_SECONDS);
    }

    public int getMinutes() {
        return (int) (nanos / CONVERT_MINUTES);
    }

    public int getHours() {
        return (int) (nanos / CONVERT_HOURS);
    }

    public Time tick() {
        return this.add(CONVERT_TICKS);
    }

    public Time add(Time time) {
        if (nanos == null) nanos = 0L;
        nanos += time.getNanos();
        return this;
    }

    public Time add(long time) {
        if (nanos == null) nanos = 0L;
        nanos += time;
        return this;
    }

    public Time multiply(long scale) {
        if (nanos == null) nanos = 0L;
        nanos *= scale;
        return this;
    }

    public boolean isPresent() {
        return nanos != null;
    }

    public boolean isMultipleOf(Time interval) {
        return nanos % interval.getNanos() == 0;
    }

    public Time diff(Time time2) {
        return new Time(this.getNanos() - time2.getNanos());
    }

    public boolean isLarger(Time time2) {
        return this.getNanos() >= time2.getNanos();
    }

    public boolean isSmaller(Time time2) {
        return this.getNanos() <= time2.getNanos();
    }

    public Time copy() {
        return new Time(this.nanos);
    }

    public String formatReadable() {
        int seconds = this.getSeconds();
        boolean isNegative = seconds < 0;
        seconds = Math.abs(seconds);

        int hours = seconds / 3600;
        int remainingSeconds = seconds % 3600;
        int minutes = remainingSeconds / 60;
        int secs = remainingSeconds % 60;

        if (hours > 0 && minutes == 0 && secs == 0) {
            return (isNegative ? "-" : "+") + hours + (hours == 1 ? " hour" : " hours");
        } else if (hours == 0 && minutes > 0 && secs == 0) {
            return (isNegative ? "-" : "+") + minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (hours == 0 && minutes == 0 && secs > 0) {
            return (isNegative ? "-" : "+") + secs + (secs == 1 ? " second" : " seconds");
        } else {
            return String.format("%s%d:%02d:%02d", isNegative ? "-" : "+", hours, minutes, secs);
        }
    }

    public String formatLong() {
        long totalSeconds = (long) Math.ceil(nanos / 1_000_000.0);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = (totalSeconds % 60);

        return TextUtils.formatString("{}:{}:{}", hours, formatTimeNumber(minutes), formatTimeNumber(seconds));
    }

    public String format() {
        long totalSeconds = (long) Math.ceil(nanos / 1_000_000.0);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = (totalSeconds % 60);
        if (hours == 0) {
            return TextUtils.formatString("{}:{}", formatTimeNumber(minutes), formatTimeNumber(seconds));
        }

        return TextUtils.formatString("{}:{}:{}", hours, formatTimeNumber(minutes), formatTimeNumber(seconds));
    }

    private static String formatTimeNumber(long time) {
        String value = String.valueOf(time);
        while (value.length() < 2) value = "0" + value;
        return value;
    }


    public static Time hours(int hours) {
        return new Time(hours * CONVERT_HOURS);
    }

    public static Time minutes(int minutes) {
        return new Time(minutes * CONVERT_MINUTES);
    }

    public static Time minutes(double minutes) {
        return new Time((long)(minutes * CONVERT_MINUTES));
    }

    public static Time seconds(int seconds) {
        return new Time(seconds * CONVERT_SECONDS);
    }

    public static Time ticks(int ticks) {
        return new Time(ticks * CONVERT_TICKS);
    }

    public static Time millis(long millis) {
        return new Time(millis * CONVERT_MILLIS);
    }

    public static Time nanos(long nanos) {
        return new Time(nanos);
    }

    public static Time now() {
        return new Time(System.currentTimeMillis()* CONVERT_MILLIS);
    }

    public static Time nullTime() {
        return new Time(null);
    }

    public static Time zero() {
        return new Time(0L);
    }
}
