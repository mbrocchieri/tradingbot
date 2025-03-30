package org.tradingbot.common.persistence;

import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.converter.ZonedDateTimeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "CANDLES")
@IdClass(CandleId.class)
public class CandleEntity implements Serializable {

    @Id
    @Column(name = "STOCK_ID")
    private int stockId;

    @Convert(converter = ZonedDateTimeConverter.class)
    @Id
    @Column(name = "START_TIME", nullable = false)
    private ZonedDateTime startTime;

    @Id
    @Column(name = "INTERVAL")
    @Enumerated(EnumType.STRING)
    private Interval interval;

    @Column(name = "HIGH_PRICE", precision = 20, scale = 10)
    private BigDecimal highPrice;

    @Column(name = "LOW_PRICE", precision = 20, scale = 10)
    private BigDecimal lowPrice;

    @Column(name = "OPEN_PRICE", precision = 20, scale = 10)
    private BigDecimal openPrice;

    @Column(name = "CLOSE_PRICE", precision = 20, scale = 10)
    private BigDecimal closePrice;

    @Column(name = "ADJ_CLOSE_PRICE", precision = 20, scale = 10)
    private BigDecimal adjClosePrice;

    @Column(name = "VOLUME")
    private long volume;

    @Column(name = "UPDATE_DATE")
    private ZonedDateTime updateDate;

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getAdjClosePrice() {
        return adjClosePrice;
    }

    public void setAdjClosePrice(BigDecimal adjClosePrice) {
        this.adjClosePrice = adjClosePrice;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CandleEntity that = (CandleEntity) o;
        return stockId == that.stockId && interval == that.interval && volume == that.volume &&
                startTime.compareTo(that.startTime) == 0 && highPrice.compareTo(that.highPrice) == 0 &&
                lowPrice.compareTo(that.lowPrice) == 0 && openPrice.compareTo(that.openPrice) == 0 &&
                closePrice.compareTo(that.closePrice) == 0 && adjClosePrice.compareTo(that.adjClosePrice) == 0 &&
                updateDate.compareTo(that.updateDate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, startTime, interval, highPrice, lowPrice, openPrice, closePrice, adjClosePrice,
                volume, updateDate);
    }

    @Override
    public String toString() {
        return "CandleEntity{" + "stockId=" + stockId + ", startTime=" + startTime + ", interval=" + interval +
                ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", openPrice=" + openPrice + ", closePrice=" +
                closePrice + ", adjClosePrice=" + adjClosePrice + ", volume=" + volume + ", updateDate=" + updateDate +
                '}';
    }
}
