package org.tradingbot.common;

import org.tradingbot.common.math.Decimal;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.stock.CandleBean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Candle implements Serializable {

    private final ZonedDateTime date;
    private final Decimal open;
    private final Decimal low;
    private final Decimal high;
    private final Decimal close;
    private final Decimal adjClose;
    private final Long volume;

    private Candle(Builder builder) {
        if (builder.volume < 0 ||
                builder.open.isLessThan(Decimal.ZERO) ||
                builder.low.isLessThan(Decimal.ZERO) ||
                builder.high.isLessThan(Decimal.ZERO) ||
                builder.close.isLessThan(Decimal.ZERO)/* ||
                builder.adjClose.isLessThan(Decimal.ZERO)*/) {
            throw new IllegalArgumentException("Negative are not accepted : " + "Candle{" +
                    "date=" + builder.date +
                    ", open=" + builder.open +
                    ", low=" + builder.low +
                    ", high=" + builder.high +
                    ", close=" + builder.close +
                    ", adjClose=" + builder.adjClose +
                    ", volume=" + builder.volume +
                    '}');
        }

        this.date = requireNonNull(builder.date);
        this.open = requireNonNull(builder.open);
        this.low = requireNonNull(builder.low);
        this.high = requireNonNull(builder.high);
        this.close = requireNonNull(builder.close);
        this.adjClose = requireNonNull(builder.adjClose);
        this.volume = requireNonNull(builder.volume);
    }

    public static Candle toCandle(CandleEntity c) {
        return new Builder()
                .high(c.getHighPrice())
                .low(c.getLowPrice())
                .open(c.getOpenPrice())
                .close(c.getClosePrice())
                .adjClose(c.getAdjClosePrice())
                .volume(c.getVolume())
                .date(c.getStartTime())
                .build();
    }

    public static Candle toCandle(CandleBean c) {
        return new Builder()
                .high(c.getHighPrice())
                .low(c.getLowPrice())
                .open(c.getOpenPrice())
                .close(c.getClosePrice())
                .adjClose(c.getAdjClosePrice())
                .volume(c.getVolume())
                .date(c.getStartTime())
                .build();
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Decimal getOpen() {
        return open;
    }

    public Decimal getLow() {
        return low;
    }

    public Decimal getHigh() {
        return high;
    }

    public Decimal getClose() {
        return close;
    }

    public Decimal getAdjClose() {
        return adjClose;
    }

    public Long getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Candle{" +
                "date=" + date +
                ", open=" + open +
                ", low=" + low +
                ", high=" + high +
                ", close=" + close +
                ", adjClose=" + adjClose +
                ", volume=" + volume +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var candle = (Candle) o;
        return date.toInstant().compareTo(candle.date.toInstant()) == 0 &&
                open.equals(candle.open) && low.equals(candle.low) && high.equals(candle.high) &&
                close.equals(candle.close) && /*adjClose.equals(candle.adjClose) && */volume.equals(candle.volume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, open, low, high, close, adjClose, volume);
    }

    public void updateCandleEntity(CandleEntity c) {
        Objects.requireNonNull(c);
        c.setAdjClosePrice(getAdjClose().getBigDecimal());
        c.setClosePrice(getClose().getBigDecimal());
        c.setHighPrice(getHigh().getBigDecimal());
        c.setLowPrice(getLow().getBigDecimal());
        c.setOpenPrice(getOpen().getBigDecimal());
        c.setVolume(getVolume());
        c.setUpdateDate(ZonedDateTime.now());
    }

    public static class Builder {
        private ZonedDateTime date;
        private Decimal open;
        private Decimal low;
        private Decimal high;
        private Decimal close;
        private Decimal adjClose;
        private Long volume;

        public Builder date(ZonedDateTime date) {
            this.date = date;
            return this;
        }

        public Builder open(BigDecimal open) {
            return open(new Decimal(open));
        }

        public Builder low(BigDecimal low) {
            return low(new Decimal(low));
        }

        public Builder high(BigDecimal high) {
            return high(new Decimal(high));
        }

        public Builder close(BigDecimal close) {
            return close(new Decimal(close));
        }

        public Builder adjClose(BigDecimal adjClose) {
            return adjClose(new Decimal(adjClose));
        }

        public Builder open(Decimal open) {
            this.open = open;
            return this;
        }

        public Builder low(Decimal low) {
            this.low = low;
            return this;
        }

        public Builder high(Decimal high) {
            this.high = high;
            return this;
        }

        public Builder close(Decimal close) {
            this.close = close;
            return this;
        }

        public Builder adjClose(Decimal adjClose) {
            this.adjClose = adjClose;
            return this;
        }


        public Builder volume(Long volume) {
            this.volume = volume;
            return this;
        }

        public Candle build() {
            return new Candle(this);
        }
    }
}
