package org.tradingbot.tradingconfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.ConfigParameterEntity;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StrategyEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

public class TradingConfigBean {

    private final int id;
    private final int strategyId;
    private final String strategyName;
    private final int stockId;
    private final boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final ZonedDateTime startTime;
    private final Interval interval;
    private final Map<String, BigDecimal> parameters = new HashMap<>();
    private final String stockName;

    public TradingConfigBean(int id, StrategyEntity strategy, StockEntity stock, boolean active, ZonedDateTime startTime,
                             Interval interval, List<ConfigParameterEntity> parameterEntities) {
        this.id = id;
        this.strategyId = strategy.getId();
        this.strategyName = strategy.getName();
        this.stockId = stock.getId();
        this.stockName = stock.getName();
        this.active = active;
        this.startTime = startTime;
        this.interval = interval;
        for (var cpe : parameterEntities) {
            parameters.put(cpe.getParameter().getName(), cpe.getValue());
        }
    }

    public int getId() {
        return id;
    }

    public int getStrategyId() {
        return strategyId;
    }

    public int getStockId() {
        return stockId;
    }

    public boolean isActive() {
        return active;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public Interval getInterval() {
        return interval;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public String getStockName() {
        return stockName;
    }

    public Map<String, BigDecimal> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
}
