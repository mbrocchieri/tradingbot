package org.tradingbot.common.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;

public class DoubleConstantIndicator extends ConstantIndicator<Num> implements Indicator<Num> {
    public DoubleConstantIndicator(BarSeries series, Num aNum) {
        super(series, aNum);
    }
}
