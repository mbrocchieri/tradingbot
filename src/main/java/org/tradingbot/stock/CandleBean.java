package org.tradingbot.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.CandleEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CandleBean {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime startTime;
    private Interval interval;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal adjClosePrice;
    private long volume;

    public CandleBean() {
    }

    public CandleBean(CandleEntity candleEntity) {
        this.startTime = candleEntity.getStartTime();
        this.interval = candleEntity.getInterval();
        this.highPrice = candleEntity.getHighPrice();
        this.lowPrice = candleEntity.getLowPrice();
        this.openPrice = candleEntity.getOpenPrice();
        this.closePrice = candleEntity.getClosePrice();
        this.adjClosePrice = candleEntity.getAdjClosePrice();
        this.volume = candleEntity.getVolume();
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
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
}
