package org.tradingbot.common.persistence;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "TRADING_RECORDS_SUMMARY", uniqueConstraints = {
        @UniqueConstraint(name = "trading_records_summary_unique_constraint",
                columnNames = {"CONFIG_ID", "BUY_CANDLE_START_TIME"})})
public class TradingRecordSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "CONFIG_ID", nullable = false)
    private TradingConfigEntity config;

    @Column(name = "BUY_CANDLE_START_TIME", nullable = false)
    private ZonedDateTime buyCandleStartTime;

    @Column(name = "SELL_CANDLE_START_TIME", nullable = false)
    private ZonedDateTime sellCandleStartTime;

    @Column(name = "PERCENT_PERFORMANCE", nullable = false)
    private BigDecimal percentPerformance;

    public TradingRecordSummaryEntity() {
    }

    public TradingRecordSummaryEntity(TradingConfigEntity config, ZonedDateTime buyCandleStartTime,
                                      ZonedDateTime sellCandleStartTime, BigDecimal percentPerformance) {
        this.config = config;
        this.buyCandleStartTime = buyCandleStartTime;
        this.sellCandleStartTime = sellCandleStartTime;
        this.percentPerformance = percentPerformance;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradingRecordSummaryEntity that = (TradingRecordSummaryEntity) o;
        return id == that.id && Objects.equals(config, that.config) &&
                Objects.equals(buyCandleStartTime, that.buyCandleStartTime) &&
                Objects.equals(sellCandleStartTime, that.sellCandleStartTime) &&
                Objects.equals(percentPerformance, that.percentPerformance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, config, buyCandleStartTime, sellCandleStartTime, percentPerformance);
    }

    @Override
    public String toString() {
        return "TradingRecordSummaryEntity{" + "id=" + id + ", config=" + config + ", buyCandleStartTime=" +
                buyCandleStartTime + ", sellCandleStartTime=" + sellCandleStartTime + ", percentPerformance=" +
                percentPerformance + '}';
    }

    public int getId() {
        return id;
    }

    public TradingConfigEntity getConfig() {
        return config;
    }

    public ZonedDateTime getBuyCandleStartTime() {
        return buyCandleStartTime;
    }

    public ZonedDateTime getSellCandleStartTime() {
        return sellCandleStartTime;
    }

    public BigDecimal getPercentPerformance() {
        return percentPerformance;
    }
}
