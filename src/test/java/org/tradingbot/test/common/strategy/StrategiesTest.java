package org.tradingbot.test.common.strategy;

import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.bot.LiveTradingBot;
import org.tradingbot.common.bot.StockReload;
import org.tradingbot.common.persistence.TradingRecordEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.util.FinancialProviderFile;
import org.tradingbot.util.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StrategiesTest extends TradingBotApplicationTests {


    private List<TradingRecordEntity> testStrategy(String stockFileName, String strategyXml,
                                                   Map<String, BigDecimal> parameters) throws IOException {
        FinancialProviderFile financialProviderFile = new FinancialProviderFile(-2, Utils.getFile(stockFileName), "SW");
        ProvidersSingletonUtils.addProvider("test", financialProviderFile, providerRepository);
        var config =
                Utils.createConfig(tradingConfigRepository, stockRepository, stockController, strategyController, tradingConfigController, financialProviderFile,
                        strategyXml);

        var bot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            public void runAllConfig(StockReload stockReload) {
                super.runAllConfig(stockReload);
                stop();
            }

            @Override
            protected FinancialProvider getProvider() {
                return financialProviderFile;
            }
        };
        bot.run();
        var configEntity = tradingConfigRepository.findById(config.getId()).orElseThrow();
        return tradingRecordRepository.findAllByConfig(configEntity);
    }

    @Test
    public void testStrategy1() throws IOException {
        var result = testStrategy("CGG.csv", "strategies/doublebottom_stop.xml", Collections.emptyMap());

        assertEquals(17, result.size());

        assertEquals("2021-05-18T00:00Z[UTC]", result.get(0).getCandleStartTime().toString());
        assertTrue(result.get(0).isBuy());
        assertEquals("2021-05-26T00:00Z[UTC]", result.get(1).getCandleStartTime().toString());
        assertFalse(result.get(1).isBuy());
        assertEquals("2021-07-26T00:00Z[UTC]", result.get(2).getCandleStartTime().toString());
        assertTrue(result.get(2).isBuy());
        assertEquals("2021-07-30T00:00Z[UTC]", result.get(3).getCandleStartTime().toString());
        assertFalse(result.get(3).isBuy());
        assertEquals("2021-08-06T00:00Z[UTC]", result.get(4).getCandleStartTime().toString());
        assertTrue(result.get(4).isBuy());
        assertEquals("2021-08-16T00:00Z[UTC]", result.get(5).getCandleStartTime().toString());
        assertFalse(result.get(5).isBuy());
        assertEquals("2021-08-26T00:00Z[UTC]", result.get(6).getCandleStartTime().toString());
        assertTrue(result.get(6).isBuy());
        assertEquals("2021-09-20T00:00Z[UTC]", result.get(7).getCandleStartTime().toString());
        assertFalse(result.get(7).isBuy());
    }

    @Test
    public void testSimpleDivergence() throws IOException {
        var result = testStrategy("CGG_2022.csv", "strategies/simple_divergence.xml", Collections.emptyMap());

        assertEquals(2, result.size());
        assertEquals(ZonedDateTime.parse("2021-06-01T00:00Z[UTC]"), result.get(0).getCandleStartTime());
        assertEquals(ZonedDateTime.parse("2021-06-18T00:00Z[UTC]"), result.get(1).getCandleStartTime());
    }
}
