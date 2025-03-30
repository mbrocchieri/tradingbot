package org.tradingbot.common.persistence;

import java.io.Serializable;
import java.util.Objects;

public class PerfByPeriodId implements Serializable {
    String period;
    int strategyId;

    public PerfByPeriodId(String period, int strategyId) {
        this.period = period;
        this.strategyId = strategyId;
    }

    public PerfByPeriodId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PerfByPeriodId that = (PerfByPeriodId) o;
        return strategyId == that.strategyId && Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, strategyId);
    }

    @Override
    public String toString() {
        return "PerfByPeriodId{" + "period='" + period + '\'' + ", strategyId=" + strategyId + '}';
    }
}
