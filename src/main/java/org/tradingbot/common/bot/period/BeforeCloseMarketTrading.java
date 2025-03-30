package org.tradingbot.common.bot.period;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;
import org.tradingbot.stock.RestBarSeries;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class BeforeCloseMarketTrading implements TradingPeriod {

    private final long secondBeforeClose;

    /**
     * @param secondBeforeClose number of seconds before close market
     */
    public BeforeCloseMarketTrading(long secondBeforeClose) {
        this.secondBeforeClose = secondBeforeClose;
    }

    @Override
    public boolean sendAction(RestBarSeries barSeries, int index) {
        return barSeries.getEndIndex() == index;
    }

    @Override
    public boolean shouldTrade(RestBarSeries barSeries, int index) {
        if (index < barSeries.getEndIndex()) {
            return true;
        }
        final var marketHours = barSeries.getMarketHours();
        ZonedDateTime closeTimeForToday =
                ZonedDateTime.of(LocalDate.now(), marketHours.getClose(), marketHours.getZoneId());
        ZonedDateTime firstLimit = closeTimeForToday.minusSeconds(secondBeforeClose);
        ZonedDateTime now = ZonedDateTime.now();
        return now.isAfter(firstLimit) && now.isBefore(closeTimeForToday);
    }

    @Override
    public int getTradeIndex(int index) {
        return index;
    }

    @Override
    public Num getTradePrice(BarSeries barSeries, int index) {
        return barSeries.getBar(index).getClosePrice();
    }
}
