package org.tradingbot.trade;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TradeUpdateBean {

    private int stockId;
    private BigDecimal stockPrice;
    private int quantity;
    private BigDecimal fees;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime date;
    private boolean paperTrading;
    private boolean buy;
    private String comment;

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public BigDecimal getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(BigDecimal stockPrice) {
        this.stockPrice = stockPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public boolean isPaperTrading() {
        return paperTrading;
    }

    public void setPaperTrading(boolean paperTrading) {
        this.paperTrading = paperTrading;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
