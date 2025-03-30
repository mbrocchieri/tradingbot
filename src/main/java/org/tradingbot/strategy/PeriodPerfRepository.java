package org.tradingbot.strategy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.PerfByPeriodId;
import org.tradingbot.common.persistence.PerfByPeriodViewEntity;

import java.util.List;

public interface PeriodPerfRepository extends JpaRepository<PerfByPeriodViewEntity, PerfByPeriodId> {

    @Query(value =
            "select c.strategy_id as strategy_id, sum(percent_performance) as perf, to_char(buy_candle_start_time, 'yyyy') as period from trading_records_summary s, trading_configs c where s.config_id = c.id group by c.strategy_id, to_char(buy_candle_start_time, 'yyyy')",
            nativeQuery = true)
    List<PerfByPeriodViewEntity> findAllPeriods();
}
