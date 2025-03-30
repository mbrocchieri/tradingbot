package org.tradingbot.util;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiFinancialProvider implements FinancialProvider {
    private final Map<String, FinancialProvider> map = new HashMap<>();

    public void add(String name, FinancialProvider provider) {
        map.put(name, provider);
    }

    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval)
            throws IOException {
        return map.get(symbol).getCandles(symbol, from, to, interval);
    }

    @Override
    public StockData getStockData(String providerCode) throws IOException {
        return map.get(providerCode).getStockData(providerCode);
    }

    @Override
    public int getProviderId() {
        return -1;
    }
}
