package org.tradingbot.common.persistence;

import org.tradingbot.common.Interval;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class CandleId implements Serializable {

    private int stockId;

    private ZonedDateTime startTime;

    private Interval interval;

    // For Hibernate
    public CandleId() {
    }

    public CandleId(int stockId, ZonedDateTime startTime, Interval interval) {
        this.stockId = stockId;
        this.startTime = startTime;
        this.interval = interval;
    }

    public int getStockId() {
        return stockId;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public Interval getInterval() {
        return interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CandleId candleId = (CandleId) o;
        return stockId == candleId.stockId && startTime.equals(candleId.startTime) && interval == candleId.interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, startTime, interval);
    }
}
