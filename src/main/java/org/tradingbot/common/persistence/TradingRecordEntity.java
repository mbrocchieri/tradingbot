package org.tradingbot.common.persistence;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "TRADING_RECORDS", indexes = {@Index(name = "tr_config_id", columnList = "CONFIG_ID"),
        @Index(name = "tr_config_id_start_time", columnList = "CONFIG_ID,CANDLE_START_TIME" )}
        )
public class TradingRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "CONFIG_ID", nullable = false)
    private TradingConfigEntity config;

    @Column(name = "TRADE_START_BUY", nullable = false)
    private boolean buy;

    @Column(name = "CANDLE_START_TIME", nullable = false)
    private ZonedDateTime candleStartTime;

    @Column(name = "AMOUNT", nullable = false, precision = 20, scale = 10)
    private BigDecimal amount;

    @Column(name = "CREATE_DATE", nullable = false)
    private ZonedDateTime createDate;

    @Column(name = "STOCK_PRICE", nullable = false, precision = 20, scale = 10)
    private BigDecimal stockPrice;



    // for hibernate
    public TradingRecordEntity() {

    }

    public TradingRecordEntity(TradingConfigEntity config, boolean buy, ZonedDateTime candleStartTime,
                               BigDecimal amount, BigDecimal stockPrice) {
        this.config = config;
        this.buy = buy;
        this.candleStartTime = candleStartTime;
        this.amount = amount;
        this.stockPrice = stockPrice;
        createDate = ZonedDateTime.now();
    }

    public int getId() {
        return id;
    }

    public TradingConfigEntity getConfig() {
        return config;
    }

    public boolean isBuy() {
        return buy;
    }

    public ZonedDateTime getCandleStartTime() {
        return candleStartTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getStockPrice() {
        return stockPrice;
    }

    @Override
    public String toString() {
        return "TradingRecordEntity{" + "id=" + id + ", config=" + config + ", buy=" + buy + ", candleStartTime=" +
                candleStartTime + ", amount=" + amount + ", createDate=" + createDate + ", stockPrice=" + stockPrice +
                '}';
    }
}
