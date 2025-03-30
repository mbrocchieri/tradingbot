package org.tradingbot.common.provider;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public interface FinancialProvider extends Serializable {
    /**
     * @param symbol   stock identifier for provider
     * @param from     date of the first candle
     * @param to       date of the last candle
     * @param interval to have a candle a day, a week, a month...
     * @return List of candles containing the period for the interval. An empty list if not found.
     * @throws IOException if error reading data from provider
     */
    List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval)
            throws IOException;

    StockData getStockData(String providerCode) throws IOException;

    int getProviderId();

}
