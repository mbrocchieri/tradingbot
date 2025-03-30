package org.tradingbot.test.controller;

import org.junit.jupiter.api.Test;
import org.ta4j.core.num.DecimalNum;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.stock.RestBarSeries;
import org.tradingbot.stock.RestTradingRecord;
import org.tradingbot.util.Utils;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tradingbot.common.Interval.DAILY;

public class StatisticsTest extends TradingBotApplicationTests {

    @Test
    public void testOneTrade() throws IOException {

        int providerId = createProvider(Utils.getFile("CGG.csv"), "GCC");
        var stockEntity = createStock(providerId, "GCC");
        var strategyEntity = createStrategy(Utils.getFile("strategies/doublebottom_stop.xml"));

        var configEntity =
                tradingConfigRepository.findByStockAndStrategyAndInterval(stockEntity, strategyEntity, DAILY);

        var barSeries = new RestBarSeries(stockController, stockEntity.getId());
        RestTradingRecord tradingRecords =
                RestTradingRecord.loadFromConfig(repositories, Collections.emptyList(), configEntity, barSeries);
        tradingRecords.enter(10, barSeries.getBar(10).getClosePrice(), DecimalNum.valueOf(10));
        tradingRecords.exit(20, barSeries.getBar(20).getClosePrice(), DecimalNum.valueOf(10));

        var configStats = tradingConfigController.getStats(configEntity.getId());
        assertEquals(1, configStats.getNbTrade());
        assertEqualsBd(100, configStats.getStartPrice());
        assertEqualsBd("93.1569742100", configStats.getEndPrice());
        assertEqualsBd("-6.8430257900", configStats.getPercentGlobalConfigPerf());

        var statStats = statisticsController.getStrategyStatistics(strategyEntity.getId());
        assertEquals(1, statStats.getNbTrades());
        assertEqualsBd(100, statStats.getAllBuys());
        assertEqualsBd("-11.79", statStats.getPerformance());
    }

}
