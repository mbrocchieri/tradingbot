package org.tradingbot.trading;

public class TradingStrategyData extends TradingData {
    private final int configId;
    private final int strategyId;
    private final String strategyName;

    public TradingStrategyData(int configId, int strategyId, String strategyName) {
        this.configId = configId;
        this.strategyId = strategyId;
        this.strategyName = strategyName;
    }

    public int getConfigId() {
        return configId;
    }

    public int getStrategyId() {
        return strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }
}
