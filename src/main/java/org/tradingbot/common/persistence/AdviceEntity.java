package org.tradingbot.common.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "ADVICES")
public class AdviceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "STOCK_ID")
    private StockEntity stockEntity;

    @Column(name = "BUY")
    private boolean buy;

    @Column(name = "PRICE", precision = 20, scale = 10)
    private BigDecimal price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ADVISER_ID")
    private AdviserEntity adviserEntity;

    @Column(name = "ADVICE_DATE")
    private LocalDate adviceDate;

    @ManyToOne(optional = false)
    private AdviceCategoryEntity categoryEntity;

    @Column(name = "EXECUTED")
    private boolean executed;

    @Column(name = "DELETED")
    private ZonedDateTime deleted;

    @Column(name = "CREATION_DATE")
    private ZonedDateTime creationDate;


    // For JPA
    public AdviceEntity() {
    }

    public AdviceEntity(StockEntity stockEntity, boolean buy, BigDecimal price, AdviserEntity adviserEntity,
                        LocalDate adviceDate, AdviceCategoryEntity categoryEntity) {
        this.stockEntity = stockEntity;
        this.buy = buy;
        this.price = price;
        this.adviserEntity = adviserEntity;
        this.adviceDate = adviceDate;
        this.categoryEntity = categoryEntity;
        creationDate = ZonedDateTime.now();
    }

    public int getId() {
        return id;
    }

    public StockEntity getStockEntity() {
        return stockEntity;
    }

    public boolean isBuy() {
        return buy;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public AdviserEntity getAdviserEntity() {
        return adviserEntity;
    }

    public LocalDate getAdviceDate() {
        return adviceDate;
    }

    public AdviceCategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdviceEntity that = (AdviceEntity) o;
        return id == that.id && buy == that.buy && executed == that.executed && stockEntity.equals(that.stockEntity) &&
                price.equals(that.price) && adviserEntity.equals(that.adviserEntity) &&
                adviceDate.equals(that.adviceDate) && categoryEntity.equals(that.categoryEntity) &&
                Objects.equals(deleted, that.deleted) && creationDate.equals(that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stockEntity, buy, price, adviserEntity, adviceDate, categoryEntity, executed, deleted,
                creationDate);
    }
}
