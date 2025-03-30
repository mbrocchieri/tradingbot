package org.tradingbot.tradingconfig;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;

@Service
public class TradingConfigService {

    @Autowired
    private TradingConfigRepository tradingConfigRepository;

    public TradingConfigEntity create(StrategyEntity strategy, StockEntity stock) {
        var tradingConfigEntity = new TradingConfigEntity(strategy, stock, true,
        ZonedDateTime.parse("2000-01-01T00:00:00+00:00"), Interval.DAILY);
        return tradingConfigRepository.save(tradingConfigEntity);
    }

}
