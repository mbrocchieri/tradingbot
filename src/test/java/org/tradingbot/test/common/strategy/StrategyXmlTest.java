package org.tradingbot.test.common.strategy;

import org.junit.jupiter.api.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.rules.AndRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OrRule;
import org.tradingbot.common.strategy.Strategy;
import org.tradingbot.common.strategy.StrategyInitException;
import org.tradingbot.util.Strategies;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StrategyXmlTest {

    @Test
    public void testNominal() throws StrategyInitException {
        var strategyXml = new Strategy("test",
                "<strategy>\n" + "    <entryRule>\n" + "        <org.ta4j.core.rules.CrossedUpIndicatorRule>\n" +
                        "             <org.ta4j.core.indicators.RSIIndicator>\n" +
                        "                 <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
                        "                 <java.lang.Integer>14</java.lang.Integer>\n" +
                        "             </org.ta4j.core.indicators.RSIIndicator>\n" +
                        "             <org.ta4j.core.indicators.helpers.ConstantIndicator>" +
                        "                 <org.ta4j.core.num.Num>50</org.ta4j.core.num.Num>" +
                        "             </org.ta4j.core.indicators.helpers.ConstantIndicator>\n" +
                        "        </org.ta4j.core.rules.CrossedUpIndicatorRule>\n" + "    </entryRule>\n" +
                        "    <exitRule>\n" + "        <org.ta4j.core.rules.OrRule>" +
                        "            <org.ta4j.core.rules.CrossedDownIndicatorRule>\n" +
                        "                <org.ta4j.core.indicators.RSIIndicator>\n" +
                        "                    <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
                        "                    <java.lang.Integer>14</java.lang.Integer>\n" +
                        "                </org.ta4j.core.indicators.RSIIndicator>\n" +
                        "                <org.ta4j.core.indicators.helpers.ConstantIndicator>" +
                        "                    <org.ta4j.core.num.Num>50</org.ta4j.core.num.Num>" +
                        "                </org.ta4j.core.indicators.helpers.ConstantIndicator>\n" +
                        "            </org.ta4j.core.rules.CrossedDownIndicatorRule>\n" +
                        "            <org.ta4j.core.rules.StopLossRule>\n" +
                        "                 <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
                        "                 <java.lang.Double>1.2</java.lang.Double>\n" +
                        "            </org.ta4j.core.rules.StopLossRule>\n" + "        </org.ta4j.core.rules.OrRule>" +
                        "    </exitRule>\n" + "</strategy>");

        BarSeries bs = new BaseBarSeries();
        var strategy = strategyXml.internalToT4JStrategy(bs, Collections.emptyMap());
        assertTrue(strategy.getEntryRule() instanceof CrossedUpIndicatorRule);
        assertTrue(strategy.getExitRule() instanceof OrRule);
    }

    @Test
    public void testStochEmaSupertrend() throws StrategyInitException {
        var strategyXml = new Strategy("test", Strategies.STOCH_EMA_SUPERTREND);
        BarSeries bs = new BaseBarSeries();
        var strategy = strategyXml.internalToT4JStrategy(bs, Collections.emptyMap());
        assertTrue(strategy.getEntryRule() instanceof AndRule);
        assertTrue(strategy.getExitRule() instanceof OrRule);


    }

}
