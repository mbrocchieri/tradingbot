/**
 * MIT License
 *
 * Copyright (c) 2021 nunoalmarques
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/
package org.tradingbot.common.indicator;

import org.apache.commons.lang3.tuple.Pair;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.TRIndicator;
import org.ta4j.core.num.Num;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static org.tradingbot.common.indicator.Trend.DOWN;
import static org.tradingbot.common.indicator.Trend.UP;

public abstract class AbstractSuperTrend<T> extends AbstractIndicator<T> {
    private final int barCount;
    private final Num barCountNum;
    private final Num multiplier;
    private final Num two;
    private final Num zero;
    private final TRIndicator trIndicator;
    private final ClosePriceIndicator closePriceIndicator;
    private final HighPriceIndicator highPriceIndicator;
    private final LowPriceIndicator lowPriceIndicator;

    private final BinaryOperator<Num> upper;
    private final BinaryOperator<Num> lower;

    private final Map<Integer, Pair<Num, Num>> prevLower;
    private final Map<Integer, Pair<Num, Num>> prevUpper;
    private final Map<Integer, Pair<Trend, Num>> prevTrend;

    protected AbstractSuperTrend(BarSeries series, int barCount, int multiplier) {
        super(series);
        this.barCount = barCount;
        this.barCountNum = this.numOf(barCount);
        this.multiplier = this.numOf(multiplier);
        this.two = this.numOf(2);
        this.zero = this.numOf(0);
        this.trIndicator = new TRIndicator(series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.highPriceIndicator = new HighPriceIndicator(series);
        this.lowPriceIndicator = new LowPriceIndicator(series);
        this.upper = (midpoint, atr) -> midpoint.plus((this.multiplier).multipliedBy(atr));
        this.lower = (midpoint, atr) -> midpoint.minus((this.multiplier).multipliedBy(atr));
        this.prevLower = new HashMap<>();
        this.prevUpper = new HashMap<>();
        this.prevTrend = new HashMap<>();
    }

    protected Pair<Trend, Num> calculate(int index) {

        int start;
        if (prevTrend.keySet().isEmpty()) {
            start = 0;
        } else {
            start = Collections.max(prevTrend.keySet()) + 1;
        }
        Pair<Trend, Num> last = null;
        for (int i = start; i <= index ; i++) {
            Num close = closePriceIndicator.getValue(i);
            Pair<Num, Num> finalUpperBand = calculateUpperBand(i);
            Pair<Num, Num> finalLowerBand = calculateLowerBand(i);

            Pair<Trend, Num> result = Pair.of(UP, finalLowerBand.getLeft());
            if (i < barCount) {
                last = result;
                prevTrend.put(i, result);
            } else {

                Pair<Trend, Num> previousSuperTrend = prevTrend.get(i - 1);

                if (previousSuperTrend.getRight().isEqual(finalUpperBand.getRight()) && close.isLessThanOrEqual(finalUpperBand.getLeft())) {
                    last = Pair.of(DOWN, finalUpperBand.getLeft());
                    prevTrend.put(i, last);
                } else {
                    if (previousSuperTrend.getRight().isEqual(finalUpperBand.getRight()) && close.isGreaterThan(finalUpperBand.getLeft())) {
                        last = Pair.of(UP, finalLowerBand.getLeft());
                        prevTrend.put(i, last);
                    } else {
                        if (previousSuperTrend.getRight().isEqual(finalLowerBand.getRight()) && close.isGreaterThanOrEqual(finalLowerBand.getLeft())) {
                            last = Pair.of(UP, finalLowerBand.getLeft());
                            prevTrend.put(i, last);
                        } else {
                            if (previousSuperTrend.getRight().isEqual(finalLowerBand.getRight()) && close.isLessThan(finalUpperBand.getLeft())) {
                                last = Pair.of(DOWN, finalUpperBand.getLeft());
                                prevTrend.put(i, last);
                            }
                        }
                    }
                }
            }
        }
        if (last != null) {
            return last;
        }
        return prevTrend.get(index);
    }

    private Pair<Num, Num> calculateUpperBand(int index) {
        int start;
        if (prevUpper.keySet().isEmpty()) {
            start = 0;
        } else {
            start = Collections.max(prevUpper.keySet()) + 1;
        }
        Pair<Num, Num> last = null;
        for (int i = start; i <= index ; i++) {
            Num currentBasicUpperBand = getBasicBandValue(i, upper);
            if (i < barCount) {
                last = Pair.of(currentBasicUpperBand, zero);
                prevUpper.put(i, last);
            } else {
                var previous = prevUpper.get(i - 1);
                Num previousClose = closePriceIndicator.getValue(i - 1);
                if (currentBasicUpperBand.isLessThan(previous.getLeft())
                        || previousClose.isGreaterThan(previous.getLeft())) {
                    last = Pair.of(currentBasicUpperBand, previous.getLeft());
                    prevUpper.put(i, last);
                } else {
                    last = Pair.of(previous.getLeft(), previous.getLeft());
                    prevUpper.put(i, last);
                }
            }
        }
        if (last != null) {
            return last;
        }
        return prevUpper.get(index);
    }

    private Pair<Num, Num> calculateLowerBand(int index) {
        int start;
        if (prevLower.keySet().isEmpty()) {
            start = 0;
        } else {
            start = Collections.max(prevLower.keySet()) + 1;
        }
        Pair<Num, Num> last = null;
        for (int i = start; i <= index ; i++) {
            Num currentBasicLowerBand = getBasicBandValue(i, lower);
            if (i < barCount) {
                last = Pair.of(currentBasicLowerBand, zero);
                prevLower.put(i, last);
            } else {
                var previous = prevLower.get(i - 1);
                Num previousClose = closePriceIndicator.getValue(i - 1);
                if (currentBasicLowerBand.isGreaterThan(previous.getLeft())
                        || previousClose.isLessThan(previous.getLeft())) {
                    last = Pair.of(currentBasicLowerBand, previous.getLeft());
                    prevLower.put(i, last);
                } else {
                    last = Pair.of(previous.getLeft(), previous.getLeft());
                    prevLower.put(i, last);
                }
            }
        }
        if (last != null) {
            return last;
        }
        return prevLower.get(index);
    }

    private Num getBasicBandValue(int index, BinaryOperator<Num> calculator) {
        Num high = highPriceIndicator.getValue(index);
        Num low = lowPriceIndicator.getValue(index);
        Num atr = calculateSimpleAverageTrueRange(index);
        Num midpoint = (high.plus(low)).dividedBy(two);
        return calculator.apply(midpoint, atr);
    }

    private Num calculateSimpleAverageTrueRange(int index) {
        Num sum = zero;
        for (int i = index - barCount + 1; i <= index; i++) {
            sum = sum.plus(trIndicator.getValue(i));
        }
        return sum.dividedBy(barCountNum);
    }

}
