package org.tradingbot.stock;

import java.math.BigDecimal;

public class PriceBean {
    private final BigDecimal amount;
    private final String currency;

    public PriceBean(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
