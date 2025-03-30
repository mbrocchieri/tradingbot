package org.tradingbot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.TradeEntity;

public interface TradeRepository extends JpaRepository<TradeEntity, Integer> {
}
