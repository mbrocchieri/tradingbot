package org.tradingbot.test.common;

import org.junit.jupiter.api.Test;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Constants;
import org.tradingbot.common.math.Decimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CandleTest {
    final ZonedDateTime date = ZonedDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.MIN, Constants.UTC);

    @Test
    public void testToString() {

        var candle = new Candle.Builder().volume(100L).high(BigDecimal.valueOf(10)).close(BigDecimal.valueOf(8))
                .adjClose(BigDecimal.valueOf(9)).open(BigDecimal.valueOf(7)).low(BigDecimal.valueOf(6))
                .date(ZonedDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.MIN, Constants.UTC)).build();
        assertEquals("Candle{date=2020-01-01T00:00Z[UTC], open=7, low=6, high=10, close=8, adjClose=9, volume=100}",
                candle.toString());

        assertEquals(100L, (long) candle.getVolume());
        assertEquals(Decimal.valueOf(10), candle.getHigh());
        assertEquals(Decimal.valueOf(8), candle.getClose());
        assertEquals(Decimal.valueOf(9), candle.getAdjClose());
        assertEquals(Decimal.valueOf(7), candle.getOpen());
        assertEquals(Decimal.valueOf(6), candle.getLow());

        assertEquals("2020-01-01T00:00Z[UTC]", candle.getDate().toString());
    }

    @Test
    public void testNegative() {


        try {
            new Candle.Builder().volume(100L).high(BigDecimal.valueOf(-10)).close(BigDecimal.valueOf(8))
                    .adjClose(BigDecimal.valueOf(9)).open(BigDecimal.valueOf(7)).low(BigDecimal.valueOf(6)).date(date)
                    .build();
            fail("Must throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Negative are not accepted : Candle{date=2020-01-01T00:00Z[UTC], open=7, low=6, high=-10, close=8, adjClose=9, volume=100}",
                    e.getMessage());
        }
    }

    @Test
    public void testHashCode() {
        var candle = new Candle.Builder().volume(100L).high(BigDecimal.valueOf(10)).close(BigDecimal.valueOf(8))
                .adjClose(BigDecimal.valueOf(9)).open(BigDecimal.valueOf(7)).low(BigDecimal.valueOf(6)).date(date)
                .build();
        assertEquals(-1940581711, candle.hashCode());
    }
}
