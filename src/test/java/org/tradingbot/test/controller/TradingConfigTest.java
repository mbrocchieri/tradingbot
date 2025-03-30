package org.tradingbot.test.controller;

import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.Constants;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.*;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.strategy.StrategyCreationBean;
import org.tradingbot.strategy.StrategyParameterCreationBean;
import org.tradingbot.util.Strategies;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.CLOSE_MARKET;

public class TradingConfigTest extends TradingBotApplicationTests {

    @Test
    void testConfigAdd() throws IOException {

        var list = tradingConfigController.listTradingConfigs();
        assertEquals(0, list.size());

        var stock = createStock();
        var strategy = createStrategy();
        tradingConfigRepository.deleteAll();

        var tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.parse("2022-01-01T00:00:00+00:00"));
        tradingConfig.getParameterEntities()
                .add(new ConfigParameterEntity(tradingConfig, strategy.getDefaultParameters().iterator().next(),
                        BigDecimal.ONE));

        tradingConfigController.createTradingConfig(tradingConfig);

        list = tradingConfigController.listTradingConfigs();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getParameters().size());
    }

    private StockEntity createStock() {
        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_TEST);
        var stockBean = stockController.createStock(stock);
        return stockRepository.findById(stockBean.getId()).orElseThrow();
    }

    private StrategyEntity createStrategy() {
        var strategy = new StrategyCreationBean();
        strategy.setName("A");
        strategy.setXml(Strategies.RSI5050);
        strategy.setTradingPeriod(CLOSE_MARKET);
        for (var entry : Strategies.RSI5050_PARAMETERS.entrySet()) {
            var strategyParameter = new StrategyParameterCreationBean(entry.getKey(), entry.getValue());
            strategy.getDefaultParameters().add(strategyParameter);
        }
        return strategyController.createStrategy(strategy);
    }

    @Test
    void test2SameConfigWithoutParameters() throws IOException {

        var stock = createStock();
        var strategy = createStrategy();
        tradingConfigRepository.deleteAll();

        var tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, Constants.UTC));

        tradingConfigController.createTradingConfig(tradingConfig);

        tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, Constants.UTC));

        try {
            tradingConfigController.createTradingConfig(tradingConfig);
            fail("must throw exception");
        } catch (IOException e) {
            // OK
        }
    }

    @Test
    void test2SameConfigWithParameters() throws IOException {

        var stock = createStock();
        var strategy = createStrategy();
        List<StrategyParameterEntity> list = new ArrayList<>(strategy.getDefaultParameters());

        var tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, Constants.UTC));
        tradingConfig.getParameterEntities().add(new ConfigParameterEntity(tradingConfig, list.get(0), BigDecimal.ONE));

        tradingConfigController.createTradingConfig(tradingConfig);

        tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, Constants.UTC));
        tradingConfig.getParameterEntities().add(new ConfigParameterEntity(tradingConfig, list.get(0), BigDecimal.ONE));

        try {
            tradingConfigController.createTradingConfig(tradingConfig);
            fail("must throw an exception");
        } catch (IOException e) {
            // OK
        }
    }

    @Test
    void testDifferentParameters() throws IOException {

        var stock = createStock();
        var strategy = createStrategy();
        tradingConfigRepository.deleteAll();
        List<StrategyParameterEntity> list = new ArrayList<>(strategy.getDefaultParameters());

        var tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, Constants.UTC));
        tradingConfig.getParameterEntities().add(new ConfigParameterEntity(tradingConfig, list.get(0), BigDecimal.ONE));

        tradingConfigController.createTradingConfig(tradingConfig);

        tradingConfig = new TradingConfigEntity();
        tradingConfig.setStock(stock);
        tradingConfig.setStrategy(strategy);
        tradingConfig.setActive(true);
        tradingConfig.setInterval(Interval.DAILY);
        tradingConfig.setStartTime(ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, Constants.UTC));
        tradingConfig.getParameterEntities().add(new ConfigParameterEntity(tradingConfig, list.get(1), BigDecimal.ONE));

        tradingConfigController.createTradingConfig(tradingConfig);

        assertEquals(2, tradingConfigController.listTradingConfigs().size());
    }

}
