package org.tradingbot.trading;

public class TradingAdviceData extends TradingData {
    private final String adviser;

    public TradingAdviceData(String adviser) {
        this.adviser = adviser;
    }

    public String getAdviser() {
        return adviser;
    }
}
