package org.tradingbot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.TradingStatisticsEntity;

import java.util.List;

public interface TradingStatisticsRepository extends JpaRepository<TradingStatisticsEntity, Integer> {

    @Query(value =
            "select 1 as id, count(1) as nb_trades, sum(percent_performance) as PERCENT_PERF from trading_records_summary trs, trading_configs tc where trs.config_id = tc.id and tc.strategy_id = :strategyId ", nativeQuery = true)
    List<TradingStatisticsEntity> findStrategyStatistics(int strategyId);

    @Query(value =
            "select tc.strategy_id as id, count(1) as nb_trades, sum(percent_performance) as PERCENT_PERF from trading_records_summary trs, trading_configs tc where trs.config_id = tc.id and tc.strategy_id in :strategyIds group by tc.strategy_id ", nativeQuery = true)
    List<TradingStatisticsEntity> findStrategyStatistics(List<Integer> strategyIds);
}
