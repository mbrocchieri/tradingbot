package org.tradingbot.stock;

import java.time.LocalTime;
import java.time.ZoneId;

public class MarketHours {
    private final LocalTime open;
    private final LocalTime close;
    private final ZoneId zoneId;

    public MarketHours(LocalTime open, LocalTime close, String timezone) {
        this.open = open;
        this.close = close;
        this.zoneId = ZoneId.of(timezone);
    }

    public LocalTime getOpen() {
        return open;
    }

    public LocalTime getClose() {
        return close;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public String toString() {
        return "MarketHours{" + "open=" + open + ", close=" + close + ", zoneId='" + zoneId + '\'' + '}';
    }
}
