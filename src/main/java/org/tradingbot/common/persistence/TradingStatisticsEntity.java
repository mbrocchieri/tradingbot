package org.tradingbot.common.persistence;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Immutable
public class TradingStatisticsEntity {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "NB_TRADES")
    private int nbTrades;

    @Column(name = "PERCENT_PERF")
    private BigDecimal percentPerf;

    public int getId() {
        return id;
    }

    public int getNbTrades() {
        return nbTrades;
    }

    public BigDecimal getPercentPerf() {
        return percentPerf;
    }

    public List<TradingStatisticsEntity> findAll() {
        return null;
    }
}
