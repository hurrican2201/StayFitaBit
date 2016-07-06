package com.lidago.stayfitabit;

import java.util.concurrent.TimeUnit;

/**
 * Created on 23.06.2016.
 */
public class Time {
    private long hours;
    private long minutes;
    private long seconds;

    public Time(long hours, long minutes, long seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public static class UnitConverter {
        public static Time ConvertMillisToTime(long milliseconds) {
            long time = milliseconds;
            long hours = TimeUnit.MILLISECONDS.toHours(time);
            time -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
            time -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
            return new Time(hours, minutes, seconds);
        }

        public static Time ConvertMillisToPace(long milliseconds) {
            long time = milliseconds;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
            time -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
            return new Time(0, minutes, seconds);
        }
    }
}
