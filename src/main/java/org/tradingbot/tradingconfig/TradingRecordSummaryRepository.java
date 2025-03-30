package org.tradingbot.tradingconfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordSummaryEntity;

import java.util.Collection;

public interface TradingRecordSummaryRepository extends JpaRepository<TradingRecordSummaryEntity, Integer> {

    Collection<TradingRecordSummaryEntity> findAllByConfig(TradingConfigEntity config);
}
