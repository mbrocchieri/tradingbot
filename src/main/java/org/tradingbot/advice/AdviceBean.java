package org.tradingbot.advice;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdviceBean {
    private int id;
    private int stockId;
    private boolean buy;
    private BigDecimal price;
    private int adviserId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate adviceDate;
    private int adviceCategoryId;

    public AdviceBean(int id, int stockId, boolean buy, BigDecimal price, int adviserId, LocalDate adviceDate,
                      int adviceCategoryId) {
        this.id = id;
        this.stockId = stockId;
        this.buy = buy;
        this.price = price;
        this.adviserId = adviserId;
        this.adviceDate = adviceDate;
        this.adviceCategoryId = adviceCategoryId;
    }

    public int getId() {
        return id;
    }

    public int getStockId() {
        return stockId;
    }

    public boolean isBuy() {
        return buy;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getAdviserId() {
        return adviserId;
    }

    public LocalDate getAdviceDate() {
        return adviceDate;
    }

    public int getAdviceCategoryId() {
        return adviceCategoryId;
    }
}
