package org.tradingbot.common.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

public class DoubleTopIndicator extends DoubleBottomIndicator {
    public DoubleTopIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Boolean computeDetection(Num closePrice, Num closePriceMinus1, Num closePriceMinus2, Num closePriceMinus3, Num closePriceMinus4) {
        return closePrice.isLessThan(closePriceMinus1) && closePriceMinus1.isGreaterThan(closePriceMinus2) &&
                closePriceMinus2.isLessThan(closePriceMinus3) && closePriceMinus3.isGreaterThan(closePriceMinus4) &&
                closePrice.isLessThanOrEqual(closePriceMinus2);
    }
}
