package org.tradingbot.util;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class FinancialProviderFile implements FinancialProvider {

    private final List<Candle> candles;
    private final String stockName;
    private final int providerId;

    public FinancialProviderFile(int providerId, File f, String stockName) throws IOException {
        this.providerId = providerId;
        candles = Utils.load(f);
        this.stockName = stockName;
    }

    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
        List<Candle> l = new ArrayList<>();
        for (var c : candles) {
            if ((c.getDate().isEqual(from) || c.getDate().isAfter(from)) &&
                    (c.getDate().isEqual(to) || c.getDate().isBefore(to))) {
                l.add(c);
            }
        }
        return l;
    }

    @Override
    public StockData getStockData(String providerCode) throws IOException {
        if (!stockName.equals(providerCode)) {
            throw new IOException(providerCode + " not found");
        }
        return new StockData("EUR", stockName, stockName, 1);
    }

    @Override
    public int getProviderId() {
        return providerId;
    }
}
