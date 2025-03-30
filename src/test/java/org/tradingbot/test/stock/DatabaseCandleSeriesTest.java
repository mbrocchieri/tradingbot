package org.tradingbot.test.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tradingbot.common.Interval.DAILY;
import static org.tradingbot.util.Utils.getFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.RestBarSeries;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.util.FinancialProviderFile;

public class DatabaseCandleSeriesTest  extends TradingBotApplicationTests {

    @Test
    public void testNominal() throws IOException {
        final var financialProvider = new FinancialProviderFile(-2, getFile("CGG.csv"), "GCC");
        final var provId = -2;
        ProvidersSingletonUtils.addProvider("test", financialProvider, providerRepository);;
        final var from = ZonedDateTime.parse("2021-01-04T00:00:00Z");
        final var to = ZonedDateTime.parse("2021-01-31T00:00:00Z");

        var stockEntity = (StockBean) stockController.createStock(new StockCreateBean(provId, "GCC"));
        assertNotNull(stockEntity);
        stockController.refreshCandles(stockEntity.getId(), DAILY, provId, new RefreshBean(from, to));

        List<CandleEntity> candles = candleRepository.findAllByStockIdAndIntervalOrderByStartTime(stockEntity.getId(), DAILY);
        assertEquals(20, candles.size());
        assertEquals("2021-01-04T00:00Z[UTC]", candles.get(0).getStartTime().toString(), candles.toString());

        var barSeries = new RestBarSeries(stockController, stockEntity.getId());
        var firstBar = barSeries.getFirstBar();
        assertEquals(ZonedDateTime.parse("2021-01-04T00:00:00Z").toInstant(), from.toInstant());
        assertEquals(ZonedDateTime.parse("2021-01-04T00:00:00Z").toInstant(), firstBar.getBeginTime().toInstant());
    }
}
