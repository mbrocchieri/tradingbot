package org.tradingbot.tradingconfig;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public class RecordDetailBean {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime from;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime to;

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }
}
