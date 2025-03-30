package org.tradingbot.common.indicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PeakDivergenceIndicator extends CachedIndicator<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(PeakDivergenceIndicator.class);
    private final Indicator<Num> ref;
    private final Indicator<Num> other;
    private final int barCount;

    public PeakDivergenceIndicator(Indicator<Num> ref, Indicator<Num> other, int barCount) {
        super(ref);
        this.ref = ref;
        this.other = other;
        this.barCount = barCount;
    }

    @Override
    protected Boolean calculate(int index) {
        if (index > barCount) {
            var barSeries = ref.getBarSeries();
            List<Position> positionList = new ArrayList<>();
            for (int i = index - barCount; i <= index; i++) {
                Bar bar = barSeries.getBar(i);
                positionList.add(new Position(ref.getValue(i), i, bar.getBeginTime()));
            }
            removeIntermediary(positionList);

            // get the 2 lowest
            positionList.sort(Comparator.comparing(Position::getPrice));
            List<Position> lowest = new ArrayList<>();
            lowest.add(positionList.get(0));
            lowest.add(positionList.get(1));
            lowest.sort(Comparator.comparing(Position::getStartDate));
            if (lowest.get(1).getPrice().isLessThan(lowest.get(0).getPrice())) {
                int index1 = lowest.get(0).getIndex();
                int index2 = lowest.get(1).getIndex();

                Num value1 = other.getValue(index1);
                Num value2 = other.getValue(index2);

                if (!isPeakDown(other, index1) || !isPeakDown(other, index2) || !isPeakDown(ref, index1) || !isPeakDown(ref, index2)) {
                    return false;
                }

                var toReturn = value2.isGreaterThan(value1) &&
                        // test the idea of the length of the divergence
                        index - index2 <= 5;// index2 - index1;

                if (toReturn && LOG.isDebugEnabled()) {
                    LOG.debug("Peak divergence detected with dates {} and {}", lowest.get(0).getStartDate(), lowest.get(1).getStartDate());
                }

                return toReturn;
            }
        }
        return false;
    }

    private boolean isPeakDown(Indicator<Num> indicator, int index) {
        var value = indicator.getValue(index);
        if (index + 1 >= indicator.getBarSeries().getBarCount()) { // to not  have OutOfBoundException
            return true;
        }
        return indicator.getValue(index - 1).isGreaterThan(value) && indicator.getValue(index + 1).isGreaterThan(value);
    }

    private void removeIntermediary(List<Position> positions) {
        List<Integer> toRemove = new ArrayList<>();
        do {
            toRemove.clear();
            var end = positions.size() - 1;
            for (var i = 1; i < end; i++) {
                var previous = positions.get(i - 1).price;
                var current = positions.get(i).price;
                var next = positions.get(i + 1).price;
                if (current.isGreaterThanOrEqual(previous) && current.isLessThanOrEqual(next) ||
                        current.isLessThanOrEqual(previous) && current.isGreaterThanOrEqual(next)) {
                    toRemove.add(i);
                }
            }
            for (int i = toRemove.size() - 1; i >= 0; --i) {
                positions.remove((int) toRemove.get(i));
            }
        } while (toRemove.isEmpty());
    }

    public static class Position {
        private final Num price;
        private final int index;
        private final ZonedDateTime startDate;

        public Position(Num price, int index, ZonedDateTime startDate) {
            this.price = price;
            this.index = index;
            this.startDate = startDate;
        }

        public Num getPrice() {
            return price;
        }

        public int getIndex() {
            return index;
        }

        public ZonedDateTime getStartDate() {
            return startDate;
        }
    }
}
