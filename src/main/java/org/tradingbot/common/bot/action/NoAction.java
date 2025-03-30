package org.tradingbot.common.bot.action;

import org.ta4j.core.num.Num;

public class NoAction implements TradingAction {

    public static final NoAction INSTANCE = new NoAction();

    private NoAction() {

    }

    @Override
    public void exit(String strategyName, String symbol, Num closePrice) {
        // Noting to do
    }

    @Override
    public void enter(String strategyName, String symbol, Num closePrice) {
        // Noting to do
    }

    @Override
    public void sendStockCandlesCompareError(String symbol) {
        // Noting to do
    }

    @Override
    public void sendConfigError(int id) {
        // Noting to do
    }
}
