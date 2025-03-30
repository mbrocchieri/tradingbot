package org.tradingbot.common.persistence;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Immutable
@IdClass(PerfByPeriodId.class)
public class PerfByPeriodViewEntity implements Serializable {
    @Id
    @Column(name = "period")
    String period;
    @Id
    @Column(name = "strategy_id")
    int strategyId;
    @Column(name = "perf")
    BigDecimal perf;

    public PerfByPeriodViewEntity() {
    }

    public int getStrategyId() {
        return strategyId;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setPerf(BigDecimal perf) {
        this.perf = perf;
    }

    public String getPeriod() {
        return period;
    }

    public BigDecimal getPerf() {
        return perf;
    }

    public List<TradingViewEntity> findAll() {
        return null;
    }
}
