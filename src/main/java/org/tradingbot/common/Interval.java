package org.tradingbot.common;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public enum Interval {
    MINUTELY(Duration.ofMinutes(1)),
    HOURLY(Duration.ofHours(1)),
    DAILY(Duration.ofDays(1)),
    WEEKLY(Duration.ofDays(7)),
    MONTHLY(Duration.ofDays(30));

    static {
        MINUTELY.upper = List.of(HOURLY);
        HOURLY.upper = List.of(DAILY);
        DAILY.upper = List.of(WEEKLY, MONTHLY);
        WEEKLY.upper = Collections.emptyList();
        MONTHLY.upper = Collections.emptyList();
    }

    private final Duration duration;
    private List<Interval> upper;

    Interval(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public long toSeconds() {
        return duration.toSeconds();
    }

    public List<Interval> getUpperInterval() {
        return upper;
    }
}
