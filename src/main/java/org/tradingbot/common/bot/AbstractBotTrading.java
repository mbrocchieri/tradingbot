package org.tradingbot.common.bot;

import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.strategy.Strategy;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.util.Objects.requireNonNull;

abstract class AbstractBotTrading {

    protected final String symbol;
    protected final FinancialProvider provider;
    protected final ZonedDateTime from;
    protected final ZonedDateTime to;
    protected final org.tradingbot.common.strategy.Strategy strategy;
    protected final Interval interval;
    protected final EntityManager em;
    protected final Map<String, BigDecimal> parameters;

    AbstractBotTrading(EntityManager em, String symbol, FinancialProvider provider, Period period,
                       Strategy strategy, Map<String, BigDecimal> parameters) {
        this.em = requireNonNull(em);
        this.symbol = symbol;
        this.provider = requireNonNull(provider);
        this.from = requireNonNull(period.getFrom());
        this.to = requireNonNull(period.getTo());
        this.strategy = requireNonNull(strategy);
        this.interval = requireNonNull(period.getInterval());
        this.parameters = requireNonNull(parameters);
    }
}
