package org.tradingbot.common.persistence;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Immutable
public class TradingViewEntity {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "DATE")
    private ZonedDateTime date;
    @Column(name = "STOCK_ID")
    private int stockId;
    @Column(name = "BUY")
    private boolean buy;
    @Column(name = "STRATEGY_ID")
    private int strategyId;
    @Column(name = "CONFIG_ID")
    private int configId;

    @Column(name = "COMPUTED_DATE")
    private ZonedDateTime computedDate;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public int getStockId() {
        return stockId;
    }

    public int getStrategyId() {
        return strategyId;
    }

    public int getConfigId() {
        return configId;
    }

    public boolean isBuy() {
        return buy;
    }

    public ZonedDateTime getComputedDate() {
        return computedDate;
    }

    public List<TradingViewEntity> findAll() {
        return null;
    }
}
