package org.tradingbot.common.strategy;

public class StrategyInitException extends Exception {
    public StrategyInitException(String message) {
        super(message);
    }

    public StrategyInitException(String message, Throwable t) {
        super(message, t);
    }
}
