package org.tradingbot.common.persistence;

import org.tradingbot.common.Interval;

import java.io.Serializable;
import java.util.Objects;

public class StockMetadataId implements Serializable {

    private int stockId;
    private Interval interval;

    // For Hibernate
    public StockMetadataId() {

    }

    public StockMetadataId(int stockId, Interval interval) {
        this.stockId = stockId;
        this.interval = interval;
    }

    public int getStockId() {
        return stockId;
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
        StockMetadataId that = (StockMetadataId) o;
        return stockId == that.stockId && interval == that.interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, interval);
    }
}
