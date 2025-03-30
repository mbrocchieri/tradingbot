package org.tradingbot.strategy;

import org.tradingbot.common.bot.period.TradingPeriodEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StrategyBean {
    private final int id;
    private final String name;
    private final String xml;
    private final boolean alerts;
    private final Collection<StrategyParameterBean> defaultParameters = new ArrayList<>();
    private BigDecimal perfPercent;
    private final Map<String, BigDecimal> perfByPeriod;
    private final TradingPeriodEnum tradingPeriod;
    private final Map<String, BigDecimal> tradingPeriodParameters = new HashMap<>();

    public StrategyBean(int id, String name, String xml, Collection<StrategyParameterBean> defaultParameters,
                        boolean alerts, BigDecimal perfPercent, Map<String, BigDecimal> perfByPeriod, TradingPeriodEnum tradingPeriod) {
        this.id = id;
        this.name = name;
        this.xml = xml;
        this.tradingPeriod = tradingPeriod;
        this.defaultParameters.addAll(defaultParameters);
        this.alerts = alerts;
        this.perfPercent = perfPercent;
        this.perfByPeriod = perfByPeriod;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getXml() {
        return xml;
    }

    public boolean isAlerts() {
        return alerts;
    }

    public BigDecimal getPerfPercent() {
        return perfPercent;
    }

    public void setPerfPercent(BigDecimal perfPercent) {
        this.perfPercent = perfPercent;
    }

    public Collection<StrategyParameterBean> getDefaultParameters() {
        return defaultParameters;
    }

    public Map<String, BigDecimal> getPerfByPeriod() {
        return perfByPeriod;
    }

    public TradingPeriodEnum getTradingPeriod() {
        return tradingPeriod;
    }

    public Map<String, BigDecimal> getTradingPeriodParameters() {
        return tradingPeriodParameters;
    }
}
