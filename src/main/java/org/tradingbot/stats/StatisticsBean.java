package org.tradingbot.stats;

import java.math.BigDecimal;

public class StatisticsBean {
    private final BigDecimal allBuys;
    private final BigDecimal allSells;
    private final int nbTrades;
    private final BigDecimal performance;

    public StatisticsBean(BigDecimal allBuys, BigDecimal allSells, int nbTrades, BigDecimal performance) {
        this.allBuys = allBuys;
        this.allSells = allSells;
        this.nbTrades = nbTrades;
        this.performance = performance;
    }

    public BigDecimal getAllBuys() {
        return allBuys;
    }

    public BigDecimal getAllSells() {
        return allSells;
    }

    public int getNbTrades() {
        return nbTrades;
    }

    public BigDecimal getPerformance() {
        return performance;
    }
}
