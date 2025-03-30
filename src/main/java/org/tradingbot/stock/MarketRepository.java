package org.tradingbot.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.MarketEntity;

public interface MarketRepository extends JpaRepository<MarketEntity, Integer> {
}
