package org.tradingbot.common.flag;

public class NoWaitFlag implements Flag {
    public static final NoWaitFlag INSTANCE = new NoWaitFlag();

    private NoWaitFlag() {

    }

    @Override
    public void waitFlag() {
        // do nothing to not wait
    }
}
