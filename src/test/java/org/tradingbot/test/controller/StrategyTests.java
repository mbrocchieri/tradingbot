package org.tradingbot.test.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.strategy.StrategyCreationBean;
import org.tradingbot.strategy.StrategyParameterCreationBean;
import org.tradingbot.util.Strategies;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.OPEN_MARKET;

public class StrategyTests extends TradingBotApplicationTests {

    @BeforeEach
    void before() {
        cleanDatabase();
    }

    @Test
    void testStrategyAdd() {

        var list = strategyController.listStrategies();
        assertEquals(0, list.size());

        var strategy = new StrategyCreationBean();
        strategy.setName("A");
        strategy.setXml(Strategies.RSI5050);
        strategy.setTradingPeriod(OPEN_MARKET);
        for (var entry : Strategies.RSI5050_PARAMETERS.entrySet()) {
            var strategyParameter = new StrategyParameterCreationBean(entry.getKey(), entry.getValue());
            strategy.getDefaultParameters().add(strategyParameter);
        }
        strategyController.createStrategy(strategy);

        list = strategyController.listStrategies();

        assertEquals(1, list.size());
        var actualStrategy = list.get(0);
        assertEquals("A", actualStrategy.getName());
        assertEquals(Strategies.RSI5050, actualStrategy.getXml());
        var defaultParameters = actualStrategy.getDefaultParameters();
        assertEquals(2, defaultParameters.size());
        Map<String, BigDecimal> actualMap = new HashMap<>();
        defaultParameters.iterator().forEachRemaining(p -> actualMap.put(p.getName(), p.getDefaultValue()));

        assertEquals(0, BigDecimal.valueOf(50).compareTo(actualMap.get("threshold")));
        assertEquals(0, BigDecimal.valueOf(14).compareTo(actualMap.get("barCount")));
    }

    @Test
    void testStockAddAlreadyExists() {

        var strategy = new StrategyCreationBean();
        strategy.setName("A");
        strategy.setXml(Strategies.RSI5050);
        strategy.setTradingPeriod(OPEN_MARKET);
        for (var entry : Strategies.RSI5050_PARAMETERS.entrySet()) {
            var strategyParameter = new StrategyParameterCreationBean(entry.getKey(), entry.getValue());
            strategy.getDefaultParameters().add(strategyParameter);
        }
        strategyController.createStrategy(strategy);

        strategy = new StrategyCreationBean();
        strategy.setName("A");
        strategy.setXml(Strategies.RSI5050);
        for (var entry : Strategies.RSI5050_PARAMETERS.entrySet()) {
            var strategyParameter = new StrategyParameterCreationBean(entry.getKey(), entry.getValue());
            strategy.getDefaultParameters().add(strategyParameter);
        }

        try {
            strategyController.createStrategy(strategy);
            Assertions.fail("Must throw an exception");
        } catch (DataIntegrityViolationException e) {
            // OK
        }

    }

    @Test
    void testInvalidXml() {
        var strategy = new StrategyCreationBean();
        strategy.setName("A");
        strategy.setXml("X");
        var strategyParameter = new StrategyParameterCreationBean("B", BigDecimal.ONE);
        strategy.getDefaultParameters().add(strategyParameter);
        try {
            strategyController.createStrategy(strategy);
            Assertions.fail("must throw an exception");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    void testDivergenceXml() {
        var strategy = new StrategyCreationBean();
        strategy.setName("A");
        strategy.setXml("<?xml version=\"1.0\"?>\n" + "\n" +
                "<strategy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"strategy.xsd\">\n" +
                "  <entryRule>\n" + "    <org.ta4j.core.rules.BooleanIndicatorRule>\n" +
                "      <org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator>\n" +
                "        <org.ta4j.core.indicators.helpers.ClosePriceIndicator></org.ta4j.core.indicators.helpers.ClosePriceIndicator>\n" +
                "        <org.ta4j.core.indicators.RSIIndicator>\n" +
                "          <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
                "          <barCount>14</barCount>\n" + "        </org.ta4j.core.indicators.RSIIndicator>\n" +
                "        <barCount>14</barCount>\n" +
                "        <org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator.ConvergenceDivergenceType>positiveConvergent</org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator.ConvergenceDivergenceType>\n" +
                "      </org.ta4j.core.indicators.helpers.ConvergenceDivergenceIndicator>\n" +
                "    </org.ta4j.core.rules.BooleanIndicatorRule>\n" + "  </entryRule>\n" + "  <exitRule>\n" +
                "    <org.ta4j.core.rules.OrRule>\n" + "      <org.ta4j.core.rules.StopGainRule>\n" +
                "        <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
                "        <gainPercentage>10</gainPercentage>\n" + "      </org.ta4j.core.rules.StopGainRule>\n" +
                "      <org.ta4j.core.rules.StopLossRule>\n" +
                "        <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
                "        <lossPercentage>2</lossPercentage>\n" + "      </org.ta4j.core.rules.StopLossRule>\n" +
                "    </org.ta4j.core.rules.OrRule>\n" + "  </exitRule>\n" + "</strategy>\n");
        strategyController.createStrategy(strategy);
    }
}
