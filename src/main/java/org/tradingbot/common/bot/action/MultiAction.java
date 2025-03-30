package org.tradingbot.common.bot.action;

import org.ta4j.core.num.Num;

import java.util.Objects;

public class MultiAction implements TradingAction {
    private final TradingAction[] actions;

    public MultiAction(TradingAction... actions) {
        this.actions = Objects.requireNonNull(actions);
        if (actions.length < 2) {
            throw new IllegalStateException("Multi Action must not be used when actions.length = " + actions.length);
        }
    }

    @Override
    public void exit(String strategyName, String symbol, Num closePrice) {
        for (var action : actions) {
            action.exit(strategyName, symbol, closePrice);
        }
    }

    @Override
    public void enter(String strategyName, String symbol, Num closePrice) {
        for (var action : actions) {
            action.enter(strategyName, symbol, closePrice);
        }
    }

    @Override
    public void sendStockCandlesCompareError(String symbol) {
        for (var action : actions) {
            action.sendStockCandlesCompareError(symbol);
        }
    }

    @Override
    public void sendConfigError(int id) {
        for (var action : actions) {
            action.sendConfigError(id);
        }
    }
}
