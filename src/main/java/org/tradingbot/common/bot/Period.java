package org.tradingbot.common.bot;

import org.tradingbot.common.Interval;

import java.io.Serializable;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public class Period implements Serializable {
    private final ZonedDateTime from;
    private final ZonedDateTime to;
    private final Interval interval;

    public Period(ZonedDateTime from, ZonedDateTime to, Interval interval) {
        this.from = requireNonNull(from);
        this.to = requireNonNull(to);
        this.interval = requireNonNull(interval);
    }

    public ZonedDateTime getFrom() {
        return from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public Interval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "Period{" + "from=" + from + ", to=" + to + ", interval=" + interval + '}';
    }
}
