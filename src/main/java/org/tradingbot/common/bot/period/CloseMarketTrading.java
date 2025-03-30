package org.tradingbot.common.bot.period;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;
import org.tradingbot.stock.RestBarSeries;

/**
 * Trading are executed when market is closed.
 * Do not trade the current day
 */
public class CloseMarketTrading implements TradingPeriod {
    @Override
    public boolean sendAction(RestBarSeries barSeries, int index) {
        return barSeries.getEndIndex() - 1 >= index;
    }

    @Override
    public boolean shouldTrade(RestBarSeries barSeries, int index) {
        return index < barSeries.getEndIndex();
    }

    @Override
    public int getTradeIndex(int index) {
        return index + 1;
    }

    @Override
    public Num getTradePrice(BarSeries barSeries, int index) {
        return barSeries.getBar(index + 1).getOpenPrice();
    }
}
