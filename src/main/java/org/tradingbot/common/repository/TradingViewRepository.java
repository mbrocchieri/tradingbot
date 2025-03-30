package org.tradingbot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.TradingViewEntity;
import org.tradingbot.common.persistence.TradingViewId;

import java.util.List;

public interface TradingViewRepository extends JpaRepository<TradingViewEntity, TradingViewId> {
    @Query(value =
            "select id, type, date, buy, stock_id, strategy_id, config_id, computed_date from (SELECT tr.id, 'TR' as type, tr.candle_start_time as date, tr.TRADE_START_BUY as buy, tc.stock_id, tc.STRATEGY_ID, tr.config_id, create_date as computed_date " +
                    "FROM TRADING_RECORDS tr, TRADING_CONFIGS tc, STRATEGIES s WHERE tr.config_id = tc.id AND tc.strategy_id = s.id and s.alerts = true " +
                    "UNION " +
                    "SELECT ta.id, 'TA' as type, ta.date, ta.buy, A.STOCK_ID, A.ADVISER_ID as strategy_id, 0 as config_id, ta.date as computed_date " +
                    "FROM TRADING_ADVICE ta LEFT JOIN ADVICES a on ta.ADVICE_ID = A.ID ) as trading_view order by date desc, stock_id, strategy_id limit :size offset :page ", nativeQuery = true)
    List<TradingViewEntity> findAllPaginated(int page, int size);

    @Query(value =
            "select count(1) from (SELECT tr.id, 'TR' as type, tr.candle_start_time as date, tr.TRADE_START_BUY as buy, tc.stock_id, tc.STRATEGY_ID, tr.config_id " +
                    "FROM TRADING_RECORDS tr, TRADING_CONFIGS tc, STRATEGIES s WHERE tr.config_id = tc.id AND tc.strategy_id = s.id and s.alerts = true " +
                    "UNION " +
                    "SELECT ta.id, 'TA' as type, ta.date, ta.buy, A.STOCK_ID, A.ADVISER_ID as strategy_id, 0 as config_id " +
                    "FROM TRADING_ADVICE ta LEFT JOIN ADVICES a on ta.ADVICE_ID = A.ID ) as trading_view ", nativeQuery = true)

    long countTrading();

}
