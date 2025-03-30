package org.tradingbot.strategy;

import org.tradingbot.common.bot.period.TradingPeriodEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.tradingbot.common.bot.period.TradingPeriodEnum.OPEN_MARKET;

public class StrategyCreationBean {

    private String name;
    private String xml;
    private Collection<StrategyParameterCreationBean> defaultParameters = new ArrayList<>();
    private TradingPeriodEnum tradingPeriod = OPEN_MARKET;
    private Map<String, BigDecimal> tradingPeriodParameters;

    public StrategyCreationBean() {
    }



    public StrategyCreationBean(String name, String xml, TradingPeriodEnum tradingPeriod) {
        this.name = name;
        this.xml = xml;
        this.tradingPeriod = tradingPeriod;
    }



    public StrategyCreationBean(String name, String xml, Collection<StrategyParameterCreationBean> defaultParameters, TradingPeriodEnum tradingPeriod) {
        this.name = name;
        this.xml = xml;
        this.defaultParameters = defaultParameters;
        this.tradingPeriod = tradingPeriod;
    }



    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getXml() {
        return xml;
    }


    public void setXml(String xml) {
        this.xml = xml;
    }



    public Collection<StrategyParameterCreationBean> getDefaultParameters() {
        return defaultParameters;
    }



    public void setDefaultParameters(Collection<StrategyParameterCreationBean> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }

    public TradingPeriodEnum getTradingPeriod() {
        return tradingPeriod;
    }

    public void setTradingPeriod(TradingPeriodEnum tradingPeriod) {
        this.tradingPeriod = tradingPeriod;
    }

    public Map<String, BigDecimal> getTradingPeriodParameters() {
        return tradingPeriodParameters;
    }

    public void setTradingPeriodParameters(Map<String, BigDecimal> tradingPeriodParameters) {
        this.tradingPeriodParameters = tradingPeriodParameters;
    }
}
