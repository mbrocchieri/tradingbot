package org.tradingbot.strategy;

import java.math.BigDecimal;

public class StrategyParameterBean {
    private final int id;
    private final String name;
    private final BigDecimal defaultValue;

    public StrategyParameterBean(int id, String name, BigDecimal defaultValue) {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getDefaultValue() {
        return defaultValue;
    }
}
