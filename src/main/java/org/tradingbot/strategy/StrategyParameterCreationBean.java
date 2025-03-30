package org.tradingbot.strategy;

import java.math.BigDecimal;

public class StrategyParameterCreationBean {
    private final String name;
    private final BigDecimal defaultValue;

    public StrategyParameterCreationBean(String name, BigDecimal defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getDefaultValue() {
        return defaultValue;
    }
}
