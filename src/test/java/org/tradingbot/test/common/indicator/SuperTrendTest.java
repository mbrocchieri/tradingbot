package org.tradingbot.test.common.indicator;

import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.indicator.SuperTrend;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.RestBarSeries;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.util.FinancialProviderFile;
import org.tradingbot.util.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.tradingbot.common.Interval.DAILY;

class SuperTrendTest extends TradingBotApplicationTests {

    @Test
    public void testNominal() throws IOException {
        int providerId = -2;
        ProvidersSingletonUtils.addProvider("providerTestStockCandles",
                new FinancialProviderFile(-2, Utils.getFile("CGG.csv"), "A"), providerRepository);

        final var from = ZonedDateTime.parse("2021-01-04T00:00:00+00:00");
        final var to = ZonedDateTime.parse("2021-01-31T00:00:00+00:00");

        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stock);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));

        RestBarSeries barSeries = new RestBarSeries(stockController, stockEntity.getId());
        SuperTrend superTrend = new SuperTrend(barSeries, 10, 1);

        assertEqualsBd("0.8007", ((BigDecimal) superTrend.getValue(0).getDelegate()));
        assertEqualsBd("0.80642", ((BigDecimal) superTrend.getValue(1).getDelegate()));
        assertEqualsBd("0.88736", ((BigDecimal) superTrend.getValue(2).getDelegate()));
        assertEqualsBd("0.9105800000", ((BigDecimal) superTrend.getValue(3).getDelegate()));
        assertEqualsBd("0.9256000000", ((BigDecimal) superTrend.getValue(4).getDelegate()));
        assertEqualsBd("0.9064400000", ((BigDecimal) superTrend.getValue(5).getDelegate()));
        assertEqualsBd("0.9050800000", ((BigDecimal) superTrend.getValue(6).getDelegate()));
        assertEqualsBd("0.9135400000", ((BigDecimal) superTrend.getValue(7).getDelegate()));
        assertEqualsBd("0.8889200000", ((BigDecimal) superTrend.getValue(8).getDelegate()));
        assertEqualsBd("0.8818200000", ((BigDecimal) superTrend.getValue(9).getDelegate()));
        assertEqualsBd("0.8818200000", ((BigDecimal) superTrend.getValue(10).getDelegate()));
        assertEqualsBd("0.8958400000", ((BigDecimal) superTrend.getValue(11).getDelegate()));
        assertEqualsBd("0.9022600000", ((BigDecimal) superTrend.getValue(12).getDelegate()));
        assertEqualsBd("0.9053400000", ((BigDecimal) superTrend.getValue(13).getDelegate()));
        assertEqualsBd("0.9327400000", ((BigDecimal) superTrend.getValue(14).getDelegate()));
        assertEqualsBd("0.8942200000", ((BigDecimal) superTrend.getValue(15).getDelegate()));
        assertEqualsBd("0.8942200000", ((BigDecimal) superTrend.getValue(16).getDelegate()));
        assertEqualsBd("0.8898400000", ((BigDecimal) superTrend.getValue(17).getDelegate()));
        assertEqualsBd("0.8898400000", ((BigDecimal) superTrend.getValue(18).getDelegate()));
        assertEqualsBd("0.8898400000", ((BigDecimal) superTrend.getValue(19).getDelegate()));
    }

}