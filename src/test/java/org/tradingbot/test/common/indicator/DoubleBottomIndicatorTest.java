package org.tradingbot.test.common.indicator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.Constants;
import org.tradingbot.common.Interval;
import org.tradingbot.common.indicator.DoubleBottomIndicator;
import org.tradingbot.common.provider.YahooProvider;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.RestBarSeries;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockCreateBean;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoubleBottomIndicatorTest extends TradingBotApplicationTests {

    @Disabled("YahooProvider deprecated")
    @Test
    public void testWGoogle() throws IOException {

        var stockCreateBean = new StockCreateBean();
        stockCreateBean.setCode("XRP-EUR");
        int providerId = YahooProvider.PROVIDER_ID;
        stockCreateBean.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stockCreateBean);

        ZonedDateTime from = ZonedDateTime.of(LocalDate.of(2021, Month.FEBRUARY, 1), LocalTime.MIN, Constants.UTC);
        ZonedDateTime to = ZonedDateTime.of(LocalDate.of(2021, Month.MARCH, 19), LocalTime.MIN, Constants.UTC);

        stockController.refreshCandles(stockEntity.getId(), Interval.DAILY, providerId, new RefreshBean(from, to));

        var series = new RestBarSeries(stockController, stockEntity.getId());
        var doubleBottomIndicator = new DoubleBottomIndicator(series);
        for (var i = 0; i < 14; i++) {
            if (i == 12) {
                assertTrue(doubleBottomIndicator.getValue(i), "for i = " + i);
            } else {
                assertFalse(doubleBottomIndicator.getValue(i), "for i = " + i);
            }
        }
    }
}
