package org.tradingbot.common.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DoubleBottomIndicator extends CachedIndicator<Boolean> {

    private final List<ClosePosition> simple;

    public DoubleBottomIndicator(BarSeries series) {
        super(series);
        var candles = series.getBarData();

        List<ClosePosition> close = new ArrayList<>(candles.size());
        for (var i = 0; i < candles.size(); i++) {
            var candle = candles.get(i);
            close.add(new ClosePosition(i, candle.getClosePrice(), candle.getBeginTime()));
        }

        while (removeIntermediary(close)) ;
        this.simple = Collections.unmodifiableList(close);

    }

    @Override
    protected Boolean calculate(int index) {
        if (index < 4) {
            return false;
        }
        int i;
        for (i = 0; i < simple.size(); i++) {
            if (simple.get(i).getIndex() == index) {
                break;
            }
        }
        if (i < 4 || i == simple.size()) {
            return false;
        }
        var closePrice = simple.get(i).getClose();
        var closePriceMinus1 = simple.get(i - 1).getClose();
        var closePriceMinus2 = simple.get(i - 2).getClose();
        var closePriceMinus3 = simple.get(i - 3).getClose();
        var closePriceMinus4 = simple.get(i - 4).getClose();
        return computeDetection(closePrice, closePriceMinus1, closePriceMinus2, closePriceMinus3, closePriceMinus4);
    }

    protected Boolean computeDetection(Num closePrice, Num closePriceMinus1, Num closePriceMinus2, Num closePriceMinus3, Num closePriceMinus4) {
        return closePrice.isGreaterThan(closePriceMinus1) && closePriceMinus1.isLessThan(closePriceMinus2) &&
                closePriceMinus2.isGreaterThan(closePriceMinus3) && closePriceMinus3.isLessThan(closePriceMinus4) &&
                closePrice.isGreaterThanOrEqual(closePriceMinus2);
    }

    private boolean removeIntermediary(List<ClosePosition> close) {
        List<Integer> toRemove = new ArrayList<>();
        var end = close.size() - 1;
        for (var i = 1; i < end; i++) {
            var previous = close.get(i - 1).getClose();
            var current = close.get(i).getClose();
            var next = close.get(i + 1).getClose();
            if (current.isGreaterThanOrEqual(previous) && current.isLessThanOrEqual(next) ||
                    current.isLessThanOrEqual(previous) && current.isGreaterThanOrEqual(next)) {
                toRemove.add(i);
            }
        }
        for (int i = toRemove.size() - 1; i >= 0; --i) {
            close.remove((int) toRemove.get(i));
        }
        return !toRemove.isEmpty();
    }

    public static class ClosePosition {
        private final Num close;
        private final ZonedDateTime date;
        private final int index;

        public ClosePosition(int index, Num close, ZonedDateTime date) {
            this.close = Objects.requireNonNull(close);
            this.date = Objects.requireNonNull(date);
            this.index = index;
        }

        public Num getClose() {
            return close;
        }

        public ZonedDateTime getDate() {
            return date;
        }

        public int getIndex() {
            return index;
        }
    }

}
