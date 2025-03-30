package org.tradingbot.tradingconfig;

import java.math.BigDecimal;
import java.util.Map;

public class TradingConfigAddBean {

    private int stockId;
    private int strategyId;
    private Map<Integer, BigDecimal> parameters;
    
    

    public Map<Integer, BigDecimal> getParameters() {
        return parameters;
    }
    public int getStrategyId() {
        return strategyId;
    }
    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
    public int getStockId() {
        return stockId;
    }
    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
    public void setParameters(Map<Integer, BigDecimal> parameters) {
        this.parameters = parameters;
    }

    
    
}
