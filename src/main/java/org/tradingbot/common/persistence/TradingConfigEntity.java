package org.tradingbot.common.persistence;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.converter.ZonedDateTimeConverter;

import javax.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "TRADING_CONFIGS")
public class TradingConfigEntity implements Serializable {

    @OneToMany(mappedBy = "config", fetch = FetchType.EAGER)
    @Cascade(value = {CascadeType.ALL})
    private final List<ConfigParameterEntity> parameterEntities = new ArrayList<>();
    @OneToMany(mappedBy = "config")
    private final List<TradingRecordEntity> tradingRecord = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @ManyToOne
    @JoinColumn(name = "STRATEGY_ID", nullable = false)
    private StrategyEntity strategy;
    @ManyToOne
    @JoinColumn(name = "STOCK_ID", nullable = false)
    private StockEntity stock;
    @Column(name = "ACTIVE")
    private boolean active;
    @Convert(converter = ZonedDateTimeConverter.class)
    @Column(name = "START_TIME")
    private ZonedDateTime startTime;
    @Column(name = "INTERVAL_TIME")
    @Enumerated(EnumType.STRING)
    private Interval interval;

    // default constructor for hibernate
    public TradingConfigEntity() {

    }

    public TradingConfigEntity(StrategyEntity strategy, StockEntity stock, boolean active, ZonedDateTime startTime, Interval interval) {
        this.strategy = strategy;
        this.stock = stock;
        this.active = active;
        this.startTime = startTime;
        this.interval = interval;
    }

    public int getId() {
        return id;
    }

    public StrategyEntity getStrategy() {
        return strategy;
    }

    public void setStrategy(StrategyEntity strategy) {
        this.strategy = strategy;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public StockEntity getStock() {
        return stock;
    }

    public void setStock(StockEntity stock) {
        this.stock = stock;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public List<ConfigParameterEntity> getParameterEntities() {
        return parameterEntities;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<TradingRecordEntity> getTradingRecord() {
        return tradingRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradingConfigEntity that = (TradingConfigEntity) o;
        return id == that.id && active == that.active && parameterEntities.equals(that.parameterEntities) &&
                tradingRecord.equals(that.tradingRecord) && strategy.equals(that.strategy) &&
                stock.equals(that.stock) && startTime.equals(that.startTime) && interval == that.interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterEntities, tradingRecord, id, strategy, stock, active, startTime, interval);
    }

    @Override
    public String toString() {
        return "TradingConfigEntity{" + "parameterEntities=" + parameterEntities +
                ", id=" + id + ", strategy=" + strategy + ", stock=" + stock + ", active=" + active + ", startTime=" +
                startTime + ", interval=" + interval + '}';
    }
}
