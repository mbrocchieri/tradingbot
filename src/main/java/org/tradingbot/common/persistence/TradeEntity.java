package org.tradingbot.common.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "TRADES")
public class TradeEntity {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "STOCK_ID")
    private StockEntity stock;
    @Column(name = "stock_price", nullable = false)
    private BigDecimal stockPrice;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "fees", nullable = false)
    private BigDecimal fees;
    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime date;
    @Column(name = "paper_trading", nullable = false)
    private boolean paperTrading;
    @Column(name = "buy", nullable = false)
    private boolean buy;
    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    public TradeEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StockEntity getStock() {
        return stock;
    }

    public void setStock(StockEntity stock) {
        this.stock = stock;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradeEntity that = (TradeEntity) o;
        return id == that.id && paperTrading == that.paperTrading && buy == that.buy &&
                Objects.equals(stock, that.stock) && Objects.equals(stockPrice, that.stockPrice) &&
                Objects.equals(quantity, that.quantity) && Objects.equals(fees, that.fees) &&
                Objects.equals(date, that.date) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stock, stockPrice, quantity, fees, date, paperTrading, buy, comment);
    }

    @Override
    public String toString() {
        return "TradeEntity{" + "id=" + id + ", stock=" + stock + ", stockPrice=" + stockPrice + ", quantity=" +
                quantity + ", fees=" + fees + ", date=" + date + ", paperTrading=" + paperTrading + ", buy=" + buy +
                ", comment='" + comment + '\'' + '}';
    }
}
