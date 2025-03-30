package org.tradingbot.tradingconfig;

import org.tradingbot.stock.CandleBean;

import java.util.ArrayList;
import java.util.List;

public class TradingRecordBean {
    private final List<CandleBean> candles = new ArrayList<>();
    private final List<TradingActionBean> actions = new ArrayList<>();

    public TradingRecordBean() {
    }

    public List<CandleBean> getCandles() {
        return candles;
    }

    public List<TradingActionBean> getActions() {
        return actions;
    }
}
