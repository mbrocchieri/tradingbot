package org.tradingbot.util;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;

import java.time.ZonedDateTime;
import java.util.List;

public class PaperTradingFinancialProvider implements FinancialProvider {
    private List<Candle> candles;
    private final int providerId;

    public PaperTradingFinancialProvider(int providerId) {
        this.providerId = providerId;
    }

    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
        return candles;
    }

    @Override
    public StockData getStockData(String providerCode) {
        return new StockData("EUR", "MOCK", "MOCK", 1);
    }

    @Override
    public int getProviderId() {
        return providerId;
    }

    public void setCandles(List<Candle> candles) {
        this.candles = candles;
    }
}
