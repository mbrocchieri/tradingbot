package org.tradingbot.util;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Constants;
import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FinancialProviderMock implements FinancialProvider {
    private final int providerId;

    public FinancialProviderMock(int providerId) {
        this.providerId = providerId;
    }
    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
        List<Candle> candles = new ArrayList<>();
        LocalDate startDate = LocalDate.ofYearDay(2020, 1);
        for (var i = 1; i < 600; i++) {
            ZonedDateTime date = ZonedDateTime.of(startDate.plus(i - 1, ChronoUnit.DAYS), LocalTime.MIN, Constants.UTC);
            if (date.equals(from) || date.equals(to) || date.isAfter(from) && date.isBefore(to)) {
                var dayOfWeek = date.getDayOfWeek();
                if (!dayOfWeek.equals(DayOfWeek.SUNDAY) && !dayOfWeek.equals(DayOfWeek.SATURDAY)) {
                    var candle = new Candle.Builder()
                            .date(date)
                            .low(BigDecimal.valueOf(1 + i))
                            .open(BigDecimal.valueOf(2 + i))
                            .close(BigDecimal.valueOf(3 + i))
                            .adjClose(BigDecimal.valueOf(3 + i))
                            .high(BigDecimal.valueOf(4 + i))
                            .volume(100L)
                            .build();
                    candles.add(candle);
                }
            }
        }
        return candles;
    }

    @Override
    public StockData getStockData(String providerCode) {
        return new StockData("EUR", providerCode + "_ID", providerCode, 1);
    }

    @Override
    public int getProviderId() {
        return providerId;
    }
}
