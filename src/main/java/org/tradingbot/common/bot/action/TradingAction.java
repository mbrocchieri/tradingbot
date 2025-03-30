package org.tradingbot.common.bot.action;

import org.ta4j.core.num.Num;

public interface TradingAction {
    void exit(String strategyName, String symbol, Num closePrice);

    void enter(String strategyName, String symbol, Num closePrice);

    void sendStockCandlesCompareError(String symbol);

    void sendConfigError(int id);
}
