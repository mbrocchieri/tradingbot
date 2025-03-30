package org.tradingbot.util;

import org.ta4j.core.num.Num;
import org.tradingbot.common.bot.action.TradingAction;

public class TestTradingAction implements TradingAction {
    @Override
    public void exit(String strategyName, String symbol, Num closePrice) {
        throw new IllegalStateException();
    }

    @Override
    public void enter(String strategyName, String symbol, Num closePrice) {
        throw new IllegalStateException();
    }

    @Override
    public void sendStockCandlesCompareError(String symbol) {
        throw new IllegalStateException();
    }

    @Override
    public void sendConfigError(int id) {
        throw new IllegalStateException();
    }
}
