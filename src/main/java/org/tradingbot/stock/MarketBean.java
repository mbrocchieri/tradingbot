package org.tradingbot.stock;

import org.tradingbot.common.persistence.MarketEntity;

import java.time.LocalTime;

public class MarketBean {

    private final String name;
    private final String timezone;
    private final LocalTime open;
    private final LocalTime close;

    public MarketBean(MarketEntity marketEntity) {
        name = marketEntity.getName();
        timezone = marketEntity.getTimezone();
        open = marketEntity.getOpenHour();
        close = marketEntity.getCloseHour();
    }

    public String getName() {
        return name;
    }

    public String getTimezone() {
        return timezone;
    }

    public LocalTime getOpen() {
        return open;
    }

    public LocalTime getClose() {
        return close;
    }

    @Override
    public String toString() {
        return "MarketBean{" + "name='" + name + '\'' + ", timezone='" + timezone + '\'' + ", open=" + open +
                ", close=" + close + '}';
    }
}
