package org.tradingbot.common.bot.period;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;
import org.tradingbot.stock.RestBarSeries;

public interface TradingPeriod {

    boolean sendAction(RestBarSeries barSeries, int index);

    boolean shouldTrade(RestBarSeries barSeries, int index);

    int getTradeIndex(int index);

    Num getTradePrice(BarSeries barSeries, int index);
}
