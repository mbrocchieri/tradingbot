package org.tradingbot.tradingconfig;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;

public interface TradingConfigRepository extends JpaRepository<TradingConfigEntity, Integer> {

    Collection<TradingConfigEntity> findAllByStockAndStrategy(StockEntity stock, StrategyEntity strategy);

    TradingConfigEntity findByStockAndStrategyAndInterval(StockEntity stock, StrategyEntity strategy, Interval interval);

    Collection<TradingConfigEntity> findAllByActiveTrue();

    List<TradingConfigEntity> findAllByStockIn(Pageable pageable, List<StockEntity> stockList);

    List<TradingConfigEntity> findAllByStrategyIn(Pageable pageable, List<StrategyEntity> strategyList);

    List<TradingConfigEntity> findAllByStockInAndStrategyIn(Pageable pageable, List<StockEntity> stockList, List<StrategyEntity> strategyList);

    long countByStockIn(List<StockEntity> stockList);

    long countByStrategyIn(List<StrategyEntity> strategyList);

    long countByStockInAndStrategyIn(List<StockEntity> stockList, List<StrategyEntity> strategyList);

}
