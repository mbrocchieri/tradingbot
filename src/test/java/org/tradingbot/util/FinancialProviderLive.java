package org.tradingbot.util;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Constants;
import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class FinancialProviderLive implements FinancialProvider {
    private final FinancialProvider provider;
    private final Duration diff;

    /**
     * @param provider provider
     * @param toNow    to date from provider that will be considered like now
     */
    public FinancialProviderLive(FinancialProvider provider, ZonedDateTime toNow) {
        this.provider = provider;
        diff = Duration.between(toNow, ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, Constants.UTC));
    }

    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) throws IOException {

        var oldCandles = provider.getCandles(symbol, from.minus(diff), to.minus(diff), interval);
        List<Candle> newCandles = new ArrayList<>();
        for (var candle : oldCandles) {
            Candle c = new Candle.Builder()
                    .adjClose(candle.getAdjClose())
                    .close(candle.getClose())
                    .open(candle.getOpen())
                    .low(candle.getLow())
                    .high(candle.getHigh())
                    .volume(candle.getVolume())
                    .date(candle.getDate().plus(diff))
                    .build();
            newCandles.add(c);
        }
        return newCandles;
    }

    @Override
    public StockData getStockData(String providerCode) throws IOException {
        return provider.getStockData(providerCode);
    }

    @Override
    public int getProviderId() {
        return provider.getProviderId();
    }
}
