package org.tradingbot.common.persistence;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "TRADING_ADVICE", indexes = @Index(name = "ta_date", columnList = "date"))
public class TradingAdviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ADVICE_ID", nullable = false)
    private AdviceEntity advice;

    @Column(name = "BUY", nullable = false)
    private boolean buy;

    @Column(name = "DATE", nullable = false)
    private ZonedDateTime date;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    public int getId() {
        return id;
    }

    public AdviceEntity getAdvice() {
        return advice;
    }

    public void setAdvice(AdviceEntity advice) {
        this.advice = advice;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
