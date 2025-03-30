package org.tradingbot.trading;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TradingBean {
    private final int id;
    private final TradingSource source;
    private final TradingData data;
    private final boolean buy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final ZonedDateTime date;
    private final BigDecimal amount;
    private final int stockId;
    private final String stockName;
    private final int configId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final ZonedDateTime computedDate;

    public TradingBean(int id, TradingSource source, TradingData data, boolean buy, ZonedDateTime date,
                       BigDecimal amount, int stockId, String stockName, int configId, ZonedDateTime computedDate) {
        this.id = id;
        this.source = source;
        this.data = data;
        this.buy = buy;
        this.date = date;
        this.amount = amount;
        this.stockId = stockId;
        this.stockName = stockName;
        this.configId = configId;
        this.computedDate = computedDate;
    }

    public int getId() {
        return id;
    }

    public TradingSource getSource() {
        return source;
    }

    public TradingData getData() {
        return data;
    }

    public boolean isBuy() {
        return buy;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getStockId() {
        return stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public int getConfigId() {
        return configId;
    }

    public ZonedDateTime getComputedDate() {
        return computedDate;
    }
}
