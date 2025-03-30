package org.tradingbot.trading;


import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.TradingAdviceEntity;

public interface TradingAdviceRepository extends JpaRepository<TradingAdviceEntity, Integer> {
}
