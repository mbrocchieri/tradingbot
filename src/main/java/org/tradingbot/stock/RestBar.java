package org.tradingbot.stock;

import org.apache.commons.lang3.NotImplementedException;
import org.ta4j.core.Bar;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.tradingbot.common.persistence.CandleEntity;

import java.time.Duration;
import java.time.ZonedDateTime;

public class RestBar implements Bar {

    private final CandleBean candle;

    public RestBar(CandleEntity candle) {
        this.candle = new CandleBean(candle);
    }

    public RestBar(CandleBean candle) {
        this.candle = candle;
    }

    @Override
    public Num getOpenPrice() {
        return DecimalNum.valueOf(candle.getOpenPrice());
    }

    @Override
    public Num getLowPrice() {
        return DecimalNum.valueOf(candle.getLowPrice());
    }

    @Override
    public Num getHighPrice() {
        return DecimalNum.valueOf(candle.getHighPrice());
    }

    @Override
    public Num getClosePrice() {
        return DecimalNum.valueOf(candle.getClosePrice());
    }

    @Override
    public Num getVolume() {
        return DecimalNum.valueOf(candle.getVolume());
    }

    @Override
    public long getTrades() {
        throw new NotImplementedException();
    }

    @Override
    public Num getAmount() {
        throw new NotImplementedException();
    }

    @Override
    public Duration getTimePeriod() {
        throw new NotImplementedException();
    }

    @Override
    public ZonedDateTime getBeginTime() {
        return candle.getStartTime();
    }

    @Override
    public ZonedDateTime getEndTime() {
        return getBeginTime().plusSeconds(candle.getInterval().toSeconds());
    }

    @Override
    public void addTrade(Num tradeVolume, Num tradePrice) {
        throw new NotImplementedException();
    }

    @Override
    public void addPrice(Num price) {
        throw new NotImplementedException();
    }
}
