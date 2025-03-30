package org.tradingbot.tradingconfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordEntity;

import java.time.ZonedDateTime;
import java.util.List;

public interface TradingRecordRepository extends JpaRepository<TradingRecordEntity, Integer> {

    List<TradingRecordEntity> findAllByConfig(TradingConfigEntity config);

    @Query("from TradingRecordEntity t where t.config = :config and t.candleStartTime < :before order by candleStartTime desc")
    List<TradingRecordEntity> findFirstByConfigAndStartTimeOrderByDesc(TradingConfigEntity config, ZonedDateTime before);

    @Query("from TradingRecordEntity t where t.config = :config and t.candleStartTime >= :afterOrEqual order by candleStartTime")
    List<TradingRecordEntity> findAllByConfigIdAfterOrderByCandleStartTime(TradingConfigEntity config, ZonedDateTime afterOrEqual);
}
