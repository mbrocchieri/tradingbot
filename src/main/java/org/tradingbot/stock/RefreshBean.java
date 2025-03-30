package org.tradingbot.stock;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public class RefreshBean {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final ZonedDateTime from;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final ZonedDateTime to;

    public RefreshBean(ZonedDateTime from, ZonedDateTime to) {
        this.from = from;
        this.to = to;
    }

    public ZonedDateTime getFrom() {
        return from;
    }

    public ZonedDateTime getTo() {
        return to;
    }
}
