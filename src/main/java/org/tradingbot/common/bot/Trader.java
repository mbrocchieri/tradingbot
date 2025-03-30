package org.tradingbot.common.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.DecimalNum;
import org.tradingbot.common.bot.action.NoAction;
import org.tradingbot.common.bot.action.TradingAction;
import org.tradingbot.common.strategy.Strategy;
import org.tradingbot.common.strategy.StrategyInitException;
import org.tradingbot.stock.RestBarSeries;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * Trade only one symbol
 */
public class Trader {
    private static final Logger LOG = LoggerFactory.getLogger(Trader.class);

    private final TradingRecord tradingRecord;
    private final TradingAction action;
    private final Strategy strategy;
    private final Map<String, BigDecimal> parameters;
    private final RestBarSeries barSeries;
    private int currentIndex;

    public Trader(RestBarSeries barSeries, Strategy strategy, TradingAction action, TradingRecord tradingRecord,
                  Map<String, BigDecimal> parameters) {
        this.strategy = strategy;
        this.parameters = parameters;
        this.action = action;
        this.tradingRecord = tradingRecord;
        this.barSeries = barSeries;
        if (tradingRecord.getLastTrade() != null) {
            currentIndex = tradingRecord.getLastTrade().getIndex();
        }

    }

    public Trader(RestBarSeries barSeries, Strategy strategy, TradingAction action, TradingRecord tradingRecord) {
        this(barSeries, strategy, action, tradingRecord, Collections.emptyMap());
    }

    public Trader(RestBarSeries barSeries, Strategy strategy, TradingAction action) {
        this(barSeries, strategy, action, new BaseTradingRecord(), Collections.emptyMap());
    }

    public Trader(RestBarSeries barSeries, Strategy strategy) {
        this(barSeries, strategy, NoAction.INSTANCE, new BaseTradingRecord());
    }

    public void refresh() {
        try {
            var t4JStrategy = strategy.toT4JStrategy(barSeries, parameters);
            final var endIndex = barSeries.getEndIndex();
            for (; currentIndex <= endIndex; currentIndex++) {
                computeStrategy(currentIndex, t4JStrategy, barSeries);
            }

        } catch (StrategyInitException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void computeStrategy(int index, org.ta4j.core.Strategy t4JStrategy, RestBarSeries barSeries) {
        final var tradingPeriod = strategy.getTradingPeriod();
        if (tradingPeriod.shouldTrade(barSeries, index)) {
            if (t4JStrategy.shouldEnter(index, tradingRecord)) {
                final var tradePrice = tradingPeriod.getTradePrice(barSeries, index);
                boolean entered =
                        tradingRecord.enter(tradingPeriod.getTradeIndex(index), tradePrice, DecimalNum.valueOf(10));
                if (entered) {

                    if (tradingPeriod.sendAction(barSeries, index)) {
                        action.enter(strategy.getName(), barSeries.getName(), tradePrice);
                    }

                    if (LOG.isDebugEnabled()) {
                        Trade entry = tradingRecord.getLastEntry();
                        LOG.debug("ENTER symbol {} Strategy {} on candleIndex {} (price={}, amount={}, index={})",
                                barSeries.getName(), strategy.getName(), entry.getIndex(),
                                entry.getNetPrice().doubleValue(), entry.getAmount().doubleValue(), index);
                    }

                }
            } else if (t4JStrategy.shouldExit(index, tradingRecord)) {
                final var tradePrice = tradingPeriod.getTradePrice(barSeries, index);
                boolean exited =
                        tradingRecord.exit(tradingPeriod.getTradeIndex(index), tradePrice, DecimalNum.valueOf(10));
                if (exited) {
                    if (tradingPeriod.sendAction(barSeries, index)) {
                        action.exit(strategy.getName(), barSeries.getName(), tradePrice);
                    }

                    if (LOG.isDebugEnabled()) {
                        Trade entry = tradingRecord.getLastEntry();
                        LOG.debug("EXIT symbol {} Strategy {} on candleIndex {} (price={}, amount={}, index={})",
                                barSeries.getName(), strategy.getName(), entry.getIndex(),
                                entry.getNetPrice().doubleValue(), entry.getAmount().doubleValue(), index);
                    }
                }
            }
        }
    }

    public TradingRecord getTradingRecord() {
        return tradingRecord;
    }
}
