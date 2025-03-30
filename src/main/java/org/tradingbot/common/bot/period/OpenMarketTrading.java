package org.tradingbot.common.bot.period;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;
import org.tradingbot.stock.RestBarSeries;

/**
 * Always trade
 */
public class OpenMarketTrading implements TradingPeriod {
    @Override
    public boolean sendAction(RestBarSeries barSeries, int index) {
        return barSeries.getEndIndex() == index;
    }

    @Override
    public boolean shouldTrade(RestBarSeries barSeries, int index) {
        return true;
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
