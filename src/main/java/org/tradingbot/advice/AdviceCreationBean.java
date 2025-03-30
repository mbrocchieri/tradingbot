package org.tradingbot.advice;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdviceCreationBean {
    private int stockId;
    private boolean buy;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate adviceDate;
    private int adviserId;
    private int adviceCategoryId;

    // for Spring
    public AdviceCreationBean() {}

    public AdviceCreationBean(int stockId, boolean buy, BigDecimal price, LocalDate adviceDate, int adviserId,
                              int adviceCategoryId) {
        this.stockId = stockId;
        this.buy = buy;
        this.price = price;
        this.adviceDate = adviceDate;
        this.adviserId = adviserId;
        this.adviceCategoryId = adviceCategoryId;
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

    public LocalDate getAdviceDate() {
        return adviceDate;
    }

    public int getAdviserId() {
        return adviserId;
    }

    public int getAdviceCategoryId() {
        return adviceCategoryId;
    }
}
