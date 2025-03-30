package org.tradingbot.common.persistence;

import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.converter.ZonedDateTimeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "STOCKS_CANDLES")
@IdClass(StockMetadataId.class)
public class StockMetadataEntity implements Serializable {
    @Id
    @Column(name = "STOCK_ID")
    private int stockId;
    @Id
    @Column(name = "INTERVAL")
    @Enumerated(EnumType.STRING)
    private Interval interval;
    @Convert(converter = ZonedDateTimeConverter.class)
    @Column(name = "START_TIME")
    private ZonedDateTime startTime;
    @Convert(converter = ZonedDateTimeConverter.class)
    @Column(name = "END_TIME")
    private ZonedDateTime endTime;

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMetadataEntity that = (StockMetadataEntity) o;
        return stockId == that.stockId && interval == that.interval && startTime.equals(that.startTime) && endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, interval, startTime, endTime);
    }
}
