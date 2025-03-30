package org.tradingbot.tradingconfig;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TradingActionBean {
    private boolean buy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime candleStartTime;
    private BigDecimal price;

    public TradingActionBean() {
    }

    public TradingActionBean(boolean buy, ZonedDateTime candleStartTime, BigDecimal price) {
        this.buy = buy;
        this.candleStartTime = candleStartTime;
        this.price = price;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public ZonedDateTime getCandleStartTime() {
        return candleStartTime;
    }

    public void setCandleStartTime(ZonedDateTime candleStartTime) {
        this.candleStartTime = candleStartTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
