package org.tradingbot.util;

import org.apache.commons.lang3.StringUtils;
import org.tradingbot.common.Strings;
import org.tradingbot.common.strategy.StrategyParameter;

import java.math.BigDecimal;
import java.util.Map;

public class Strategies {
    public static final String RSI4060 = Strings.prettyFormatXml("<?xml version=\"1.0\"?>" + "" +
            "<strategy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:noNamespaceSchemaLocation=\"strategy2.xsd\"><entryRule><org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "<org.ta4j.core.indicators.RSIIndicator><org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<barCount>14</barCount></org.ta4j.core.indicators.RSIIndicator>" +
            "<threshold>40</threshold></org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "</entryRule><exitRule><org.ta4j.core.rules.CrossedDownIndicatorRule><org.ta4j.core.indicators.RSIIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<barCount>14</barCount></org.ta4j.core.indicators.RSIIndicator>" +
            "<threshold>60</threshold></org.ta4j.core.rules.CrossedDownIndicatorRule></exitRule></strategy>");

    public static final String RSI5050 = Strings.prettyFormatXml("<?xml version=\"1.0\"?>" +
            "<strategy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"strategy2.xsd\">" +
            "<entryRule><org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "<org.ta4j.core.indicators.RSIIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<barCount>${barCount}</barCount>" + "</org.ta4j.core.indicators.RSIIndicator>" +
            "<threshold>${threshold}</threshold>" + "</org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "</entryRule>" + "<exitRule>" + "<org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "<org.ta4j.core.indicators.RSIIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<barCount>${barCount}</barCount>" + "</org.ta4j.core.indicators.RSIIndicator>" +
            "<threshold>${threshold}</threshold>" + "</org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "</exitRule>" + "</strategy>");
    public static final Map<String, BigDecimal> RSI5050_PARAMETERS = Map.of("barCount", BigDecimal.valueOf(14), "threshold", BigDecimal.valueOf(50));

    public static final String RSI5050_COMMAND = "!strategyAdd RSI \"" +
            StringUtils.replace(Strategies.RSI5050, "\"", "\\\"") + "\" " + StrategyParameter.toString(Strategies.RSI5050_PARAMETERS);

    public static final String BASIC_RSI = Strings.prettyFormatXml("<?xml version=\"1.0\"?>" + "" +
            "<strategy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"strategy2.xsd\">" +
            "" + "<entryRule>" + "<org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "<org.ta4j.core.indicators.RSIIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<barCount>${barCount}</barCount>" + "</org.ta4j.core.indicators.RSIIndicator>" +
            "<threshold>${thresholdEntry}</threshold>" + "</org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "</entryRule>" + "<exitRule>" + "<org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "<org.ta4j.core.indicators.RSIIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<barCount>${barCount}</barCount>" + "</org.ta4j.core.indicators.RSIIndicator>" +
            "<threshold>${thresholdExit}</threshold>" + "</org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "</exitRule>" + "</strategy>");
    public static final Map<String, BigDecimal> BASIC_RSI_PARAMETERS = Map.of("barCount", BigDecimal.valueOf(14), "thresholdEntry", BigDecimal.valueOf(30), "thresholdExit", BigDecimal.valueOf(70));

    public static final String MOVING_MOMENTUM = Strings.prettyFormatXml("<strategy>" +
            "<entryRule>" +
            "<org.ta4j.core.rules.AndRule>" +
            "<org.ta4j.core.rules.AndRule>" +
            "<org.ta4j.core.rules.OverIndicatorRule>" +
            "<org.ta4j.core.indicators.EMAIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<java.lang.Integer>20</java.lang.Integer>" +
            "</org.ta4j.core.indicators.EMAIndicator>" +
            "<org.ta4j.core.indicators.EMAIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<java.lang.Integer>150</java.lang.Integer>" +
            "</org.ta4j.core.indicators.EMAIndicator>" +
            "</org.ta4j.core.rules.OverIndicatorRule>" +
            "<org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "<org.ta4j.core.indicators.StochasticOscillatorKIndicator>" +
            "<java.lang.Integer>14</java.lang.Integer>" +
            "</org.ta4j.core.indicators.StochasticOscillatorKIndicator>" +
            "<java.lang.Integer>20</java.lang.Integer>" +
            "</org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "</org.ta4j.core.rules.AndRule>" +
            "<org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "<org.ta4j.core.indicators.MACDIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<java.lang.Integer>9</java.lang.Integer>" +
            "<java.lang.Integer>26</java.lang.Integer>" +
            "</org.ta4j.core.indicators.MACDIndicator>" +
            "<java.lang.Integer>0</java.lang.Integer>" +
            "</org.ta4j.core.rules.CrossedDownIndicatorRule>" +
            "</org.ta4j.core.rules.AndRule>" +
            "</entryRule>" +
            "<exitRule>" +
            "<org.ta4j.core.rules.AndRule>" +
            "<org.ta4j.core.rules.AndRule>" +
            "<org.ta4j.core.rules.UnderIndicatorRule>" +
            "<org.ta4j.core.indicators.EMAIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<java.lang.Integer>20</java.lang.Integer>" +
            "</org.ta4j.core.indicators.EMAIndicator>" +
            "<org.ta4j.core.indicators.EMAIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<java.lang.Integer>150</java.lang.Integer>" +
            "</org.ta4j.core.indicators.EMAIndicator>" +
            "</org.ta4j.core.rules.UnderIndicatorRule>" +
            "<org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "<org.ta4j.core.indicators.StochasticOscillatorKIndicator>" +
            "<java.lang.Integer>14</java.lang.Integer>" +
            "</org.ta4j.core.indicators.StochasticOscillatorKIndicator>" +
            "<java.lang.Integer>80</java.lang.Integer>" +
            "</org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "</org.ta4j.core.rules.AndRule>" +
            "<org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "<org.ta4j.core.indicators.MACDIndicator>" +
            "<org.ta4j.core.indicators.helpers.ClosePriceIndicator />" +
            "<java.lang.Integer>9</java.lang.Integer>" +
            "<java.lang.Integer>26</java.lang.Integer>" +
            "</org.ta4j.core.indicators.MACDIndicator>" +
            "<java.lang.Integer>0</java.lang.Integer>" +
            "</org.ta4j.core.rules.CrossedUpIndicatorRule>" +
            "</org.ta4j.core.rules.AndRule>" +
            "</exitRule>" +
            "</strategy>");

    public static final String STOCH_EMA_SUPERTREND = "<?xml version=\"1.0\"?>\n" + "\n" +
            "            <strategy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"strategy2.xsd\">\n" +
            "                <entryRule>\n" + "                    <org.ta4j.core.rules.AndRule>\n" +
            "                        <org.ta4j.core.rules.AndRule>\n" +
            "                            <org.ta4j.core.rules.CrossedUpIndicatorRule>\n" +
            "                                <org.ta4j.core.indicators.StochasticOscillatorKIndicator>\n" +
            "                                    <barCount>14</barCount>\n" +
            "                                </org.ta4j.core.indicators.StochasticOscillatorKIndicator>\n" +
            "                                <org.ta4j.core.indicators.StochasticOscillatorDIndicator>\n" +
            "                                    <org.ta4j.core.indicators.StochasticOscillatorKIndicator>\n" +
            "                                        <barCount>14</barCount>\n" +
            "                                    </org.ta4j.core.indicators.StochasticOscillatorKIndicator>\n" +
            "                                </org.ta4j.core.indicators.StochasticOscillatorDIndicator>\n" +
            "                            </org.ta4j.core.rules.CrossedUpIndicatorRule>\n" +
            "                            <org.ta4j.core.rules.OverIndicatorRule>\n" +
            "                                <org.ta4j.core.indicators.EMAIndicator>\n" +
            "                                    <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
            "                                    <barCount>200</barCount>\n" +
            "                                </org.ta4j.core.indicators.EMAIndicator>\n" +
            "                                <threshold>0</threshold>\n" +
            "                            </org.ta4j.core.rules.OverIndicatorRule>\n" +
            "                        </org.ta4j.core.rules.AndRule>\n" +
            "                        <org.ta4j.core.rules.OverIndicatorRule>\n" +
            "                            <org.tradingbot.common.indicator.SuperTrend>\n" +
            "                                <barCount>10</barCount>\n" +
            "                                <multiplier>3</multiplier>\n" +
            "                            </org.tradingbot.common.indicator.SuperTrend>\n" +
            "                            <threshold>0</threshold>\n" +
            "                        </org.ta4j.core.rules.OverIndicatorRule>\n" +
            "                    </org.ta4j.core.rules.AndRule>\n" + "                </entryRule>\n" +
            "                <exitRule>\n" + "                    <org.ta4j.core.rules.OrRule>\n" +
            "                        <org.ta4j.core.rules.StopGainRule>\n" +
            "                            <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
            "                            <gainPercentage>10</gainPercentage>\n" +
            "                        </org.ta4j.core.rules.StopGainRule>\n" +
            "                        <org.ta4j.core.rules.StopLossRule>\n" +
            "                            <org.ta4j.core.indicators.helpers.ClosePriceIndicator />\n" +
            "                            <lossPercentage>5</lossPercentage>\n" +
            "                        </org.ta4j.core.rules.StopLossRule>\n" +
            "                    </org.ta4j.core.rules.OrRule>\n" + "                </exitRule>\n" +
            "            </strategy>";

    public static final String DOUBLE_BOTTOM_STOP_LOSS = Strings.prettyFormatXml("<?xml version=\"1.0\"?>\n" +
            "\n" +
            "<strategy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"strategy2.xsd\">\n" +
            "    <entryRule>\n" +
            "        <org.ta4j.core.rules.AndRule>\n" +
            "            <org.ta4j.core.rules.BooleanIndicatorRule>\n" +
            "                <org.tradingbot.common.indicator.DoubleBottomIndicator/>\n" +
            "            </org.ta4j.core.rules.BooleanIndicatorRule>\n" +
            "            <org.ta4j.core.rules.OverIndicatorRule>\n" +
            "                <org.ta4j.core.indicators.EMAIndicator>\n" +
            "                    <org.ta4j.core.indicators.helpers.ClosePriceIndicator/>\n" +
            "                    <barCount>200</barCount>\n" +
            "                </org.ta4j.core.indicators.EMAIndicator>\n" +
            "                <org.ta4j.core.indicators.helpers.ClosePriceIndicator/>\n" +
            "            </org.ta4j.core.rules.OverIndicatorRule>\n" +
            "        </org.ta4j.core.rules.AndRule>\n" +
            "    </entryRule>\n" +
            "    <exitRule>\n" +
            "        <org.ta4j.core.rules.OrRule>\n" +
            "            <org.ta4j.core.rules.StopGainRule>\n" +
            "                <org.ta4j.core.indicators.helpers.ClosePriceIndicator/>\n" +
            "                <gainPercentage>10</gainPercentage>\n" +
            "            </org.ta4j.core.rules.StopGainRule>\n" +
            "            <org.ta4j.core.rules.StopLossRule>\n" +
            "                <org.ta4j.core.indicators.helpers.ClosePriceIndicator/>\n" +
            "                <lossPercentage>5</lossPercentage>\n" +
            "            </org.ta4j.core.rules.StopLossRule>\n" +
            "        </org.ta4j.core.rules.OrRule>\n" +
            "    </exitRule>\n" +
            "</strategy>\n");
}
