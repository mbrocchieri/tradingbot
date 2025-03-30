package org.tradingbot.common.bot.period;

import org.tradingbot.common.strategy.StrategyParameter;

import java.util.function.Function;

public enum TradingPeriodEnum {
    CLOSE_MARKET(1, (ignore) -> new CloseMarketTrading()),
    OPEN_MARKET(2, (ignore) -> new OpenMarketTrading()),
    BEFORE_CLOSE_MARKET(3, (parameters) -> {
        var p = StrategyParameter.toMap(parameters);
        return new BeforeCloseMarketTrading(p.get("seconds").longValue());
    });

    private final int id;
    private final Function<String, TradingPeriod> instantiate;

    TradingPeriodEnum(int id, Function<String, TradingPeriod> instantiate) {
        this.id = id;
        this.instantiate = instantiate;
    }

    public int getId() {
        return id;
    }

    public TradingPeriod create(String parameters) {
        return instantiate.apply(parameters);
    }

    public static TradingPeriodEnum getFromId(int id) {
        for (var value : TradingPeriodEnum.values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalStateException("id " + id + " not found");
    }
}
